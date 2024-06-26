package begh.vismasyncservice.type;

import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.dto.AccountTypeDTO;
import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.util.DatabaseWriter;
import begh.vismasyncservice.util.InfoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
        return infoService.pageError(page, message);
    }

    @Override
    public Mono<Void> complete(Long totalOfSeconds) {
        return infoService.complete(totalOfSeconds);
    }

    @Override
    public Mono<Void> start() {
        return infoService.start();
    }

    @Override
    public Mono<InfoDTO> info() {
        return infoService.collectInfo();
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
