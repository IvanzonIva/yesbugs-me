package api.models;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositResponse extends BaseModel {
    private long id;
    private String accountNumber;
    private double balance;
    private List<TransactionResponse> transactions;
}