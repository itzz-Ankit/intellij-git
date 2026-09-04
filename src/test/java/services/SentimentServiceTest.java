package services;

import enums.Sentiment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentimentServiceTest {

    private SentimentService sentimentService;

    @BeforeEach
    void setUp() {
        sentimentService = new SentimentService();
    }

    @Test
    void positiveContentReturnsPositive() {
        assertEquals(Sentiment.POSITIVE, sentimentService.analyzeSentiment("I am happy and love this"));
    }

    @Test
    void negativeContentReturnsNegative() {
        assertEquals(Sentiment.NEGATIVE, sentimentService.analyzeSentiment("I feel sad and lonely"));
    }

    @Test
    void emptyOrNeutralReturnsNeutral() {
        assertEquals(Sentiment.NEUTRAL, sentimentService.analyzeSentiment(""));
        assertEquals(Sentiment.NEUTRAL, sentimentService.analyzeSentiment("today is a normal day"));
    }
}
