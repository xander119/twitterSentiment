package twitter;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Zechen on 2016/11/10.
 */
public class ARunAll {


    public static void main(String args[]) throws IOException, InterruptedException {
        //todo we are using tweet_set_two_processed1 for basedline
        //todo we are using tweet_set_two_processed2airportcode_expanded for Dataset B part1 expanded airportcode
        //todo we are using tweet_set_two_processed2slang_expanded for Dataset B part2 expanded slang
        //todo we are using tweet_set_two_processed2hashtags_expanded for Dataset B part2 expanded hashtags
        //todo we are using tweet_set_two_processed2emojis_expanded for Dataset B part2 replaced tweet emojis
        //todo we are using tweet_set_two_processed3_expanded for Dataset B part2 removed tweet emojis


        long startTime = System.currentTimeMillis();
        System.out.println("Start Time : " + new Date());

        String dataFileOne = "G:/1Data/TweetsCollectedMainOne.xlsx";
        String dataFileTwo = "G:/1Data/TweetsCrowdflowerMainTwo.xlsx";

      /*  LoadExcelToDB excelToDB = new LoadExcelToDB();
        excelToDB.getSecondSetTweetsListFromExcel(dataFileTwo);

        TweetPreProcessorTwo tweetPreprocessor = new TweetPreProcessorTwo();
        tweetPreprocessor.preProcessingSaveToP1();*/
       /* tweetPreprocessor.replaceEmojisFromP1SaveToP2();
        tweetPreprocessor.removeEmojisFromP1SaveToP3();

        TweetPreProcessorTwoExpand tweetPreProcessorTwoExpand = new TweetPreProcessorTwoExpand();
        tweetPreProcessorTwoExpand.saveToProcessed2airportcode();
        tweetPreProcessorTwoExpand.airportCodeSlang();
        tweetPreProcessorTwoExpand.airportCodeSlangHashTags();
        tweetPreProcessorTwoExpand.airportCodeSlangHashtagsEmojis();
        tweetPreProcessorTwoExpand.removeEmojisFromP1SaveToP3();*/
        //Analysis
        //where c.labelled_sentiment = 'positive' or c.labelled_sentiment = 'negative'

        String numberOfTweets = "2151";
        CombinedClassifiers.main(null);

        CombinedClassifierLex.main(null);

       /* CombinedClassifiers.main(new String[]{"result/RunD1_AVERAGE_RULE_Result(Expanded).txt", numberOfTweets});

        CombinedClassifierLex.main(new String[]{"result/RunD1_AVERAGE_RULE_ResultLex(Expanded,Filtered).txt", numberOfTweets});
*/
        long endTime = System.currentTimeMillis();
        long totalTime = ((endTime - startTime) / 1000) / 60;
        long totalHours = totalTime / 60;
        System.out.println("End Time : " + new Date());
        System.out.println("Duration: " + totalTime + " mins");
        System.out.println("Duration: " + totalHours + " hours");
    }

//     multiple emojis that replaced by words/ /change weight of Aliases

//      break words such as notHappy
// !!!!!  Analysis lexicon creation weighting of words
//
//    using reviews
//    wendesday email the progress on report.
//

}
