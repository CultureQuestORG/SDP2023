package ch.epfl.culturequest.utils;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

import java.util.AbstractMap.SimpleEntry;

/**
 * A class that provides methods to perform auto-completion on a dictionary.
 */
public class AutoComplete {
    private static final int PENALTY = 1000;


    /**
     * Returns the top n matches for a given word in a dictionary using the Levenshtein distance.
     * The complexity of this algorithm is O(a*b*m*log n), where:
     *  -a is the length of the input word,
     *  -b is the length of the longest word in the dictionary.
     *  -m is the size of the dictionary,
     *  -n is the number of top matches to return.
     * @param word the word to match
     * @param dictionary the dictionary to match against
     * @param n the number of top matches to return
     * @return the top 5 matches for the word in the dictionary
     */
    public static List<String> topNMatches(String word, Set<String> dictionary,int n) {
        // Convert the input word to lowercase
        String lowerWord = word.toLowerCase();

        // Create a priority queue to store the top n matches along with their Levenshtein distance
        PriorityQueue<SimpleEntry<String, Integer>> topMatches = new PriorityQueue<>(
                Comparator.comparingInt(SimpleEntry<String, Integer>::getValue).reversed());


        for (String entry : dictionary) {
            String lowerEntry = entry.toLowerCase();
            // Calculate the Levenshtein distance, with penalty if the entry doesn't start with the input word
            int distance = levenshteinDistance(lowerEntry, lowerWord) + (lowerEntry.startsWith(lowerWord) ? 0 : PENALTY);

            // Insert the entry and its distance into the priority queue
            topMatches.offer(new SimpleEntry<>(entry, distance));
            // Remove the entry with the highest distance if the priority queue size is greater than 5
            if (topMatches.size() > n) {
                topMatches.poll();
            }
        }

        List<String> result = new ArrayList<>(topMatches.size());

        // Add entries from the priority queue to the result list in reverse order of their distance
        while (!topMatches.isEmpty()) {
            result.add(Objects.requireNonNull(topMatches.poll()).getKey());
        }
        // Reverse the result list to have entries in ascending order of their distance
        Collections.reverse(result);

        return result;
    }


    /**
     * Computes the Levenshtein distance between two strings.
     * <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">https://en.wikipedia.org/wiki/Levenshtein_distance</a>
     * @param a first string
     * @param b second string
     * @return the Levenshtein distance between a and b
     */
    private static int levenshteinDistance(String a, String b) {
        int[][] distance = new int[a.length() + 1][b.length() + 1];

        // initialize the default values
        for (int i = 0; i <= a.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= b.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= a.length(); i++)
            for (int j = 1; j <= b.length(); j++)
                // compute the distance
                distance[i][j] = Math.min(Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + ((a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1));

        return distance[a.length()][b.length()];
    }

}
