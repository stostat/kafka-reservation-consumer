package mongoTest;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class MongoTest {

    private static final String URI        = "mongodb://root:example@localhost:27017/?authSource=admin";
    private static final String DATABASE   = "local";
    private static final String COLLECTION = "kafkaTest";

    private MongoClient client;
    private MongoCollection<Document> collection;
    private String          testId;

    @BeforeEach
    void connect() {
        client     = MongoClients.create(URI);
        MongoDatabase db = client.getDatabase(DATABASE);
        collection = db.getCollection(COLLECTION);
        testId     = "test-" + UUID.randomUUID();
    }

    @AfterEach
    void cleanup() {
        collection.deleteOne(new Document("_id", testId));
        client.close();
    }

    private Document buildEvent(String topic, String payload) {
        return Document.parse("""
            {
              "_id"      : "%s",
              "topic"    : "%s",
              "payload"  : %s,
              "processed": false
            }
            """.formatted(testId, topic, payload));
    }

    @Test
    @DisplayName("Connection is reachable and collection exists (or is created on insert)")
    void connectionIsReachable() {
        // A simple ping via the driver — throws if Mongo is unreachable
        Document ping = client.getDatabase("admin")
                .runCommand(Document.parse("{ ping: 1 }"));
        assertThat(ping.getDouble("ok")).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Document inserted with JSON string template is persisted in kafkaTest")
    void insertJsonString_documentIsPersisted() {
        String payload = """
            { "orderId": 42, "item": "widget", "qty": 3 }
            """;

        Document doc = buildEvent("orders", payload);
        collection.insertOne(doc);

        Document found = collection.find(new Document("_id", testId)).first();
        assertThat(found).isNotNull();
        assertThat(found.getString("topic")).isEqualTo("orders");
        assertThat(found.getBoolean("processed")).isFalse();
    }

    @Test
    @DisplayName("Collection count increases by 1 after insert")
    void insertJsonString_countIncreasesBy1() {
        long before = collection.countDocuments();

        collection.insertOne(buildEvent("payments", """
            { "amount": 99.99, "currency": "EUR" }
            """));

        assertThat(collection.countDocuments()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("All JSON fields survive the write/read round-trip")
    void insertJsonString_allFieldsMatchAfterRead() {
        String payload = """
            { "trackingId": "XYZ-001", "status": "SHIPPED" }
            """;

        collection.insertOne(buildEvent("shipments", payload));

        Document found = collection.find(new Document("_id", testId)).first();
        assertThat(found).isNotNull();
        assertThat(found.getString("topic")).isEqualTo("shipments");

        // Payload was stored as a nested document — verify a nested field
        Document inner = (Document) found.get("payload");
        assertThat(inner.getString("trackingId")).isEqualTo("XYZ-001");
        assertThat(inner.getString("status")).isEqualTo("SHIPPED");
    }

    @Test
    @DisplayName("Nested JSON array payload is stored and retrieved correctly")
    void insertJsonString_nestedArrayPayload() {
        collection.insertOne(buildEvent("batch", """
            { "events": [
                { "id": 1, "type": "click" },
                { "id": 2, "type": "view"  }
              ]
            }
            """));

        Document found  = collection.find(new Document("_id", testId)).first();
        assertThat(found).isNotNull();

        Document inner = (Document) found.get("payload");
        assertThat(inner.getList("events", Document.class)).hasSize(2);
    }


}
