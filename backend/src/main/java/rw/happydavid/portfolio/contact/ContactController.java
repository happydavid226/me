package rw.happydavid.portfolio.contact;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactService contactService;
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> submit(@Valid @RequestBody ContactRequest body) {
        if (body.website() != null && !body.website().isBlank()) {
            return ResponseEntity.accepted().body(new ApiResponse("Thanks."));
        }
        contactService.send(body);
        return ResponseEntity.accepted().body(new ApiResponse("Your message was accepted by the mail server."));
    }
}
