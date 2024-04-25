package begh.vismasyncservice.type;

import begh.vismasyncservice.models.dto.AccountTypeDTO;
import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.SyncDTO;
import begh.vismasyncservice.util.FluxProcessor;
import begh.vismasyncservice.visma.MetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountTypeService {

    private final FluxProcessor<AccountTypeDTO> accountTypeDTOFluxProcessor;
    private final AccountTypeWriter accountTypeWriter;

    public Mono<Void> syncAccount(int startPage, int limit, int callsPerSecond, String accessToken) {
        return accountTypeDTOFluxProcessor.fetchDataAndStore(
                AccountTypeDTO.class,
                startPage, limit,
                "/v2/accountTypes",
                callsPerSecond,
                accessToken,
                accountTypeWriter
        );
    }

    public Mono<InfoDTO> getInfo(){
        return accountTypeWriter.info();
    }

    public Mono<SyncDTO> checkIfSyncIsNeeded(int page, int limit, String accessToken){
        Mono<MetaData<AccountTypeDTO>> fetchedPage = accountTypeDTOFluxProcessor.fetchPage(AccountTypeDTO.class, page, limit, "/v2/accountTypes", accessToken);
        Mono<Integer> databaseCount = accountTypeWriter.getDatabaseCount();

        return Mono.zip(fetchedPage, databaseCount, (pageData, dbCount) -> {
            int fetchedCount = pageData.getData().size();
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
