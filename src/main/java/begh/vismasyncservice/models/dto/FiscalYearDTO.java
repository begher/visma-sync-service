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
public class FiscalYearDTO {
    @JsonProperty("Id")
    UUID id;
    @JsonProperty("StartDate")
    Date startDate;
    @JsonProperty("EndDate")
    Date endDate;
    @JsonProperty("IsLockedForAccounting")
    boolean isLockedForAccounting;
    @JsonProperty("BookkeepingMethod")
    int bookkeepingMethod;
}
