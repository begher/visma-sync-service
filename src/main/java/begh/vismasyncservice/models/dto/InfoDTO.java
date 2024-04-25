package begh.vismasyncservice.models.dto;

import begh.vismasyncservice.models.CompletedDTO;
import begh.vismasyncservice.models.ErrorPageDTO;
import begh.vismasyncservice.models.StartDTO;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoDTO {
    private StartDTO start;
    private ErrorPageDTO error;
    private CompletedDTO completed;
}
