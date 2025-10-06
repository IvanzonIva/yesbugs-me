package api.models;

import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeNameResponse extends BaseModel{
    private CreateUserResponse customer;
    private String message;
}