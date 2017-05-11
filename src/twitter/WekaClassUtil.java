package twitter;

import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ArffSaver;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToString;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Zechen on 2016/11/21.
 */
public class WekaClassUtil {
    public static void countClasses(Instances instances) {
        int positiveCount = 0, negativeCount = 0, neutralCount = 0, nullCount = 0;
        for (int i = 0; i < instances.numInstances(); i++) {
            double i1 = instances.instance(i).classValue();
            if (i1 == 0.0) {
                neutralCount++;

            } else if (i1 == 1.0) {
                negativeCount++;

            } else if (i1 == 2.0) {
                positiveCount++;

            } else {
                nullCount++;

            }
        }
        System.out.println("Number of Instances: " + instances.numInstances());
        System.out.println("Class Attribute: " + instances.classAttribute().name());
        System.out.println("Number of Attributes: " + instances.numAttributes());
        /*for (int i=0;i<instances.numAttributes();i++) {
            System.out.print("Attributes: " + instances.attribute(i).name() + " ");
        }*/
        System.out.println("Positive: " + positiveCount + "\nNegative: " + negativeCount + "\nNeutral: " + neutralCount);
    }

    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
        Instances[][] split = new Instances[2][numberOfFolds];

        for (int i = 0; i < numberOfFolds; i++) {
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }

        return split;
    }

    public static Instances[] percentageSplitByCategory(Instances instances, double v) {
        ArrayList<Attribute> attributes = new ArrayList<>(2);
        attributes.add(instances.attribute(0));
        attributes.add(instances.attribute(1));
        Instances poInstance = new Instances("poInstance", attributes, instances.numInstances());
        Instances negaInstance = new Instances("negaInstance", attributes, instances.numInstances());
        Instances neuInstance = new Instances("neuInstance", attributes, instances.numInstances());
        for (int i = 0; i < instances.numInstances(); i++) {
//            System.out.println("instance  : " + "#" + i + "  " + instances.instance(i).stringValue(0) + " Sentiment: " + instances.instance(i).stringValue(1));
//            System.out.println("attribute 2 : "+instances.get(i).attribute(1).value(i));
            switch (instances.instance(i).stringValue(1)) {
                case "positive":
                    poInstance.add(instances.instance(i));
                    break;
                case "neutral":
                    neuInstance.add(instances.instance(i));
                    break;
                case "negative":
                    negaInstance.add(instances.instance(i));
                    break;
            }
        }
        Instances trainSub1 = new Instances(poInstance, 0, poInstance.numInstances() / 2);
        Instances trainSub2 = new Instances(negaInstance, 0, negaInstance.numInstances() / 2);
        Instances trainSub3 = new Instances(neuInstance, 0, neuInstance.numInstances() / 2);
        Instances train = new Instances("trainInstances", attributes, instances.numInstances() / 2);
        for (int i = 0; i < 1116; i++) {
            train.add(trainSub1.instance(i));
            train.add(trainSub2.instance(i));
            train.add(trainSub3.instance(i));
        }
        Instances test = new Instances("testInstances", attributes, instances.numInstances() / 2);
        Instances testSub1 = new Instances(poInstance, poInstance.numInstances() / 2, poInstance.numInstances() / 2);
        Instances testSub2 = new Instances(negaInstance, negaInstance.numInstances() / 2, negaInstance.numInstances() / 2);
        Instances testSub3 = new Instances(neuInstance, neuInstance.numInstances() / 2, neuInstance.numInstances() / 2);
        for (int i = 0; i < 1116; i++) {
            test.add(testSub1.instance(i));
            test.add(testSub2.instance(i));
            test.add(testSub3.instance(i));
        }
        train.setClassIndex(1);
        test.setClassIndex(1);
        return new Instances[]{train, test};
    }

    public static Instances[] percentageSplit(Instances instances, double v) {
        int trainSize = (int) Math.round(instances.numInstances() * v);
        int testSize = instances.numInstances() - trainSize;

        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);
        /*System.out.println("Num of Training instances: " + train.numInstances());
        countClasses(train);
        System.out.println("Num of Test instances: " + test.numInstances());
        countClasses(test);*/

        return new Instances[]{train, test};
    }

    public static Instances applyNominalToStringFilter(Instances instances) {
        Attribute text = instances.attribute("text") == null ? instances.attribute("Text") : instances.attribute("text");
        if (text != null && text.type() == Attribute.NOMINAL) {
            NominalToString nominalToStringFilter = new NominalToString();
            try {
                nominalToStringFilter.setAttributeIndexes("1");
                nominalToStringFilter.setInputFormat(instances);
                instances = Filter.useFilter(instances, nominalToStringFilter);
                System.out.println("NominalToStringFilter Applied.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instances;
    }

    public static Instances convertToStringToWordVector(Instances instances) {

        StringToWordVector stringToWordVector = new StringToWordVector();
        try {
            stringToWordVector.setIDFTransform(false);
            stringToWordVector.setTFTransform(false);
            stringToWordVector.setDebug(false);
            stringToWordVector.setAttributeIndices("first-last");
            stringToWordVector.setDoNotCheckCapabilities(false);
            stringToWordVector.setDoNotOperateOnPerClassBasis(false);
            stringToWordVector.setInvertSelection(false);
            stringToWordVector.setLowerCaseTokens(false);
            stringToWordVector.setMinTermFreq(1);
            stringToWordVector.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
            stringToWordVector.setPeriodicPruning(-1.0);
            stringToWordVector.setSaveDictionaryInBinaryForm(false);

            stringToWordVector.setStopwordsHandler(new Rainbow());
            NGramTokenizer nGramTokenizer = new NGramTokenizer();
            nGramTokenizer.setNGramMaxSize(3);
            nGramTokenizer.setNGramMinSize(1);
            stringToWordVector.setTokenizer(nGramTokenizer);
            stringToWordVector.setWordsToKeep(9999);

            stringToWordVector.setInputFormat(instances); //data instances that you are going to input to the filter
            Instances instances1 = Filter.useFilter(instances, stringToWordVector);
            ArffSaver saver = new ArffSaver();
            saver.setInstances(instances1);
            saver.setFile(new File("StringToWV.arff"));
            saver.writeBatch();
            return instances1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instances;
    }

    public static StringToWordVector getStringToWordVectorFilter() {
        StringToWordVector stringToWordVector = new StringToWordVector();
        try {
            stringToWordVector.setIDFTransform(false);
            stringToWordVector.setTFTransform(false);
            stringToWordVector.setDebug(false);
            stringToWordVector.setAttributeIndices("first");
            stringToWordVector.setDoNotCheckCapabilities(false);
            stringToWordVector.setDoNotOperateOnPerClassBasis(false);
            stringToWordVector.setInvertSelection(false);
            stringToWordVector.setLowerCaseTokens(false);
            stringToWordVector.setMinTermFreq(1);
            stringToWordVector.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
            stringToWordVector.setPeriodicPruning(-1.0);
            stringToWordVector.setSaveDictionaryInBinaryForm(false);

//            stringToWordVector.setStemmer();
            stringToWordVector.setStopwordsHandler(new Rainbow());
            stringToWordVector.setTokenizer(new WordTokenizer());
            stringToWordVector.setWordsToKeep(9999);

            return stringToWordVector;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringToWordVector;
    }

    public static Set<String> constructWordList() {
        Set<String> wordDict = new HashSet<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("wordList.txt"));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (validated(line)) {
                    wordDict.add(line);
                }
            }
            br.close();
            return wordDict;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordDict;
    }

    /**
     * This method is to check and ensure
     * the word is not a char,
     * the word is not abbreviation
     * the word do not contain '
     *
     * @param line
     * @return valiated
     */
    private static boolean validated(String line) {
        return line.charAt(0) != '#' && !line.contains("'") && !StringUtils.isAllUpperCase(line) && line.length() != 1;
    }

    public static Map<String, String> constructAirportCodeList() {
        Map<String, String> airportMap = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("AirportCodeUS.txt"));
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                airportMap.put(split[0], split[1]);
            }
            br.close();
            return airportMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return airportMap;
    }

    public static Map<String, String> constructTweetSlangList() {
        Map<String, String> slangMap = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("TwitterSlang.txt"));
            String line = "";
            int index = 1;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("=");
                slangMap.put(split[0], split[1]);
                index++;
            }
            br.close();
            return slangMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return slangMap;

    }
}
