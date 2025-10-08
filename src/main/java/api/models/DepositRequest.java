package api.models;

import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest extends BaseModel {
    private long id;
    private double balance;

}