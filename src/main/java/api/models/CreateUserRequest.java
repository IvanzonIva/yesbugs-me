package api.models;

import api.configs.Config;
import lombok.*;
import api.utils.GeneratingRule;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateUserRequest extends BaseModel {
    @GeneratingRule(regex = "^[A-Za-z0-9]{3,15}$")
    private String username;
    @GeneratingRule(regex = "^[A-Z]{3}[a-z]{3}\\d{3}!@$")
    private String password;
    @GeneratingRule(regex = "^USER$")
    private UserRole role;

    public static CreateUserRequest getAdmin() {
        return CreateUserRequest.builder().username(Config.getProperty("admin.username"))
                .password(Config.getProperty("admin.password")).build();
    }
}