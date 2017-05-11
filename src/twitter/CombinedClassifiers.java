package twitter;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.meta.Vote;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by Zechen on 2016/11/17.
 */
public class CombinedClassifiers {

    public static void main(String[] args) throws IOException, InterruptedException {
        CombinedClassifiers combinedClassifiers = new CombinedClassifiers();
        String defaultPathname = "result/RunD2Result7fold.txt";
        String numTweet = "600";

        if (args != null) {
            if (args.length > 0 && args[0] != null) {
                defaultPathname = args[0];
            }
            if (args.length > 1 && args[1] != null) {
                numTweet = args[1];
            }
        }
        File result = new File(defaultPathname);

        String sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        String title = numTweet + " order by confidence with Ensemble Classifiers tweet_set_two_processed1 BASELINE RESULT" + " \n";
        String tweet_set_two_processed1 = combinedClassifiers.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: classifyTweets sleep 3 seconds");

        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2airportcode_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2airportcode_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2airportcode_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Random Tweets 66% Split processed with Ensemble Classifiers tweet_set_two_processed2airportcode_expanded" + " \n";
        String airportcode = combinedClassifiers.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: classifyTweets airportcode sleep 3 seconds");

        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2slang_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2slang_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2slang_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Random Tweets 66% Split processed with Ensemble Classifiers tweet_set_two_processed2slang_expanded" + " \n";
        String airportCodeSlang = combinedClassifiers.classifyTweets(result, title, sql);
        System.out.println("Executed: classifyTweets airportCodeSlang sleep 3 seconds");

        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2hashtags_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2hashtags_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2hashtags_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Random Tweets 66% Split processed with Ensemble Classifiers tweet_set_two_processed2hashtags_expanded" + " \n";
        String airportCodeSlangHashtags = combinedClassifiers.classifyTweets(result, title, sql);
        System.out.println("Executed: classifyTweets airportCodeSlangHashtags sleep 3 seconds");

        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Random Tweets 66% Split processed with Ensemble Classifiers tweet_set_two_processed2emojis_expanded" + " \n";
        String airportCodeSlangHashtagsEmoji = combinedClassifiers.classifyTweets(result, title, sql);
        System.out.println("Executed: classifyTweets airportCodeSlangHashtagsEmoji sleep 3 seconds");

        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Random Tweets 66% Split processed with Ensemble Classifiers tweet_set_two_processed3_expanded" + " \n";
        String removedFeatures = combinedClassifiers.classifyTweets(result, title, sql);
        System.out.println("Executed: classifyTweets airportCodeSlangHashtagsEmoji sleep 3 seconds");


        System.out.println("All Complete");

        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println("Results produced from with ensemble classifier.AVERAGE_RULE");

        pw.println("Preprocessed Baseline: " + numTweet + " No.tweets: " + tweet_set_two_processed1);
        pw.println("Preprocessed with processed2 airportcode expanded Baseline, NoTweet : " + numTweet + " F-measure: " + airportcode);
        pw.println("Preprocessed with processed2 airportCodeSlang expanded Baseline, NoTweet : " + numTweet + " F-measure: " + airportCodeSlang);
        pw.println("Preprocessed with processed2 airportCodeSlangHashtags expanded Baseline, NoTweet : " + numTweet + " F-measure: " + airportCodeSlangHashtags);
        pw.println("Preprocessed with processed2 airportCodeSlangHashtagsEmoji expanded Baseline, NoTweet : " + numTweet + " F-measure: " + airportCodeSlangHashtagsEmoji);
        pw.println("Preprocessed with processed3 removed features Baseline, NoTweet : " + numTweet + " F-measure: " + removedFeatures);

        pw.close();

    }

//    public void fromFile() throws IOException {
//        long startTime = System.currentTimeMillis();
//        Date startDate = new Date();
//        String startDataStr = "Start Time : " + startDate + "\n";
//        System.out.println(startDataStr);
//        String runTitle = "2000 Random Tweets 66% Split From File \n";
//        System.out.println(runTitle);
//
//
//        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
//        BufferedReader reader =
//                new BufferedReader(new FileReader("Tweets_processed_p2_2000.arff"));
//        ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
//        Instances instances = arff.getData();
//        instances.setClass(instances.attribute("labelled_sentiment"));
//        instances = WekaClassUtil.convertToStringToWordVector(instances);
//        instances.setClass(instances.attribute("labelled_sentiment"));
//        WekaClassUtil.countClasses(instances);
//
//        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.66);
//
//        Instances trainingSplits = split[0];
//        Instances testingSplits = split[1];
//        FastVector predictions = new FastVector();
//        Evaluation validation = null;
//
//        try {
//            validation = new Evaluation(trainingSplits);
//            vote.buildClassifier(trainingSplits);
////            validation.crossValidateModel(vote, instances, 10, new Random(1L));
//            validation.evaluateModel(vote, testingSplits);
////            validation = EnsembleClassifierConfig.classify(vote, trainingSplits, testingSplits);
//
//            ArrayList<Prediction> predictions1 = validation.predictions();
//            predictions.appendElements(predictions1);
//
//            String correct = "Pct-Correct: " + validation.pctCorrect() + "%";
//            String incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%";
//            String recall = "Recall: " + validation.weightedRecall() * 100 + "%";
//            String precision = "Precision: " + validation.weightedPrecision() * 100 + "%";
//            String fMeasure = "F-Measure: " + validation.weightedFMeasure() * 100 + "%";
//            String summaryString = validation.toSummaryString();
//            String matrixString = validation.toMatrixString();
//            String classDetailsString = validation.toClassDetailsString();
//            System.out.println(correct);
//            System.out.println(recall);
//            System.out.println(precision);
//            System.out.println(fMeasure);
//            System.out.println(summaryString);
//            // Uncomment to see the summary for each training-testing pair.
//            //                System.out.println("summary for each training-testing pair ");
//            //                System.out.println(models[j].toString());
//            //                System.out.println("" + validation.toSummaryString());
//            //                System.out.println(validation.toClassDetailsString());
//            //                System.out.println("predictions" + validation.toMatrixString());
//
//            // Calculate overall accuracy of current classifier on all splits
//            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);
//
//            // Print current classifier's name and accuracy in a complicated,
//            // but nice-looking way.
//            String accuracyString = "Accuracy of " + vote.getClass().getSimpleName() + ": "
//                    + String.format("%.2f%%", accuracy)
//                    + "\n---------------------------------";
//            System.out.println(accuracyString);
//            instances.clear();
//            long endTime = System.currentTimeMillis();
//            long seconds = (endTime - startTime) / 1000;
//            double totalTime = seconds / 60;
//            Date now = new Date();
//            String endTimeStr = "End Time : " + now + "\n";
//            String durationString = "Duration: " + totalTime + " mins\n";
//
//            System.out.println(endTimeStr);
//            System.out.println(durationString);
//
//            File result = new File("Result.txt");
//            saveToFile(startDataStr, correct, incorrect, recall, precision, fMeasure, summaryString, matrixString, classDetailsString, accuracyString, endTimeStr, durationString, runTitle, result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    public void fromDB() throws IOException {
//        long startTime = System.currentTimeMillis();
//        Date startDate = new Date();
//        System.out.println("Start Time : " + startDate);
//
//        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
//
//        String selectTweetsRandom = "SELECT c.text,c.labelled_sentiment from tweet_set_two as c order by c.tweet_id limit 9000";
//        String selectTweetsP2 = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'negative' order by c.text limit 1116) " +
//                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'positive' order by c.text limit 1116)" +
//                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'neutral' order by c.text limit 1116)";
//        String originalTweets = "(SELECT c.text,c.labelled_sentiment from tweet_set_two as c where c.labelled_sentiment = 'negative' order by c.text limit 1116) " +
//                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two as c where c.labelled_sentiment = 'positive' order by c.text limit 1116)" +
//                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two as c where c.labelled_sentiment = 'neutral' order by c.text limit 1116)";
//        String selectTweetsP3 = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3 as c where c.labelled_sentiment = 'negative' order by c.text limit 1116) " +
//                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3 as c where c.labelled_sentiment = 'positive' order by c.text limit 1116)" +
//                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3 as c where c.labelled_sentiment = 'neutral' order by c.text limit 1116)";
//        String p2Tweets = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'negative' order by c.text limit 1116) " +
//                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'positive' order by c.text limit 1116)" +
//                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'neutral' order by c.text limit 1116)";
//        Map<String, String> sqlMap = new HashMap<String, String>();
//        sqlMap.put("9000 Tweets ACS by ID", selectTweetsRandom);
//        sqlMap.put("9000 Replaced Emojis ACS by ID", selectTweetsRandom);
//        sqlMap.put("9000 removed Emojis ACS by ID", selectTweetsRandom);/*
//        sqlMap.put("Original Tweets with three Categories", originalTweets);
//        sqlMap.put("Tweets with Replaced Emojis with three Categories", selectTweetsP2);
//        sqlMap.put("Tweets with removed Emojis with three Categories", selectTweetsP3);*/
//        File result = new File("Result.txt");
//        FileWriter fw = new FileWriter(result, true);
//        BufferedWriter bw = new BufferedWriter(fw);
//        PrintWriter pw = new PrintWriter(bw);
//        for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
//            /*if (!pw.checkError()) {
//                pw = new PrintWriter(new FileOutputStream(result),true);
//            }*/
//            System.out.println("Selection: " + /*originalTweets*/entry.getKey() + "\n");
//            pw.println("****************************" + entry.getKey() + "\n");
//            Instances instances = DBHelper.getTweetInstancesFromDB(entry.getValue());
//            instances.setClass(instances.attribute("labelled_sentiment"));
//            instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instances));
//            WekaClassUtil.countClasses(instances);
//
//            Instances[] split = WekaClassUtil.percentageSplit(instances, 0.66);
//
//            Instances trainingSplits = split[0];
//            Instances testingSplits = split[1];
//            FastVector predictions = new FastVector();
//            Evaluation validation = null;
//            try {
////                validation = new Evaluation(instances);
////                vote.buildClassifier(instances);
////                validation.crossValidateModel(vote, instances, 10, new Random(1L));
//                validation = EnsembleClassifierConfig.classify(vote, trainingSplits, testingSplits);
//                ArrayList<Prediction> predictions1 = validation.predictions();
//                predictions.appendElements(predictions1);
//                String correct = "Pct-Correct: " + validation.pctCorrect() + "%\n";
//                String incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%\n";
//                String recall = "Recall: " + validation.weightedRecall() * 100 + "%\n";
//                String precision = "Precision: " + validation.weightedPrecision() * 100 + "%\n";
//                String fMeasure = "F-Measure: " + validation.weightedFMeasure() * 100 + "%\n";
//                String summaryString = validation.toSummaryString() + "\n";
//                String matrixString = validation.toMatrixString() + "\n";
//                String classDetailsString = validation.toClassDetailsString() + "\n";
//                System.out.println(correct);
//                System.out.println(recall);
//                System.out.println(precision);
//                System.out.println(fMeasure);
//                pw.println(correct);
//                pw.println(incorrect);
//                pw.println(recall);
//                pw.println(precision);
//                pw.println(fMeasure);
//                pw.println(summaryString);
//                pw.println(matrixString);
//                pw.println(classDetailsString);
//                // Uncomment to see the summary for each training-testing pair.
//                //                System.out.println("summary for each training-testing pair ");
//                //                System.out.println(models[j].toString());
//                //                System.out.println("" + validation.toSummaryString());
//                //                System.out.println(validation.toClassDetailsString());
//                //                System.out.println("predictions" + validation.toMatrixString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            // Calculate overall accuracy of current classifier on all splits
//            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);
//
//            // Print current classifier's name and accuracy in a complicated,
//            // but nice-looking way.
//            String accuracyString = "Accuracy of " + vote.getClass().getSimpleName() + ": "
//                    + String.format("%.2f%%", accuracy)
//                    + "\n---------------------------------\n";
//            System.out.println(accuracyString);
//            pw.println(accuracyString);
//            instances.clear();
////            pw.close();
//        }
//        long endTime = System.currentTimeMillis();
//        long seconds = (endTime - startTime) / 1000;
//        double totalTime = seconds / 60;
//        Date now = new Date();
//        System.out.println("End Time : " + now);
//        System.out.println("Duration: " + totalTime + " mins");
////        pw = new PrintWriter(result);
//        pw.println("Start Time : " + startDate + "\n");
//        pw.println("Duration: " + totalTime + " mins\n");
//        pw.println("End Time : " + now + "\n");
//        pw.close();
//
//    }

    public String classifyTweets(File result, String title, String sql) throws IOException {
        long startTime = System.currentTimeMillis();
        Date startDate = new Date();
        String startDataStr = "Start Time : " + startDate + "\n";
        System.out.println(startDataStr);

        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();

        Instances instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(DBHelper.getTweetInstancesFromDB(sql)));
        if (instances != null) {
            instances.setClass(instances.attribute("labelled_sentiment"));
        }
        AttributeSelection attributeSelection = new AttributeSelection();  // package weka.filters.supervised.attribute!
        InfoGainAttributeEval eval = new InfoGainAttributeEval();

        Ranker search = new Ranker();
        search.setNumToSelect(instances.numInstances());
        search.setThreshold(-1.7976931348623157E308);

        attributeSelection.setEvaluator(eval);
        attributeSelection.setSearch(search);

        try {
            attributeSelection.setInputFormat(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // generate new data
        Instances filteredData = null;
        try {
            filteredData = Filter.useFilter(instances, attributeSelection);
            System.out.println("Used Filter with : " + instances.numInstances() + " attributes selected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(title);
        WekaClassUtil.countClasses(filteredData);

        FastVector predictions = new FastVector();

        try {
            weka.classifiers.evaluation.Evaluation validation = new weka.classifiers.evaluation.Evaluation(filteredData);
            validation.crossValidateModel(vote, filteredData, 7, new Random(1L));
            ArrayList<Prediction> predictions1 = validation.predictions();
            predictions.appendElements(predictions1);
            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);
            String correct = "Pct-Correct: " + validation.pctCorrect() + "%\n";
            String incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%\n";
            String recall = "Recall: " + validation.weightedRecall() * 100 + "%\n";
            String precision = "Precision: " + validation.weightedPrecision() * 100 + "%\n";
            String fMeasure = "F-Measure: " + validation.weightedFMeasure() * 100 + "%\n";
            String summaryString = validation.toSummaryString() + "\n";
            String matrixString = validation.toMatrixString() + "\n";
            String classDetailsString = validation.toClassDetailsString() + "\n";
            System.out.println(correct);
            System.out.println(recall);
            System.out.println(precision);
            System.out.println(fMeasure);
            System.out.println(summaryString);
            System.out.println(matrixString);

            String accuracyString = "Accuracy of " + vote.getClass().getSimpleName() + ": "
                    + String.format("%.2f%%", accuracy)
                    + "\n---------------------------------\n";
            System.out.println(accuracyString);

            long endTime = System.currentTimeMillis();
            long seconds = (endTime - startTime) / 1000;
            double totalTime = seconds / 60;
            Date now = new Date();
            String endTimeStr = "End Time : " + now + "\n";
            String durationString = "Duration: " + totalTime + " mins\n";

            System.out.println(endTimeStr);
            System.out.println(durationString);

            saveToFile(startDataStr, correct, incorrect, recall, precision, fMeasure, summaryString, matrixString, classDetailsString, accuracyString, endTimeStr, durationString, title, result);
            return fMeasure;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

//    public String fromDBP12000(File result) throws IOException {
//        long startTime = System.currentTimeMillis();
//        Date startDate = new Date();
//        String startDataStr = "Start Time : " + startDate + "\n";
//        System.out.println(startDataStr);
//
//        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
//        String selectTweetsRandom = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c order by c.tweet_id limit 2000";
//
//
//        Instances instances = DBHelper.getTweetInstancesFromDB(selectTweetsRandom);
//        instances.setClass(instances.attribute("labelled_sentiment"));
//        instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instances));
//        String runTitle = "P1 2000 Random Tweets 66% Split  \n";
//        System.out.println(runTitle);
//        WekaClassUtil.countClasses(instances);
//
//        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.66);
//
//        Instances trainingSplits = split[0];
//        Instances testingSplits = split[1];
//        FastVector predictions = new FastVector();
//        Evaluation validation = null;
//        try {
////            validation = new Evaluation(instances);
////            vote.buildClassifier(instances);
////            validation.crossValidateModel(vote, instances, 10, new Random(1L));
//            validation = new Evaluation(trainingSplits);
//            vote.buildClassifier(trainingSplits);
////            validation.crossValidateModel(vote, instances, 10, new Random(1L));
//            validation.evaluateModel(vote, testingSplits);
////            validation = EnsembleClassifierConfig.classify(vote, trainingSplits, testingSplits);
//
//            ArrayList<Prediction> predictions1 = validation.predictions();
//            predictions.appendElements(predictions1);
//            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);
//            String correct = "Pct-Correct: " + validation.pctCorrect() + "%\n";
//            String incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%\n";
//            String recall = "Recall: " + validation.weightedRecall() * 100 + "%\n";
//            String precision = "Precision: " + validation.weightedPrecision() * 100 + "%\n";
//            String fMeasure = "F-Measure: " + validation.weightedFMeasure() * 100 + "%\n";
//            String summaryString = validation.toSummaryString() + "\n";
//            String matrixString = validation.toMatrixString() + "\n";
//            String classDetailsString = validation.toClassDetailsString() + "\n";
//            System.out.println(correct);
//            System.out.println(recall);
//            System.out.println(precision);
//            System.out.println(fMeasure);
//            System.out.println(summaryString);
//            System.out.println(matrixString);
//
//            String accuracyString = "Accuracy of " + vote.getClass().getSimpleName() + ": "
//                    + String.format("%.2f%%", accuracy)
//                    + "\n---------------------------------\n";
//            System.out.println(accuracyString);
//
//            long endTime = System.currentTimeMillis();
//            long seconds = (endTime - startTime) / 1000;
//            double totalTime = seconds / 60;
//            Date now = new Date();
//            String endTimeStr = "End Time : " + now + "\n";
//            String durationString = "Duration: " + totalTime + " mins\n";
//
//            System.out.println(endTimeStr);
//            System.out.println(durationString);
//
//            saveToFile(startDataStr, correct, incorrect, recall, precision, fMeasure, summaryString, matrixString, classDetailsString, accuracyString, endTimeStr, durationString, runTitle, result);
//            return fMeasure;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//
//
//    }
//
//    public String fromDBP22000(File result) throws IOException {
//        long startTime = System.currentTimeMillis();
//        Date startDate = new Date();
//        String startDataStr = "Start Time : " + startDate + "\n";
//        System.out.println(startDataStr);
//
//
//        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
//        String selectTweetsRandom = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c order by c.tweet_id limit 2000";
//
//
//        Instances instances = DBHelper.getTweetInstancesFromDB(selectTweetsRandom);
//        instances.setClass(instances.attribute("labelled_sentiment"));
//        instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instances));
//        String runTitle = "P2 2000 Random Tweets 66% Split  \n";
//        System.out.println(runTitle);
//        WekaClassUtil.countClasses(instances);
//
//        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.66);
//
//        Instances trainingSplits = split[0];
//        Instances testingSplits = split[1];
//        FastVector predictions = new FastVector();
//        Evaluation validation = null;
//        try {
////            validation = new Evaluation(instances);
////            vote.buildClassifier(instances);
////            validation.crossValidateModel(vote, instances, 10, new Random(1L));
//            validation = new Evaluation(trainingSplits);
//            vote.buildClassifier(trainingSplits);
////            validation.crossValidateModel(vote, instances, 10, new Random(1L));
//            validation.evaluateModel(vote, testingSplits);
////            validation = EnsembleClassifierConfig.classify(vote, trainingSplits, testingSplits);
//
//            ArrayList<Prediction> predictions1 = validation.predictions();
//            predictions.appendElements(predictions1);
//            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);
//            String correct = "Pct-Correct: " + validation.pctCorrect() + "%\n";
//            String incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%\n";
//            String recall = "Recall: " + validation.weightedRecall() * 100 + "%\n";
//            String precision = "Precision: " + validation.weightedPrecision() * 100 + "%\n";
//            String fMeasure = "F-Measure: " + validation.weightedFMeasure() * 100 + "%\n";
//            String summaryString = validation.toSummaryString() + "\n";
//            String matrixString = validation.toMatrixString() + "\n";
//            String classDetailsString = validation.toClassDetailsString() + "\n";
//            System.out.println(correct);
//            System.out.println(recall);
//            System.out.println(precision);
//            System.out.println(fMeasure);
//            System.out.println(summaryString);
//            System.out.println(matrixString);
//
//            String accuracyString = "Accuracy of " + vote.getClass().getSimpleName() + ": "
//                    + String.format("%.2f%%", accuracy)
//                    + "\n---------------------------------\n";
//            System.out.println(accuracyString);
//
//            long endTime = System.currentTimeMillis();
//            long seconds = (endTime - startTime) / 1000;
//            double totalTime = seconds / 60;
//            Date now = new Date();
//            String endTimeStr = "End Time : " + now + "\n";
//            String durationString = "Duration: " + totalTime + " mins\n";
//
//            System.out.println(endTimeStr);
//            System.out.println(durationString);
//
//            saveToFile(startDataStr, correct, incorrect, recall, precision, fMeasure, summaryString, matrixString, classDetailsString, accuracyString, endTimeStr, durationString, runTitle, result);
//            return fMeasure;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//
//
//    }
//
//    public String fromDBP32000(File result) throws IOException {
//        long startTime = System.currentTimeMillis();
//        Date startDate = new Date();
//        String startDataStr = "Start Time : " + startDate + "\n";
//        System.out.println(startDataStr);
//
//
//        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
//        String selectTweetsRandom = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3 as c order by c.tweet_id limit 2000";
//
//
//        Instances instances = DBHelper.getTweetInstancesFromDB(selectTweetsRandom);
//        instances.setClass(instances.attribute("labelled_sentiment"));
//        instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instances));
//        String runTitle = "P3 2000 Random Tweets 66% Split \n";
//        System.out.println(runTitle);
//        WekaClassUtil.countClasses(instances);
//
//        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.66);
//
//        Instances trainingSplits = split[0];
//        Instances testingSplits = split[1];
//        FastVector predictions = new FastVector();
//        Evaluation validation = null;
//        try {
////            validation = new Evaluation(instances);
////            vote.buildClassifier(instances);
////            validation.crossValidateModel(vote, instances, 10, new Random(1L));
//            validation = new Evaluation(trainingSplits);
//            vote.buildClassifier(trainingSplits);
////            validation.crossValidateModel(vote, instances, 10, new Random(1L));
//            validation.evaluateModel(vote, testingSplits);
////            validation = EnsembleClassifierConfig.classify(vote, trainingSplits, testingSplits);
//
//            ArrayList<Prediction> predictions1 = validation.predictions();
//            predictions.appendElements(predictions1);
//            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);
//            String correct = "Pct-Correct: " + validation.pctCorrect() + "%\n";
//            String incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%\n";
//            String recall = "Recall: " + validation.weightedRecall() * 100 + "%\n";
//            String precision = "Precision: " + validation.weightedPrecision() * 100 + "%\n";
//            String fMeasure = "F-Measure: " + validation.weightedFMeasure() * 100 + "%\n";
//            String summaryString = validation.toSummaryString() + "\n";
//            String matrixString = validation.toMatrixString() + "\n";
//            String classDetailsString = validation.toClassDetailsString() + "\n";
//            System.out.println(correct);
//            System.out.println(recall);
//            System.out.println(precision);
//            System.out.println(fMeasure);
//            System.out.println(summaryString);
//            System.out.println(matrixString);
//
//            String accuracyString = "Accuracy of " + vote.getClass().getSimpleName() + ": "
//                    + String.format("%.2f%%", accuracy)
//                    + "\n---------------------------------\n";
//            System.out.println(accuracyString);
//
//            long endTime = System.currentTimeMillis();
//            long seconds = (endTime - startTime) / 1000;
//            double totalTime = seconds / 60;
//            Date now = new Date();
//            String endTimeStr = "End Time : " + now + "\n";
//            String durationString = "Duration: " + totalTime + " mins\n";
//
//            System.out.println(endTimeStr);
//            System.out.println(durationString);
//
//            saveToFile(startDataStr, correct, incorrect, recall, precision, fMeasure, summaryString, matrixString, classDetailsString, accuracyString, endTimeStr, durationString, runTitle, result);
//            return fMeasure;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//
//
//    }


    private void saveToFile(String startDataStr,
                            String correct,
                            String incorrect,
                            String recall,
                            String precision,
                            String fMeasure,
                            String summaryString,
                            String matrixString,
                            String classDetailsString,
                            String accuracyString,
                            String endTimeStr,
                            String durationString,
                            String runTitle,
                            File result) throws IOException {
        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        pw.println("Results produced from with Original Ensemble classifier.");
        pw.println(runTitle);
        pw.println(correct);
        pw.println(incorrect);
        pw.println(recall);
        pw.println(precision);
        pw.println(fMeasure);
        pw.println(summaryString);
        pw.println(matrixString);
        pw.println(classDetailsString);
        // Uncomment to see the summary for each training-testing pair.
        //                System.out.println("summary for each training-testing pair ");
        //                System.out.println(models[j].toString());
        //                System.out.println("" + validation.toSummaryString());
        //                System.out.println(validation.toClassDetailsString());
        //                System.out.println("predictions" + validation.toMatrixString());
        // Calculate overall accuracy of current classifier on all splits

        // Print current classifier's name and accuracy in a complicated,
        // but nice-looking way.

        pw.println(accuracyString);


        pw.println(startDataStr);
        pw.println(durationString);
        pw.println(endTimeStr);
        pw.close();
    }

    public void fromDBSmall() throws IOException {
        long startTime = System.currentTimeMillis();
        Date startDate = new Date();
        System.out.println("Start Time : " + startDate);


        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
        String p2TweetsSmall = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'negative' order by c.text limit 200) " +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'positive' order by c.text limit 200)" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c where c.labelled_sentiment = 'neutral' order by c.text limit 200)";
        String randomSmall = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2 as c  order by c.tweet_id limit 1200";
        File result = new File("ResultSmall.txt");
        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

            /*if (!pw.checkError()) {
                pw = new PrintWriter(new FileOutputStream(result),true);
            }*/
        System.out.println("Selection: 200 each class \n");
        pw.println("\n\nRun **********************************200 each class \n");
        Instances instances = DBHelper.getTweetInstancesFromDB(randomSmall);
        instances.setClass(instances.attribute("labelled_sentiment"));
        instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instances));
        WekaClassUtil.countClasses(instances);

        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.66);

        Instances trainingSplits = split[0];
        Instances testingSplits = split[1];
        FastVector predictions = new FastVector();
        Evaluation validation = null;
        try {
//            validation = new Evaluation(instances);
//            vote.buildClassifier(instances);
//            validation.crossValidateModel(vote, instances, 10, new Random(1L));
            validation = EnsembleClassifierConfig.classify(vote, trainingSplits, testingSplits);
            ArrayList<Prediction> predictions1 = validation.predictions();
            predictions.appendElements(predictions1);
            String correct = "Pct-Correct: " + validation.pctCorrect() + "%\n";
            String incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%\n";
            String recall = "Recall: " + validation.weightedRecall() * 100 + "%\n";
            String precision = "Precision: " + validation.weightedPrecision() * 100 + "%\n";
            String fMeasure = "F-Measure: " + validation.weightedFMeasure() * 100 + "%\n";
            String summaryString = validation.toSummaryString() + "\n";
            String matrixString = validation.toMatrixString() + "\n";
            String classDetailsString = validation.toClassDetailsString() + "\n";
            System.out.println(correct);
            System.out.println(recall);
            System.out.println(precision);
            System.out.println(fMeasure);
            System.out.println(summaryString);
            System.out.println(matrixString);
            pw.println(correct);
            pw.println(incorrect);
            pw.println(recall);
            pw.println(precision);
            pw.println(fMeasure);
            pw.println(summaryString);
            pw.println(matrixString);
            pw.println(classDetailsString);
            // Uncomment to see the summary for each training-testing pair.
            //                System.out.println("summary for each training-testing pair ");
            //                System.out.println(models[j].toString());
            //                System.out.println("" + validation.toSummaryString());
            //                System.out.println(validation.toClassDetailsString());
            //                System.out.println("predictions" + validation.toMatrixString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Calculate overall accuracy of current classifier on all splits
        double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);

        // Print current classifier's name and accuracy in a complicated,
        // but nice-looking way.
        String accuracyString = "Accuracy of " + vote.getClass().getSimpleName() + ": "
                + String.format("%.2f%%", accuracy)
                + "\n---------------------------------\n";
        System.out.println(accuracyString);
        pw.println(accuracyString);
        instances.clear();
//            pw.close();
//        }
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        double totalTime = seconds / 60;
        Date now = new Date();
        System.out.println("End Time : " + now);
        System.out.println("Duration: " + totalTime + " mins");
//        pw = new PrintWriter(result);
        pw.println("Start Time : " + startDate + "\n");
        pw.println("Duration: " + totalTime + " mins\n");
        pw.println("End Time : " + now + "\n");
        pw.close();

    }
}
