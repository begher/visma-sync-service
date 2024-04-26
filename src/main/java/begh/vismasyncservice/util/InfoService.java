package begh.vismasyncservice.util;

import begh.vismasyncservice.models.CompletedDTO;
import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.ErrorPageDTO;
import begh.vismasyncservice.models.StartDTO;
import begh.vismasyncservice.models.dto.InfoDTO;
import org.springframework.dao.DuplicateKeyException;
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

    public Mono<Void> complete(Long totalOfSeconds) {
        String sqlFindId = "SELECT id FROM data_type WHERE type = :type";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sqlFindId)
                .bind("type", dataType.toString())
                .map(row -> row.get("id", Integer.class))
                .first()
                .flatMap(dataTypeId -> {
                    String sqlInsertError = "INSERT INTO completed (duration, data_type_id, created_at) VALUES (:duration, :data_type_id, NOW())";
                    return r2dbcEntityTemplate.getDatabaseClient()
                            .sql(sqlInsertError)
                            .bind("duration", totalOfSeconds)
                            .bind("data_type_id", dataTypeId)
                            .then();
                });
    }

    public Mono<Void> start() {
        return insertDataTypeIfNotExists()
                .then(insertStart())
                .onErrorResume(DuplicateKeyException.class, e -> Mono.empty())
                .then();
    }

    private Mono<Void> insertDataTypeIfNotExists() {
        String sqlInsert = "INSERT INTO data_type (type) VALUES (:type) ON CONFLICT (type) DO NOTHING";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sqlInsert)
                .bind("type", dataType.toString())
                .then();
    }

    private Mono<Object> insertStart() {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql("SELECT id FROM data_type WHERE type = :type")
                .bind("type", dataType.toString())
                .map(row -> row.get("id", Integer.class))
                .first()
                .flatMap(dataTypeId -> {
                    String sqlInsertError = "INSERT INTO start (message, data_type_id, created_at) VALUES (:message, :data_type_id, NOW())";
                    return r2dbcEntityTemplate.getDatabaseClient()
                            .sql(sqlInsertError)
                            .bind("message", "message")
                            .bind("data_type_id", dataTypeId)
                            .then();
                });
    }

    public Mono<Void> pageError(int page, String message) {
        String sqlFindId = "SELECT id FROM data_type WHERE type = :type";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sqlFindId)
                .bind("type", dataType.toString())
                .map(row -> row.get("id", Integer.class))
                .first()
                .flatMap(dataTypeId -> {
                    String sqlInsertError = "INSERT INTO page_error (message, data_type_id, created_at, page) VALUES (:message, :data_type_id, NOW(), :page)";
                    return r2dbcEntityTemplate.getDatabaseClient()
                            .sql(sqlInsertError)
                            .bind("message", message)
                            .bind("data_type_id", dataTypeId)
                            .bind("page", page)
                            .then();
                });
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
