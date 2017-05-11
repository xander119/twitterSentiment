package twitter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Zechen on 2016/11/11.
 */
public class SelectionTest {

    public static void main(String args[]) {
        ArrayList<TweetSecSetEn> tweetSecSetEns = new ArrayList<>();
        Connection connection = DBHelper.getConnection();
        TweetSecSetEn tweetSecSetEn;
        ResultSet rs = DBHelper.excSelectQuery(connection, "SELECT * from twitterresearch.tweet_set_two_processed2;");
        if (rs == null) throw new NullPointerException("No results returned ");
        try {
            while (rs.next()) {
                tweetSecSetEn = new TweetSecSetEn();
               /* tweetSecSetEn.setTweet_id(rs.getLong("tweet_id"));
                tweetSecSetEn.setAirline_sentiment_confidence(rs.getLong("airline_sentiment_confidence"));
                tweetSecSetEn.setNegativereason(rs.getString("negativereason"));
                tweetSecSetEn.setNegativereason_confidence(rs.getLong("negativereason_confidence"));
                tweetSecSetEn.setAirline(rs.getString("airline"));
                tweetSecSetEn.setAirline_sentiment_gold(rs.getLong("airline_sentiment_gold"));
                tweetSecSetEn.setName(rs.getString("name"));
                tweetSecSetEn.setNegativereason_gold(rs.getLong("negativereason_gold"));
                tweetSecSetEn.setRetweet_count(rs.getString("retweet_count"));
                tweetSecSetEn.setTweet_coord(rs.getString("tweet_coord"));
                tweetSecSetEn.setTweet_created(rs.getTimestamp("tweet_created"));
                tweetSecSetEn.setTweet_location(rs.getString("tweet_location"));
                tweetSecSetEn.setUser_timezone(rs.getString("user_timezone"));
*/
                tweetSecSetEn.setLabelledSentiment(rs.getString("labelled_sentiment"));

                tweetSecSetEn.setText(rs.getString("text"));

                tweetSecSetEns.add(tweetSecSetEn);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
        List<String> gasList = new ArrayList<>();

        for (TweetSecSetEn tweetSecSetEn1 : tweetSecSetEns) {
            String charset = DetectCharset.detect(tweetSecSetEn1.getText());
            gasList.add(charset);
            if (!charset.equalsIgnoreCase("null")) {
                System.out.println(tweetSecSetEn1.getText());
            }
        }
        Set<String> uniqueGas = new HashSet<>(gasList);

        System.out.println("Unique gas count: " + uniqueGas.size());

        System.out.println("Unique: " + uniqueGas);
    }


}
