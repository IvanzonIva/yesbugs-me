package api.models;

import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse extends BaseModel {
    private long receiverAccountId;
    private long senderAccountId;
    private String message;
    private BigDecimal amount;

}
