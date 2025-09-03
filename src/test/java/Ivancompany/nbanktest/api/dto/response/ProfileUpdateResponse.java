package Ivancompany.nbanktest.api.dto.response;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileUpdateResponse {
    private UserResponse customer;
    private String message;
}