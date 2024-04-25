package begh.vismasyncservice.visma;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Setter
@RequiredArgsConstructor
public class VismaTokenService {

    private final WebClient webClient;
    private long refreshed = 0;
    private TokenDTO token = TokenDTO.builder()
            .accessToken("")
            .expiresIn(3600)
            .tokenType("")
            .refreshToken("")
            .build();

    public TokenDTO getToken() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - refreshed) / 1000 > 3000) {
            this.token = fetchNewToken();
            this.refreshed = currentTime;
        }
        return this.token;
    }

    private TokenDTO fetchNewToken() {
        return webClient.get()
                .uri("https://api.imats.se/visma-auth-service/tokens")
                .retrieve()
                .bodyToMono(TokenDTO.class)
                .block();
    }
}
