package Ivancompany.nbanktest.api.dto.response;

import groovy.beans.Bindable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Bindable
public class LoginResponse {
    private String username;
    private String password;
    private String role;
    private String token;
}
