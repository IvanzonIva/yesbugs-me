package api.models;

import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest extends BaseModel {
    private long id;
    private BigDecimal balance;

}