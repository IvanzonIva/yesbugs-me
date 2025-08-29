package Ivancompany.nbanktest.api.dto.response;

import lombok.Data;

@Data
public class TransferResponse {
    private Long senderAccountId;
    private Long receiverAccountId;
    private Double amount;
    private String message;
}
