package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VatCodeDTO {
    @JsonProperty("Id")
    String id;
    @JsonProperty("Code")
    String code;
    @JsonProperty("Description")
    String description;
    @JsonProperty("VatRate")
    double vatRate;
    @JsonProperty("OssCodeType")
    int ossCodeType;
    @JsonProperty("RelatedAccounts")
    RelatedAccounts relatedAccounts;
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelatedAccounts {
        @JsonProperty("AccountNumber1")
        String accountNumber1;
        @JsonProperty("AccountNumber2")
        String accountNumber2;
        @JsonProperty("AccountNumber3")
        String accountNumber3;
    }
}
