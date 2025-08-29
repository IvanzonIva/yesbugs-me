package Ivancompany.nbanktest.api.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Double balance;
    private List<TransactionResponse> transactions;
}

@Data
class TransactionResponse {
    private Long id;
    private Double amount;
    private String type;
    private String timestamp;
    private Long relatedAccountId;
}