import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.TopologyBuilder;

public class App {
    public static void main(String[] args){
        final String SPOUT_ID = "spout";
        final String FOOD_BOLT_ID = "food bolt";
        final String COUNT_BOLT_ID = "count bolt";
        final String CLOUD_TOPOLOGY = "Cloud_NLP_Topology";
        final String SCORE_BOLT_ID = "score bolt";
        final String WRITE_BOLT_ID = "write bolt";
        final String BEAUTY_BOLT_ID = "beauty bolt";
        final String VEGGIES_BOLT_ID = "veggies bolt";

        String keyword = "food";

        Spout spout = new Spout();
        FoodBolt foodBolt = new FoodBolt();
        CountBolt countBolt = new CountBolt();
        ScoreBolt scoreBolt = new ScoreBolt();
        WriteBolt writeBolt = new WriteBolt();
        BeautyBolt beautyBolt = new BeautyBolt();
        VeggiesBolt veggiesBolt = new VeggiesBolt();

        TopologyBuilder topologyBuilder = new TopologyBuilder();
        LocalCluster localCluster = new LocalCluster();

        topologyBuilder.setSpout(SPOUT_ID,spout);
        topologyBuilder.setBolt(FOOD_BOLT_ID, foodBolt,3).shuffleGrouping(SPOUT_ID);

        topologyBuilder.setBolt(SCORE_BOLT_ID,scoreBolt,3).shuffleGrouping(SPOUT_ID);
        topologyBuilder.setBolt(WRITE_BOLT_ID,writeBolt).globalGrouping(SCORE_BOLT_ID);
        topologyBuilder.setBolt(BEAUTY_BOLT_ID,beautyBolt,3).shuffleGrouping(SPOUT_ID);
        topologyBuilder.setBolt(VEGGIES_BOLT_ID,veggiesBolt,3).shuffleGrouping(SPOUT_ID);

        BoltDeclarer boltDeclarer = topologyBuilder.setBolt(COUNT_BOLT_ID, countBolt);
        boltDeclarer.globalGrouping(VEGGIES_BOLT_ID,veggiesBolt.VEGGIES_STREAM);
        boltDeclarer.globalGrouping(BEAUTY_BOLT_ID,beautyBolt.BEAUTY_STREAM);
        boltDeclarer.globalGrouping(FOOD_BOLT_ID, foodBolt.FOOD_STREAM);

        Config config = new Config();

        config.put("input model","pre_model_29_04_2018.txt");
        //config.put("input model","src\\main\\resources\\pre_model_29_04_2018.txt");

        config.put("keyword",keyword);
        localCluster.submitTopology(CLOUD_TOPOLOGY, config, topologyBuilder.createTopology());

        //localCluster.killTopology(CLOUD_TOPOLOGY);
        //localCluster.shutdown();

    }
}
