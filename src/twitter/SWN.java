package twitter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Zechen on 2016/11/25.
 */
public class SWN {
    private String pathToSWN = "SentiWordNet_3.0.0_20130122.txt";
    private HashMap<String, Double> _dict;

    public SWN() {

        _dict = new HashMap<String, Double>();
        HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
        try {
            BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
            String line = "";
            while ((line = csv.readLine()) != null) {
                if (line.charAt(0) != '#') {

                    String[] data = line.split("\t");
                    //pos - neg
                    Double score = null;
                    String pos = data[0];
                    String posScore = data[2];
                    String negScore = data[3];
                    score = Double.parseDouble(posScore) - Double.parseDouble(negScore);
                    String[] words = data[4].split(" ");
                    for (String w : words) {
                        String[] w_n = w.split("#");
                        w_n[0] += "#" + pos;
                        int index = Integer.parseInt(w_n[1]) - 1;
                        if (_temp.containsKey(w_n[0])) {
                            Vector<Double> v = _temp.get(w_n[0]);
                            if (index > v.size())
                                for (int i = v.size(); i < index; i++)
                                    v.add(0.0);
                            v.add(index, score);
                            _temp.put(w_n[0], v);
                        } else {
                            Vector<Double> v = new Vector<Double>();
                            for (int i = 0; i < index; i++)
                                v.add(0.0);
                            v.add(index, score);
                            _temp.put(w_n[0], v);
                        }
                    }
                }
            }
            Set<String> temp = _temp.keySet();
            for (Iterator<String> iterator = temp.iterator(); iterator.hasNext(); ) {
                String word = (String) iterator.next();
                Vector<Double> v = _temp.get(word);
                double score = 0.0;
                double sum = 0.0;
                for (int i = 0; i < v.size(); i++)
                    score += ((double) 1 / (double) (i + 1)) * v.get(i);
                for (int i = 1; i <= v.size(); i++)
                    sum += (double) 1 / (double) i;
                score /= sum;
                /*if (score >= 0.75)
                    sent = "strong_positive";
                else if (score > 0.25 && score <= 0.5)
                    sent = "positive";
                else if (score > 0 && score >= 0.25)
                    sent = "weak_positive";
                else if (score < 0 && score >= -0.25)
                    sent = "weak_negative";
                else if (score < -0.25 && score >= -0.5)
                    sent = "negative";
                else if (score <= -0.75)
                    sent = "strong_negative";*/
                word = word.replaceAll("_", " ");
                _dict.put(word, score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Double extract(String word) {
        Double total = (double) 0;
        if (_dict.get(word + "#n") != null)
            total = _dict.get(word + "#n") + total;
        if (_dict.get(word + "#a") != null)
            total = _dict.get(word + "#a") + total;
        if (_dict.get(word + "#r") != null)
            total = _dict.get(word + "#r") + total;
        if (_dict.get(word + "#v") != null)
            total = _dict.get(word + "#v") + total;
        return total;
    }

    public double classifytweet(String twit) {
        String[] words = twit.split("\\s+");
        double totalScore = 0;
        for (String word : words) {
            word = word.replaceAll("([^a-zA-Z\\s])", "");

            if (this.extract(word) == 0)
                continue;
            totalScore += this.extract(word);

        }
        return totalScore;
    }


}
