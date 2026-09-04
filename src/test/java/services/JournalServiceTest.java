package services;

import api.response.WeatherResponse;
import dto_request.JournalEntryRequest;
import dto_response.JournalEntryResponse;
import entity.JournalEntry;
import entity.User;
import enums.Sentiment;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.JournalEntryRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalEntryRepository journalEntryRepository;
    @Mock
    private UserService userService;
    @Mock
    private SentimentService sentimentService;
    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private JournalService journalService;

    @Test
    void createEntryUsesSentimentWeatherAndMapper() {
        User user = User.builder()
                .id(new ObjectId())
                .username("ayush")
                .password("encoded")
                .journalEntries(new ArrayList<>())
                .build();

        JournalEntryRequest request = new JournalEntryRequest("My Day", "I am happy");
        WeatherResponse.Current current = new WeatherResponse.Current();
        current.setFeelslike(31);
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setCurrent(current);

        when(userService.findByUserName("ayush")).thenReturn(user);
        when(sentimentService.analyzeSentiment("I am happy")).thenReturn(Sentiment.POSITIVE);
        when(weatherService.getWeather("Mumbai")).thenReturn(weatherResponse);
        when(journalEntryRepository.save(any(JournalEntry.class))).thenAnswer(invocation -> {
            JournalEntry entry = invocation.getArgument(0);
            entry.setId(new ObjectId());
            return entry;
        });

        JournalEntryResponse response = journalService.createEntry(request, "ayush");

        assertEquals("My Day", response.getTitle());
        assertEquals("POSITIVE", response.getSentiment());
        assertEquals("Feels like 31", response.getWeather());
        verify(userService).saveNewEntry(eq(user));
    }
}
