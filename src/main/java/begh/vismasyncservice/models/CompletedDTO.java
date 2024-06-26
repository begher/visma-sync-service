package begh.vismasyncservice.models;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletedDTO {
    private Integer duration;
    @Column(name = "data_type_id")
    private LocalDateTime createdAt;
}
