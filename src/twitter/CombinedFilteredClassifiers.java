package twitter;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.Vote;
import weka.core.Instances;

import java.io.*;
import java.util.Date;
import java.util.Random;

/**
 * Created by Zechen on 2016/11/17.
 */
public class CombinedFilteredClassifiers {

    public static void main(String[] args) throws IOException, InterruptedException {
        CombinedFilteredClassifiers combinedClassifiers = new CombinedFilteredClassifiers();
        String defaultPathname = "result/FilterResultEC3.txt";
        String numTweet = "1000";
        String isExpand = "_expanded";

        if (args != null) {
            if (args.length > 0 && args[0] != null) {
                defaultPathname = args[0];
            }
            if (args.length > 1 && args[1] != null) {
                numTweet = args[1];
            }
            if (args.length > 2 && args[2] != null) {
                isExpand = args[2];
            }
        }
        File result = new File(defaultPathname);
        String training = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'negative' order by c.tweet_id limit 1116) union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'positive' order by c.tweet_id limit 1116) union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'neutral' order by c.tweet_id limit 1116);";
        String testing = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'negative' order by c.tweet_id desc limit 1116) union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'positive' order by c.tweet_id desc limit 1116) union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1_expanded as c where c.labelled_sentiment = 'neutral' order by c.tweet_id desc limit 1116);";

        String sql = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1" + isExpand + " as c  limit " + numTweet;
        String title = numTweet + " Random Tweets 66% Split processed with Ensemble Classifiers tweet_set_two_processed1" + isExpand + " \n";
        String fromDBP12000 = combinedClassifiers.fromDBOrg2000(result, title, sql);
       /* Thread.sleep(3000);
        System.out.println("Executed: fromDBP12000 sleep 3 seconds");
        sql = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2" + isExpand + " as c order by c.tweet_id limit " + numTweet;
        title = numTweet + " Random Tweets 66% Split processed with Ensemble Classifiers tweet_set_two_processed2" + isExpand + " \n";
        String fromDBP22000 = combinedClassifiers.fromDBOrg2000(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: fromDBP22000 sleep 3 seconds");

        sql = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3" + isExpand + " as c order by c.tweet_id limit " + numTweet;
        title = numTweet + " Random Tweets 66% Split processed with Ensemble Classifiers tweet_set_two_processed3" + isExpand + " \n";
        String fromDBP32000 = combinedClassifiers.fromDBOrg2000(result, title, sql);
        System.out.println("Executed: fromDBP32000 sleep 3 seconds");
        System.out.println("All Complete");

        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println("Results produced from with ensemble classifier.AVERAGE_RULE");

        pw.println("Preprocessed: " + numTweet + " No.tweets: " + fromDBP12000);
        pw.println("Preprocessed with replaced Emojis  " + numTweet + " No.tweets: " + fromDBP22000);
        pw.println("Preprocessed with removed Emojis " + numTweet + " No.tweets: " + fromDBP32000);

        pw.close();*/

    }


    public String fromDBOrg2000(File result, String title, String sql) throws IOException {
        long startTime = System.currentTimeMillis();
        Date startDate = new Date();
        String startDataStr = "Start Time : " + startDate + "\n";
        System.out.println(startDataStr);

        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();

        FilteredClassifier filteredClassifier = new FilteredClassifier();
        filteredClassifier.setFilter(WekaClassUtil.getStringToWordVectorFilter());
        filteredClassifier.setClassifier(vote);

        Instances instances = WekaClassUtil.applyNominalToStringFilter(DBHelper.getTweetInstancesFromDB(sql));
        instances.setClass(instances.attribute("labelled_sentiment"));
        Instances[] split = WekaClassUtil.percentageSplit(instances, 0.6);

        Instances trainingSplits = split[0];
        Instances testingSplits = split[1];

        trainingSplits.setClass(instances.attribute("labelled_sentiment"));
        testingSplits.setClass(instances.attribute("labelled_sentiment"));

        try {

            Evaluation evalAll = new Evaluation(trainingSplits);
            evalAll.crossValidateModel(filteredClassifier, testingSplits, 10, new Random(1L));
            // output evaluation
            System.out.println();
            System.out.println(evalAll.toSummaryString("=== " + 10 + "-fold Cross-validation ===", false));
            System.out.println(evalAll.toMatrixString("=== toMatrixString ==="));

           /* evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(filteredClassifier,instances,10,new Random(1));*/
//            predictions.appendElements(predictions1);

            String correct = "Pct-Correct: " + evalAll.pctCorrect() + "%\n";
            String incorrect = "Pct-Incorrect: " + evalAll.pctIncorrect() + "%\n";
            String recall = "Recall: " + evalAll.weightedRecall() * 100 + "%\n";
            String precision = "Precision: " + evalAll.weightedPrecision() * 100 + "%\n";
            String fMeasure = "F-Measure: " + evalAll.weightedFMeasure() * 100 + "%\n";
            String summaryString = evalAll.toSummaryString() + "\n";
            String matrixString = evalAll.toMatrixString() + "\n";
            String classDetailsString = evalAll.toClassDetailsString() + "\n";
            System.out.println(correct);
            System.out.println(recall);
            System.out.println(precision);
            System.out.println(fMeasure);
            System.out.println(summaryString);
            System.out.println(matrixString);


            long endTime = System.currentTimeMillis();
            long seconds = (endTime - startTime) / 1000;
            double totalTime = seconds / 60;
            Date now = new Date();
            String endTimeStr = "End Time : " + now + "\n";
            String durationString = "Duration: " + totalTime + " mins\n";

            System.out.println(endTimeStr);
            System.out.println(durationString);

//            File result = new File("Result.txt");
            saveToFile(startDataStr, correct, incorrect, recall, precision, fMeasure, summaryString, matrixString, classDetailsString, "", endTimeStr, durationString, title, result);
            return fMeasure;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }


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

}
