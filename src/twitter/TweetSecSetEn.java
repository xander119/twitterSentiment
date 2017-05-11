package twitter;

import java.sql.Timestamp;

/**
 * Created by Zechen on 2016/10/31.
 */
public class TweetSecSetEn {
    private Long tweet_id = 0L;
    private String labelledSentiment = "NA";
    private Long airline_sentiment_confidence = 0L;
    private String negativereason = "NA";
    private Long negativereason_confidence = 0L;
    private String airline = "NA";
    private Long airline_sentiment_gold = 0L;
    private String name = "NA";
    private Long negativereason_gold = 0L;
    private String retweet_count = "NA";
    private String text = "NA";
    private String tweet_coord = "NA";
    private Timestamp tweet_created;
    private String tweet_location = "NA";
    private String user_timezone = "NA";

    public Long getTweet_id() {
        return tweet_id;
    }

    public void setTweet_id(Long tweet_id) {
        this.tweet_id = tweet_id;
    }

    public String getLabelledSentiment() {
        return labelledSentiment;
    }

    public void setLabelledSentiment(String labelledSentiment) {
        this.labelledSentiment = labelledSentiment;
    }

    public Long getAirline_sentiment_confidence() {
        return airline_sentiment_confidence;
    }

    public void setAirline_sentiment_confidence(Long airline_sentiment_confidence) {
        this.airline_sentiment_confidence = airline_sentiment_confidence;
    }

    public String getNegativereason() {
        return negativereason;
    }

    public void setNegativereason(String negativereason) {
        this.negativereason = negativereason;
    }

    public Long getNegativereason_confidence() {
        return negativereason_confidence;
    }

    public void setNegativereason_confidence(Long negativereason_confidence) {
        this.negativereason_confidence = negativereason_confidence;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public Long getAirline_sentiment_gold() {
        return airline_sentiment_gold;
    }

    public void setAirline_sentiment_gold(Long airline_sentiment_gold) {
        this.airline_sentiment_gold = airline_sentiment_gold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNegativereason_gold() {
        return negativereason_gold;
    }

    public void setNegativereason_gold(Long negativereason_gold) {
        this.negativereason_gold = negativereason_gold;
    }

    public String getRetweet_count() {
        return retweet_count;
    }

    public void setRetweet_count(String retweet_count) {
        this.retweet_count = retweet_count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTweet_coord() {
        return tweet_coord;
    }

    public void setTweet_coord(String tweet_coord) {
        this.tweet_coord = tweet_coord;
    }

    public Timestamp getTweet_created() {
        return tweet_created;
    }

    public void setTweet_created(Timestamp tweet_created) {
        this.tweet_created = tweet_created;
    }

    public String getTweet_location() {
        return tweet_location;
    }

    public void setTweet_location(String tweet_location) {
        this.tweet_location = tweet_location;
    }

    public String getUser_timezone() {
        return user_timezone;
    }

    public void setUser_timezone(String user_timezone) {
        this.user_timezone = user_timezone;
    }
}
