package api.models;

import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserResponse extends BaseModel{
    private String username;
    private UserRole role;
}
