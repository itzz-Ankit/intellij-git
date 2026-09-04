package dto_response;

//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;

import lombok.* ;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryResponse {

    private String id;
    private String title;
    private String content;
    private String sentiment;
    private String weather ;

}
