package begh.vismasyncservice.fiscalyear;

import begh.vismasyncservice.models.dto.FiscalYearDTO;
import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.SyncDTO;
import begh.vismasyncservice.util.FluxProcessor;
import begh.vismasyncservice.visma.MetaData;
import begh.vismasyncservice.visma.VismaTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class FiscalYearService {

    private final FluxProcessor<FiscalYearDTO> fluxProcessor;
    private final FiscalYearWriter writer;
    private final VismaTokenService tokenService;
    private final String endpoint = "/v2/fiscalyears";

    public Mono<Void> syncFiscalYear(Integer startPage, Integer limit) {
        return fluxProcessor.fetchDataAndStore(
                FiscalYearDTO.class,
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
        Mono<MetaData<FiscalYearDTO>> fetchedPage = fluxProcessor.fetchPage(FiscalYearDTO.class, page, limit, endpoint, tokenService.getToken().getAccessToken());
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
