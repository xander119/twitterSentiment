package twitter;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.Vote;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

/**
 * Created by Zechen on 2016/12/23.
 */
public class Wekatest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Wekatest wekatest = new Wekatest();
//        wekatest.run1();
//        wekatest.run2();
//        wekatest.run3();
        wekatest.run2();

        /*String sql = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c order by c.tweet_id ";
        Instances instances = DBHelper.getTweetInstancesFromDB(sql);
        instances.setClass(instances.attribute("labelled_sentiment"));
        WekaClassUtil.countClasses(instances);*/
    }

    public void run1() throws IOException {
        BufferedReader reader =
                new BufferedReader(new FileReader("C:/Program Files/Weka-3-8/data/ReutersGrain-train.arff"));
        BufferedReader reader1 =
                new BufferedReader(new FileReader("C:/Program Files/Weka-3-8/data/ReutersGrain-test.arff"));
        ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
        ArffLoader.ArffReader arff2 = new ArffLoader.ArffReader(reader1);
        Instances instancestrain = arff.getData();
        Instances instancestest = arff2.getData();
        instancestrain.setClass(instancestrain.attribute("class-att"));
        instancestest.setClass(instancestest.attribute("class-att"));
        System.out.println(instancestrain.toString());
        System.out.println(instancestest.toString());
        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setFilter(WekaClassUtil.getStringToWordVectorFilter());
        filteredClassifier.setClassifier(vote);
        Evaluation evaluation = null;
        try {
            evaluation = new Evaluation(instancestrain);
            evaluation.crossValidateModel(filteredClassifier, instancestest, 10, new Random(1L));

            System.out.println(evaluation.toSummaryString());
            System.out.println(evaluation.toMatrixString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run0() throws IOException {
        BufferedReader reader =
                new BufferedReader(new FileReader("C:/Program Files/Weka-3-8/data/ReutersGrain-train.arff"));
        BufferedReader reader1 =
                new BufferedReader(new FileReader("C:/Program Files/Weka-3-8/data/ReutersGrain-test.arff"));
        ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
        ArffLoader.ArffReader arff2 = new ArffLoader.ArffReader(reader1);
        Instances instancestrain = arff.getData();
        Instances instancestest = arff2.getData();
        instancestrain.setClass(instancestrain.attribute("class-att"));
        instancestest.setClass(instancestest.attribute("class-att"));
        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
        instancestrain = WekaClassUtil.convertToStringToWordVector(instancestrain);
        instancestest = WekaClassUtil.convertToStringToWordVector(instancestest);

        /*FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setFilter(WekaClassUtil.getStringToWordVectorFilter());
        filteredClassifier.setClassifier(vote);*/
        Evaluation evaluation = null;
        try {
            evaluation = new Evaluation(instancestrain);
            evaluation.crossValidateModel(vote, instancestest, 10, new Random(1L));

            System.out.println(evaluation.toSummaryString());
            System.out.println(evaluation.toMatrixString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run2() throws IOException {
//        where c.labelled_sentiment = 'positive' or c.labelled_sentiment = 'negative'

        long startTime = System.currentTimeMillis();
        System.out.println("Start Time : " + new Date());

        String sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit 2151)" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit 2151)" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit 2151)";
        Instances instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(DBHelper.getTweetInstancesFromDB(sql)));
        instances.setClass(instances.attribute("labelled_sentiment"));

//        System.out.println(instances.toSummaryString());

        //Feature selection
        AttributeSelection attributeSelection = new AttributeSelection();  // package weka.filters.supervised.attribute!
        InfoGainAttributeEval eval = new InfoGainAttributeEval();

        Ranker search = new Ranker();
        search.setNumToSelect(-1);
        search.setThreshold(-1.7976931348623157E308);

        attributeSelection.setEvaluator(eval);
        attributeSelection.setSearch(search);

        try {
            attributeSelection.setInputFormat(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // generate new data
        Instances newData = null;
        try {
            newData = Filter.useFilter(instances, attributeSelection);
            System.out.println("useFilter: ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();

        Evaluation evaluation = null;
        try {
            evaluation = new Evaluation(newData);
            evaluation.crossValidateModel(vote, newData, 10, new Random(1L));

            System.out.println(evaluation.toSummaryString());
            System.out.println(evaluation.toMatrixString());
            System.out.println(evaluation.toClassDetailsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long totalTime = ((endTime - startTime) / 1000) / 60;
        System.out.println("End Time : " + new Date());
        System.out.println("Duration: " + totalTime + " mins");
    }
//    public void run3() throws IOException {
//
//        //todo UnsupportedAttributeTypeException: weka.attributeSelection.InfoGainAttributeEval: Cannot handle unary class!
////        System.out.println(trainingIns.toSummaryString());
//
//        String positiveT = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment='positive' order by c.tweet_id ";
//
//        Instances positiveIns = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter((DBHelper.getTweetInstancesFromDB(positiveT))));
//        positiveIns.setClass(positiveIns.attribute("labelled_sentiment"));
//
//        int numberOfTweetSelection = positiveIns.numInstances();
//
//        Instances newSelectedNegativeTweets = getSelectedTweetByClass(numberOfTweetSelection, "negative");
//        Instances newSelectedNeutralTweets = getSelectedTweetByClass(numberOfTweetSelection, "neutral");
//
//        Instances trainingInstance;
//        trainingInstance = Instances.mergeInstances(Instances.mergeInstances(positiveIns, newSelectedNegativeTweets), newSelectedNeutralTweets);
//
//        WekaClassUtil.countClasses(trainingInstance);
///*
//
//        String training = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'negative' order by c.tweet_id limit 1116) union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'positive' order by c.tweet_id limit 1116) union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'neutral' order by c.tweet_id limit 1116);";
//        String testing = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'negative' order by c.tweet_id desc limit 1116) union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'positive' order by c.tweet_id desc limit 1116) union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'neutral' order by c.tweet_id desc limit 1116);";
//        Instances trainingIns = WekaClassUtil.applyNominalToStringFilter(DBHelper.getTweetInstancesFromDB(training));
//        Instances testingIns = WekaClassUtil.applyNominalToStringFilter(DBHelper.getTweetInstancesFromDB(testing));
//        trainingIns.setClass(trainingIns.attribute("labelled_sentiment"));
//        testingIns.setClass(testingIns.attribute("labelled_sentiment"));
//        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
//
//        FilteredClassifier filteredClassifier = new FilteredClassifier();
//        filteredClassifier.setFilter(WekaClassUtil.getStringToWordVectorFilter());
//        filteredClassifier.setClassifier(vote);
//
//        Evaluation evaluation = null;
//        try {
//            evaluation = new Evaluation(trainingIns);
//            evaluation.crossValidateModel(filteredClassifier, testingIns, 7, new Random(1L));
//
//            System.out.println(evaluation.toSummaryString());
//            System.out.println(evaluation.toMatrixString());
//            System.out.println(evaluation.toClassDetailsString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//*/
//
//    }
//
//    private Instances getSelectedTweetByClass(int numberOfTweetSelection, String className) {
//
//        String sql = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment='"+className+"' order by c.tweet_id ";
//        System.out.println("numberOfTweetSelection " + numberOfTweetSelection);
//        System.out.println("className " + className);
//
//        Instances instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(DBHelper.getTweetInstancesFromDB(sql)));
//        instances.setClass(instances.attribute("labelled_sentiment"));
//
//        System.out.println(className + " tweets: " + instances.numInstances());
//        System.out.println(instances);
//
//        //Feature selection
//        AttributeSelection attributeSelection = new AttributeSelection();  // package weka.filters.supervised.attribute!
//        InfoGainAttributeEval eval = new InfoGainAttributeEval();
//
//        Ranker search = new Ranker();
//        search.setNumToSelect(numberOfTweetSelection);
//        search.setThreshold(-1.7976931348623157E308);
//
//        attributeSelection.setEvaluator(eval);
//        attributeSelection.setSearch(search);
//
//        try {
//            attributeSelection.setInputFormat(instances);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // generate new data
//        Instances newSelectedTweets = null;
//        try {
//            newSelectedTweets = Filter.useFilter(instances, attributeSelection);
//            System.out.println("useFilter: No of negative instances: " + newSelectedTweets.numInstances());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return newSelectedTweets;
//    }
//
//    public static Instances concatInstances (Instances inst1, Instances inst2)
//    {
//        ArrayList<Instance> instAL = new ArrayList<Instance>();
//        for (int i=0; i<inst2.numInstances(); i++)
//            instAL.add(inst2.instance(i));
//        for (int i=0; i<instAL.size(); i++)
//            inst1.add(instAL.get(i));
//        return (inst1);
//    }

}
