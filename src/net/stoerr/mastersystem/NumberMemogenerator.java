package net.stoerr.mastersystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberMemogenerator {

    /** Logger for NumberMemogenerator */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
            .getLog(NumberMemogenerator.class);

    Pattern Word = Pattern.compile("[a-zA-ZäöüÄÖÜß]+");

    private Pattern R0 = Pattern.compile("(x+|z+|s+(?!ch)|ß+)");

    private Pattern R1 = Pattern.compile("([td]+)");

    private Pattern R2 = Pattern.compile("(n+)");

    private Pattern R3 = Pattern.compile("(m+)");

    private Pattern R4 = Pattern.compile("(r+)");

    private Pattern R5 = Pattern.compile("(l+)");

    private Pattern R6 = Pattern.compile("(sch|ch|j+|g+)");

    private Pattern R7 = Pattern.compile("(k+|ck|g+|c+(?!h)|c+(?!h)k+|q+|x+)");

    private Pattern R8 = Pattern.compile("(f+|v+|w+|ph)");

    private Pattern R9 = Pattern.compile("([pb]+)");

    Pattern AnyDigit = Pattern.compile("(" + R0 + "|" + R1 + "|" + R2 + "|"
            + R3 + "|" + R4 + "|" + R5 + "|" + R6 + "|" + R7 + "|" + R8 + "|"
            + R9 + ")");

    Pattern Vocal = Pattern.compile("([aeiouäöüyh]+)");

    private Pattern[] Digits = new Pattern[] { R0, R1, R2, R3, R4, R5, R6, R7, R8, R9 };

    Pattern MatchableWord = Pattern.compile("(" + Vocal + ")?(" + Vocal
            + AnyDigit + ")*");

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public List<String> getWords(String number) throws IOException {
        Pattern pat = numberPattern(number);
        List<String> words = getWordList();
        List<String> res = new ArrayList<String>();
        for (Iterator<String> iter = words.iterator(); iter.hasNext();) {
            String w = (String) iter.next();
            if (pat.matcher(w).matches()) {
                res.add(w);
            }
        }
        return res;
    }

    public void printWords(String number) throws IOException {
        Pattern pat = numberPattern(number);
        LOG.debug("number " + number + " has pattern " + pat);
        List<String> words = getWordList();
        for (Iterator<String> iter = words.iterator(); iter.hasNext();) {
            String w = (String) iter.next();
            if (pat.matcher(w).matches()) {
                System.out.println(number + "=\t" + w + ", decoded: "
                        + toNumbers(w));
            }
        }
    }

    Pattern numberPattern(String number) {
        String regex = "(?i)(" + Vocal + ")?";
        for (int i = 0; i < number.length(); ++i) {
            int z = Integer.parseInt(String.valueOf(number.charAt(i)));
            regex = regex + Digits[z] + "(" + Vocal + ")?";
        }
        Pattern pat = Pattern.compile(regex + "?");
        return pat;
    }

    BufferedReader createWordReader() throws IOException {
        InputStream wordS = getClass().getResourceAsStream(
                "/search/deutsch.txt");
        assert null != wordS;
        return new BufferedReader(new InputStreamReader(wordS, "ISO-8859-1"));
    }

    private List<String> words = null;

    List<String> getWordList() throws IOException {
        if (null == words) {
            ArrayList<String> res;
            res = new ArrayList<String>();
            BufferedReader r = createWordReader();
            String s;
            while (null != (s = r.readLine())) {
                s = s.trim();
                if (Word.matcher(s).matches()) {
                    res.add(s.toLowerCase(Locale.GERMANY));
                }
            }
            r.close();
            Set<String> uniq = new TreeSet<String>();
            uniq.addAll(res);
            res.clear();
            res.addAll(uniq);
            words = res;
        }
        return words;
    }

    /**
     * Liefert alle Varianten das Wort als Zahl zu dekodieren.
     *
     * @param word
     * @return
     */
    public List<String> toNumbers(String word) {
        Matcher m = AnyDigit.matcher(word);
        if (m.find()) {
            List<String> result = new ArrayList<String>();
            String match = m.group();
            int e = m.end();
            String rest = word.substring(e);
            List<String> restdec = toNumbers(rest);
            for (int i = 0; i <= 9; ++i) {
                if (Digits[i].matcher(match).matches()) {
                    for (String v : restdec) {
                        result.add(i + v);
                    }
                }
            }
            return result;
        } else {
            return Collections.singletonList("");
        }
    }

}
