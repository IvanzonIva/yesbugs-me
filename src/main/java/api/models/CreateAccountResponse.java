package api.models;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountResponse extends BaseModel {
    private long id;
    private String accountNumber;
    private BigDecimal balance;
    private List<TransactionResponse> transactions;
}