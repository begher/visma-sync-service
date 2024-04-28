package begh.vismasyncservice.fiscalyear;

import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.dto.FiscalYearDTO;
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
public class FiscalYearWriter implements DatabaseWriter<FiscalYearDTO> {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DataType dataType = DataType.FISCAL_YEAR;
    private InfoService infoService;

    @PostConstruct
    private void init() {
        infoService = new InfoService(r2dbcEntityTemplate, dataType);
    }
    @Override
    public Mono<Void> write(FiscalYearDTO dto) {
        String sqlInsert = "INSERT INTO fiscal_year (id, start_date, end_date, is_locked_for_accounting)" +
                "VALUES (:id, :start_date, :end_date, :is_locked_for_accounting) ON CONFLICT (id) DO NOTHING";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sqlInsert)
                .bind("id", dto.getId())
                .bind("start_date", dto.getStartDate())
                .bind("end_date", dto.getEndDate())
                .bind("is_locked_for_accounting", dto.isLockedForAccounting())
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
    @Override
    public Mono<Integer> getDatabaseCount() {
        String query = "SELECT COUNT(*) FROM fiscal_year";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .map((row) -> row.get(0, Integer.class))
                .first()
                .defaultIfEmpty(0);
    }
}
