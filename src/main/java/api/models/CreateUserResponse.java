package api.models;

import lombok.*;

import java.util.List;
import java.util.Objects;

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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateUserResponse that = (CreateUserResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, role, accounts);
    }
}
