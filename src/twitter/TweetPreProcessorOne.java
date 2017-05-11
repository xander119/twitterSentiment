package twitter;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.experiment.InstanceQuery;

import java.io.File;

/**
 * Created by Zechen on 2016/10/31.
 */
public class TweetPreProcessorOne {
    private String url = "jdbc:mysql://localhost:3306/twitterResearch";
    private String username = "root";
    private String password = "root";

    public static void main(String[] args) {
        TweetPreProcessorOne tweetPreprocessor = new TweetPreProcessorOne();

    }


    public void writingToArff(String sql, String filePath) {

        try {
            InstanceQuery instanceQuery = new InstanceQuery();
            instanceQuery.setDatabaseURL(url);
            instanceQuery.setUsername(username);
            instanceQuery.setPassword(password);

            instanceQuery.setQuery(sql);
            Instances data = instanceQuery.retrieveInstances();

            ArffSaver arffSaver = new ArffSaver();

            arffSaver.setInstances(data);
            arffSaver.setFile(new File(filePath));
            arffSaver.writeBatch();

            System.out.println("Number of tweets:" + data.size() + "\nSaved to file : '" + filePath + "' \n");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void preProcessing() {
        //1. get data sets
        //2. Pre-processing and save to different .arff and table
        //   1.


        String pathname = "./data/trainingTwo.arff";
        String pathname1 = "./data/trainingOne.arff";
        String selectTrainingOne = "select * from tweet_set_one_training;";
        String selectTrainingTwo = "select * from tweet_set_two_training;";

    }


}
/*
// The result of this query is the table which Weka is going to //use for classification or prediction
Instances data = query.retrieveInstances();
int nAttr = data.numAttributes();
int index = (int) (Math.random() * nAttr);
//set the data for classification, in this case we have set a //random attribute for classification
data.setClassIndex(index);
        // Some of my data in the database are numeric, but I need the classifier to be notified that they are actually nominal values(like the month of a year)
        NumericToNominal nm = new NumericToNominal();
        String[] options = new String[2];
        options[0] = "-R";
        options[1] = "1-2"; //set the attributes from indices 1 to 2 as

        //nominal
        nm.setOptions(options);
        nm.setInputFormat(data);
        Instances filteredData = Filter.useFilter(data, nm);

        // classifier
        ConjunctiveRule classifier = new ConjunctiveRule();
        classifier.buildClassifier(filteredData);
        outWriter.print(classifier); // Print the output of the //classifier.
        } catch (Exception e) {
        e.printStackTrace();
        outWriter.println("Exception: " + e.getMessage());
        }*/
