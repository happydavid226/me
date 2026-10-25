package rw.happydavid.portfolio.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 10, max = 5000) String message,
        @Size(max = 200) String website) {
    public ContactRequest {
        name = name == null ? null : name.trim();
        email = email == null ? null : email.trim();
        message = message == null ? null : message.trim();
        website = website == null ? null : website.trim();
    }
}
