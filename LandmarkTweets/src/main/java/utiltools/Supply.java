package utiltools;

import java.io.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.lightcouch.CouchDbClient;


public class Supply {
    private static String dirInput = "E://cloud_data/supply.json";
    private static String dbConfig = "couchDbConfig.properties";

    public static void main(String[] args) throws IOException {
        CouchDbClient couchDbClient = new CouchDbClient(dbConfig);

        File file = new File(dirInput);
        FileInputStream inputStream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream),20*1024);

        String line = br.readLine();
        while (StringUtils.isNotBlank(line)){
            System.out.println(line);
            JsonParser jsonParser = new JsonParser();
            JsonObject rawPost = jsonParser.parse(line).getAsJsonObject();
            JsonObject outPost = new JsonObject();
            if(!rawPost.get("geo").isJsonNull()){
                outPost.add("geo",rawPost.get("geo"));
                outPost.addProperty("place","null");
                outPost.add("created_at",rawPost.get("created_at"));
                outPost.add("text",rawPost.get("text"));
                outPost.addProperty("_id",rawPost.get("id").getAsString());

                System.out.println(outPost.toString());
                couchDbClient.save(outPost);
            }

            line = br.readLine();
        }
    }


}
