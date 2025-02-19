package net.aros.util;

import net.aros.brain.Command;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SimilarityFinder {

    public enum Status {
        GREAT(query -> null),
        OK(query -> "Вы хотите " + query + "?"),
        STRANGE(query -> "Как я понял, вы хотите " + query),
        BAD(query -> "Я вас плохо понимаю, но думаю что вы хотите " + query);

        private final Function<String, String> format;

        Status(Function<String, String> format) {
            this.format = format;
        }

        public String format(String description) {
            return format.apply(description);
        }
    }

    public record MatchResult(Command command, Status status) {
    }

    public Optional<MatchResult> findBestMatch(String query, List<Command> data) {
        if (query == null || query.isEmpty() || data == null || data.isEmpty()) {
            return Optional.empty();
        }

        String[] queryWords = preprocessQuery(query);
        if (queryWords.length == 0) return Optional.empty();

        MatchResult bestMatch = null;
        double maxSimilarity = -1;

        for (Command command : data) {
            double similarity = calculateSimilarity(queryWords, command.phrases());
            Status status = determineStatus(similarity);

            if (status != null && similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatch = new MatchResult(command, status);
            }
        }

        return Optional.ofNullable(bestMatch);
    }

    private String[] preprocessQuery(String query) {
        return query.toLowerCase()
                .replaceAll("[^а-яa-z\\s]", "")
                .trim()
                .split("\\s+");
    }

    private double calculateSimilarity(String[] queryWords, List<String> targetWords) {
        int matches = 0;
        for (String qWord : queryWords) {
            if (qWord.isEmpty()) continue;

            for (String tWord : targetWords) {
                String processedTWord = tWord.toLowerCase().replaceAll("[^а-яa-z]", "");
                int distance = levenshteinDistance(qWord, processedTWord);

                if (distance <= 2) {
                    matches++;
                    break;
                }
            }
        }
        return (double) matches / queryWords.length;
    }

    private Status determineStatus(double similarity) {
        if (similarity >= 1.0) return Status.GREAT;
        if (similarity >= 0.75) return Status.OK;
        if (similarity >= 0.5) return Status.STRANGE;
        if (similarity >= 0.25) return Status.BAD;
        return null;
    }

    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }
}
