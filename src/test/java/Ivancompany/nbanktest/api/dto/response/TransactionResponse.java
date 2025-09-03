package Ivancompany.nbanktest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse {
    private Long id;
    private Double amount;
    private String type;
    private String timestamp;
    private Long relatedAccountId;
}
