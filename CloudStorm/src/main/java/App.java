import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class App {
    public static void main(String[] args){
        final String SPOUT_ID = "spout";
        final String MODEL_BOLT_ID = "model bolt";
        final String COUNT_BOLT_ID = "count bolt";
        final String CLOUD_TOPOLOGY = "Cloud_NLP_Topology";
        String keyword = "food";

        Spout spout = new Spout();
        ModelBolt modelBolt = new ModelBolt();
        CountBolt countBolt = new CountBolt();

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        LocalCluster localCluster = new LocalCluster();

        topologyBuilder.setSpout(SPOUT_ID,spout);
        topologyBuilder.setBolt(MODEL_BOLT_ID,modelBolt).shuffleGrouping(SPOUT_ID);
        topologyBuilder.setBolt(COUNT_BOLT_ID,countBolt).globalGrouping(MODEL_BOLT_ID);

        Config config = new Config();
        config.put("input model","src/main/resources/pre_model_29_04_2018.txt");
        config.put("keyword",keyword);

        localCluster.submitTopology(CLOUD_TOPOLOGY, config, topologyBuilder.createTopology());

        //localCluster.killTopology(CLOUD_TOPOLOGY);
        //localCluster.shutdown();

    }
}
