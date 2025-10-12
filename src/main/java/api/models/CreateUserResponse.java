package api.models;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponse extends BaseModel{
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private List<CreateAccountResponse> accounts;
}
