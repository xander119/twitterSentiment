package twitter;


import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by Zechen on 2016/10/27.
 */
public class LoadExcelToDB {

    private static final String FILE_PATH_ONE = "G:/1Data/TweetsCollectedMainOne.xlsx";
    private static final String FILE_PATH_TWO = "G:/1Data/TweetsCrowdflowerMainTwo.xlsx";
    private static final String FILE_PATH_TWO_CSV = "G:/1Data/airline-twitter-sentiment/Tweets.csv";
    private int numOfFirstTrainData = 2300;
    private int numOfSecondTrainData = 7000;

    public static void main(String[] args) throws Exception {
        String selectQuery = "(SELECT c.* from tweet_set_two_processed1 as c where c.labelled_sentiment = 'negative' order by c.text limit 1116) " +
                "union all (SELECT c.* from tweet_set_two_processed1 as c where c.labelled_sentiment = 'positive' order by c.text limit 1116)" +
                "union all (SELECT c.* from tweet_set_two_processed1 as c where c.labelled_sentiment = 'neutral' order by c.text limit 1116)";

        LoadExcelToDB obj = new LoadExcelToDB();
        obj.createTrainingSetFromSecond(selectQuery);
        /*obj.getFirstSetTweetsListFromExcel(FILE_PATH_ONE);
        obj.getSecondSetTweetsListFromExcel(FILE_PATH_TWO);
        obj.readTweetsTwoCSV(FILE_PATH_TWO_CSV);

        System.out.println(DetectCharset.detect("I  flying @VirginAmerica. \uD83D\uDC4D"));
        System.out.println("\u0905\u092d\u0940\u0938\u092e\u092f\u0939\u0948\u091c \uD83D\uDC9C✿# \uD83D\uDC9C✿#\uD83D\uDC4F\uD83D\uDC4D\uD83D\uDC4D✈️✈️\uD83D\uDC97");

        ResultSet resultSet = DBHelper.excSelectQuery(DBHelper.getConnection(), "select col1 from t1;");
        while (resultSet.next()){
            System.out.println(resultSet.getString("col1"));

        }

        System.out.println(EmojiParser.parseFromUnicode("I  flying @VirginAmerica. \uD83D\uDC4D", ) + EmojiManager.isEmoji("\uD83D\uDC4D"));
        obj.createTrainingSetFromFirst();
        obj.createTrainingSetFromSecond();*/
    }

    public void getFirstSetTweetsListFromExcel(String filePath) {
        FileInputStream fis = null;
        Connection connection = DBHelper.getConnection();
        try {
            fis = new FileInputStream(filePath);

            // Using XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new XSSFWorkbook(fis);

            int numberOfSheets = workbook.getNumberOfSheets();

            //looping over each workbook sheet
            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                TweetFirstSetEn tweetFirstSetEn = null;
                //iterating over each row
                for (Row row : sheet) {
                    Iterator cellIterator = row.cellIterator();
                    tweetFirstSetEn = new TweetFirstSetEn();
                    //Iterating over each cell (column wise)  in a particular row.
                    while (cellIterator.hasNext()) {
                        Cell cell = (Cell) cellIterator.next();

                        if (row.getRowNum() != 0) {
                            switch (cell.getColumnIndex()) {
                                case 0:
                                    //Create At
                                    Timestamp timestamp = new Timestamp(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()).getTime());
                                    tweetFirstSetEn.setCreatedAt(timestamp);
                                    break;
                                case 1:
                                    // From-User
                                    tweetFirstSetEn.setFromUser(cell.getStringCellValue());
                                    break;
                                case 2:
                                    // From-User-Id
                                    tweetFirstSetEn.setFromUserId((long) cell.getNumericCellValue());
                                    break;
                                case 3:
                                    // To-User
                                    tweetFirstSetEn.setToUser(cell.getStringCellValue());
                                    break;
                                case 4:
                                    // To-User-Id
                                    tweetFirstSetEn.setToUserId((long) cell.getNumericCellValue());
                                    break;
                                case 5:
                                    // Language
                                    tweetFirstSetEn.setLanguage(cell.getStringCellValue());
                                    break;
                                case 6:
                                    // Source
                                    tweetFirstSetEn.setSource(cell.getStringCellValue());
                                    break;
                                case 7:
                                    // Text
                                    tweetFirstSetEn.setText(cell.getStringCellValue());
                                    break;
                                case 8:
                                    // Geo-Location-Latitude
                                    tweetFirstSetEn.setGeoLocationLatitude((long) cell.getNumericCellValue());
                                    break;
                                case 9:
                                    // Geo-Location-Longitude
                                    tweetFirstSetEn.setGeoLocationLongitude(((long) cell.getNumericCellValue()));
                                    break;
                                case 10:
                                    // Retweet-Count
                                    tweetFirstSetEn.setRetweet_Count(((long) cell.getNumericCellValue()));
                                    break;
                                case 11:
                                    // Id
                                    tweetFirstSetEn.setId(((long) cell.getNumericCellValue()));
                                    break;
                                default:
                                    break;
                            }

                        }
                    }
                    //end iterating a row, add all the elements of a row to DB
                    if (tweetFirstSetEn.getId() != 0L) {
                        PreparedStatement insertTweet;
                        try {
                            insertTweet = connection.prepareStatement("INSERT INTO tweet_set_one" +
                                    "(Created_At," +
                                    "To_User, " +
                                    "Geo_Location_Longitude," +
                                    "Id," +
                                    "Source," +
                                    "From_User," +
                                    "Retweet_Count," +
                                    "Text," +
                                    "Language," +
                                    "To_User_Id," +
                                    "Geo_Location_Latitude," +
                                    "From_User_Id," +
                                    "labelled_sentiment)" +
                                    "VALUES(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?)");
                            insertTweet.setTimestamp(1, tweetFirstSetEn.getCreatedAt());
                            insertTweet.setString(2, tweetFirstSetEn.getToUser());
                            insertTweet.setLong(3, tweetFirstSetEn.getGeoLocationLongitude());
                            insertTweet.setLong(4, tweetFirstSetEn.getId());
                            insertTweet.setString(5, tweetFirstSetEn.getSource());
                            insertTweet.setString(6, tweetFirstSetEn.getFromUser());
                            insertTweet.setLong(7, tweetFirstSetEn.getRetweet_Count());
                            insertTweet.setString(8, tweetFirstSetEn.getText());
                            insertTweet.setString(9, tweetFirstSetEn.getLanguage());
                            insertTweet.setLong(10, tweetFirstSetEn.getToUserId());
                            insertTweet.setLong(11, tweetFirstSetEn.getGeoLocationLatitude());
                            insertTweet.setLong(12, tweetFirstSetEn.getFromUserId());
                            insertTweet.setString(13, tweetFirstSetEn.getLabledSentiment());

                            insertTweet.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        System.out.println(row.getRowNum() + "Storing Tweet set one id: '" + tweetFirstSetEn.getId() + "' ");
                    }

                }

            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSecondSetTweetsListFromExcel(String filePath) {
        FileInputStream fis = null;
        Connection connection = DBHelper.getConnection();
        try {
            fis = new FileInputStream(filePath);

            // Using XSSF for xlsx format, for xls use HSSF
            Workbook workbook = new XSSFWorkbook(fis);

            int numberOfSheets = workbook.getNumberOfSheets();

            //looping over each workbook sheet

            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                TweetSecSetEn tweetSecSetEn = null;
                //iterating over each row
                int index = 0;
                int count = 0;
                final int batchSize = 1000;
                PreparedStatement insertTweet = null;
                try {
                    insertTweet = connection.prepareStatement("INSERT INTO twitterresearch.tweet_set_two" +
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
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                for (Row row : sheet) {
                    Iterator cellIterator = row.cellIterator();
                    tweetSecSetEn = new TweetSecSetEn();
                    //Iterating over each cell (column wise)  in a particular row.

                    while (cellIterator.hasNext()) {
                        Cell cell = (Cell) cellIterator.next();

                        if (row.getRowNum() != 0) {
                            switch (cell.getColumnIndex()) {
                                case 0:
                                    //tweet_id
                                    if (cell.getCellType() == cell.CELL_TYPE_NUMERIC)
                                        tweetSecSetEn.setTweet_id((long) cell.getNumericCellValue() + row.getRowNum());
                                    break;
                                case 1:
                                    // airline_sentiment
                                    if (cell.getCellType() == cell.CELL_TYPE_STRING)
                                        tweetSecSetEn.setLabelledSentiment(cell.getStringCellValue());
                                    break;
                                case 2:
                                    // airline_sentiment_confidence
                                    if (cell.getCellType() == cell.CELL_TYPE_NUMERIC)
                                        tweetSecSetEn.setAirline_sentiment_confidence((long) cell.getNumericCellValue());
                                    break;
                                case 3:
                                    // negativereason
                                    if (cell.getCellType() == cell.CELL_TYPE_STRING)
                                        tweetSecSetEn.setNegativereason(cell.getStringCellValue());
                                    break;
                                case 4:
                                    // negativereason_confidence
                                    if (cell.getCellType() == cell.CELL_TYPE_NUMERIC)
                                        tweetSecSetEn.setNegativereason_confidence((long) cell.getNumericCellValue());
                                    break;
                                case 5:
                                    // airline
                                    if (cell.getCellType() == cell.CELL_TYPE_STRING)
                                        tweetSecSetEn.setAirline(cell.getStringCellValue());
                                    break;
                                case 6:
                                    // airline_sentiment_gold
                                    if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
                                        tweetSecSetEn.setAirline_sentiment_gold((long) cell.getNumericCellValue());
                                    }
                                    break;
                                case 7:
                                    // name
                                    if (cell.getCellType() == cell.CELL_TYPE_STRING)
                                        tweetSecSetEn.setName(cell.getStringCellValue());
                                    break;
                                case 8:
                                    // negativereason_gold
                                    if (cell.getCellType() == cell.CELL_TYPE_NUMERIC)
                                        tweetSecSetEn.setNegativereason_gold((long) cell.getNumericCellValue());
                                    break;
                                case 9:
                                    // retweet_count
                                    if (cell.getCellType() == cell.CELL_TYPE_NUMERIC)
                                        tweetSecSetEn.setRetweet_count(String.valueOf(cell.getNumericCellValue()));
                                    break;
                                case 10:
                                    // text
                                    //decode and encode

                                    if (cell.getCellType() == cell.CELL_TYPE_STRING) {
                                        tweetSecSetEn.setText(cell.getStringCellValue());
//                                        tweetSecSetEn.setText(StringEscapeUtils.escapeJava(cell.getStringCellValue()));
                                    }

                                    break;
                                case 11:
                                    // tweet_coord
                                    if (cell.getCellType() == cell.CELL_TYPE_STRING) {
                                        tweetSecSetEn.setTweet_coord(cell.getStringCellValue());
                                    } else {
                                        tweetSecSetEn.setTweet_coord(String.valueOf(cell.getNumericCellValue()));
                                    }
                                    break;
                                case 12:
                                    // tweet_created
                                    Timestamp timestamp = null;
                                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                        timestamp = new Timestamp(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()).getTime());
                                    } else {
                                        timestamp = null;
                                    }
                                    tweetSecSetEn.setTweet_created(timestamp);
                                    break;
                                case 13:
                                    // tweet_location
                                    if (cell.getCellType() == cell.CELL_TYPE_STRING) {
                                        tweetSecSetEn.setTweet_location(cell.getStringCellValue());
                                    } else if (cell.getCellType() == cell.CELL_TYPE_ERROR) {
                                        tweetSecSetEn.setTweet_location(String.valueOf(cell.getErrorCellValue()));
                                    } else if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
                                        tweetSecSetEn.setTweet_location(String.valueOf(cell.getBooleanCellValue()));
                                    } else {
                                        tweetSecSetEn.setTweet_location(String.valueOf(cell.getNumericCellValue()));
                                    }
                                    break;
                                case 14:
                                    // user_timezone
                                    if (cell.getCellType() == cell.CELL_TYPE_STRING)
                                        tweetSecSetEn.setUser_timezone(cell.getStringCellValue());
                                    break;
                                default:
                                    break;
                            }

                        }
                    }

                    //end iterating a row, add all the elements of a row to DB
                    if (tweetSecSetEn.getTweet_id() != 0L && !"na".equalsIgnoreCase(tweetSecSetEn.getText())) {
                        try {
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
                            index++;
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println(tweetSecSetEn.getText());
                            break;
                        }

                    }

                }
                System.out.println("Tweets count: " + index);
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create training data set from the collected data set
     * split
     */
    public void createTrainingSetFromFirst() {
        Connection connection = DBHelper.getConnection();
        PreparedStatement insertTweet;

        ArrayList<TweetFirstSetEn> tweetFirstSetEns = new ArrayList<>();
        TweetFirstSetEn tweetFirstSetEn = null;
        try {
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM twitterresearch.tweet_set_one AS c ORDER BY c.Text ASC LIMIT " + numOfFirstTrainData);
            while (rs.next()) {
                tweetFirstSetEn = new TweetFirstSetEn();
                tweetFirstSetEn.setCreatedAt(rs.getTimestamp("Created_At"));
                tweetFirstSetEn.setToUser(rs.getString("To_User"));
                tweetFirstSetEn.setId(rs.getLong("Id"));
                tweetFirstSetEn.setSource(rs.getString("Source"));
                tweetFirstSetEn.setFromUser(rs.getString("From_User"));
                tweetFirstSetEn.setRetweet_Count(rs.getLong("Retweet_Count"));
                tweetFirstSetEn.setText(rs.getString("Text"));
                tweetFirstSetEn.setLanguage(rs.getString("Language"));
                tweetFirstSetEn.setToUserId(rs.getLong("To_User_Id"));
                tweetFirstSetEn.setGeoLocationLongitude(rs.getLong("Geo_Location_Longitude"));
                tweetFirstSetEn.setGeoLocationLatitude(rs.getLong("Geo_Location_Latitude"));
                tweetFirstSetEn.setFromUserId(rs.getLong("From_User_Id"));
                tweetFirstSetEn.setLabledSentiment(rs.getString("labelled_sentiment"));

                tweetFirstSetEns.add(tweetFirstSetEn);
            }
            System.out.println("Size: " + tweetFirstSetEns.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (TweetFirstSetEn tweetFirstSetEn1 : tweetFirstSetEns) {
            try {

                insertTweet = connection.prepareStatement("INSERT INTO tweet_set_one_training" +
                        "(Created_At," +
                        "To_User, " +
                        "Geo_Location_Longitude," +
                        "Id," +
                        "Source," +
                        "From_User," +
                        "Retweet_Count," +
                        "Text," +
                        "Language," +
                        "To_User_Id," +
                        "Geo_Location_Latitude," +
                        "From_User_Id," +
                        "labelled_sentiment)" +
                        "VALUES(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?)");
                insertTweet.setTimestamp(1, tweetFirstSetEn1.getCreatedAt());
                insertTweet.setString(2, tweetFirstSetEn1.getToUser());
                insertTweet.setLong(3, tweetFirstSetEn1.getGeoLocationLongitude());
                insertTweet.setLong(4, tweetFirstSetEn1.getId());
                insertTweet.setString(5, tweetFirstSetEn1.getSource());
                insertTweet.setString(6, tweetFirstSetEn1.getFromUser());
                insertTweet.setLong(7, tweetFirstSetEn1.getRetweet_Count());
                insertTweet.setString(8, tweetFirstSetEn1.getText());
                insertTweet.setString(9, tweetFirstSetEn1.getLanguage());
                insertTweet.setLong(10, tweetFirstSetEn1.getToUserId());
                insertTweet.setLong(11, tweetFirstSetEn1.getGeoLocationLatitude());
                insertTweet.setLong(12, tweetFirstSetEn1.getFromUserId());
                insertTweet.setString(13, tweetFirstSetEn1.getLabledSentiment());

                insertTweet.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create training data set from the cleaned data set
     * split
     */
    public void createTrainingSetFromSecond(String selectQuery) {
        Connection connection = DBHelper.getConnection();
        PreparedStatement insertTweet;
        ArrayList<TweetSecSetEn> tweetSecSetEns = new ArrayList<>();
        TweetSecSetEn tweetSecSetEn = null;
        try {
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery(selectQuery);
            while (rs.next()) {
                tweetSecSetEn = new TweetSecSetEn();
                tweetSecSetEn.setTweet_id(rs.getLong("tweet_id"));
                tweetSecSetEn.setLabelledSentiment(rs.getString("labelled_sentiment"));
                tweetSecSetEn.setAirline_sentiment_confidence(rs.getLong("airline_sentiment_confidence"));
                tweetSecSetEn.setNegativereason(rs.getString("negativereason"));
                tweetSecSetEn.setNegativereason_confidence(rs.getLong("negativereason_confidence"));
                tweetSecSetEn.setAirline(rs.getString("airline"));
                tweetSecSetEn.setAirline_sentiment_gold(rs.getLong("airline_sentiment_gold"));
                tweetSecSetEn.setName(rs.getString("name"));
                tweetSecSetEn.setNegativereason_gold(rs.getLong("negativereason_gold"));
                tweetSecSetEn.setRetweet_count(rs.getString("retweet_count"));
                tweetSecSetEn.setText(rs.getString("text"));
                tweetSecSetEn.setTweet_coord(rs.getString("tweet_coord"));
                tweetSecSetEn.setTweet_created(rs.getTimestamp("tweet_created"));
                tweetSecSetEn.setTweet_location(rs.getString("tweet_location"));
                tweetSecSetEn.setUser_timezone(rs.getString("user_timezone"));

                tweetSecSetEns.add(tweetSecSetEn);
            }
            System.out.println("Size: " + tweetSecSetEns.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (TweetSecSetEn tweetSecSetEn1 : tweetSecSetEns) {
            try {

                insertTweet = connection.prepareStatement("INSERT INTO tweet_set_two_training_process1" +
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
                insertTweet.setLong(1, tweetSecSetEn1.getTweet_id());
                insertTweet.setString(2, tweetSecSetEn1.getLabelledSentiment());
                insertTweet.setLong(3, tweetSecSetEn1.getAirline_sentiment_confidence());
                insertTweet.setString(4, tweetSecSetEn1.getNegativereason());
                insertTweet.setLong(5, tweetSecSetEn1.getNegativereason_confidence());
                insertTweet.setString(6, tweetSecSetEn1.getAirline());
                insertTweet.setLong(7, tweetSecSetEn1.getAirline_sentiment_gold());
                insertTweet.setString(8, tweetSecSetEn1.getName());
                insertTweet.setLong(9, tweetSecSetEn1.getNegativereason_gold());
                insertTweet.setString(10, tweetSecSetEn1.getRetweet_count());
                insertTweet.setString(11, tweetSecSetEn1.getText());
                insertTweet.setString(12, tweetSecSetEn1.getTweet_coord());
                insertTweet.setTimestamp(13, tweetSecSetEn1.getTweet_created());
                insertTweet.setString(14, tweetSecSetEn1.getTweet_location());
                insertTweet.setString(15, tweetSecSetEn1.getUser_timezone());

                insertTweet.executeUpdate();
                System.out.println("With Tweet ID: " + tweetSecSetEn1.getTweet_id());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void readTweetsTwoCSV(String csvFile) {

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
//            System.out.println(reader.readAll().size());
            String[] line;
            int index = 0;
            while ((line = reader.readNext()) != null) {
                if (line[7].equalsIgnoreCase("thebrandiray")) {
                    String text = line[10];

                    System.out.println("unescapeJava " + StringEscapeUtils.escapeJava(text));

                    System.out.println("Tweets [ " + line[0] + ",  " + line[1] + " , " + line[2] + " , " +
                            line[3] + " , " + line[4] + " , " + line[5] +
                            " , " + line[6] + " , " + line[7] + " , " + line[8] +
                            " , " + line[9] + " , " + new String(line[10].getBytes(StandardCharsets.UTF_8)) + " , " + line[11] +
                            " , " + line[12] + " , " + line[13] + " , " + line[14]);
                }
                index++;
            }
            System.out.println(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
