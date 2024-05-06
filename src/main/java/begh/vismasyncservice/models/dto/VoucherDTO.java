package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDTO {
    @JsonProperty("Id")
    UUID id;
    @JsonProperty("VoucherDate")
    Date voucherDate;
    @JsonProperty("VoucherText")
    String voucherText;
    @JsonProperty("Rows")
    List<VoucherRowDTO> rows;
}
