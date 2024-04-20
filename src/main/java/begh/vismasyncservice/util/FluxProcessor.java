package begh.vismasyncservice.util;

import begh.vismasyncservice.visma.MetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@Component
public class FluxProcessor<T> {
    private final WebClient webClient;
    public Mono<Void> fetchDataAndStore(
            int startPage, int limit, String endPoint, int callsPerSecond,
            DatabaseWriter<T> databaseWriter) {

        Instant start = Instant.now();
        databaseWriter.start();
        return fetchPage(startPage, limit, endPoint)
                .expand(response -> response.getMeta().getCurrentPage() < response.getMeta().getTotalNumberOfPages()
                        ? Mono.delay(Duration.ofMillis(1000 / callsPerSecond))
                        .then(fetchPage(response.getMeta().getCurrentPage() + 1, limit, endPoint))
                        : Mono.empty())
                .flatMap(response -> Flux.fromIterable(response.getData())
                        .flatMap(databaseWriter::write)
                        .onErrorResume(e -> databaseWriter.pageError(response.getMeta().getCurrentPage(), e.getMessage())))
                .then()
                .then(databaseWriter.complete(Duration.between(start, Instant.now()).toSeconds()));
    }

    private Mono<MetaData<T>> fetchPage(int page, int limit, String endPoint) {
        String url = String.format("https://api.example.com%s?page=%d&pagesize=%d", endPoint, page, limit);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
