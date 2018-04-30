package com.unimelb.COMP90019;
/**
 * Usage: java twitter4j.examples.search.SearchTweets [query]
 *
 * @param args
 * search query
 * @throws JSONException
 */
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lightcouch.CouchDbClient;
import twitter4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listener {
	static JSONObject jsonObj;
	private static String dbConfig = "couchDbConfig.properties";
	static CouchDbClient couchDbClient = new CouchDbClient(dbConfig);

    static StatusListener listener = new StatusListener() {

        // @Override
        public void onStatus(Status tweet) {
            jsonObj = new JSONObject();
            if (!SearchTweets.keywordflag) {
                try {
                    if (tweet.getGeoLocation()!= null) {
                        Map<String, Object> geoLocation = new HashMap<>();
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

                        couchDbClient.save(jsonParser.parse(jsonObj.toString()));
                    }
                } catch (twitter4j.JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //System.out.print(jsonString.toString());
            }
        }

        // @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        }

        // @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
        }

        // @Override
        public void onScrubGeo(long userId, long upToStatusId) {
            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
        }

        // @Override
        public void onStallWarning(StallWarning warning) {
            System.out.println("Got stall warning:" + warning);
        }

        // @Override
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
    };
}
