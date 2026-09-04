package services;

import Mapper.journalMapper;
import api.response.WeatherResponse;
import dto_request.JournalEntryRequest;
import dto_request.JournalUpdateRequest;
import dto_response.JournalEntryResponse;
import entity.JournalEntry;
import entity.User;
import enums.Sentiment;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import repository.JournalEntryRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final UserService userService;
    private final SentimentService sentimentService;
    private final WeatherService weatherService;

    public JournalService(JournalEntryRepository journalEntryRepository,
                          UserService userService,
                          SentimentService sentimentService,
                          WeatherService weatherService) {
        this.journalEntryRepository = journalEntryRepository;
        this.userService = userService;
        this.sentimentService = sentimentService;
        this.weatherService = weatherService;
    }

    public JournalEntryResponse createEntry(JournalEntryRequest request, String userName) {
        User user = requireUser(userName);

        Sentiment sentiment = sentimentService.analyzeSentiment(request.getContent());
        String weather = resolveWeather("Mumbai");

        JournalEntry entry = JournalEntry.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .date(LocalDateTime.now())
                .sentiment(sentiment)
                .weather(weather)
                .build();

        JournalEntry saved = journalEntryRepository.save(entry);

        if (user.getJournalEntries() == null) {
            user.setJournalEntries(new ArrayList<>());
        }
        user.getJournalEntries().add(saved);
        userService.saveNewEntry(user);

        return journalMapper.toResponse(saved);
    }

    public List<JournalEntryResponse> getEntriesForUser(String userName) {
        User user = requireUser(userName);
        if (user.getJournalEntries() == null) {
            return new ArrayList<>();
        }
        return user.getJournalEntries().stream()
                .filter(entry -> entry != null)
                .map(journalMapper::toResponse)
                .collect(Collectors.toList());
    }

    public JournalEntryResponse getEntryById(ObjectId id, String userName) {
        JournalEntry entry = findOwnedEntry(id, userName);
        return journalMapper.toResponse(entry);
    }

    public JournalEntryResponse updateEntry(ObjectId id, JournalUpdateRequest request, String userName) {
        JournalEntry old = findOwnedEntry(id, userName);

        if (request.getTitle() != null) {
            old.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            old.setContent(request.getContent());
            old.setSentiment(sentimentService.analyzeSentiment(request.getContent()));
        }
        old.setWeather(resolveWeather("Mumbai"));

        JournalEntry saved = journalEntryRepository.save(old);
        syncUserJournalEntry(userName, saved);
        return journalMapper.toResponse(saved);
    }

    public void deleteEntry(ObjectId id, String userName) {
        User user = requireUser(userName);
        if (user.getJournalEntries() == null) {
            throw new IllegalArgumentException("Journal entry not found");
        }

        boolean removed = user.getJournalEntries()
                .removeIf(entry -> entry != null && id.equals(entry.getId()));
        if (!removed) {
            throw new IllegalArgumentException("Journal entry not found");
        }

        userService.saveNewEntry(user);
        journalEntryRepository.deleteById(id);
    }

    private JournalEntry findOwnedEntry(ObjectId id, String userName) {
        User user = requireUser(userName);
        if (user.getJournalEntries() == null) {
            throw new IllegalArgumentException("Journal entry not found");
        }

        return user.getJournalEntries().stream()
                .filter(entry -> entry != null && id.equals(entry.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Journal entry not found"));
    }

    private void syncUserJournalEntry(String userName, JournalEntry updated) {
        User user = requireUser(userName);
        if (user.getJournalEntries() == null) {
            return;
        }
        for (int i = 0; i < user.getJournalEntries().size(); i++) {
            JournalEntry entry = user.getJournalEntries().get(i);
            if (entry != null && updated.getId().equals(entry.getId())) {
                user.getJournalEntries().set(i, updated);
                break;
            }
        }
        userService.saveNewEntry(user);
    }

    private User requireUser(String userName) {
        User user = userService.findByUserName(userName);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    private String resolveWeather(String city) {
        try {
            WeatherResponse weatherResponse = weatherService.getWeather(city);
            if (weatherResponse != null && weatherResponse.getCurrent() != null) {
                return "Feels like " + weatherResponse.getCurrent().getFeelslike();
            }
        } catch (Exception ignored) {
            // weather is optional enrichment
        }
        return "Weather unavailable";
    }
}
