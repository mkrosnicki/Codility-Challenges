import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private final static int BEGIN = 0;
    private final static int MIDDLE = 1;
    private final static int END = 2;
    private final static int WHOLE = 3;


    public int solution(final String[] words) {

        Map<Character, SubstringsSet> map = new HashMap<>();

        for (int wordIndex = 0; wordIndex < words.length; wordIndex++) {

            String word = words[wordIndex];
            int wordSize = word.length();

            for (int letterIndex = 0; letterIndex < wordSize; letterIndex++) {

                char c = word.charAt(letterIndex);
                int count = 1;
                int substringType = (letterIndex == 0) ? BEGIN : MIDDLE;

                while ((letterIndex < wordSize - 1) && word.charAt(letterIndex) == word.charAt(letterIndex + 1)) {
                    letterIndex++;
                    count++;
                }

                if (letterIndex == wordSize - 1) {
                    if (substringType == BEGIN) {
                        substringType = WHOLE;
                    } else {
                        substringType = END;
                    }
                }

                if (!map.containsKey(c)) {
                    map.put(c, new SubstringsSet());
                }
                map.get(c).add(new Substring(count, substringType, wordIndex));
            }

        }

        return calculateLongest(map);
    }

    private int calculateLongest(final Map<Character, SubstringsSet> map) {

        int longestGlobal = 0;

        for (char c : map.keySet()) {

            final SubstringsSet substringsSet = map.get(c);

            int longest = 0;

            longest += calculateLongestPrefixAndSuffixPair(substringsSet);
            longest += substringsSet.wholesSum;

            longest = Math.max(substringsSet.longestMiddle, longest);
            longestGlobal = Math.max(longest, longestGlobal);
        }

        return longestGlobal;
    }


    private int calculateLongestPrefixAndSuffixPair(final SubstringsSet substringsSet) {

        int bestResult;

        if (substringsSet.begins.isEmpty() && substringsSet.ends.isEmpty()) {
            return 0;
        } else if (substringsSet.begins.isEmpty()) {
            bestResult = findLongest(substringsSet.ends);
        } else if (substringsSet.ends.isEmpty()) {
            bestResult = findLongest(substringsSet.begins);
        } else if (substringsSet.begins.size() == 1 && substringsSet.ends.size() == 1) {
            final Substring longestBegin = substringsSet.begins.get(0);
            final Substring longestEnd = substringsSet.ends.get(0);
            if (longestBegin.wordIndex == longestEnd.wordIndex) {
                bestResult = Math.max(longestBegin.length, longestEnd.length);
            } else {
                bestResult = longestBegin.length + longestEnd.length;
            }
        } else {

            int longest = 0;
            for (Substring begin : substringsSet.begins) {
                for (Substring end : substringsSet.ends) {
                    int sum = begin.length;
                    if (end.wordIndex != begin.wordIndex) {
                        sum += end.length;
                    }
                    if (sum > longest) {
                        longest = sum;
                    }
                }
            }
            bestResult = longest;
        }
        return bestResult;
    }

    private int findLongest(final List<Substring> substrings) {
        int longest = 0;
        for (Substring substring : substrings) {
            longest = Math.max(substring.length, longest);
        }
        return longest;
    }


    private class SubstringsSet {

        private List<Substring> begins = new ArrayList<>(2);
        private List<Substring> ends = new ArrayList<>(2);
        private int longestMiddle = 0;
        private int wholesSum = 0;

        public void add(final Substring substring) {

            final int type = substring.type;

            if (type == WHOLE) {
                wholesSum += substring.length;
            } else if (type == MIDDLE) {
                if (substring.length > longestMiddle) {
                    longestMiddle = substring.length;
                }
            } else if (type == BEGIN) {
                addTo(begins, substring);
            } else {
                addTo(ends, substring);
            }

        }

        private void addTo(final List<Substring> substrings, final Substring substring) {

            if (substrings.size() < 2) {
                substrings.add(substring);
            } else {

                final Substring longest1 = substrings.get(0);
                final Substring longest2 = substrings.get(1);

                if (longest1.length > longest2.length) {
                    if (substring.length > longest2.length) {
                        substrings.remove(longest2);
                        substrings.add(substring);
                    }
                } else {
                    if (substring.length > longest1.length) {
                        substrings.remove(longest1);
                        substrings.add(substring);
                    }
                }
            }
        }

    }

    private class Substring {

        private int length;

        private int type;

        private int wordIndex;


        public Substring(int length, int substringType, final int wordIndex) {
            this.length = length;
            this.type = substringType;
            this.wordIndex = wordIndex;
        }

    }


}