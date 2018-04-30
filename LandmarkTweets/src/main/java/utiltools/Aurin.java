package utiltools;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.json.simple.JSONObject;
import org.lightcouch.CouchDbClient;

import java.io.*;

public class Aurin {
    private static Gson gson = new Gson();
    private static File inFile = new File("E://cloud_data/aurin_raw.json");
    private static String outFile = "E://cloud_data/aurin_features.json";
    private static String aurinConfig = "aurinDBConfig.properties";

    public static void main(String[] args) throws IOException {
        formatAurinData();
    }

    private static void formatAurinData() throws IOException {
        JsonReader jsonReader = new JsonReader(new FileReader(inFile));
        JsonParser jsonParser = new JsonParser();
        CouchDbClient couchDbClient = new CouchDbClient(aurinConfig);

        FileWriter fileWriter = new FileWriter(outFile);

        JsonObject jsonWhole = jsonParser.parse(jsonReader).getAsJsonObject();
        JsonArray features = jsonWhole.getAsJsonArray("features");
        for(JsonElement f:features){
            JsonObject feature = f.getAsJsonObject();
            JsonObject out = new JsonObject();
            out.add("property_id",feature.get("properties").getAsJsonObject().get("property_id"));
            out.add("trading_name",feature.get("properties").getAsJsonObject().get("trading_name"));
            out.add("seating_type",feature.get("properties").getAsJsonObject().get("seating_type"));//
            out.add("industry_anzsic4_code",feature.get("properties").getAsJsonObject().get("industry_anzsic4_code"));
            out.add("industry_anzsic4_description",feature.get("properties").getAsJsonObject().get("industry_anzsic4_description"));
            out.add("street_address",feature.get("properties").getAsJsonObject().get("street_address"));
            out.add("block_id",feature.get("properties").getAsJsonObject().get("block_id"));
            out.add("geo",feature.get("geometry"));

            System.out.println(out.toString());
            fileWriter.writeSingleLine(out.toString());
            couchDbClient.save(out);
        }

        couchDbClient.close();
        couchDbClient.shutdown();
        fileWriter.close();

    }
}
