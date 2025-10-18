package api.models;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositResponse extends BaseModel {
    private long id;
    private String accountNumber;
    private BigDecimal balance;
    private List<TransactionResponse> transactions;
}