package begh.vismasyncservice.models;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private Integer number;
    private String name;
}
