package twitter;

import org.apache.commons.lang3.StringEscapeUtils;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.experiment.InstanceQuery;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Zechen on 2016/11/5.
 */
public class TweetImplSetTwo {
    public String url = "jdbc:mysql://localhost:3306/twitterResearch";
    public String username = "root";
    public String password = "root";

    public static void main(String[] args) {
        TweetImplSetTwo tweetImplSetTwo = new TweetImplSetTwo();
        tweetImplSetTwo.implementationWithStepOne();
//        tweetImplSetTwo.implementationWithStepOneFromFile();
//        tweetImplSetTwo.implementationStepOneFromFileSepTrainTestSet();
        tweetImplSetTwo.implementationWithStepTwo();
//        tweetImplSetTwo.replaceEmojis();
//        tweetImplSetTwo.removeEmojisSaveToP3();
    }


    public static String printGCStats() {
        long totalGarbageCollections = 0;
        long garbageCollectionTime = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = gc.getCollectionCount();

            if (count >= 0) {
                totalGarbageCollections += count;
            }

            long time = gc.getCollectionTime();

            if (time >= 0) {
                garbageCollectionTime += time;
            }
        }
        return "Garbage Collections: " + totalGarbageCollections + "\n" +
                "Garbage Collection Time (ms): " + garbageCollectionTime + "\n";
    }


    public void implementationWithStepOne() {
        /**
         * neutral: 3003
         *      select count(a.text) from (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'neutral') as a;
         * positive: 2232
         *      select count(a.text) from (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'positive') as a;
         * negative: 9056
         *      select count(a.text) from (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'negative') as a;
         */
        System.out.println("Implementation With Step One From Database.....");

        String selectTweets = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'negative' order by c.text limit 2232) " +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'positive' order by c.text limit 2232)" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'neutral' order by c.text limit 2232)";

        String selectTweetsRandom = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c order by c.tweet_id limit 9000";
        String selectAllTweets = "SELECT c.text,c.labelled_sentiment from tweet_set_two_training_process1 as c order by c.tweet_id ";

        Instances instances = getTrainingInstances(selectTweetsRandom);
        instances.setClass(instances.attribute("labelled_sentiment"));
        Instances toStringFilter = WekaClassUtil.applyNominalToStringFilter(instances);
        instances = WekaClassUtil.convertToStringToWordVector(toStringFilter);
//        Instances testingSplits = getTestingInstances(selectTweetsRandom);

        Classifier[] models = EnsembleClassifierConfig.getClassifiers();

/*
        // Do 10-split cross validation
//        Instances[][] split = crossValidationSplit(instances, 5);
        Instances[] split = WekaClassUtil.percentageSplitByCategory(instances, 0.6);
        // Separate split into training and testing arrays
        Instances instances = split[0];
        Instances testingSplits = split[1];*/
        /*Instances instances =
        Instances testingSplits =*/
        WekaClassUtil.countClasses(instances);

        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.6);

        Instances trainingSplits = split[0];
        Instances testingSplits = split[1];
        for (Classifier model : models) {

            // Collect every group of predictions for current model in a FastVector
            FastVector predictions = new FastVector();

//            for (int i = 0; i < instances.length; i++) {
            Evaluation validation = null;
            try {
                validation = EnsembleClassifierConfig.classify(model, trainingSplits, testingSplits);
                ArrayList<Prediction> predictions1 = validation.predictions();
                predictions.appendElements(predictions1);

                // Uncomment to see the summary for each training-testing pair.
//                System.out.println("summary for each training-testing pair ");
//                System.out.println(models[j].toString());
//                System.out.println("" + validation.toSummaryString());
//                System.out.println(validation.toClassDetailsString());
//                System.out.println("predictions" + validation.toMatrixString());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            }


            // Calculate overall accuracy of current classifier on all splits
            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);

            // Print current classifier's name and accuracy in a complicated,
            // but nice-looking way.
            System.out.println("Accuracy of " + model.getClass().getSimpleName() + ": "
                    + String.format("%.2f%%", accuracy)
                    + "\n---------------------------------");
        }
    }

    public void implementationWithStepOneFromFile() {
        System.out.println("---------------------------------Implementation With Step One From ARFF File.....");
        Instances instances = null;
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("Tweets_processed_9000.arff");
            instances = source.getDataSet();
            instances.setClass(instances.attribute("labelled_sentiment"));
            instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instances));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Classifier[] models = EnsembleClassifierConfig.getClassifiers();

/*
        // Do 10-split cross validation
//        Instances[][] split = crossValidationSplit(instances, 5);
//        Instances[] split = WekaClassUtil.percentageSplitByCategory(instances, 0.6);
*/
        // Separate split into training and testing arrays
        /*Instances trainingSplits =
        Instances testingSplits =*/
        WekaClassUtil.countClasses(instances);
        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.6);
        Instances trainingSplits = split[0];
        Instances testingSplits = split[1];

        for (Classifier model : models) {

            // Collect every group of predictions for current model in a FastVector
            FastVector predictions = new FastVector();

//            for (int i = 0; i < trainingSplits.length; i++) {
            Evaluation validation = null;
            try {
                validation = EnsembleClassifierConfig.classify(model, trainingSplits, testingSplits);
                ArrayList<Prediction> predictions1 = validation.predictions();
                predictions.appendElements(predictions1);

                // Uncomment to see the summary for each training-testing pair.
//                System.out.println("summary for each training-testing pair ");
//                System.out.println(models[j].toString());
//                System.out.println(validation.toSummaryString());
//                System.out.println(validation.toClassDetailsString());
//                System.out.println(validation.toMatrixString());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            }


            // Calculate overall accuracy of current classifier on all splits
            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);

            // Print current classifier's name and accuracy in a complicated,
            // but nice-looking way.
            System.out.println("Accuracy of " + model.getClass().getSimpleName() + ": "
                    + String.format("%.2f%%", accuracy)
                    + "\n---------------------------------");
        }
    }


    //This is not working due to unequal data set headers
    public void implementationStepOneFromFileSepTrainTestSet() {
        Instances instancesTest = null;
        Instances instancesTrain = null;
        try {
            ConverterUtils.DataSource sourceTrain = new ConverterUtils.DataSource("Tweets_processed_training.arff");
            ConverterUtils.DataSource sourceTest = new ConverterUtils.DataSource("Tweets_processed_9000.arff");
            //test set
            instancesTest = sourceTest.getDataSet();
            instancesTest.setClass(instancesTest.attribute("labelled_sentiment"));
            instancesTest = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instancesTest));

            //training set
            instancesTrain = sourceTrain.getDataSet();
            instancesTrain.setClass(instancesTrain.attribute("labelled_sentiment"));
            instancesTrain = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instancesTrain));
        } catch (Exception e) {
            e.printStackTrace();
        }

        NaiveBayes naiveBayes = EnsembleClassifierConfig.getNaiveBayes();
        BayesNet bayesNet = EnsembleClassifierConfig.getBayesNet();
        J48 j48 = EnsembleClassifierConfig.getJ48();
        RandomForest randomForest = EnsembleClassifierConfig.getRandomForest();

        Classifier[] models = {
                j48, // a decision tree
                bayesNet,
                naiveBayes,//decision table majority classifier
                randomForest//one-level decision tree
        };

        for (Classifier model : models) {

            // Collect every group of predictions for current model in a FastVector
            FastVector predictions = new FastVector();

//            for (int i = 0; i < trainingSplits.length; i++) {
            Evaluation validation = null;
            try {
                System.out.println(instancesTest.equalHeaders(instancesTrain));
                validation = EnsembleClassifierConfig.classify(model, instancesTrain, instancesTest);
                ArrayList<Prediction> predictions1 = validation.predictions();
                predictions.appendElements(predictions1);

                // Uncomment to see the summary for each training-testing pair.
//                System.out.println("summary for each training-testing pair ");
//                System.out.println(models[j].toString());
                System.out.println(validation.toSummaryString());
                System.out.println(validation.toClassDetailsString());
                System.out.println(validation.toMatrixString());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            }


            // Calculate overall accuracy of current classifier on all splits
            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);

            // Print current classifier's name and accuracy in a complicated,
            // but nice-looking way.
          /*  System.out.println("Accuracy of " + model.getClass().getSimpleName() + ": "
                    + String.format("%.2f%%", accuracy)
                    + "\n---------------------------------");*/
        }
    }


    public void implementationWithStepTwo() {

        //Remove all emojis

        String selectTweets = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'negative' order by c.text limit 2232) " +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'positive' order by c.text limit 2232)" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'neutral' order by c.text limit 2232)";

        String selectTweetsRandom = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c order by c.tweet_id limit 9000";


        Instances instances = this.getTweetInstancesFromDB(selectTweetsRandom);
        if (instances == null) throw new NullPointerException("No instances from DB");

        //set class variable
        instances.setClassIndex(instances.numAttributes() - 1);
       /* int i = 0;
        for (Instance instance : instances) {
            System.out.println(instances.instance(i));
            i++;
        }*/

        Classifier[] models = EnsembleClassifierConfig.getClassifiers();
        // Do percentage split
        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.6);

        // Separate split into training and testing arrays
        Instances trainingSplits = split[0];
        Instances testingSplits = split[1];
        for (Classifier model : models) {

            // Collect every group of predictions for current model in a FastVector
            FastVector predictions = new FastVector();

            try {
                Evaluation validation = EnsembleClassifierConfig.classify(model, trainingSplits, testingSplits);
                ArrayList<Prediction> predictions1 = validation.predictions();
                predictions.appendElements(predictions1);

                // Uncomment to see the summary for each training-testing pair.
//                System.out.println("summary for each training-testing pair ");
//                System.out.println(models[j].toString());
                System.out.println(validation.toSummaryString());
                System.out.println(validation.toClassDetailsString());
                System.out.println("predictions" + validation.toMatrixString());


                // Calculate overall accuracy of current classifier on all splits
                double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);

                // Print current classifier's name and accuracy in a complicated,
                // but nice-looking way.
                System.out.println("Accuracy of " + model.getClass().getSimpleName() + ": "
                        + String.format("%.2f%%", accuracy)
                        + "\n---------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public Instances getTweetInstancesFromDB(String sql) {
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


//custom rules for the word matrix, Emojis, (Preprocessing )

    public Instances getTestingInstances(String selectTweets) {
        Instances testingSplits = this.getTweetInstancesFromDB(selectTweets);

        if (testingSplits == null) throw new NullPointerException("No testingSplits instances from DB");
        //set class variable

        testingSplits = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(testingSplits));
        testingSplits.setClass(testingSplits.attribute("labelled_sentiment"));
        return testingSplits;
    }

    public Instances getTrainingInstances(String selectTweets) {
        Instances trainingSplits = this.getTweetInstancesFromDB(selectTweets);

        if (trainingSplits == null) throw new NullPointerException("No trainingSplits instances from DB");
        //set class variable
        trainingSplits = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(trainingSplits));
        trainingSplits.setClass(trainingSplits.attribute("labelled_sentiment"));
        return trainingSplits;
    }


    public ArrayList<TweetSecSetEn> getTweetsFromDB(String selectTweets) {
        ArrayList<TweetSecSetEn> tweetSecSetEns = new ArrayList<>();
        Connection connection = DBHelper.getConnection();
        TweetSecSetEn tweetSecSetEn;
        ResultSet rs = DBHelper.excSelectQuery(connection, selectTweets);
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

                tweetSecSetEn.setText(StringEscapeUtils.escapeJava(rs.getString("text")));

                tweetSecSetEns.add(tweetSecSetEn);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return tweetSecSetEns;
    }

}
