package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse extends BaseModel {
    private long receiverAccountId;
    private long senderAccountId;
    private String message;
    private double amount;

}
