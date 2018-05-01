package utiltools;

import java.io.*;
import java.math.BigDecimal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.lightcouch.CouchDbClient;
import org.lightcouch.DocumentConflictException;


public class Supply {
    private static String dirInput = "E://cloud_data/supply.json";
    private static String dbConfig = "twitterDbConfig.properties";
    private static String dirOutput = "E://cloud_data/formatted_supply.json";
    private static BigDecimal xmin = new BigDecimal(144.2930);
    private static BigDecimal xmax = new BigDecimal(145.9080);
    private static BigDecimal ymin = new BigDecimal(-38.5185);
    private static BigDecimal ymax = new BigDecimal(-37.1542);

    private static int start = 0;
    private static int end = 2000;

    public static void main(String[] args) throws IOException {
        CouchDbClient couchDbClient = new CouchDbClient(dbConfig);

        File file = new File(dirInput);
        FileInputStream inputStream = new FileInputStream(file);
        GeoCoder geoCoder = new GeoCoder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream),20*1024);
        FileWriter fileWriter = new FileWriter(dirOutput);

        int count = 0;
        String line = br.readLine();
        while (StringUtils.isNotBlank(line)){
            if(count>=start&&count<=end){
                System.out.println(line);
                JsonParser jsonParser = new JsonParser();
                JsonObject rawPost = jsonParser.parse(line).getAsJsonObject();
                JsonObject outPost = new JsonObject();
                if(!rawPost.get("geo").isJsonNull()){
                    outPost.addProperty("_id",rawPost.get("id").getAsString());
                    //outPost.add("geo",rawPost.get("geo"));
                    JsonObject temGeo = rawPost.getAsJsonObject("geo");
                    JsonArray coordinates = temGeo.getAsJsonArray("coordinates");
                    double[] coor = new double[2];
                    coor[0] = coordinates.get(1).getAsDouble();//longtitude
                    coor[1] = coordinates.get(0).getAsDouble();//latitude
                    if(inBoundingBox(coor)){
                        JsonArray cooArray = new JsonArray();
                        for(double c:coor){
                            cooArray.add(c);
                        }
                        String type = temGeo.get("type").getAsString();
                        JsonObject newGeo = new JsonObject();
                        newGeo.addProperty("type",type);
                        newGeo.add("coordinates",cooArray);
                        outPost.add("geo",newGeo);
                        String sub = geoCoder.getSuburbByGeometry(coor);
                        outPost.addProperty("suburb",sub);
                        outPost.addProperty("place","null");
                        outPost.add("created_at",rawPost.get("created_at"));
                        outPost.add("text",rawPost.get("text"));
                        System.out.println(outPost.toString());

                        try{
                            couchDbClient.save(outPost);
                        } catch(DocumentConflictException e){
                            count++;
                            line = br.readLine();
                            continue;
                        }
                        fileWriter.writeSingleLine(outPost.toString());
                    }
                }

            }
            count++;
            line = br.readLine();
        }

        couchDbClient.close();
        couchDbClient.shutdown();
        fileWriter.close();
    }

    public static boolean inBoundingBox(double[] coor) {
        BigDecimal lon = new BigDecimal(coor[0]);
        BigDecimal lan = new BigDecimal(coor[1]);

        if((lon.compareTo(xmin)==1)&&(lon.compareTo(xmax)==-1)){
            if((lan.compareTo(ymin)==1)&&(lan.compareTo(ymax)==-1)){
                return true;
            }
        }

        return false;
    }


}
