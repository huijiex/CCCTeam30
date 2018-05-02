package Utils;

import com.google.gson.JsonObject;
import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;

public class TestUtil {
    public static CouchDbClient couchDbClient = new CouchDbClient("testChange.properties");

    public static void main(String[] args){
        continuesChange(couchDbClient);
    }

    public static void continuesChange(CouchDbClient dbClient){
        // feed type continuous
        Changes changes = dbClient.changes()
                .includeDocs(true)
                .heartBeat(30000)
                .continuousChanges();

        while (changes.hasNext()) {
            ChangesResult.Row feed = changes.next();
            String docId = feed.getId();
            JsonObject doc = feed.getDoc();
            System.out.println(doc.toString());
            // changes.stop(); // stop continuous feed
        }
    }

    public static void normalChange(CouchDbClient dbClient){
        // feed type normal
        String since = dbClient.context().info().getUpdateSeq(); // latest update seq
        ChangesResult changeResult = dbClient.changes()
                .since(since)
                .limit(10)
                .filter("example/filter")
                .getChanges();

        for (ChangesResult.Row row : changeResult.getResults()) {
            String docId = row.getId();
            JsonObject doc = row.getDoc();
        }
    }
}
