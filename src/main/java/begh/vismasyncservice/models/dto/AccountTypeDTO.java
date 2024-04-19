package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountTypeDTO {
    @JsonProperty("Type")
    private int type;
    @JsonProperty("TypeDescription")
    private String typeDescription;
}
