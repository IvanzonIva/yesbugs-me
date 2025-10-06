package api.models;

import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeNameRequest extends BaseModel {
    private String name;
}