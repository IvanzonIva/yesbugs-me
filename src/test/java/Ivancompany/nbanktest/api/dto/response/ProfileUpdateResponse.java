package Ivancompany.nbanktest.api.dto.response;

import lombok.Data;

@Data
public class ProfileUpdateResponse {
    private UserResponse customer;
    private String message;
}