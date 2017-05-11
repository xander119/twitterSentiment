package twitter;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * Created by Zechen on 2016/10/21.
 */
public class TweetFirstSetEn implements Serializable {
    private Timestamp createdAt;
    private String fromUser = "NA";
    private Long fromUserId = 0L;
    private String toUser = "NA";
    private Long toUserId = 0L;
    private String language = "NA";
    private String source = "NA";
    private String text = "NA";
    private Long geoLocationLatitude = 0L;
    private Long geoLocationLongitude = 0L;
    private Long retweet_Count = 0L;
    private Long Id = 0L;
    private String labledSentiment = "NA";

    public String getLabledSentiment() {
        return labledSentiment;
    }

    public void setLabledSentiment(String labledSentiment) {
        this.labledSentiment = labledSentiment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getGeoLocationLatitude() {
        return geoLocationLatitude;
    }

    public void setGeoLocationLatitude(Long geoLocationLatitude) {
        this.geoLocationLatitude = geoLocationLatitude;
    }

    public Long getGeoLocationLongitude() {
        return geoLocationLongitude;
    }

    public void setGeoLocationLongitude(Long geoLocationLongitude) {
        this.geoLocationLongitude = geoLocationLongitude;
    }

    public Long getRetweet_Count() {
        return retweet_Count;
    }

    public void setRetweet_Count(Long retweet_Count) {
        this.retweet_Count = retweet_Count;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }
}
