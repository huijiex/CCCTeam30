import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ScoreBolt extends BaseRichBolt{
	private OutputCollector collector;
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector=collector;
		//NLP.init();
	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		String sentence = input.getString(0);
		JsonParser data = new JsonParser();
		JsonObject dataJson = (JsonObject) data.parse(sentence);
		String textcontent=dataJson.get("text").getAsString();
		//NLP.init();
		NLP.init();
		int s=NLP.findSentiment(CleanupTweets(textcontent));
		if(s>=2) {
			sentence=sentence+"    "+"P";
			collector.emit(new Values(sentence));
			//System.out.println("ScoreBolt Emit P Success");
		}
		else {
			sentence=sentence+"    "+"N";
			collector.emit(new Values(sentence));
			//System.out.println("ScoreBolt Emit N Success");
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("TweetfromSpout"));
	}

	public String CleanupTweets (String str) {
		String strres="";
		String[] words = str.split(" ");
		for(String word : words) {
			word = word.trim();
			if(StringUtils.isNotBlank(word)) {
				word=word.toLowerCase();
                String pattern = "http.*";//RegExp
                String pattern1=".*[A-Za-z|\\;|\\:|\\)|\\=|\\^|\\-|\\^|\\(|'].*";//RegExp
                String pattern2 = "@.*";//RegExp
                String pattern3 = "#.*";//RegExp
                boolean isMatch = Pattern.matches(pattern, word);
                boolean isMatch1 = Pattern.matches(pattern1, word);
                boolean isMatch2 = Pattern.matches(pattern2, word);
                boolean isMatch3 = Pattern.matches(pattern3, word);
                if(!isMatch&&isMatch1&&!isMatch2&&!isMatch3) //pick up useful words for each tweet
                {
                	if(strres=="") {
                		strres=strres+word; 
                	}
                	else {
                		strres=strres+" "+word;
                	}
                	                
                }
			}
		}
		return strres;
	}
	

}
