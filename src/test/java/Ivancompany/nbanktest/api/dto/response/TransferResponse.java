package Ivancompany.nbanktest.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferResponse {
    private Long senderAccountId;
    private Long receiverAccountId;
    private Double amount;
    private String message;
}
