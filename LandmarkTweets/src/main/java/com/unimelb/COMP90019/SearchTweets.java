package com.unimelb.COMP90019;

import com.alibaba.fastjson.JSONException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author minjiez
 * @version 2.0
 */
public class SearchTweets {
    public static String keywords = lmlist(0);

    static double[][] boundingBox = new double[2][2];
    static boolean keywordflag = false;
    static FilterQuery keywordfilter = new FilterQuery();
    static FilterQuery boundfilter = new FilterQuery();


    public static void main(String[] args) throws JSONException {
//        SearchTweets searchTweets = new SearchTweets();
//
//        Twitter twitter = new TwitterFactory().getInstance();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        /*cb.setOAuthConsumerKey("0kzHxdlmuE3RNu8YUL7wOYklG");
        cb.setOAuthConsumerSecret("Q0fV8FnECFkhHN8pZgxyKGX2YDtrxtoKtsbHpFUsr1fuk65MRz");
        cb.setOAuthAccessToken("970228986386137088-eWp1RptsUuP996GcZYrmBt27RGdHlvr");
        cb.setOAuthAccessTokenSecret("qQKUvq4CLxCfrRHsLzJOLmkhb6gDQvc6k5qHiGBRHhcGU");*/

        cb.setOAuthConsumerKey("TwRuKCE3rpe8xDy19NaTVwL5g");
        cb.setOAuthConsumerSecret("iu5mCl1yfTPaTVpsh1JneyrdrCpTyz8rtLqMpO0j1SQ7KfR3mY");
        cb.setOAuthAccessToken("970215325676462080-sLkH8A1U1fi3fuAF8B9r4vqCuQF9nTx");
        cb.setOAuthAccessTokenSecret("pq0NqvFWoKLkQwMUkiXcKyyY1gCmj02tUAtz1KNIeJjZV");


        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(Listener.listener);

        if (!keywordflag) {
            /*Twitter4j only support small bounding box lat(max&min) difference and lon(max&min) difference
                should less than 1, some landmark should not use that.
             */
            Map<String, Double> coords;
            coords = OpenStreetMapUtils.getInstance().getCoordinates("Melbourne");
            //System.out.println("latitude :" + coords.get("latmin") + coords.get("latmax"));
            //System.out.println("longitude:" + coords.get("lonmin") + coords.get("lonmax"));
            //{{-55.3228175,-9.0882278},{72.2460938,168.2249543}};//au
            //boundingBox[0][0] = 103.618248;boundingBox[0][1] = 1.1158;boundingBox[1][0] = 104.40847;boundingBox[1][1] = 1.47062;//sing
            boundingBox[0][0] = coords.get("latmin");
            boundingBox[0][1] = coords.get("lonmin");
            boundingBox[1][0] = coords.get("latmax");
            boundingBox[1][1] = coords.get("lonmax");
            boundfilter.locations(boundingBox);
            twitterStream.filter(boundfilter);

        } else {
            keywordfilter.track(keywords);
            twitterStream.filter(keywordfilter);
        }
    }



    public static String lmlist(int i){
        ArrayList<String> landmarklist = new ArrayList<String>();
        landmarklist.add("");//0
        landmarklist.add("State Library");
        landmarklist.add("Great Ocean Rd");
        landmarklist.add("Queen Victoria Market");//3
        landmarklist.add("Luna Park");
        landmarklist.add("Flinders Street");//5
        landmarklist.add("Melbourne Zoo");
        landmarklist.add("Shrine of Remembrance");
        return landmarklist.get(i);

    }


}
