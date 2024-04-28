package begh.vismasyncservice.account;

import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.dto.AccountDTO;
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
public class AccountWriter implements DatabaseWriter<AccountDTO> {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DataType dataType = DataType.ACCOUNT;
    private InfoService infoService;

    @PostConstruct
    private void init() {
        infoService = new InfoService(r2dbcEntityTemplate, dataType);
    }

    @Override
    public Mono<Void> write(AccountDTO dto) {
        String sqlInsert = "INSERT INTO accounts (number, name, vat_code_id, fiscal_year_id, " +
                "reference_code, type_id, modified_at, created_at, is_active, is_project_allowed, " +
                "is_cost_center_allowed, is_blocked_for_manual_booking)" +
                "VALUES (:number, :name, :vat_code_id, :fiscal_year_id, " +
                ":reference_code, :type_id, :modified_at, :created_at, :is_active, :is_project_allowed, " +
                ":is_cost_center_allowed, :is_blocked_for_manual_booking) ON CONFLICT (number) DO NOTHING";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sqlInsert)
                .bind("number", dto.getNumber())
                .bind("name", dto.getName())
                .bind("vat_code_id", dto.getVatCodeId())
                .bind("fiscal_year_id", dto.getFiscalYearId())
                .bind("reference_code", dto.getReferenceCode())
                .bind("type_id", dto.getType())
                .bind("modified_at", dto.getModifiedUtc())
                .bind("created_at", dto.getCreatedUtc())
                .bind("is_active", dto.isActive())
                .bind("is_project_allowed", dto.isProjectAllowed())
                .bind("is_cost_center_allowed", dto.isCostCenterAllowed())
                .bind("is_blocked_for_manual_booking", dto.isBlockedForManualBooking())
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
        String query = "SELECT COUNT(*) FROM accounts";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .map((row) -> row.get(0, Integer.class))
                .first()
                .defaultIfEmpty(0);
    }
}
