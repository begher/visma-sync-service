package begh.vismasyncservice.type;

import begh.vismasyncservice.models.dto.AccountTypeDTO;
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
public class AccountTypeService {

    private final VismaTokenService tokenService;
    private final FluxProcessor<AccountTypeDTO> fluxProcessor;
    private final AccountTypeWriter writer;

    private final String endpoint = "/v2/accountTypes";

    public Mono<Void> syncAccountType(int startPage, int limit) {
        return fluxProcessor.fetchDataAndStore(
                AccountTypeDTO.class,
                startPage, limit,
                endpoint,
                tokenService.getToken().getAccessToken(),
                writer
        );
    }

    public Mono<InfoDTO> getInfo(){
        return writer.info();
    }

    public Mono<SyncDTO> checkIfSyncIsNeeded(int page, int limit){
        Mono<MetaData<AccountTypeDTO>> fetchedPage = fluxProcessor.fetchPage(AccountTypeDTO.class, page, limit, endpoint, tokenService.getToken().getAccessToken());
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
