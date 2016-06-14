package net.stoerr.mastersystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class NumberMemogenerator {

    private Pattern Word = Pattern.compile("[a-zA-ZäöüÄÖÜß]+");

    private Pattern R0 = Pattern.compile("(x+|z+|s+(?!ch)|ß+)");

    private Pattern R1 = Pattern.compile("([td]+)");

    private Pattern R2 = Pattern.compile("([nñ]+)");

    private Pattern R3 = Pattern.compile("(m+)");

    private Pattern R4 = Pattern.compile("(r+)");

    private Pattern R5 = Pattern.compile("(l+)");

    private Pattern R6 = Pattern.compile("(sch|ch|j+|g+)");

    private Pattern R7 = Pattern.compile("(k+|ck|g+|c+(?!h)|c+(?!h)k+|q+|x+)");

    private Pattern R8 = Pattern.compile("(f+|v+|w+|ph)");

    private Pattern R9 = Pattern.compile("([pb]+)");

    private Pattern AnyDigit = Pattern.compile("(" + R0 + "|" + R1 + "|" + R2 + "|"
            + R3 + "|" + R4 + "|" + R5 + "|" + R6 + "|" + R7 + "|" + R8 + "|"
            + R9 + ")");

    private Pattern Ignored = Pattern.compile("([aeiouäöüAEUIOÄÖÜyhéâêà.' -]+)");

    private Pattern[] Digits = new Pattern[]{R0, R1, R2, R3, R4, R5, R6, R7, R8, R9};

    private Pattern MatchableWord = Pattern.compile("(" + Ignored + "|" + AnyDigit + ")*", Pattern.CASE_INSENSITIVE);

    private Pattern JSonWord = Pattern.compile("\"" + MatchableWord + "\",", Pattern.CASE_INSENSITIVE);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        NumberMemogenerator g = new NumberMemogenerator();
        /* Files.lines(Paths.get("html", "allde.json"), Charset.forName("ISO8859-1"))
                .filter((l) -> g.JSonWord.matcher(l).matches()).limit(10).forEach(System.out::println); */
        System.out.println("\n\n\nNot Matching:\n");
        Files.lines(Paths.get("html", "allen.json"), Charset.forName("ISO8859-1"))
                .filter((l) -> !g.JSonWord.matcher(l).matches()).forEach(System.out::println);
    }

    public List<String> getWords(String number) throws IOException {
        Pattern pat = numberPattern(number);
        List<String> words = getWordList();
        List<String> res = new ArrayList<String>();
        for (Iterator<String> iter = words.iterator(); iter.hasNext(); ) {
            String w = (String) iter.next();
            if (pat.matcher(w).matches()) {
                res.add(w);
            }
        }
        return res;
    }

    public void printWords(String number) throws IOException {
        Pattern pat = numberPattern(number);
        System.out.println("number " + number + " has pattern " + pat);
        List<String> words = getWordList();
        for (Iterator<String> iter = words.iterator(); iter.hasNext(); ) {
            String w = (String) iter.next();
            if (pat.matcher(w).matches()) {
                System.out.println(number + "=\t" + w + ", decoded: "
                        + toNumbers(w));
            }
        }
    }

    Pattern numberPattern(String number) {
        String regex = "(?i)(" + Ignored + ")?";
        for (int i = 0; i < number.length(); ++i) {
            int z = Integer.parseInt(String.valueOf(number.charAt(i)));
            regex = regex + Digits[z] + "(" + Ignored + ")?";
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
