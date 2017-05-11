package twitter;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zechen on 2016/11/4.
 */
public class DetectCharset {


    public static String detect(String text) {
        // (1)
        byte[] buf = text.getBytes();
        UniversalDetector detector = new UniversalDetector(null);
        InputStream fis = new ByteArrayInputStream(buf);
        // (2)
        int nread;
        try {
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
// (3)
        detector.dataEnd();

        // (4)
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding != null) {
            return encoding;
        } else {
            return "null";
        }

// (5)


    }
}
