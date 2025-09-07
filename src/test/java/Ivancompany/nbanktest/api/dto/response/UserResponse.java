package Ivancompany.nbanktest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<AccountResponse> accounts;
}