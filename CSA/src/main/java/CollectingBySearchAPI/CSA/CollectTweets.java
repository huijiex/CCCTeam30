package CollectingBySearchAPI.CSA;
import org.lightcouch.CouchDbClient;

import twitter4j.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.json.simple.parser.JSONParser;

public class CollectTweets {
	static JSONObject jsonObj=new JSONObject();
	private static CollectingClass c =new CollectingClass();
	private static GeoCoder geoCoder = new GeoCoder();
	public static void main(String[] args) throws JSONException {
		while(true) {
		//System.out.println("I'm Running");
		// TODO Auto-generated method stub
		GeoLocation location = new GeoLocation(-37.814,144.96332); 
		Twitter twitter = new TwitterFactory().getInstance();
		String couchDbProperties = "couchDbConfig.properties";
		CouchDbClient couchDbClient = new CouchDbClient(couchDbProperties);
		 try {
	            Query query = new Query();
	            QueryResult result;
	            query.setGeoCode(location, 10.00, Query.KILOMETERS);//Search for the tweets whose user's profile location is near Melbourne
	            do {
	                result = twitter.search(query);
	                List<Status> tweets = result.getTweets();
	                for (Status tweet : tweets) {
	                	if(tweet.getGeoLocation()!=null) {
	                		System.out.println("????????????????????");
	                		Map<String, Object> geoLocation = new HashMap<String, Object>();
                           geoLocation.put("type", "Point");
                           double[] geoMetry = new double[2];
                           geoMetry[0] = tweet.getGeoLocation().getLongitude();
                           geoMetry[1] = tweet.getGeoLocation().getLatitude();
                           geoLocation.put("coordinates", geoMetry);
                           String suburb;
						try {
							suburb = geoCoder.getSuburbByGeometry(geoMetry);
							jsonObj.put("suburb",suburb);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                          // System.out.println(geoLocation);
                           //System.out.println( jsonObj);
                           jsonObj.put("geo", geoLocation);
                           
                           if (tweet.getPlace() != null) {
                               Map<String, String> place = new HashMap<String, String>();
                               place.put("type", "polygon");
                               place.put("coordinates", tweet.getPlace().toString());
                               jsonObj.put("place", place);

                           } else {
                               jsonObj.put("place", "null");
                           }
                           jsonObj.put("created_at", tweet.getCreatedAt());
                           jsonObj.put("text", tweet.getText());
                           jsonObj.put("_id",String.valueOf(tweet.getId()));

                           JSONParser jsonParser = new JSONParser();
                           couchDbClient.save(jsonObj);
	                	}
	         	 }
	            } while ((query = result.nextQuery()) != null);
	            System.exit(0);
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to search tweets: " + te.getMessage());
	            System.exit(-1);
	        } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 try {
				Thread.sleep(1000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
