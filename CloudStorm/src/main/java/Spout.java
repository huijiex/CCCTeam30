import com.google.gson.JsonObject;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.lightcouch.Changes;
import org.lightcouch.ChangesResult;
import org.lightcouch.CouchDbClient;

import java.util.Map;

public class Spout extends BaseRichSpout {
    private String twitterdbConfig = "twitterDbConfig.properties";
    private SpoutOutputCollector collector;
    private CouchDbClient couchDbClient;
    private Changes changes;

    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.couchDbClient = new CouchDbClient(twitterdbConfig);
        this.changes = this.couchDbClient.changes()
                .includeDocs(true)
                .heartBeat(30000)
                .continuousChanges();
        this.collector = spoutOutputCollector;
    }

    public void nextTuple() {
        while(this.changes.hasNext()){
            ChangesResult.Row feed = this.changes.next();
            JsonObject doc = feed.getDoc();
            //System.out.println(doc.toString());
            if((!doc.has("text"))||(!doc.has("suburb"))){
                continue;
            }else {
                this.collector.emit(new Values(doc.toString()));
            }

        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("raw tweet"));
    }

}
