package CollectingBySearchAPI.CSA;
import org.lightcouch.CouchDbClient;
import twitter4j.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;

public class CollectTweets {
	static JSONObject jsonObj;
	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub
		 GeoLocation location = new GeoLocation(-37.814,144.96332); 
		 Twitter twitter = new TwitterFactory().getInstance();
		 String couchDbProperties = "couchDbConfig.properties";
		CouchDbClient couchDbClient = new CouchDbClient(couchDbProperties);
		 try {
	            Query query = new Query("hi");
	            QueryResult result;
	            do {
	                result = twitter.search(query);
	                List<Status> tweets = result.getTweets();
	                for (Status tweet : tweets) {
	                	if(tweet.getPlace()!=null) {
	                		if(tweet.getPlace().getName().equals("Melbourne")) {
	                			query.geoCode(location, 1000, tweet.getUser().getScreenName());
		                	    QueryResult innerresult= twitter.search(query);
		                	    List<Status> innertweets = innerresult.getTweets();
		                	    for(Status innertweet : innertweets) {
		                		if(tweet.getGeoLocation()!= null) {
		                			Map<String, Object> geoLocation = new HashMap<String, Object>();
		                            geoLocation.put("type", "Point");
		                            double[] geoMetry = new double[2];
		                            geoMetry[0] = tweet.getGeoLocation().getLatitude();
		                            geoMetry[1] = tweet.getGeoLocation().getLongitude();
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
		                            //Add to CouchDB Here
		                			//System.out.println("inner tweets: @" + innertweet.getUser().getScreenName() + " - " + innertweet.getText()+" id - "+innertweet.getId());
		                		}
	                		}
	                	}
	                	}
	                	else if(tweet.getGeoLocation()!=null) {
	                		if((-44<tweet.getGeoLocation().getLatitude()&&tweet.getGeoLocation().getLatitude()<-10&&112<tweet.getGeoLocation().getLongitude()&&tweet.getGeoLocation().getLongitude()<154)) {
	                		query.geoCode(location, 1000, tweet.getUser().getScreenName());
	                	    QueryResult innerresult= twitter.search(query);
	                	    List<Status> innertweets = innerresult.getTweets();
	                	    for(Status innertweet : innertweets) {
	                		if(tweet.getGeoLocation()!= null) {
	                			Map<String, Object> geoLocation = new HashMap<String, Object>();
	                            geoLocation.put("type", "Point");
	                            double[] geoMetry = new double[2];
	                            geoMetry[0] = tweet.getGeoLocation().getLatitude();
	                            geoMetry[1] = tweet.getGeoLocation().getLongitude();
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
	                            
	                          //Add to CouchDB Here
	                			//System.out.println("inner tweets: @" + innertweet.getUser().getScreenName() + " - " + innertweet.getText()+" id - "+innertweet.getId());
	                		}
	                	}
	                	}
	                	}
	                		//System.out.println("Outer Tweets: @" + tweet.getUser().getScreenName() + " - " + tweet.getText()+" id - "+tweet.getId()+" GeoLocation: "+tweet.getGeoLocation());
	                	//}
	                }
	            } while ((query = result.nextQuery()) != null);
	            System.exit(0);
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to search tweets: " + te.getMessage());
	            System.exit(-1);
	        }
	}

}
