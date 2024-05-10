package begh.vismasyncservice.util;

import begh.vismasyncservice.visma.MetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.lang.reflect.Type;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@Component
public class FluxProcessor<T> {
    private final WebClient webClient;
    public Mono<Void> fetchDataAndStore(Class<T> typeClass,
                                        int startPage, int limit, String endPoint,
                                        String accessToken, DatabaseWriter<T> databaseWriter) {

        Instant start = Instant.now();
        databaseWriter.start().subscribe();
        return fetchPage(typeClass, startPage, limit, endPoint, accessToken)
                .expand(response -> response.getMeta().getCurrentPage() < response.getMeta().getTotalNumberOfPages()
                        ? fetchPage(typeClass, response.getMeta().getCurrentPage() + 1, limit, endPoint, accessToken)
                        : Mono.empty())
                .flatMap(response -> Flux.fromIterable(response.getData())
                        .flatMap(databaseWriter::write)
                        .onErrorResume(e -> databaseWriter.pageError(response.getMeta().getCurrentPage(), e.getMessage())))
                .then()
                .then(Mono.defer(() -> databaseWriter.complete(Duration.between(start, Instant.now()).toSeconds())));
    }

    public <T> Mono<MetaData<T>> fetchPage(Class<T> typeClass, int page, int limit, String endPoint, String accessToken) {
        String url = String.format("https://eaccountingapi-sandbox.test.vismaonline.com%s?$page=%d&$pagesize=%d", endPoint, page, limit);
        return webClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<MetaData<T>>() {
                    @Override
                    public Type getType() {
                        return ResolvableType.forClassWithGenerics(MetaData.class, ResolvableType.forClass(typeClass)).getType();
                    }
                })
                .onErrorResume(e -> Mono.just(new MetaData<>()));
    }
}
