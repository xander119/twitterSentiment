package twitter;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Zechen on 2016/11/11.
 */
public class CreateArffFiles {
    private String url = "jdbc:mysql://localhost:3306/twitterResearch";
    private String username = "root";
    private String password = "root";

    public static void main(String args[]) {
        String orgselectTweets = "SELECT c.text,c.labelled_sentiment from tweet_set_two as c order by c.tweet_id limit 500";
        String p1selectTweets = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c order by c.tweet_id limit 500";
        String p2selectTweets = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2_expanded as c order by c.tweet_id limit 500";
        String p3selectTweets = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c order by c.tweet_id limit 500";
        String fileName = "arff/BTweets_processed_500_WordVec_expanded.arff";
        String fileNamep1 = "arff/BTweets_processed_p1_500_WordVec_expanded.arff";
        String fileNamep2 = "arff/BTweets_processed_p2_500_WordVec_expanded.arff";
        String fileNamep3 = "arff/BTweets_processed_p3_500_WordVec_expanded.arff";

        CreateArffFiles createArffFiles = new CreateArffFiles();
//        createArffFiles.createLabelledArff(orgselectTweets,fileName);
        createArffFiles.createLabelledArff(p1selectTweets, fileNamep1);
        createArffFiles.createLabelledArff(p2selectTweets, fileNamep2);
        createArffFiles.createLabelledArff(p3selectTweets, fileNamep3);


    }

    public void createLabelledArff(String sql, String fileNme) {
        String unknownFileName = "Tweets_processed_p2_1000_unknown.arff";
        String selectTweets = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'negative' order by c.text limit 1116) " +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'positive' order by c.text limit 1116)" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'neutral' order by c.text limit 1116 )";
//        createArffFiles.createUnknownArff(selectTweetsRandom, unknownFileName);

        CreateArffFiles createArffFiles = new CreateArffFiles();
        Instances testingSplits = createArffFiles.getTweetInstancesFromDB(sql);
        File file = new File(fileNme);
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("@relation Tweets");
            bw.newLine();
            bw.newLine();
            bw.write("@attribute text string");
            bw.newLine();
            bw.write("@attribute labelled_sentiment {positive,neutral,negative}");
            bw.newLine();
            bw.newLine();
            bw.write("@data");
            bw.newLine();
            for (int i = 0; i < testingSplits.numInstances(); i++) {
                bw.write("\"" + testingSplits.instance(i).stringValue(0) + "\"," + testingSplits.instance(i).stringValue(1));
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void createUnknownArff(String sql, String fileNme) {
        CreateArffFiles createArffFiles = new CreateArffFiles();

        Instances testingSplits = createArffFiles.getTweetInstancesFromDB(sql);
        File file = new File(fileNme);
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < testingSplits.numInstances(); i++) {
                bw.write("\"" + testingSplits.instance(i).stringValue(0) + "\",?");
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Instances getTweetInstancesFromDB(String sql) {
        InstanceQuery instanceQuery = null;
        try {
            instanceQuery = new InstanceQuery();
            instanceQuery.setDatabaseURL(url);
            instanceQuery.setUsername(username);
            instanceQuery.setPassword(password);

            instanceQuery.setQuery(sql);
            return instanceQuery.retrieveInstances();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
