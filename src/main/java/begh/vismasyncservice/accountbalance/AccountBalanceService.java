package begh.vismasyncservice.accountbalance;

import begh.vismasyncservice.models.dto.AccountBalanceDTO;
import begh.vismasyncservice.models.dto.AccountDTO;
import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.SyncDTO;
import begh.vismasyncservice.util.FluxProcessor;
import begh.vismasyncservice.visma.MetaData;
import begh.vismasyncservice.visma.VismaTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AccountBalanceService {
    private final VismaTokenService tokenService;
    private final FluxProcessor<AccountBalanceDTO> fluxProcessor;
    private final AccountBalanceWriter writer;

    private final String endpoint = "/v2/accountbalances/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    public Mono<Void> syncAccountBalance(Integer startPage, Integer limit) {
        return fluxProcessor.fetchDataAndStore(
                AccountBalanceDTO.class,
                startPage, limit,
                endpoint,
                tokenService.getToken().getAccessToken(),
                writer
        );
    }
    public Mono<InfoDTO> getInfo(){
        return writer.info();
    }

    public Mono<SyncDTO> checkIfSyncIsNeeded(int page, int limit) {
        Mono<MetaData<AccountDTO>> fetchedPage = fluxProcessor.fetchPage(AccountDTO.class, page, limit, endpoint, tokenService.getToken().getAccessToken());
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
