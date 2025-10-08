package api.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeNameRequest extends BaseModel {
    private String name;
}