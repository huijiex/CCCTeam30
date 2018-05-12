import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.lightcouch.CouchDbClient;
import org.lightcouch.DocumentConflictException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class CountBolt extends BaseRichBolt {
    private JsonParser jsonParser;
    private String foodDbConfig = "foodDbConfig.properties";
    private String beautyDbConfig = "beautyDbConfig.properties";
    private String veggiesDbConfig = "veggiesDbConfig.properties";
    private CouchDbClient foodDbClient;
    private CouchDbClient veggiesDbClient;
    private CouchDbClient beautyDbClient;
    private final String FOOD_STREAM = "food stream";
    private final String BEAUTY_STREAM = "beauty stream";
    private final String VEGGIES_STREAM = "veggies stream";

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.foodDbClient = new CouchDbClient(this.foodDbConfig);
        this.beautyDbClient = new CouchDbClient(this.beautyDbConfig);
        this.veggiesDbClient = new CouchDbClient(this.veggiesDbConfig);
        this.jsonParser = new JsonParser();
    }

    public void execute(Tuple tuple) {
        JsonObject tweet = jsonParser.parse(tuple.getStringByField("on-topic tweet")).getAsJsonObject();
        String sub = tweet.get("suburb").getAsString().replaceAll(" ","_");

        if(tuple.getSourceStreamId().equals(FOOD_STREAM)){
            if(this.foodDbClient.contains(sub)){
                //find and update
                InputStream in = this.foodDbClient.find(sub);
                InputStreamReader reader = new InputStreamReader(in);
                JsonObject oldValue = jsonParser.parse(reader).getAsJsonObject();
                int count = oldValue.get("count").getAsInt();
                JsonObject newValue = new JsonObject();
                newValue.addProperty("_id",sub);
                newValue.addProperty("_rev",oldValue.get("_rev").getAsString());
                newValue.addProperty("count",(count+1));
                this.foodDbClient.update(newValue);
                return;

            }else{
                //save
                JsonObject jsonResult = new JsonObject();
                jsonResult.addProperty("_id",sub);
                jsonResult.addProperty("count",1);
                try{
                    this.foodDbClient.save(jsonResult);
                } catch (DocumentConflictException e){
                    return;
                }

            }
        }else if(tuple.getSourceStreamId().equals(BEAUTY_STREAM)){
            if(this.beautyDbClient.contains(sub)){
                //find and update
                InputStream in = this.beautyDbClient.find(sub);
                InputStreamReader reader = new InputStreamReader(in);
                JsonObject oldValue = jsonParser.parse(reader).getAsJsonObject();
                int count = oldValue.get("count").getAsInt();
                JsonObject newValue = new JsonObject();
                newValue.addProperty("_id",sub);
                newValue.addProperty("_rev",oldValue.get("_rev").getAsString());
                newValue.addProperty("count",(count+1));
                this.beautyDbClient.update(newValue);
                return;

            }else{
                //save
                JsonObject jsonResult = new JsonObject();
                jsonResult.addProperty("_id",sub);
                jsonResult.addProperty("count",1);
                try{
                    this.beautyDbClient.save(jsonResult);
                } catch (DocumentConflictException e){
                    return;
                }

            }
        } else if(tuple.getSourceStreamId().equals(VEGGIES_STREAM)){
            if(this.veggiesDbClient.contains(sub)){
                //find and update
                InputStream in = this.veggiesDbClient.find(sub);
                InputStreamReader reader = new InputStreamReader(in);
                JsonObject oldValue = jsonParser.parse(reader).getAsJsonObject();
                int count = oldValue.get("count").getAsInt();
                JsonObject newValue = new JsonObject();
                newValue.addProperty("_id",sub);
                newValue.addProperty("_rev",oldValue.get("_rev").getAsString());
                newValue.addProperty("count",(count+1));
                this.veggiesDbClient.update(newValue);
                return;

            }else{
                //save
                JsonObject jsonResult = new JsonObject();
                jsonResult.addProperty("_id",sub);
                jsonResult.addProperty("count",1);
                try{
                    this.veggiesDbClient.save(jsonResult);
                } catch (DocumentConflictException e){
                    return;
                }

            }
        }


    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
