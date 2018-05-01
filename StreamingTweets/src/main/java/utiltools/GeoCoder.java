package utiltools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class GeoCoder {
    private final String urlPrefix = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private final String apiKey = "AIzaSyDdy7vzoFsQlkyjxrSJSj3ohWT5IvH4Nss";
    private HttpURLConnection httpURLConnection;
    private JsonParser jsonParser = new JsonParser();

    public String getSuburbByGeometry(double[] coordinates) throws IOException {
        double latitude = coordinates[1];  //纬度
        double longtitude = coordinates[0];  //经度

        String query = urlPrefix+latitude+","+longtitude+"&key="+apiKey+"&language=en";  //add language property

        URL url = new URL(query);

        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoInput(true);

        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),
                "UTF-8"));
        String line;
        StringBuffer result= new StringBuffer();
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        httpURLConnection.disconnect();

        //System.out.println(result.toString());
        JsonObject jsonResponse = jsonParser.parse(result.toString()).getAsJsonObject();

        String suburb = getSuburbName(jsonResponse);
        return suburb;


    }

    private String getSuburbName(JsonObject jsonResponse) {
        JsonArray resArray = jsonResponse.getAsJsonArray("results");

        if(resArray.size()==0){
            return "null";
        }

        JsonObject firstObj = resArray.get(0).getAsJsonObject();
        String formattedAddress = firstObj.get("formatted_address").getAsString();
        //System.out.println(formattedAddress);

        JsonArray addComponents = firstObj.getAsJsonArray("address_components");
        for(JsonElement component:addComponents){
            JsonObject jsonObject = component.getAsJsonObject();
            JsonArray types = jsonObject.getAsJsonArray("types");
            String subName = jsonObject.get("long_name").getAsString();
            for(JsonElement e:types){
                String type = e.getAsString();
                if(type.equalsIgnoreCase("locality")){
                    return subName;
                }
            }
        }

        return "null";
    }
}
