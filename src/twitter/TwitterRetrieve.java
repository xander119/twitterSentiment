package twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Zechen on 2016/10/20.
 */
public class TwitterRetrieve {

    public static void main(String args[]) throws TwitterException {


        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("5owhbpkXGoKwlz511Q1T9oDLg")
                .setOAuthConsumerSecret("0haIgmGUZGkdHojzsqn3Kz8St6dH57jC4lVqeW3JRXgUsQwJUY")
                .setOAuthAccessToken("255203924-88LvwgqwuspXKBfrzFFQEf1ufR3BJCs1oQ2uzZ4X")
                .setOAuthAccessTokenSecret("afxrt2RFpGEcVjjuDBNr2xRy3DSmHyjML1kCvz8Hm8OqD");

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter4j.Twitter twitter = tf.getInstance();
        String queryString = "delta flight";
        Query query = new Query(queryString);
        QueryResult result = twitter.search(query);
        System.out.println(result.getTweets().size());
        for (Status status : result.getTweets()) {

            System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
        }

    }
}
