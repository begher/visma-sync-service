package begh.vismasyncservice.vatcode;

import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.VatCodeDTO;
import begh.vismasyncservice.util.DatabaseWriter;
import begh.vismasyncservice.util.InfoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
@RequiredArgsConstructor
public class VatCodeWriter implements DatabaseWriter<VatCodeDTO> {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DataType dataType = DataType.VAT_CODE;
    private InfoService infoService;

    @PostConstruct
    private void init() {
        infoService = new InfoService(r2dbcEntityTemplate, dataType);
    }
    @Override
    public Mono<Void> write(VatCodeDTO dto) {
        String sqlInsert = "INSERT INTO vat_code (id, code, description, vat_rate) VALUES (:id, :code, :description, :vat_rate) ON CONFLICT (type) DO NOTHING";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(sqlInsert)
                .bind("id", dto.getId())
                .bind("code", dto.getCode())
                .bind("description", dto.getDescription())
                .bind("vat_rate", dto.getVatRate())
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
        String query = "SELECT COUNT(*) FROM vat_code";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .map((row) -> row.get(0, Integer.class))
                .first()
                .defaultIfEmpty(0);
    }
}
