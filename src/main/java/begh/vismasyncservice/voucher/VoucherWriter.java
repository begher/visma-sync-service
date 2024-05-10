package begh.vismasyncservice.voucher;

import begh.vismasyncservice.models.DataType;
import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.VoucherDTO;
import begh.vismasyncservice.models.dto.VoucherRowDTO;
import begh.vismasyncservice.util.DatabaseWriter;
import begh.vismasyncservice.util.InfoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VoucherWriter implements DatabaseWriter<VoucherDTO> {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DataType dataType = DataType.VAT_CODE;
    private InfoService infoService;

    @PostConstruct
    private void init() {
        infoService = new InfoService(r2dbcEntityTemplate, dataType);
    }
    @Override
    public Mono<Void> write(VoucherDTO dto) {
        String sqlVoucher = "INSERT INTO voucher (id, voucher_date, voucher_text)" +
                "VALUES (:id, :voucher_date, :voucher_text) ON CONFLICT (id) DO NOTHING";

        return r2dbcEntityTemplate.getDatabaseClient().sql(sqlVoucher)
                .bind("id", dto.getId())
                .bind("voucher_date", dto.getVoucherDate())
                .bind("voucher_text", dto.getVoucherText())
                .fetch().rowsUpdated()
                .flatMap(count -> {
                    if (count > 0) {
                        return Flux.fromIterable(dto.getRows())
                                .concatMap(row -> saveVoucherRow(row, dto.getId()))
                                .then();
                    } else {
                        return Mono.empty();
                    }
                });
    }

    private Mono<Void> saveVoucherRow(VoucherRowDTO row, UUID voucherId) {
        String sqlRow = "INSERT INTO voucher_row (voucher_id, account_number, account_description, " +
                "debit_amount, credit_amount, transaction_text)" +
                "VALUES (:voucher_id, :account_number, :account_description, " +
                ":debit_amount, :credit_amount, :transaction_text)";

        DatabaseClient.GenericExecuteSpec executeSpec = r2dbcEntityTemplate.getDatabaseClient().sql(sqlRow)
                .bind("voucher_id", voucherId)
                .bind("account_number", row.getAccountNumber())
                .bind("account_description", row.getAccountDescription())
                .bind("debit_amount", row.getDebitAmount())
                .bind("credit_amount", row.getCreditAmount());

        if (row.getTransactionText() == null) {
            executeSpec = executeSpec.bindNull("transaction_text", String.class);
        } else {
            executeSpec = executeSpec.bind("transaction_text", row.getTransactionText());
        }

        return executeSpec.then();
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
        String query = "SELECT COUNT(*) FROM voucher";
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(query)
                .map((row) -> row.get(0, Integer.class))
                .first()
                .defaultIfEmpty(0);
    }
}