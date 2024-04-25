package begh.vismasyncservice.models.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyncDTO {
    private boolean syncNeeded;
    private int countDifference;
    private int vismaTotalResources;
    private int databaseResources;
}
