package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDTO {
    @JsonProperty("Id")
    String id;
    @JsonProperty("VoucherDate")
    String voucherDate;
    @JsonProperty("VoucherText")
    String voucherText;
    @JsonProperty("Rows")
    List<VoucherRowDTO> rows;
}
