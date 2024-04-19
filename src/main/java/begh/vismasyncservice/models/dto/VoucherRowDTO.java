package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRowDTO {
    @JsonProperty("AccountNumber")
    int accountNumber;

    @JsonProperty("AccountDescription")
    String accountDescription;

    @JsonProperty("DebitAmount")
    double debitAmount;

    @JsonProperty("CreditAmount")
    double creditAmount;

    @JsonProperty("TransactionText")
    String transactionText;

    @JsonProperty("CostCenterItemId1")
    String costCenterItemId1;

    @JsonProperty("CostCenterItemId2")
    String costCenterItemId2;

    @JsonProperty("CostCenterItemId3")
    String costCenterItemId3;

    @JsonProperty("VatCodeId")
    String vatCodeId;

    @JsonProperty("VatCodeAndPercent")
    String vatCodeAndPercent;

    @JsonProperty("VatAmount")
    String vatAmount;

    @JsonProperty("Quantity")
    String quantity;

    @JsonProperty("Weight")
    String weight;

    @JsonProperty("DeliveryDate")
    String deliveryDate;

    @JsonProperty("HarvestYear")
    String harvestYear;

    @JsonProperty("ProjectId")
    String projectId;
}
