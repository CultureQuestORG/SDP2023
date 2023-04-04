package ch.epfl.culturequest.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class autoCompletion {


    public static List<String> top5matches(String word, List<String> dictionary) {
        String lowerWord = word.toLowerCase();
        List<String> newDictionary = new ArrayList<>(dictionary);
        newDictionary.sort(Comparator.comparingInt(a -> {
            String lowerA= a.toLowerCase();
            return levenshteinDistance(lowerA, lowerWord) + (lowerA.startsWith(lowerWord) ? 0 : 10);

        }  ));
        return newDictionary.subList(0, Math.min(5, newDictionary.size()));

    }




    private static int levenshteinDistance(String a, String b) {
        int[][] distance = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= b.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= a.length(); i++)
            for (int j = 1; j <= b.length(); j++)
                distance[i][j] = Math.min(Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + ((a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1));

        return distance[a.length()][b.length()];
    }

}
