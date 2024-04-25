package begh.vismasyncservice.type;

import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.dto.AccountTypeDTO;
import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.util.DatabaseWriter;
import begh.vismasyncservice.util.InfoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
public class AccountTypeWriter implements DatabaseWriter<AccountTypeDTO> {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DataType dataType = DataType.ACCOUNT_TYPE;
    private InfoService infoService;

    @PostConstruct
    private void init() {
        infoService = new InfoService(r2dbcEntityTemplate, dataType);
    }

    @Override
    public Mono<Void> write(AccountTypeDTO dto) {

        String sqlInsert = "INSERT INTO account_type (id, type) VALUES (:id, :type) ON CONFLICT (type) DO NOTHING";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sqlInsert)
                .bind("id", dto.getType())
                .bind("type", dto.getTypeDescription())
                .then();
    }

    @Override
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

    @Override
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

    @Override
    public Mono<Void> start() {
        return insertDataTypeIfNotExists()
                .then(insertStart())
                .onErrorResume(DuplicateKeyException.class, e -> Mono.empty())
                .then();
    }

    @Override
    public Mono<InfoDTO> info() {
        return infoService.collectInfo();
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

    public Mono<Integer> getDatabaseCount() {
        String query = "SELECT COUNT(*) FROM account_type";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .map((row) -> row.get(0, Integer.class))
                .first()
                .defaultIfEmpty(0);
    }
}
