package twitter;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zechen on 2016/11/25.
 */
public class CombinedClassifierLex {
    public static void main(String[] args) throws IOException, InterruptedException {
        CombinedClassifierLex combinedClassifiersLex = new CombinedClassifierLex();
        String defaultPathname = "result/RunD3ResultLex10fold(onlyEmojiReplace).txt";
        String numTweet = "600";
        String isExpand = "";
        long startTime = System.currentTimeMillis();
        Date startDate = new Date();
        String startDataStr = "Start Time : " + startDate + "\n";
        System.out.println(startDataStr);
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
        String sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed1 as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";

        String title = numTweet + " Tweets cross valid processed with Lexicon Classifier tweet_set_two_processed1" + isExpand + " \n";
        /*String tweet_set_two_processed1 = combinedClassifiersLex.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: P1 sleep 3 seconds");

        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2airportcode_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2airportcode_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2airportcode_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Tweets cross valid processed with Lexicon Classifier tweet_set_two_processed2airportcode_expanded" + isExpand + " \n";
        String airportcode = combinedClassifiersLex.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: P1 sleep 3 seconds");


        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2slang_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2slang_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2slang_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Tweets cross valid processed with Lexicon Classifier tweet_set_two_processed2slang_expanded" + isExpand + " \n";
        String airportCodeSlang = combinedClassifiersLex.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: P1 sleep 3 seconds");


        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2hashtags_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2hashtags_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2hashtags_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Tweets cross valid processed with Lexicon Classifier tweet_set_two_processed2hashtags_expanded" + isExpand + " \n";
        String airportCodeSlangHashtags = combinedClassifiersLex.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: P1 sleep 3 seconds");

        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Tweets cross valid processed with Lexicon Classifier tweet_set_two_processed2emojis_expanded" + isExpand + " \n";
        String airportCodeSlangHashtagsEmoji = combinedClassifiersLex.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: P1 sleep 3 seconds");
*/
        sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed3_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";
        title = numTweet + " Tweets cross valid processed with Lexicon Classifier tweet_set_two_processed3_expanded" + isExpand + " \n";
        String removedFeatures = combinedClassifiersLex.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Executed: P1 sleep 3 seconds");

        System.out.println("All Complete");

        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println("Results produced from with Lexicon ensemble classifier.AVERAGE_RULE");
/*
        pw.println("Preprocessed Baseline: " + numTweet + " No.tweets: " + tweet_set_two_processed1);
        pw.println("Preprocessed with processed2 airportcode expanded Baseline, NoTweet : " + numTweet + " F-measure: " + airportcode);
        pw.println("Preprocessed with processed2 airportCodeSlang expanded Baseline, NoTweet : " + numTweet + " F-measure: " + airportCodeSlang);
        pw.println("Preprocessed with processed2 airportCodeSlangHashtags expanded Baseline, NoTweet : " + numTweet + " F-measure: " + airportCodeSlangHashtags);
        pw.println("Preprocessed with processed2 airportCodeSlangHashtagsEmoji expanded Baseline, NoTweet : " + numTweet + " F-measure: " + airportCodeSlangHashtagsEmoji);*/
        pw.println("Preprocessed with processed3 removed features Baseline, NoTweet : " + numTweet + " F-measure: " + removedFeatures);
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        double totalTime = seconds / 60;
        Date now = new Date();
        String endTimeStr = "End Time : " + now + "\n";
        String durationString = "Duration: " + totalTime + " mins\n";

        System.out.println(endTimeStr);
        System.out.println(durationString);
        pw.println(endTimeStr);
        pw.println(durationString);

        pw.close();

    }


    public String classifyTweets(File result, String runTitle, String sql) throws IOException {
        long startTime = System.currentTimeMillis();
        Date startDate = new Date();
        String startDataStr = "Start Time : " + startDate + "\n";
        System.out.println(startDataStr);
        double totalRecall = 0.0;
        double totalPrecision = 0.0;
        double totalFMeasure = 0.0;
        double totalAccuracy = 0.0;

//        Vote vote = EnsembleClassifierConfig.getEnsembleClassifier();

        Instances instancesInitial = DBHelper.getTweetInstancesFromDB(sql);

        if (instancesInitial == null) throw new NullPointerException("No instances from DB using: " + sql);

        instancesInitial.setClass(instancesInitial.attribute("labelled_sentiment"));


        Instances instances = WekaClassUtil.convertToStringToWordVector(WekaClassUtil.applyNominalToStringFilter(instancesInitial));
        //Apply attribute selection here

        if (instances == null) throw new NullPointerException("instances are null");

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
        System.out.println(runTitle);
        WekaClassUtil.countClasses(filteredData);


        FastVector predictions = new FastVector();
        Evaluation validation = null;
        int folds = 10;
        try {
            int size = filteredData.numInstances() / 10;
            int begin = 0;
            int end = size - 1;
            String correct;
            String incorrect;
            String recall;
            String precision;
            String fMeasure;
            String summaryString;
            String matrixString;
            String classDetailsString;
            String accuracyString;

            for (int n = 1; n <= folds; n++) {
                double lexAccuracy = 0.0;
                int correctCount = 0;
                int incorrectCount = 0;
                System.out.println("Fold: " + n);
                Instances trainingSplits = new Instances(filteredData);
                Instances testingSplits = new Instances(filteredData, begin, (end - begin));
                for (int k = 0; k < (end - begin); k++) {
                    trainingSplits.delete(begin);
                }
                Instances testInstanceLex = new Instances(instancesInitial, begin, (end - begin));
                validation = new Evaluation(trainingSplits);

                System.out.println("building Classifier NaiveBayes");
                NaiveBayes naiveBayes = EnsembleClassifierConfig.getNaiveBayes();
                naiveBayes.buildClassifier(trainingSplits);
                System.out.println("building Classifier BayesNet");
                BayesNet bayesNet = EnsembleClassifierConfig.getBayesNet();
                bayesNet.buildClassifier(trainingSplits);
                System.out.println("building Classifier J48");
                J48 j48 = EnsembleClassifierConfig.getJ48();
                j48.buildClassifier(trainingSplits);
                System.out.println("building Classifier RandomForest");
                RandomForest randomForest = EnsembleClassifierConfig.getRandomForest();
                randomForest.buildClassifier(trainingSplits);
                System.out.println("building Classifier LibSVM");
                LibSVM libsvm = EnsembleClassifierConfig.getLibSVM();
                libsvm.buildClassifier(trainingSplits);
                SWN swnClassifier = EnsembleClassifierConfig.getSWNClassifier();

                for (int i = 0; i < testingSplits.numInstances(); i++) {
                    Instance testingInstance = testingSplits.instance(i);
                    Instance instanceLex = testInstanceLex.instance(i);

                    int naiveB = (int) naiveBayes.classifyInstance(testingInstance);
                    int bayesN = (int) bayesNet.classifyInstance(testingInstance);
                    int j48Decision = (int) j48.classifyInstance(testingInstance);
                    int randomF = (int) randomForest.classifyInstance(testingInstance);
                    int svm = (int) libsvm.classifyInstance(testingInstance);

                    double naiveBConfidenceScore = getHighest(naiveBayes.distributionForInstance(testingInstance));
                    double bayesNConfidenceScore = getHighest(bayesNet.distributionForInstance(testingInstance));
                    double j48ConfidenceScore = getHighest(j48.distributionForInstance(testingInstance));
                    double randomForestConfidenceScore = getHighest(randomForest.distributionForInstance(testingInstance));
                    double svmConfidenceScore = getHighest(libsvm.distributionForInstance(testingInstance));
                    double lexResultConfidence = swnClassifier.classifytweet(instanceLex.stringValue(0));

                    int lexicon = 0;
                    if (lexResultConfidence < 0) {
                        lexicon = 1;
                    } else if (lexResultConfidence > 0) {
                        lexicon = 2;
                    } else {
                        lexicon = 0;
                    }
                    Map<String, ConfidenceAndClass> map = new HashMap<>();
                    map.put("naiveB", new ConfidenceAndClass(naiveBConfidenceScore, naiveB));
                    map.put("bayesN", new ConfidenceAndClass(bayesNConfidenceScore, bayesN));
                    map.put("j48Decision", new ConfidenceAndClass(j48ConfidenceScore, j48Decision));
                    map.put("randomF", new ConfidenceAndClass(randomForestConfidenceScore, randomF));
                    map.put("svm", new ConfidenceAndClass(svmConfidenceScore, svm));
                    map.put("lexicon", new ConfidenceAndClass(Math.abs(lexResultConfidence), lexicon));
                    double actualV = testingInstance.classValue();
                    String actual = testingSplits.classAttribute().value((int) actualV);
                    String actuallex = instanceLex.stringValue(1);

                    int[] finalResults = {naiveB, bayesN, j48Decision, randomF, svm, lexicon};

                    int finalResult = avgProb(finalResults);
                    if (finalResult == -1) {
                        finalResult = getResultByConfidence(map);
                    }

                    if (!actual.equalsIgnoreCase(actuallex))
                        throw new Exception("different instance");
                    if (!actual.equalsIgnoreCase(testingSplits.classAttribute().value((int) finalResult))) {
                        System.out.println(instanceLex.stringValue(instanceLex.attribute(0)) + " actual: " + actual + " predicted " + testingSplits.classAttribute().value((int) finalResult));
                    }
                    validation.evaluationForSingleInstance(NominalPrediction.makeDistribution(finalResult, 3), testingInstance, true);


                }
                ArrayList<Prediction> predictions1 = validation.predictions();
                predictions.appendElements(predictions1);
                double weightedRecall = validation.weightedRecall();
                double weightedPrecision = validation.weightedPrecision();
                double weightedFMeasure = validation.weightedFMeasure();
                double accuracy = EnsembleClassifierConfig.calculateAccuracy(predictions);
                totalRecall += weightedRecall;
                totalPrecision += weightedPrecision;
                totalFMeasure += weightedFMeasure;
                totalAccuracy += validation.pctCorrect();
                correct = "Pct-Correct: " + validation.pctCorrect() + "%\n";
                incorrect = "Pct-Incorrect: " + validation.pctIncorrect() + "%\n";
                recall = "Recall: " + (totalRecall / 10) * 100 + "%\n";
                precision = "Precision: " + (totalPrecision / 10) * 100 + "%\n";
                fMeasure = "F-Measure: " + (totalFMeasure / 10) * 100 + "%\n";
                summaryString = validation.toSummaryString() + "\n";
                matrixString = validation.toMatrixString() + "\n";
                classDetailsString = validation.toClassDetailsString() + "\n";

                System.out.println(summaryString);
                System.out.println("weightedFMeasure : " + weightedFMeasure);
                System.out.println("tt FMeasure : " + totalFMeasure);
                begin = end + 1;
                end += size;
                if (n == 9) {
                    end = filteredData.numInstances();
                }

            }
            recall = "Avg Recall: " + (totalRecall / 10) * 100 + "%\n";
            precision = "Avg Precision: " + (totalPrecision / 10) * 100 + "%\n";
            fMeasure = "Avg F-Measure: " + (totalFMeasure / 10) * 100 + "%\n";
            accuracyString = "Avg Accuracy: " + (totalAccuracy / 10) + "%\n";
            System.out.println(recall);
            System.out.println(precision);
            System.out.println(fMeasure);
            System.out.println(accuracyString);

            long endTime = System.currentTimeMillis();
            long seconds = (endTime - startTime) / 1000;
            double totalTime = seconds / 60;
            Date now = new Date();
            String endTimeStr = "End Time : " + now + "\n";
            String durationString = "Duration: " + totalTime + " mins\n";

            System.out.println(endTimeStr);
            System.out.println(durationString);

//            File result = new File("Result.txt");
            saveToFile(startDataStr, "correct", "incorrect", recall, precision, fMeasure, "summaryString", "toMatrixString", "classDetailsString", accuracyString, endTimeStr, durationString, runTitle, "predictCount", result);
            return fMeasure;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

    private int getResultByConfidence(Map<String, ConfidenceAndClass> map) {
        //todo Change it to calculate the avg of confidence for the tied class
        double maxConf = 0.0D;
        int result = -1;
        String currentMax = "";
        Map<Integer, Double> temp = new HashMap<>();
        for (Map.Entry<String, ConfidenceAndClass> entry : map.entrySet()) {

            double confidence = entry.getValue().getConfidence();
            int classValue = entry.getValue().getClassValue();
            if (!temp.containsKey(classValue)) {
                temp.put(classValue, confidence);
            } else {
                temp.put(classValue, temp.get(classValue) + confidence);
            }

//            if (classValue =preclassV)
            if (confidence > maxConf) {
                maxConf = confidence;
                currentMax = entry.getKey();
                result = classValue;
            }
        }
        int max = -1;
        double maxConfident = 0.0D;
        for (Map.Entry<Integer, Double> entry : temp.entrySet()) {
            Integer classV = entry.getKey();
            Double confidence = entry.getValue();
            if (confidence > maxConfident) {
                max = classV;
                maxConfident = confidence;
            }
        }
        Map<String, ConfidenceAndClass> resultMap = new HashMap<>();
        resultMap.put(currentMax, new ConfidenceAndClass(maxConf, result));

        return max;
    }

    private double getHighest(double[] doubles) {
        double max = 0.0D;
        int maxIndex = 0;

        for (int i = 0; i < doubles.length; ++i) {
            if (doubles[i] > max) {
                maxIndex = i;
                max = doubles[i];
            }
        }

        if (max > 0.0D) {
            return max;
        }
        return 0.0D;
    }

    private double avge(int[] finalResults) {
        double sum = 0.0;
        for (int i : finalResults) {
            sum += i;
        }
        return sum / finalResults.length;

    }

    int avgProb(int[] ary) {
        Map<Integer, Integer> m = new HashMap<Integer, Integer>();

        for (int a : ary) {
            Integer freq = m.get(a);
            m.put(a, (freq == null) ? 1 : freq + 1);
        }

        int max = -1;
        int mostFrequent = -1;

        for (Map.Entry<Integer, Integer> e : m.entrySet()) {
            if (e.getValue() > max) {
                mostFrequent = e.getKey();
                max = e.getValue();
            }
        }
        //Breaking the tie using weight of each classifier
        int counter = 0;
        for (Map.Entry<Integer, Integer> e : m.entrySet()) {
            if (e.getValue() == max) {
                counter++;
            }
        }
        if (counter != 1) {
            return -1;
        }

        return mostFrequent;
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
                            String prediCount, File result) throws IOException {
        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        pw.println("Results produced from with Lexicon Ensemble classifier.");
        pw.println(runTitle);
        pw.println(accuracyString);
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

        pw.println(prediCount);


        pw.println(startDataStr);
        pw.println(durationString);
        pw.println(endTimeStr);
        pw.close();
    }

}
