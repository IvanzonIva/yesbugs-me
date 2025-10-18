package api.models;

import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest extends BaseModel {
    private long senderAccountId;
    private long receiverAccountId;
    private BigDecimal amount;
}
