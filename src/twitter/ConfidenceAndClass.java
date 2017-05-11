package twitter;

/**
 * Created by Zechen on 2016/12/2.
 */
public class ConfidenceAndClass {
    private double confidence;
    private int classValue;

    public ConfidenceAndClass(double confidence, int classValue) {
        this.confidence = confidence;
        this.classValue = classValue;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public int getClassValue() {
        return classValue;
    }

    public void setClassValue(int classValue) {
        this.classValue = classValue;
    }
}
