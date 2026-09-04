package entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private ObjectId id;

    @NonNull
    @Indexed(unique = true)
    @JsonAlias("userName")
    private String username;

    @Indexed(unique = true)
    private String email;

    @NonNull
    private String password;

    @JsonAlias("sentimentAnalaysis")
    private Boolean sentimentAnalysis;

    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @DBRef
    @Builder.Default
    private List<JournalEntry> journalEntries = new ArrayList<>();
}
