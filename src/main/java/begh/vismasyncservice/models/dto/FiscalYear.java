package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FiscalYear {
    @JsonProperty("Id")
    String id;

    @JsonProperty("StartDate")
    String startDate;

    @JsonProperty("EndDate")
    String endDate;

    @JsonProperty("IsLockedForAccounting")
    boolean isLockedForAccounting;

    @JsonProperty("BookkeepingMethod")
    int bookkeepingMethod;
}
