package Ivancompany.nbanktest.api.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<AccountResponse> accounts;
}