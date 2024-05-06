package begh.vismasyncservice.vatcode;

import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.SyncDTO;
import begh.vismasyncservice.models.dto.VatCodeDTO;
import begh.vismasyncservice.util.FluxProcessor;
import begh.vismasyncservice.visma.MetaData;
import begh.vismasyncservice.visma.VismaTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VatCodeService {
    private final FluxProcessor<VatCodeDTO> fluxProcessor;
    private final VatCodeWriter writer;
    private final VismaTokenService tokenService;
    private final String endpoint = "/v2/vatcodes";

    public Mono<Void> syncVatCode(Integer startPage, Integer limit) {
        return fluxProcessor.fetchDataAndStore(
                VatCodeDTO.class,
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
        Mono<MetaData<VatCodeDTO>> fetchedPage = fluxProcessor.fetchPage(VatCodeDTO.class, page, limit, endpoint, tokenService.getToken().getAccessToken());
        Mono<Integer> databaseCount = writer.getDatabaseCount();

        return Mono.zip(fetchedPage, databaseCount, (pageData, dbCount) -> {
            int fetchedCount = pageData.getMeta().getTotalNumberOfResults();;
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
