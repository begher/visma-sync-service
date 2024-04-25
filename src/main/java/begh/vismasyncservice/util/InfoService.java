package begh.vismasyncservice.util;

import begh.vismasyncservice.models.CompletedDTO;
import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.ErrorPageDTO;
import begh.vismasyncservice.models.StartDTO;
import begh.vismasyncservice.models.dto.InfoDTO;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class InfoService{

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DataType dataType;


    public InfoService(R2dbcEntityTemplate r2dbcEntityTemplate, DataType dataType) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.dataType = dataType;
    }

    public Mono<InfoDTO> collectInfo() {
        return fetchLastStart()
                .flatMap(start ->
                        Mono.zip(
                                fetchErrorPage(start),
                                fetchCompleted(start),
                                (error, completed) -> new InfoDTO(start, error, completed)
                        )
                )
                .switchIfEmpty(Mono.just(new InfoDTO()));
    }

    private Mono<StartDTO> fetchLastStart() {
        String query = """
        SELECT s.* FROM start s
        JOIN data_type dt ON s.data_type_id = dt.id
        WHERE dt.type = :type
        ORDER BY s.created_at DESC
        LIMIT 1
    """;

        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .bind("type", dataType.toString())
                .map((row, metadata) -> StartDTO.builder()
                        .message(row.get("message", String.class))
                        .createdAt(row.get("created_at", LocalDateTime.class))
                        .build())
                .first();
    }

    private Mono<ErrorPageDTO> fetchErrorPage(StartDTO start) {
        String query = """
        SELECT ep.* FROM page_error ep
        JOIN data_type dt ON ep.data_type_id = dt.id
        WHERE dt.type = :type AND ep.created_at > :createdAt
        ORDER BY ep.created_at DESC
        LIMIT 1
    """;

        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .bind("type", dataType.toString())
                .bind("createdAt", start.getCreatedAt())
                .map((row, metadata) -> ErrorPageDTO.builder()
                        .message(row.get("message", String.class))
                        .createdAt(row.get("created_at", LocalDateTime.class))
                        .page(row.get("page", Integer.class))
                        .build())
                .first()
                .switchIfEmpty(Mono.just(ErrorPageDTO.builder().build()));

    }

    private Mono<CompletedDTO> fetchCompleted(StartDTO start){
        String query = """
        SELECT c.* FROM completed c
        JOIN data_type dt ON c.data_type_id = dt.id
        WHERE dt.type = :type AND c.created_at > :createdAt
        ORDER BY c.created_at DESC
        LIMIT 1
    """;

        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .bind("type", dataType.toString())
                .bind("createdAt", start.getCreatedAt())
                .map((row, metadata) -> CompletedDTO.builder()
                        .duration(row.get("duration", Integer.class))
                        .createdAt(row.get("created_at", LocalDateTime.class))
                        .build())
                .first()
                .switchIfEmpty(Mono.just(CompletedDTO.builder().build()));
    }
}
