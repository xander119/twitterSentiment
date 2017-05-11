package twitter;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Zechen on 2016/10/31.
 */
public class TweetPreProcessorTwoExpand {

    private String regexURL = "\\b(https?|ftp|file)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public static void main(String[] args) throws Exception {
        TweetPreProcessorTwoExpand tweetPreprocessor = new TweetPreProcessorTwoExpand();
//        tweetPreprocessor.replaceEmojisFromP1SaveToP2();
//        ArrayList<TweetSecSetEn> tweetsFromDB = tweetPreprocessor.getTweetsFromDB("select * from tweet_set_two_processed_1_2 where text like 'i%'");
//        System.out.println(EmojiParser.parseToAliases(tweetsFromDB.get(0).getText()));
//        tweetPreprocessor.saveToProcessed2airportcode();
//        tweetPreprocessor.replaceEmojisFromP1SaveToP2();
//        tweetPreprocessor.removeEmojisFromP1SaveToP3();
    }


    public void saveToProcessed2airportcode() {
        //1. get data sets
        System.out.println("----------------------------Processing Original Data ... ");

        //2. Pre-processing and save to different .arff and table
        //   1. Decode text
        //   2.Remove Duplicate Tweets
        String selectTweets = "SELECT c.* from tweet_set_two_processed1 as c order by c.tweet_id; ";
        ArrayList<TweetSecSetEn> tweetSecSetEns = getTweetsFromDB(selectTweets);

        if (tweetSecSetEns == null) throw new NullPointerException("No tweets");

        for (TweetSecSetEn tweetSecSetEn1 : tweetSecSetEns) {

            String text = tweetSecSetEn1.getText().replaceAll("\\p{Blank}{2,}+", " ");

            text = StringUtils.replaceEach(StringEscapeUtils.unescapeHtml4(text),
                    new String[]{"‿", "“", "”"}, new String[]{"", "", ""});
            System.out.println("Processing Tweet: " + text);

            //Expand airport code to full name
            text = this.expandAirportCode(text);

            //Remove URL and Username
            text = this.removeURLAndUserName(text);

            // Unescape HTML entity
            text = text.replaceAll("\\p{Blank}{2,}+", " ") // Replace any Blank (a  space or a tab) by a single space.
                    .replaceAll("\\d+", ""); // remove any digits

//            text = text.replaceAll("\\p{Punct}", "");
            // Replace text
            tweetSecSetEn1.setText(text);

            System.out.println("Processed Tweet: " + text);
        }

        List<String> gasList = new ArrayList<>();
        Map<String, TweetSecSetEn> map = new HashMap<String, TweetSecSetEn>();
        for (TweetSecSetEn tweet : tweetSecSetEns) {
            gasList.add(DetectCharset.detect(tweet.getText()));

            if (!map.containsKey(tweet.getText())) {
                map.put(tweet.getText(), tweet);
            }
        }

        Set<String> uniqueGas = new HashSet<>(gasList);
        System.out.println("Unique tweet count: " + uniqueGas.size());
        System.out.println("Unique: " + uniqueGas);
        Connection connection = DBHelper.getConnection();
        int rowCount = 0;
        int count = 0;
        final int batchSize = 1000;
        try {
            PreparedStatement insertTweet = null;
            System.out.println("map.size: " + map.size());
            insertTweet = connection.prepareStatement("INSERT INTO tweet_set_two_processed2airportcode_expanded" +
                    "(tweet_id," +
                    "labelled_sentiment, " +
                    "airline_sentiment_confidence," +
                    "negativereason," +
                    "negativereason_confidence," +
                    "airline," +
                    "airline_sentiment_gold," +
                    "name," +
                    "negativereason_gold," +
                    "retweet_count," +
                    "text," +
                    "tweet_coord," +
                    "tweet_created," +
                    "tweet_location," +
                    "user_timezone" +
                    ")" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
            for (Map.Entry<String, TweetSecSetEn> entry : map.entrySet()) {
                TweetSecSetEn tweetSecSetEn = entry.getValue();

                insertTweet.setLong(1, tweetSecSetEn.getTweet_id());
                insertTweet.setString(2, tweetSecSetEn.getLabelledSentiment());
                insertTweet.setLong(3, tweetSecSetEn.getAirline_sentiment_confidence());
                insertTweet.setString(4, tweetSecSetEn.getNegativereason());
                insertTweet.setLong(5, tweetSecSetEn.getNegativereason_confidence());
                insertTweet.setString(6, tweetSecSetEn.getAirline());
                insertTweet.setLong(7, tweetSecSetEn.getAirline_sentiment_gold());
                insertTweet.setString(8, tweetSecSetEn.getName());
                insertTweet.setLong(9, tweetSecSetEn.getNegativereason_gold());
                insertTweet.setString(10, tweetSecSetEn.getRetweet_count());
                insertTweet.setString(11, tweetSecSetEn.getText());
                insertTweet.setString(12, tweetSecSetEn.getTweet_coord());
                insertTweet.setTimestamp(13, tweetSecSetEn.getTweet_created());
                insertTweet.setString(14, tweetSecSetEn.getTweet_location());
                insertTweet.setString(15, tweetSecSetEn.getUser_timezone());

                insertTweet.addBatch();
                if (++count % batchSize == 0) {
                    System.out.println("rowCount: " + count);
                    insertTweet.executeBatch();
                }

                rowCount++;

            }
            int[] updateCounts = insertTweet != null ? insertTweet.executeBatch() : new int[0];
            System.out.println("rowCount: " + rowCount);
            assert insertTweet != null;
            insertTweet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void airportCodeSlang() {
        //1. get data sets
        System.out.println("----------------------------Processing Original Data ... ");

        //2. Pre-processing and save to different .arff and table
        //   1. Decode text
        //   2.Remove Duplicate Tweets
        String selectTweets = "SELECT c.* from tweet_set_two_processed1 as c order by c.tweet_id; ";
        ArrayList<TweetSecSetEn> tweetSecSetEns = getTweetsFromDB(selectTweets);

        if (tweetSecSetEns == null) throw new NullPointerException("No tweets");

        for (TweetSecSetEn tweetSecSetEn1 : tweetSecSetEns) {

            String text = tweetSecSetEn1.getText().replaceAll("\\p{Blank}{2,}+", " ");

            text = StringUtils.replaceEach(StringEscapeUtils.unescapeHtml4(text),
                    new String[]{"‿", "“", "”"}, new String[]{"", "", ""});
            System.out.println("Processing Tweet: " + text);

            //Expand airport code to full name
            text = this.expandAirportCode(text);

            //Expand tweet slang
            text = this.expandTweetSlang(text);

            //Remove URL and Username
            text = this.removeURLAndUserName(text);

            // Unescape HTML entity
            text = text.replaceAll("\\p{Blank}{2,}+", " ") // Replace any Blank (a  space or a tab) by a single space.
                    .replaceAll("\\d+", ""); // remove any digits

//            text = text.replaceAll("\\p{Punct}", "");
            // Replace text
            tweetSecSetEn1.setText(text);

            System.out.println("Processed Tweet: " + text);
        }

        List<String> gasList = new ArrayList<>();
        Map<String, TweetSecSetEn> map = new HashMap<String, TweetSecSetEn>();
        for (TweetSecSetEn tweet : tweetSecSetEns) {
            gasList.add(DetectCharset.detect(tweet.getText()));

            if (!map.containsKey(tweet.getText())) {
                map.put(tweet.getText(), tweet);
            }
        }

        Set<String> uniqueGas = new HashSet<>(gasList);
        System.out.println("Unique tweet count: " + uniqueGas.size());
        System.out.println("Unique: " + uniqueGas);
        Connection connection = DBHelper.getConnection();
        int rowCount = 0;
        int count = 0;
        final int batchSize = 1000;
        try {
            PreparedStatement insertTweet = null;
            System.out.println("map.size: " + map.size());
            insertTweet = connection.prepareStatement("INSERT INTO tweet_set_two_processed2slang_expanded" +
                    "(tweet_id," +
                    "labelled_sentiment, " +
                    "airline_sentiment_confidence," +
                    "negativereason," +
                    "negativereason_confidence," +
                    "airline," +
                    "airline_sentiment_gold," +
                    "name," +
                    "negativereason_gold," +
                    "retweet_count," +
                    "text," +
                    "tweet_coord," +
                    "tweet_created," +
                    "tweet_location," +
                    "user_timezone" +
                    ")" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
            for (Map.Entry<String, TweetSecSetEn> entry : map.entrySet()) {
                TweetSecSetEn tweetSecSetEn = entry.getValue();

                insertTweet.setLong(1, tweetSecSetEn.getTweet_id());
                insertTweet.setString(2, tweetSecSetEn.getLabelledSentiment());
                insertTweet.setLong(3, tweetSecSetEn.getAirline_sentiment_confidence());
                insertTweet.setString(4, tweetSecSetEn.getNegativereason());
                insertTweet.setLong(5, tweetSecSetEn.getNegativereason_confidence());
                insertTweet.setString(6, tweetSecSetEn.getAirline());
                insertTweet.setLong(7, tweetSecSetEn.getAirline_sentiment_gold());
                insertTweet.setString(8, tweetSecSetEn.getName());
                insertTweet.setLong(9, tweetSecSetEn.getNegativereason_gold());
                insertTweet.setString(10, tweetSecSetEn.getRetweet_count());
                insertTweet.setString(11, tweetSecSetEn.getText());
                insertTweet.setString(12, tweetSecSetEn.getTweet_coord());
                insertTweet.setTimestamp(13, tweetSecSetEn.getTweet_created());
                insertTweet.setString(14, tweetSecSetEn.getTweet_location());
                insertTweet.setString(15, tweetSecSetEn.getUser_timezone());

                insertTweet.addBatch();
                if (++count % batchSize == 0) {
                    System.out.println("rowCount: " + count);
                    insertTweet.executeBatch();
                }

                rowCount++;

            }
            int[] updateCounts = insertTweet != null ? insertTweet.executeBatch() : new int[0];
            System.out.println("rowCount: " + rowCount);
            assert insertTweet != null;
            insertTweet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void airportCodeSlangHashTags() {
        //1. get data sets
        System.out.println("----------------------------Processing Original Data ... ");

        //2. Pre-processing and save to different .arff and table
        //   1. Decode text
        //   2.Remove Duplicate Tweets
        String selectTweets = "SELECT c.* from tweet_set_two_processed1 as c order by c.tweet_id; ";
        ArrayList<TweetSecSetEn> tweetSecSetEns = getTweetsFromDB(selectTweets);

        if (tweetSecSetEns == null) throw new NullPointerException("No tweets");

        for (TweetSecSetEn tweetSecSetEn1 : tweetSecSetEns) {

            String text = tweetSecSetEn1.getText().replaceAll("\\p{Blank}{2,}+", " ");

            text = StringUtils.replaceEach(StringEscapeUtils.unescapeHtml4(text),
                    new String[]{"‿", "“", "”"}, new String[]{"", "", ""});
            System.out.println("Processing Tweet: " + text);

            //Expand airport code to full name
            text = this.expandAirportCode(text);

            //Expand tweet slang
            text = this.expandTweetSlang(text);

            //Remove URL and Username
            text = this.removeURLAndUserName(text);

            // Unescape HTML entity
            text = text.replaceAll("\\p{Blank}{2,}+", " ") // Replace any Blank (a  space or a tab) by a single space.
                    .replaceAll("\\d+", ""); // remove any digits

            //get hashtags and break words
            text = breakWordsInHashtags(text).replaceAll("\\p{Punct}", "");
//            text = text.replaceAll("\\p{Punct}", "");
            // Replace text
            tweetSecSetEn1.setText(text);

            System.out.println("Processed Tweet: " + text);
        }

        List<String> gasList = new ArrayList<>();
        Map<String, TweetSecSetEn> map = new HashMap<String, TweetSecSetEn>();
        for (TweetSecSetEn tweet : tweetSecSetEns) {
            gasList.add(DetectCharset.detect(tweet.getText()));

            if (!map.containsKey(tweet.getText())) {
                map.put(tweet.getText(), tweet);
            }
        }

        Set<String> uniqueGas = new HashSet<>(gasList);
        System.out.println("Unique tweet count: " + uniqueGas.size());
        System.out.println("Unique: " + uniqueGas);
        Connection connection = DBHelper.getConnection();
        int rowCount = 0;
        int count = 0;
        final int batchSize = 1000;
        try {
            PreparedStatement insertTweet = null;
            System.out.println("map.size: " + map.size());
            insertTweet = connection.prepareStatement("INSERT INTO tweet_set_two_processed2hashtags_expanded" +
                    "(tweet_id," +
                    "labelled_sentiment, " +
                    "airline_sentiment_confidence," +
                    "negativereason," +
                    "negativereason_confidence," +
                    "airline," +
                    "airline_sentiment_gold," +
                    "name," +
                    "negativereason_gold," +
                    "retweet_count," +
                    "text," +
                    "tweet_coord," +
                    "tweet_created," +
                    "tweet_location," +
                    "user_timezone" +
                    ")" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
            for (Map.Entry<String, TweetSecSetEn> entry : map.entrySet()) {
                TweetSecSetEn tweetSecSetEn = entry.getValue();

                insertTweet.setLong(1, tweetSecSetEn.getTweet_id());
                insertTweet.setString(2, tweetSecSetEn.getLabelledSentiment());
                insertTweet.setLong(3, tweetSecSetEn.getAirline_sentiment_confidence());
                insertTweet.setString(4, tweetSecSetEn.getNegativereason());
                insertTweet.setLong(5, tweetSecSetEn.getNegativereason_confidence());
                insertTweet.setString(6, tweetSecSetEn.getAirline());
                insertTweet.setLong(7, tweetSecSetEn.getAirline_sentiment_gold());
                insertTweet.setString(8, tweetSecSetEn.getName());
                insertTweet.setLong(9, tweetSecSetEn.getNegativereason_gold());
                insertTweet.setString(10, tweetSecSetEn.getRetweet_count());
                insertTweet.setString(11, tweetSecSetEn.getText());
                insertTweet.setString(12, tweetSecSetEn.getTweet_coord());
                insertTweet.setTimestamp(13, tweetSecSetEn.getTweet_created());
                insertTweet.setString(14, tweetSecSetEn.getTweet_location());
                insertTweet.setString(15, tweetSecSetEn.getUser_timezone());

                insertTweet.addBatch();
                if (++count % batchSize == 0) {
                    System.out.println("rowCount: " + count);
                    insertTweet.executeBatch();
                }

                rowCount++;

            }
            int[] updateCounts = insertTweet != null ? insertTweet.executeBatch() : new int[0];
            System.out.println("rowCount: " + rowCount);
            assert insertTweet != null;
            insertTweet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void airportCodeSlangHashtagsEmojis() {
        System.out.println("----------------------------Replacing Emojis from Processed Data ... ");
        String selectTweets = "SELECT c.* from tweet_set_two_processed1 as c order by c.tweet_id; ";
        ArrayList<TweetSecSetEn> tweetSecSetEns = getTweetsFromDB(selectTweets);

        if (tweetSecSetEns == null) throw new NullPointerException("No tweets");
        System.out.println("2TweetSize: " + tweetSecSetEns.size());
        System.out.println("Last Tweet: " + tweetSecSetEns.get(tweetSecSetEns.size() - 1).getText());
        for (TweetSecSetEn tweetSecSetEn1 : tweetSecSetEns) {
            String text = tweetSecSetEn1.getText().toLowerCase();
            //Expand airport code to full name
            text = this.expandAirportCode(text);

            //Expand tweet slang
            text = this.expandTweetSlang(text);

            //Remove URL and Username
            text = this.removeURLAndUserName(text);

            text = EmojiParser.parseToAliases(text);

            /*   Replace text with heavier weight
            * we need to change it to word
            * replace +1, :100
            * Apply Replace strategy
            */
            text = text.replace(":+1", "great")//0.875
                    .replace(":100", "superb")//0.875
                    .replace(":", " ")
                    .replaceAll("[^a-zA-Z0-9\\p{Blank}]", "");

            //expand words again as the emoji aliases need to be expanded as well
            text = this.expandTweetSlang(text).toLowerCase();

            //get hashtags and break words
            text = breakWordsInHashtags(text).replaceAll("\\p{Punct}", "");

            tweetSecSetEn1.setText(text);
        }
        System.out.println("Processed Last Tweet: " + tweetSecSetEns.get(tweetSecSetEns.size() - 1).getText());

        Connection connection = DBHelper.getConnection();
        int rowCount = 0;
        int count = 0;
        final int batchSize = 1000;
        try {
            PreparedStatement insertTweet = null;
            insertTweet = connection.prepareStatement("INSERT INTO tweet_set_two_processed2emojis_expanded" +
                    "(tweet_id," +
                    "labelled_sentiment, " +
                    "airline_sentiment_confidence," +
                    "negativereason," +
                    "negativereason_confidence," +
                    "airline," +
                    "airline_sentiment_gold," +
                    "name," +
                    "negativereason_gold," +
                    "retweet_count," +
                    "text," +
                    "tweet_coord," +
                    "tweet_created," +
                    "tweet_location," +
                    "user_timezone" +
                    ")" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
            for (TweetSecSetEn tweetSecSetEn : tweetSecSetEns) {

                insertTweet.setLong(1, tweetSecSetEn.getTweet_id());
                insertTweet.setString(2, tweetSecSetEn.getLabelledSentiment());
                insertTweet.setLong(3, tweetSecSetEn.getAirline_sentiment_confidence());
                insertTweet.setString(4, tweetSecSetEn.getNegativereason());
                insertTweet.setLong(5, tweetSecSetEn.getNegativereason_confidence());
                insertTweet.setString(6, tweetSecSetEn.getAirline());
                insertTweet.setLong(7, tweetSecSetEn.getAirline_sentiment_gold());
                insertTweet.setString(8, tweetSecSetEn.getName());
                insertTweet.setLong(9, tweetSecSetEn.getNegativereason_gold());
                insertTweet.setString(10, tweetSecSetEn.getRetweet_count());
                insertTweet.setString(11, tweetSecSetEn.getText());
                insertTweet.setString(12, tweetSecSetEn.getTweet_coord());
                insertTweet.setTimestamp(13, tweetSecSetEn.getTweet_created());
                insertTweet.setString(14, tweetSecSetEn.getTweet_location());
                insertTweet.setString(15, tweetSecSetEn.getUser_timezone());

                insertTweet.addBatch();
                if (++count % batchSize == 0) {
                    System.out.println("rowCount: " + count);
                    insertTweet.executeBatch();
                }

                rowCount++;

            }
            int[] updateCounts = insertTweet != null ? insertTweet.executeBatch() : new int[0];
            System.out.println("rowCount: " + rowCount);
            assert insertTweet != null;
            insertTweet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onlyEmojis() {
        System.out.println("----------------------------Replacing Emojis from Processed Data ... ");
        String selectTweets = "SELECT c.* from tweet_set_two_processed1 as c order by c.tweet_id; ";
        ArrayList<TweetSecSetEn> tweetSecSetEns = getTweetsFromDB(selectTweets);

        if (tweetSecSetEns == null) throw new NullPointerException("No tweets");
        System.out.println("2TweetSize: " + tweetSecSetEns.size());
        System.out.println("Last Tweet: " + tweetSecSetEns.get(tweetSecSetEns.size() - 1).getText());
        for (TweetSecSetEn tweetSecSetEn1 : tweetSecSetEns) {
            String text = tweetSecSetEn1.getText().toLowerCase();

            //Remove URL and Username
            text = this.removeURLAndUserName(text);

            text = EmojiParser.parseToAliases(text);

            /*   Replace text with heavier weight
            * we need to change it to word
            * replace +1, :100
            * Apply Replace strategy
            */
            text = text.replace(":+1", "great")//0.875
                    .replace(":100", "superb")//0.875
                    .replace(":", " ")
                    .replaceAll("[^a-zA-Z0-9\\p{Blank}]", "");

            tweetSecSetEn1.setText(text);
        }
        System.out.println("Processed Last Tweet: " + tweetSecSetEns.get(tweetSecSetEns.size() - 1).getText());

        Connection connection = DBHelper.getConnection();
        int rowCount = 0;
        int count = 0;
        final int batchSize = 1000;
        try {
            PreparedStatement insertTweet = null;
            insertTweet = connection.prepareStatement("INSERT INTO tweet_set_two_processed2emojis_only_expanded" +
                    "(tweet_id," +
                    "labelled_sentiment, " +
                    "airline_sentiment_confidence," +
                    "negativereason," +
                    "negativereason_confidence," +
                    "airline," +
                    "airline_sentiment_gold," +
                    "name," +
                    "negativereason_gold," +
                    "retweet_count," +
                    "text," +
                    "tweet_coord," +
                    "tweet_created," +
                    "tweet_location," +
                    "user_timezone" +
                    ")" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
            for (TweetSecSetEn tweetSecSetEn : tweetSecSetEns) {

                insertTweet.setLong(1, tweetSecSetEn.getTweet_id());
                insertTweet.setString(2, tweetSecSetEn.getLabelledSentiment());
                insertTweet.setLong(3, tweetSecSetEn.getAirline_sentiment_confidence());
                insertTweet.setString(4, tweetSecSetEn.getNegativereason());
                insertTweet.setLong(5, tweetSecSetEn.getNegativereason_confidence());
                insertTweet.setString(6, tweetSecSetEn.getAirline());
                insertTweet.setLong(7, tweetSecSetEn.getAirline_sentiment_gold());
                insertTweet.setString(8, tweetSecSetEn.getName());
                insertTweet.setLong(9, tweetSecSetEn.getNegativereason_gold());
                insertTweet.setString(10, tweetSecSetEn.getRetweet_count());
                insertTweet.setString(11, tweetSecSetEn.getText());
                insertTweet.setString(12, tweetSecSetEn.getTweet_coord());
                insertTweet.setTimestamp(13, tweetSecSetEn.getTweet_created());
                insertTweet.setString(14, tweetSecSetEn.getTweet_location());
                insertTweet.setString(15, tweetSecSetEn.getUser_timezone());

                insertTweet.addBatch();
                if (++count % batchSize == 0) {
                    System.out.println("rowCount: " + count);
                    insertTweet.executeBatch();
                }

                rowCount++;

            }
            int[] updateCounts = insertTweet != null ? insertTweet.executeBatch() : new int[0];
            System.out.println("rowCount: " + rowCount);
            assert insertTweet != null;
            insertTweet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removeEmojisFromP1SaveToP3() {
        System.out.println("----------------------------Removing Emojis from Processed Data ... ");
        String selectTweets = "SELECT c.* from tweet_set_two_processed1 as c order by c.tweet_id; ";
        ArrayList<TweetSecSetEn> tweetSecSetEns = getTweetsFromDB(selectTweets);

        if (tweetSecSetEns == null) throw new NullPointerException("No tweets");
        System.out.println("3TweetSize: " + tweetSecSetEns.size());
        System.out.println("Last Tweet: " + tweetSecSetEns.get(tweetSecSetEns.size() - 1).getText());

        for (TweetSecSetEn tweetSecSetEn1 : tweetSecSetEns) {
            String text = tweetSecSetEn1.getText().toLowerCase();

            //remove unicode and specical characters
            text = EmojiParser.parseToUnicode(text)
                    .replaceAll("\\\\[u]\\w+([^\\s]+)", "")
                    .replaceAll("[^a-zA-Z0-9\\p{Blank}]", "");

            //expand words again as ensure they are expended after emoji removed
            text = this.expandTweetSlang(text).toLowerCase();

            // Replace text
            tweetSecSetEn1.setText(text);
        }
        System.out.println("Processed Last Tweet: " + tweetSecSetEns.get(tweetSecSetEns.size() - 1).getText());
        List<String> gasList = new ArrayList<>();
        Map<String, TweetSecSetEn> map = new HashMap<String, TweetSecSetEn>();
        for (TweetSecSetEn tweet : tweetSecSetEns) {
            gasList.add(DetectCharset.detect(tweet.getText()));

            if (!map.containsKey(tweet.getText())) {
                map.put(tweet.getText(), tweet);
            }
        }

        Set<String> uniqueGas = new HashSet<>(gasList);
        System.out.println("Unique gas count: " + uniqueGas.size());
        System.out.println("Unique: " + uniqueGas);
        Connection connection = DBHelper.getConnection();
        int rowCount = 0;
        int count = 0;
        final int batchSize = 1000;
        try {
            PreparedStatement insertTweet = null;
            System.out.println("map.size: " + map.size());
            insertTweet = connection.prepareStatement("INSERT INTO tweet_set_two_processed3_expanded" +
                    "(tweet_id," +
                    "labelled_sentiment, " +
                    "airline_sentiment_confidence," +
                    "negativereason," +
                    "negativereason_confidence," +
                    "airline," +
                    "airline_sentiment_gold," +
                    "name," +
                    "negativereason_gold," +
                    "retweet_count," +
                    "text," +
                    "tweet_coord," +
                    "tweet_created," +
                    "tweet_location," +
                    "user_timezone" +
                    ")" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
            for (Map.Entry<String, TweetSecSetEn> entry : map.entrySet()) {
                TweetSecSetEn tweetSecSetEn = entry.getValue();

                insertTweet.setLong(1, tweetSecSetEn.getTweet_id());
                insertTweet.setString(2, tweetSecSetEn.getLabelledSentiment());
                insertTweet.setLong(3, tweetSecSetEn.getAirline_sentiment_confidence());
                insertTweet.setString(4, tweetSecSetEn.getNegativereason());
                insertTweet.setLong(5, tweetSecSetEn.getNegativereason_confidence());
                insertTweet.setString(6, tweetSecSetEn.getAirline());
                insertTweet.setLong(7, tweetSecSetEn.getAirline_sentiment_gold());
                insertTweet.setString(8, tweetSecSetEn.getName());
                insertTweet.setLong(9, tweetSecSetEn.getNegativereason_gold());
                insertTweet.setString(10, tweetSecSetEn.getRetweet_count());
                insertTweet.setString(11, tweetSecSetEn.getText());
                insertTweet.setString(12, tweetSecSetEn.getTweet_coord());
                insertTweet.setTimestamp(13, tweetSecSetEn.getTweet_created());
                insertTweet.setString(14, tweetSecSetEn.getTweet_location());
                insertTweet.setString(15, tweetSecSetEn.getUser_timezone());

                insertTweet.addBatch();
                if (++count % batchSize == 0) {
                    System.out.println("rowCount: " + count);
                    insertTweet.executeBatch();
                }

                rowCount++;

            }
            int[] updateCounts = insertTweet != null ? insertTweet.executeBatch() : new int[0];
            System.out.println("rowCount: " + rowCount);
            assert insertTweet != null;
            insertTweet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String removeURLAndUserName(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (!word.contains("@") && !word.matches(regexURL)) {
                stringBuilder.append(word);
                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString();
    }

    private String expandTweetSlang(String text) {
        Map<String, String> tweetSlangList = WekaClassUtil.constructTweetSlangList();

        StringBuilder stringBuilder = new StringBuilder();
        for (String word : text.split("\\s+")) {
            String fullWords = tweetSlangList.get(word.toUpperCase());
            if (fullWords != null) {
                word = fullWords;
            }
            stringBuilder.append(word);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    private String expandAirportCode(String text) {
        Map<String, String> airportCodeList = WekaClassUtil.constructAirportCodeList();
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : text.split("\\s+")) {
            String fullName = airportCodeList.get(word);
            if (fullName != null) {
                word = fullName;
            }
            stringBuilder.append(word);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    private String breakWordsInHashtags(String text) {
        String[] split = text.split(" ");
        List<String> hashTags = new ArrayList<>();
        for (String s : split) {
            if (s.length() > 0 && s.charAt(0) == '#') {
                hashTags.add(s);
            }
        }

        if (hashTags.size() > 0) {
            for (String hashtag : hashTags) {
                hashtag = hashtag.replace("#", "").toLowerCase();
                if (StringUtils.isNoneBlank(hashtag)) {
                    List<String> wordBreak = wordBreak(hashtag, WekaClassUtil.constructWordList());

                    String[] splitWords = {hashtag};
                    if (wordBreak.size() >= 1) {
                        splitWords = wordBreak.get(0).split(" ");
                    }
                    StringBuilder strBuilder = new StringBuilder();
                    for (String s : splitWords) {
                        strBuilder.append(s);
                        strBuilder.append(" ");
                    }
                    String newString = strBuilder.toString();
                    text = text.replace(hashtag, newString);
                }
            }
        }
        return text;
    }

    private ArrayList<TweetSecSetEn> getTweetsFromDB(String selectTweets) {
        ArrayList<TweetSecSetEn> tweetSecSetEns = new ArrayList<>();
        Connection connection = DBHelper.getConnection();
        TweetSecSetEn tweetSecSetEn;
        ResultSet rs = DBHelper.excSelectQuery(connection, selectTweets);
        if (rs == null) throw new NullPointerException("No results returned ");
        try {
            while (rs.next()) {
                tweetSecSetEn = new TweetSecSetEn();
                long tweet_id = rs.getLong("tweet_id");
                if (tweet_id != 0) {
                    tweetSecSetEn.setTweet_id(tweet_id);
                }
                String name = rs.getString("name");
                if (name != null) {
                    tweetSecSetEn.setName(name);
                }
               /*
                tweetSecSetEn.setNegativereason(rs.getString("negativereason"));
                tweetSecSetEn.setNegativereason_confidence(rs.getLong("negativereason_confidence"));
                tweetSecSetEn.setAirline(rs.getString("airline"));
                tweetSecSetEn.setAirline_sentiment_gold(rs.getLong("airline_sentiment_gold"));
                tweetSecSetEn.setNegativereason_gold(rs.getLong("negativereason_gold"));
                tweetSecSetEn.setRetweet_count(rs.getString("retweet_count"));
                tweetSecSetEn.setTweet_coord(rs.getString("tweet_coord"));
                tweetSecSetEn.setTweet_created(rs.getTimestamp("tweet_created"));
                tweetSecSetEn.setTweet_location(rs.getString("tweet_location"));
                tweetSecSetEn.setUser_timezone(rs.getString("user_timezone"));
*/
                tweetSecSetEn.setLabelledSentiment(rs.getString("labelled_sentiment"));
                tweetSecSetEn.setAirline_sentiment_confidence(rs.getLong("airline_sentiment_confidence"));

                tweetSecSetEn.setText(rs.getString("text"));

                tweetSecSetEns.add(tweetSecSetEn);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return tweetSecSetEns;
    }


    public void writingToArff(Instances data, String filePath) {

        try {
            ArffSaver arffSaver = new ArffSaver();
            arffSaver.setInstances(data);
            arffSaver.setFile(new File(filePath));
            arffSaver.writeBatch();

            System.out.println("Number of tweets:" + data.size() + "\nSaved to file : '" + filePath + "' \n");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public List<String> wordBreak(String s, Set<String> dict) {
        //create an array of ArrayList<String>
        List<String> dp[] = new ArrayList[s.length() + 1];
        dp[0] = new ArrayList<String>();

        for (int i = 0; i < s.length(); i++) {
            if (dp[i] == null)
                continue;

            for (String word : dict) {
                int len = word.length();
                int end = i + len;
                if (end > s.length())
                    continue;

                if (s.substring(i, end).equals(word)) {
                    if (dp[end] == null) {
                        dp[end] = new ArrayList<String>();
                    }
                    dp[end].add(word);
                }
            }
        }

        List<String> result = new LinkedList<String>();
        if (dp[s.length()] == null)
            return result;

        ArrayList<String> temp = new ArrayList<String>();
        dfs(dp, s.length(), result, temp);

        return result;
    }

    public void dfs(List<String> dp[], int end, List<String> result, ArrayList<String> tmp) {
        if (end <= 0) {
            String path = null;
            try {
                path = tmp.get(tmp.size() - 1);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                System.out.println("sdf");
            }
            for (int i = tmp.size() - 2; i >= 0; i--) {
                path += " " + tmp.get(i);
            }

            result.add(path);
            return;
        }

        for (String str : dp[end]) {
            tmp.add(str);
            dfs(dp, end - str.length(), result, tmp);
            tmp.remove(tmp.size() - 1);
        }
    }

}
