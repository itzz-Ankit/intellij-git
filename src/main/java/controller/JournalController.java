package controller;

import dto_request.JournalEntryRequest;
import dto_request.JournalUpdateRequest;
import dto_response.ApiResponse;
import dto_response.JournalEntryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.JournalService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/journal")
@Tag(name = "Journal APIs")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalEntryResponse>>> getMyEntries(Authentication authentication) {
        List<JournalEntryResponse> entries = journalService.getEntriesForUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.<List<JournalEntryResponse>>builder()
                .success(true)
                .message("Journal entries fetched")
                .data(entries)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JournalEntryResponse>> createEntry(
            @Valid @RequestBody JournalEntryRequest request,
            Authentication authentication) {

        JournalEntryResponse response = journalService.createEntry(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<JournalEntryResponse>builder()
                        .success(true)
                        .message("Journal entry created")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalEntryResponse>> getById(
            @PathVariable String id,
            Authentication authentication) {

        ObjectId objectId = parseObjectId(id);
        JournalEntryResponse response = journalService.getEntryById(objectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.<JournalEntryResponse>builder()
                .success(true)
                .message("Journal entry fetched")
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalEntryResponse>> updateEntry(
            @PathVariable String id,
            @Valid @RequestBody JournalUpdateRequest request,
            Authentication authentication) {

        ObjectId objectId = parseObjectId(id);
        JournalEntryResponse response = journalService.updateEntry(objectId, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.<JournalEntryResponse>builder()
                .success(true)
                .message("Journal entry updated")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEntry(
            @PathVariable String id,
            Authentication authentication) {

        ObjectId objectId = parseObjectId(id);
        journalService.deleteEntry(objectId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Journal entry deleted")
                .build());
    }

    private ObjectId parseObjectId(String id) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid journal id");
        }
        return new ObjectId(id);
    }
}
