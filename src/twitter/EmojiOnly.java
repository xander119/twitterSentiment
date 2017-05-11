package twitter;

import java.io.*;
import java.util.Date;

/**
 * Created by Zechen on 2016/11/10.
 */
public class EmojiOnly {


    public static void main(String args[]) throws IOException, InterruptedException {


        long startTime = System.currentTimeMillis();
        System.out.println("Start Time : " + new Date());


/*

        TweetPreProcessorTwoExpand tweetPreProcessorTwoExpand = new TweetPreProcessorTwoExpand();
        tweetPreProcessorTwoExpand.onlyEmojis();
*/

        CombinedClassifierLex combinedClassifiersLex = new CombinedClassifierLex();
        String numberOfTweets = "2151";
        String defaultPathname = "result/RunD3ResultLex10fold(onlyEmojiReplace).txt";
        String numTweet = "600";


        File result = new File(defaultPathname);
        String sql = "(SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_only_expanded as c where c.labelled_sentiment = 'negative' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_only_expanded as c where c.labelled_sentiment = 'positive' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")" +
                "union all (SELECT c.text,c.labelled_sentiment from tweet_set_two_processed2emojis_only_expanded as c where c.labelled_sentiment = 'neutral' order by c.airline_sentiment_confidence DESC limit " + numTweet + ")";

        String title = numTweet + " Tweets cross valid processed with Lexicon Classifier tweet_set_two_processed2emojis_only_expanded \n";
        String tweet_set_two_processed1 = combinedClassifiersLex.classifyTweets(result, title, sql);
        Thread.sleep(3000);
        System.out.println("Complete");

        FileWriter fw = new FileWriter(result, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println("Results produced from with Lexicon ensemble classifier.AVERAGE_RULE");

        pw.println("Preprocessed Baseline: " + numTweet + " No.tweets: " + tweet_set_two_processed1);

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
