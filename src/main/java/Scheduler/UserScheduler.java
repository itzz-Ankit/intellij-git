package Scheduler;

import cache.AppCache;
import entity.JournalEntry;
import entity.User;
import enums.Sentiment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import repository.UserRepositoryImpl;
import services.EmailService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    private final EmailService emailService;
    private final UserRepositoryImpl userRepository;
    private final AppCache appCache;

    public UserScheduler(EmailService emailService,
                         UserRepositoryImpl userRepository,
                         AppCache appCache) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.appCache = appCache;
    }

    @Scheduled(cron = "0 0 9 * * MON")
    public void fetchUsersAndSendSaMail() {
        List<User> users = userRepository.getUserForSA();
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        for (User user : users) {
            if (user.getEmail() == null || user.getJournalEntries() == null) {
                continue;
            }

            List<JournalEntry> recentEntries = user.getJournalEntries().stream()
                    .filter(entry -> entry != null
                            && entry.getDate() != null
                            && entry.getDate().isAfter(sevenDaysAgo))
                    .collect(Collectors.toList());

            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (JournalEntry journalEntry : recentEntries) {
                Sentiment sentiment = journalEntry.getSentiment();
                if (sentiment != null) {
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
                }
            }

            Sentiment mostFrequent = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequent = entry.getKey();
                }
            }

            if (mostFrequent != null) {
                emailService.sendEmail(
                        user.getEmail(),
                        "MindVault weekly sentiment",
                        "Your most frequent sentiment this week: " + mostFrequent.name()
                );
            }
        }
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void clearAppCache() {
        appCache.init();
    }
}
