package Ivancompany.nbanktest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Double balance;
    private List<TransactionResponse> transactions;
}
