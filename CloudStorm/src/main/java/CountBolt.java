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
    private CouchDbClient dbClient;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.dbClient = new CouchDbClient(this.foodDbConfig);
        this.jsonParser = new JsonParser();
    }

    public void execute(Tuple tuple) {
        JsonObject tweet = jsonParser.parse(tuple.getStringByField("on-topic tweet")).getAsJsonObject();
        String sub = tweet.get("suburb").getAsString().replaceAll(" ","_");

        if(this.dbClient.contains(sub)){
            //find and update
            InputStream in = this.dbClient.find(sub);
            InputStreamReader reader = new InputStreamReader(in);
            JsonObject oldValue = jsonParser.parse(reader).getAsJsonObject();
            int count = oldValue.get("count").getAsInt();
            JsonObject newValue = new JsonObject();
            newValue.addProperty("_id",sub);
            newValue.addProperty("_rev",oldValue.get("_rev").getAsString());
            newValue.addProperty("count",(count+1));
            this.dbClient.update(newValue);
            return;

        }else{
            //save
            JsonObject jsonResult = new JsonObject();
            jsonResult.addProperty("_id",sub);
            jsonResult.addProperty("count",1);
            try{
                this.dbClient.save(jsonResult);
            } catch (DocumentConflictException e){
                return;
            }

        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
