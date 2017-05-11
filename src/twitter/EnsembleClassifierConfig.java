package twitter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.net.estimate.SimpleEstimator;
import weka.classifiers.bayes.net.search.local.K2;
import weka.classifiers.bayes.net.search.local.LocalScoreSearchAlgorithm;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.SelectedTag;

import java.io.File;

/**
 * Created by Zechen on 2016/11/20.
 */
public class EnsembleClassifierConfig {

    public static RandomForest getRandomForest() {
        RandomForest randomForest = new RandomForest();
        randomForest.setBagSizePercent(100);
        randomForest.setBatchSize("1000");
        randomForest.setBreakTiesRandomly(false);
        randomForest.setCalcOutOfBag(false);
        randomForest.setDebug(false);
        randomForest.setDoNotCheckCapabilities(false);
        randomForest.setMaxDepth(0);
        randomForest.setNumDecimalPlaces(2);
        randomForest.setNumExecutionSlots(1);
        randomForest.setNumFeatures(0);
        randomForest.setNumIterations(100);
        randomForest.setOutputOutOfBagComplexityStatistics(false);
        randomForest.setPrintClassifiers(false);
        randomForest.setSeed(1);
        randomForest.setStoreOutOfBagPredictions(false);
        return randomForest;
    }

    public static J48 getJ48() {
        J48 j48 = new J48();
        j48.setBatchSize("1000");
        j48.setBinarySplits(false);
        j48.setCollapseTree(true);
        j48.setConfidenceFactor(0.25f);
        j48.setDoNotCheckCapabilities(false);
        j48.setDoNotMakeSplitPointActualValue(false);
        j48.setMinNumObj(2);
        j48.setNumDecimalPlaces(2);
        j48.setNumFolds(3);
        j48.setReducedErrorPruning(false);
        j48.setSaveInstanceData(false);
        j48.setSeed(1);
        j48.setSubtreeRaising(true);
        j48.setUnpruned(false);
        j48.setUseLaplace(false);
        j48.setUseMDLcorrection(true);
        return j48;
    }

    public static LibSVM getLibSVM() {
        LibSVM libsvm = new LibSVM();
        libsvm.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
        libsvm.setBatchSize("1000");
        libsvm.setCacheSize(40.0);
        libsvm.setCoef0(0.0);
        libsvm.setCost(1.0);
        libsvm.setDebug(false);
        libsvm.setDegree(3);
        libsvm.setDoNotCheckCapabilities(false);
        libsvm.setDoNotReplaceMissingValues(false);
        libsvm.setEps(0.001);
        libsvm.setGamma(0.0);
        libsvm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
        libsvm.setLoss(0.1);
        libsvm.setModelFile(new File("Weka-3.8"));
        libsvm.setNormalize(false);
        libsvm.setNu(0.5);
        libsvm.setNumDecimalPlaces(2);
        libsvm.setProbabilityEstimates(false);
        libsvm.setSeed(1);
        libsvm.setShrinking(true);
        return libsvm;
    }

    public static BayesNet getBayesNet() {
        BayesNet bayesNet = new BayesNet();
        bayesNet.setBatchSize("1000");
        SimpleEstimator simpleEstimator = new SimpleEstimator();
        simpleEstimator.setAlpha(0.5);
        bayesNet.setEstimator(simpleEstimator);
        bayesNet.setNumDecimalPlaces(2);
        K2 k2 = new K2();
        k2.setMaxNrOfParents(1);
        k2.setRandomOrder(false);
        k2.setInitAsNaiveBayes(true);
        k2.setScoreType(new SelectedTag("BAYES", LocalScoreSearchAlgorithm.TAGS_SCORE_TYPE));
        bayesNet.setSearchAlgorithm(k2);
        bayesNet.setUseADTree(false);
        return bayesNet;
    }

    public static NaiveBayes getNaiveBayes() {
        NaiveBayes naiveBayes = new NaiveBayes();
        naiveBayes.setBatchSize("1000");
        naiveBayes.setDebug(false);
        naiveBayes.setDisplayModelInOldFormat(false);
        naiveBayes.setDoNotCheckCapabilities(false);
        naiveBayes.setNumDecimalPlaces(2);
        naiveBayes.setUseKernelEstimator(false);
        naiveBayes.setUseSupervisedDiscretization(false);
        return naiveBayes;
    }

    public static Classifier[] getClassifiers() {
        NaiveBayes naiveBayes = getNaiveBayes();
        BayesNet bayesNet = getBayesNet();
        J48 j48 = getJ48();
        RandomForest randomForest = getRandomForest();
        LibSVM libsvm = getLibSVM();
        return new Classifier[]{
                bayesNet,
                naiveBayes,//decision table majority classifier
                libsvm,
                j48, // a decision tree
                randomForest//one-level decision tree
        };
    }

    public static Vote getEnsembleClassifier() {
        Vote vote = new Vote();
        Classifier[] classifiers = getClassifiers();
        vote.setClassifiers(classifiers);
        vote.setCombinationRule(new SelectedTag(Vote.AVERAGE_RULE, Vote.TAGS_RULES));
        vote.setDebug(false);
        vote.setSeed(1);
        return vote;
    }

    public static SWN getSWNClassifier() {
        return new SWN();
    }

    public static NaiveBayesMultinomial getNBN() {
        NaiveBayesMultinomial naiveBayesMultinomial = new NaiveBayesMultinomial();
        naiveBayesMultinomial.setBatchSize("1000");
        naiveBayesMultinomial.setDoNotCheckCapabilities(false);
        naiveBayesMultinomial.setNumDecimalPlaces(2);
        return naiveBayesMultinomial;
    }

    public static Evaluation classify(Classifier model, Instances trainingSet, Instances testingSet) {
        Evaluation evaluation = null;
        try {
            evaluation = new Evaluation(trainingSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("---------------------------Building Classifier---------------------");
            model.buildClassifier(trainingSet);
            System.out.println("---------------------------Model Built.");
        } catch (Exception e) {
            e.printStackTrace();
        }/*catch (OutOfMemoryError e1){
            e1.printStackTrace();
            System.out.print(printGCStats());
        }*/
        try {
            System.out.println("---------------------------Evaluating Model------------------------");
            evaluation.evaluateModel(model, testingSet);
            System.out.println("---------------------------Model Evaluated");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return evaluation;
    }

    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }

}
