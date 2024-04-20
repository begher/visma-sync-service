package begh.vismasyncservice.health;

import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {
    @GetMapping()
    public ResponseEntity<Health> health(){
        return ResponseEntity.ok(
                Health.builder()
                        .name("customer-service")
                        .baseURL("https://api.imats.se/visma-auth-service/")
                        .build()
        );
    }
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Health{
        private String name;
        private String baseURL;
    }
}
