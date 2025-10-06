package api.models;

import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequest extends BaseModel{
    private String username;
    private String password;
}
