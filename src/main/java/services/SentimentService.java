package services;

import enums.Sentiment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class SentimentService {

    private final Set<String> positiveWords = new HashSet<>(Arrays.asList(
            "happy",
            "good",
            "great",
            "amazing",
            "excellent",
            "love",
            "wonderful",
            "excited"
    ));

    private final Set<String> negativeWords = new HashSet<>(Arrays.asList(
            "sad",
            "bad",
            "angry",
            "hate",
            "terrible",
            "worst",
            "upset",
            "lonely"
    ));

    public Sentiment analyzeSentiment(String content) {
        if (content == null || content.trim().isEmpty()) {
            return Sentiment.NEUTRAL;
        }

        String[] words = content
                .toLowerCase()
                .replaceAll("[^a-zA-Z\\s]", "")
                .split("\\s+");

        int pos = 0;
        int neg = 0;

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (positiveWords.contains(word)) {
                pos++;
            }
            if (negativeWords.contains(word)) {
                neg++;
            }
        }

        if (pos > neg) {
            return Sentiment.POSITIVE;
        }
        if (pos < neg) {
            return Sentiment.NEGATIVE;
        }
        return Sentiment.NEUTRAL;
    }
}
