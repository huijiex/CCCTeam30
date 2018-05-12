import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.lightcouch.CouchDbClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class WriteBolt extends BaseRichBolt {
	//static int pnumber=0,nnumber=0;
	private JsonParser jsonParser;
	static int count=0;
	private String sentimentDbConfig = "sentimentDbConfig.properties";
	private CouchDbClient cc;
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.cc = new CouchDbClient(sentimentDbConfig);
		this.jsonParser=new JsonParser();
	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		count++;
		//System.out.println("Worite Bolt Receive Success!"+input.getString(0));
		int pnumber=0,nnumber=0;
		String sentence = input.getString(0);
		String[] temp=sentence.split("    ");
		if(temp[1].equals("P")) {
			pnumber++;
		}
		else {
			//Update number of Negative and Total number in CouchDB
			nnumber++;
		}
		JsonParser data = new JsonParser();
		JsonObject dataJson = (JsonObject) data.parse(temp[0]);
		String suburb=dataJson.get("suburb").getAsString();
		//System.out.println("Worite Bolt Receive Success!");
		if(suburb.contains(" ")) {
			char[] suburbarray = suburb.toCharArray();
			for(int i=0;i<suburbarray.length;i++) {
				if(suburbarray[i]==' ') {
					suburbarray[i]='_';
				}
			}
			suburb=String.valueOf(suburbarray);
		}
		
		JsonObject ResObject = new JsonObject();
		if(!this.cc.contains(suburb)) {
				ResObject.addProperty("_id", suburb);
				ResObject.addProperty("positive_number",pnumber);
				ResObject.addProperty("negative_number",nnumber);
				ResObject.addProperty("total_number",nnumber+pnumber);
				this.cc.save(ResObject);
				//System.out.println(ResObject);
		}
		else{
			InputStream OldRes=this.cc.find(suburb);
			InputStreamReader reader = new InputStreamReader(OldRes);
			JsonObject OldUpdateJson=jsonParser.parse(reader).getAsJsonObject();
			int Oldpnumber=OldUpdateJson.get("positive_number").getAsInt();
			int Oldnnumber=OldUpdateJson.get("negative_number").getAsInt();
			String RevStr=OldUpdateJson.get("_rev").getAsString();
			int newpnumber=Oldpnumber+pnumber;
			int newnnumber=Oldnnumber+nnumber;
			ResObject.addProperty("_id", suburb);
			ResObject.addProperty("positive_number",newpnumber);
			ResObject.addProperty("negative_number",newnnumber);
			ResObject.addProperty("total_number",newnnumber+newpnumber);
			ResObject.addProperty("_rev",RevStr);
			//System.out.println(ResObject);
			//System.out.println("aaaaaa");
			this.cc.update(ResObject);

		}

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
	}

}
