import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.util.List;
import java.util.Map;

public class VeggiesBolt extends BaseRichBolt {
    private String inputModel;
    private String keyword;
    private Word2Vec vec;
    private List<String> seeds;
    private JsonParser jsonParser;
    private OutputCollector collector;
    public final String VEGGIES_STREAM = "veggies stream";

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.inputModel = (String) map.get("input model");
        this.keyword = "vegetarian";
        this.vec = WordVectorSerializer.readWord2VecModel(this.inputModel);
        this.seeds = (List<String>) this.vec.wordsNearestSum(this.keyword, 300);
        System.out.println(this.seeds);

        this.jsonParser = new JsonParser();
        this.collector = outputCollector;
    }

    public void execute(Tuple tuple) {
        String raw = tuple.getStringByField("raw tweet");
        //System.out.println(raw);
        JsonObject tweet = jsonParser.parse(raw).getAsJsonObject();
        String text = normalizeText(tweet.get("text").getAsString());
        for(String seed:seeds){
            if(text.contains(seed)||text.contains(seed.replaceAll("_"," "))){
                this.collector.emit(VEGGIES_STREAM, new Values(raw));
                return;
            }
        }

    }

    private String normalizeText(String text) {
        return text.toLowerCase().
                replaceAll("[^_0-9a-zA-Z ]","").
                replaceAll("__+","").
                replaceAll("[ ]+"," ").trim();
    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(VEGGIES_STREAM, new Fields("on-topic tweet"));
    }
}
