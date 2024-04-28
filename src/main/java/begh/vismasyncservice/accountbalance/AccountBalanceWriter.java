package begh.vismasyncservice.accountbalance;

import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.dto.AccountBalanceDTO;
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
public class AccountBalanceWriter implements DatabaseWriter<AccountBalanceDTO> {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DataType dataType = DataType.ACCOUNT_BALANCE;
    private InfoService infoService;

    @PostConstruct
    private void init() {
        infoService = new InfoService(r2dbcEntityTemplate, dataType);
    }
    @Override
    public Mono<Void> write(AccountBalanceDTO dto) {
        String sqlInsert = "INSERT INTO account_balances (account_number, balance, type_id)" +
                "VALUES (:account_number, :balance, :type_id) ON CONFLICT (account_number) DO NOTHING";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sqlInsert)
                .bind("account_number", dto.getAccountNumber())
                .bind("balance", dto.getBalance())
                .bind("type_id", dto.getAccountType())
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
        String query = "SELECT COUNT(*) FROM account_balances";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .map((row) -> row.get(0, Integer.class))
                .first()
                .defaultIfEmpty(0);
    }
}
