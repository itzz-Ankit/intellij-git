package Mapper;

import dto_response.JournalEntryResponse;
import entity.JournalEntry;

public class journalMapper {

    private journalMapper() {
    }

    public static JournalEntryResponse toResponse(JournalEntry journalEntry) {
        return JournalEntryResponse.builder()
                .id(journalEntry.getId() == null ? null : journalEntry.getId().toString())
                .title(journalEntry.getTitle())
                .content(journalEntry.getContent())
                .sentiment(journalEntry.getSentiment() == null
                        ? null
                        : journalEntry.getSentiment().name())
                .weather(journalEntry.getWeather())
                .build();
    }
}
