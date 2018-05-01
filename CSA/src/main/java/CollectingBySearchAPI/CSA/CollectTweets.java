package CollectingBySearchAPI.CSA;
import org.lightcouch.CouchDbClient;
import twitter4j.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.simple.parser.JSONParser;

public class CollectTweets {
	static JSONObject jsonObj;
	private static CollectingClass c =new CollectingClass();
	public static void main(String[] args) throws JSONException {
		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
		scheduledThreadPool.scheduleAtFixedRate(c,(long)1.0, (long) 1.0,TimeUnit.HOURS);
		//scheduledThreadPool.schedule(c,(long)1.0,TimeUnit.SECONDS);
		// TODO Auto-generated method stub
		
		/* GeoLocation location = new GeoLocation(-37.814,144.96332); 
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
	                		Map<String, Object> geoLocation = new HashMap<String, Object>();
                            geoLocation.put("type", "Point");
                            double[] geoMetry = new double[2];
                            geoMetry[0] = tweet.getGeoLocation().getLongitude();
                            geoMetry[1] = tweet.getGeoLocation().getLatitude();
                            geoLocation.put("coordinates", geoMetry);
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
	                		//System.out.println("@ "+tweet.getUser().getScreenName() + " - " + tweet.getText()+" id - "+tweet.getId()+" GeoLocation: "+tweet.getGeoLocation());
	                		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!"+String.valueOf(jsonObj));
	                	    
	                	}
	                	
	                	System.out.println("@ "+tweet.getUser().getScreenName() + " - " + tweet.getText()+" id - "+tweet.getId()+" GeoLocation: "+tweet.getGeoLocation());
	                	
	                	 }
	            } while ((query = result.nextQuery()) != null);
	            System.exit(0);
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to search tweets: " + te.getMessage());
	            System.exit(-1);
	        }*/
	}

}
