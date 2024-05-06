package begh.vismasyncservice.voucher;

import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.SyncDTO;
import begh.vismasyncservice.models.dto.VoucherDTO;
import begh.vismasyncservice.util.FluxProcessor;
import begh.vismasyncservice.visma.MetaData;
import begh.vismasyncservice.visma.VismaTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private final FluxProcessor<VoucherDTO> fluxProcessor;
    private final VoucherWriter writer;
    private final VismaTokenService tokenService;
    private final String endpoint = "/v2/vouchers";

    public Mono<Void> syncVouchers(Integer startPage, Integer limit) {
        return fluxProcessor.fetchDataAndStore(
                VoucherDTO.class,
                startPage, limit,
                endpoint,
                tokenService.getToken().getAccessToken(),
                writer
        );
    }
    public Mono<InfoDTO> getInfo() {
        return writer.info();
    }

    public Mono<SyncDTO> checkIfSyncIsNeeded(int page, int limit) {
        Mono<MetaData<VoucherDTO>> fetchedPage = fluxProcessor.fetchPage(VoucherDTO.class, page, limit, endpoint, tokenService.getToken().getAccessToken());
        Mono<Integer> databaseCount = writer.getDatabaseCount();

        return Mono.zip(fetchedPage, databaseCount, (pageData, dbCount) -> {
            int fetchedCount = pageData.getMeta().getTotalNumberOfResults();
            int countDifference = fetchedCount - dbCount;

            return SyncDTO.builder()
                    .syncNeeded(countDifference != 0)
                    .countDifference(Math.abs(countDifference))
                    .vismaTotalResources(fetchedCount)
                    .databaseResources(dbCount)
                    .build();
        });
    }
}
