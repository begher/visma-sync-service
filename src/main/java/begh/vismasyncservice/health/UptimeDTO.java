package begh.vismasyncservice.health;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UptimeDTO {
    private String startedAt;
    private String upTime;
    private Long upTimeInSeconds;
}
