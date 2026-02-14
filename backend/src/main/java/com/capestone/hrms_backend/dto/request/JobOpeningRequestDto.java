package com.capestone.hrms_backend.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class JobOpeningRequestDto {
    @NotNull(message = "Title cannot be null.")
    private String title;

    @NotNull(message = "description must be there.")
    private String description;

    @NotNull(message = "Creator is required")
    private Long hrId;

    @NotNull(message = "Communication email is necessary")
    @Email(message = "Entered email id is not valid")
    private String communicationEmail;

    @NotNull(message = "YOE is required")
    @Min(value = 0,message = "Years of experience cannot be negative")
    private Float experienceRequired;

    private MultipartFile file;

}
