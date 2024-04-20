package begh.vismasyncservice.util;

import reactor.core.publisher.Mono;

public interface DatabaseWriter<T> {
    Mono<Void> write(T DTO);
    Mono<Void> pageError(int page, String message);
    Mono<Void> complete(Long totalOfSeconds);
    void start();

}
