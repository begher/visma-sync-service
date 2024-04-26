package begh.vismasyncservice.util;

import begh.vismasyncservice.models.dto.InfoDTO;
import reactor.core.publisher.Mono;

public interface DatabaseWriter<T> {
    Mono<Void> write(T DTO);
    Mono<Void> pageError(int page, String message);
    Mono<Void> complete(Long totalOfSeconds);
    Mono<Void> start();
    Mono<InfoDTO> info();
    Mono<Integer> getDatabaseCount();
}
