package twitter;

/**
 * Created by Zechen on 2016/11/25.
 */
public class SWNTest {


    public static void main(String[] args) {
        String sentence = "Thanks to the two lovely ladies at the Delta Help desk in Atlanta who re-routed me and my bag to New Orleans last wednesday Sept 14th. Saved me a 5 hour wait and got me to N O 4 hours early. ";
        SWN swn = new SWN();
        System.out.println(swn.classifytweet(sentence));
    }


}
