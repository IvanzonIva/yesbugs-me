package Ivancompany.nbanktest.api.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferRequest {
    private Long senderAccountId;
    private Long receiverAccountId;
    private Double amount;
}