package Ivancompany.nbanktest.api.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    private String username;
    private String password;
    private String role;
}