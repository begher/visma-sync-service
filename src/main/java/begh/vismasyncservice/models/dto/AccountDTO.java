package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    @JsonProperty("Name")
    String name;
    @JsonProperty("Number")
    String number;
    @JsonProperty("VatCodeId")
    String vatCodeId;
    @JsonProperty("VatCodeDescription")
    String vatCodeDescription;
    @JsonProperty("FiscalYearId")
    String fiscalYearId;
    @JsonProperty("ReferenceCode")
    String referenceCode;
    @JsonProperty("Type")
    int type;
    @JsonProperty("TypeDescription")
    String typeDescription;
    @JsonProperty("ModifiedUtc")
    String modifiedUtc;
    @JsonProperty("CreatedUtc")
    String createdUtc;
    @JsonProperty("IsActive")
    boolean isActive;
    @JsonProperty("IsProjectAllowed")
    boolean isProjectAllowed;
    @JsonProperty("IsCostCenterAllowed")
    boolean isCostCenterAllowed;
    @JsonProperty("IsBlockedForManualBooking")
    boolean isBlockedForManualBooking;
}
