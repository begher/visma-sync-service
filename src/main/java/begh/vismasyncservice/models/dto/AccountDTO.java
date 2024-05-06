package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    @JsonProperty("Name")
    String name;
    @JsonProperty("Number")
    int number;
    @JsonProperty("VatCodeId")
    UUID vatCodeId;
    @JsonProperty("VatCodeDescription")
    String vatCodeDescription;
    @JsonProperty("FiscalYearId")
    UUID fiscalYearId;
    @JsonProperty("ReferenceCode")
    String referenceCode;
    @JsonProperty("Type")
    int type;
    @JsonProperty("TypeDescription")
    String typeDescription;
    @JsonProperty("ModifiedUtc")
    Date modifiedUtc;
    @JsonProperty("CreatedUtc")
    Date createdUtc;
    @JsonProperty("IsActive")
    boolean isActive;
    @JsonProperty("IsProjectAllowed")
    boolean isProjectAllowed;
    @JsonProperty("IsCostCenterAllowed")
    boolean isCostCenterAllowed;
    @JsonProperty("IsBlockedForManualBooking")
    boolean isBlockedForManualBooking;
}
