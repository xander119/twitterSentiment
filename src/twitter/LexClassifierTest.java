package twitter;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Zechen on 2016/11/25.
 */
public class LexClassifierTest {
    public static void main(String args[]) throws IOException, InterruptedException {
        LexClassifierTest combinedClassifiersLex = new LexClassifierTest();

        File result = new File("result/(individual)LexAlone.txt");
        String fromDBLexFirst = combinedClassifiersLex.fromDBLexFirst(result, "1000");

        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        pw.println("classifyTweets: " + fromDBLexFirst);
        pw.close();
       /* double bayesNet = 47.246614521800076;
        double naiveBayes = 59.25747847313367;
        double libSVM = 43.57298474945534;
        double j48 = 46.23761538322038;
        double randomForest = 62.57400454280594;
        double classifyTweets = 49.84108160847197;
        double[] all = {bayesNet, naiveBayes, libSVM, j48, randomForest, classifyTweets};
        String[] names = {"bayesNet", "naiveBayes", "libSVM", "j48", "randomForest", "classifyTweets"};
        double sum = bayesNet
                + naiveBayes
                + libSVM
                + j48
                + randomForest
                + classifyTweets;
        System.out.println("Sum is " + sum);
        int i = 0;
        for (double fmeasure : all) {
            System.out.println(names[i] + " Weight "+(fmeasure / sum)*100 + "%");
            i++;
        }
*/
    }


    public String fromDBLexFirst(File result, String numTweets) throws IOException {
        long startTime = System.currentTimeMillis();
        Date startDate = new Date();
        String startDataStr = "Start Time : " + startDate + "\n";
        System.out.println(startDataStr);

        TweetImplSetTwo tweetImplSetTwo = new TweetImplSetTwo();
//        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();
        String selectTweetsRandom = "SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3 as c order by c.tweet_id limit " + numTweets;


        Instances instancesInitial = tweetImplSetTwo.getTweetInstancesFromDB(selectTweetsRandom);
        instancesInitial.setClass(instancesInitial.attribute("labelled_sentiment"));
        String runTitle = numTweets + " Random Tweets 66% Split with only Lexicon Classifer \n";

        System.out.println(runTitle);
        WekaClassUtil.countClasses(instancesInitial);

        FastVector predictions = new FastVector();
        Evaluation validation = null;

        try {
            validation = new Evaluation(instancesInitial);
            SWN swnClassifier = EnsembleClassifierConfig.getSWNClassifier();
            int correctCount = 0, incorrectCount = 0;
            for (int i = 0; i < instancesInitial.numInstances(); i++) {
                Instance testingInstance = instancesInitial.instance(i);
                double lexicon = swnClassifier.classifytweet(instancesInitial.instance(i).stringValue(0));

                double actualV = testingInstance.classValue();
                if (lexicon < 0) {
                    lexicon = 1.0;
                } else if (lexicon > 0) {
                    lexicon = 2.0;
                } else {
                    lexicon = 0.0;
                }
                String predicated = instancesInitial.classAttribute().value((int) lexicon);
                String actual = instancesInitial.classAttribute().value((int) actualV);
//                System.out.println("A: " + actual + " - " + actualV + " P: " + predicated + " - " + lexicon);

                validation.evaluationForSingleInstance(NominalPrediction.makeDistribution(lexicon, 3), testingInstance, true);

                if (predicated.equalsIgnoreCase(actual))
                    correctCount++;
                else
                    incorrectCount++;

            }
            ArrayList<Prediction> predictions1 = validation.predictions();
            predictions.appendElements(predictions1);

            double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);
            String correct = "Pct-Correct: " + validation.pctCorrect() + "%\n";
            String incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%\n";
            String recall = "Recall: " + validation.weightedRecall() * 100 + "%\n";
            String precision = "Precision: " + validation.weightedPrecision() * 100 + "%\n";
            String fMeasure = "F-Measure: " + validation.weightedFMeasure() * 100 + "%\n";
            String errorRate = "Error-Rate: " + validation.errorRate() * 100 + "%\n";
            String summaryString = validation.toSummaryString() + "\n";
            String matrixString = validation.toMatrixString() + "\n";
            String classDetailsString = validation.toClassDetailsString() + "\n";
            System.out.println(correct);
            System.out.println(recall);
            System.out.println(precision);
            System.out.println(fMeasure);
            System.out.println(summaryString);
            System.out.println(matrixString);
            String correctCounts = "CorrectCount : " + correctCount + " IncorrectCount :" + incorrectCount;
            System.out.println(correctCounts);
            System.out.println(errorRate);

            String accuracyString = "Accuracy of Lex" + /*vote.getClass().getSimpleName() +*/ ": "
                    + String.format("%.2f%%", accuracy)
                    + "\n---------------------------------\n";
            System.out.println(accuracyString);

            long endTime = System.currentTimeMillis();
            long seconds = (endTime - startTime) / 1000;
            double totalTime = seconds / 60;
            Date now = new Date();
            String endTimeStr = "End Time : " + now + "\n";
            String durationString = "Duration: " + totalTime + " mins\n";
            System.out.println(correctCounts);

            System.out.println(endTimeStr);
            System.out.println(durationString);

            saveToFile(startDataStr, correct, incorrect, recall, precision, fMeasure, summaryString, matrixString, classDetailsString, accuracyString, endTimeStr, durationString, runTitle, correctCounts, errorRate, result);
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
                            String correctCounts,
                            String errorRate,
                            File result) throws IOException {
        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

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
        pw.println(correctCounts);
        pw.println(errorRate);
        pw.println(startDataStr);
        pw.println(durationString);
        pw.println(endTimeStr);
        pw.close();
    }

}
