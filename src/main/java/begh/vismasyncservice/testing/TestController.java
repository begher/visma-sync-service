//package begh.vismasyncservice.testing;
//
//import begh.vismasyncservice.models.Account;
//import begh.vismasyncservice.models.dto.AccountDTO;
//import begh.vismasyncservice.visma.MetaData;
//import begh.vismasyncservice.visma.VismaTokenService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.core.ResolvableType;
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.lang.reflect.Type;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@RestController
//@RequiredArgsConstructor
//@CrossOrigin
//@RequestMapping("/test")
//public class TestController {
//    private final VismaTokenService service;
//    private final WebClient webClient;
//    private final R2dbcEntityTemplate r2dbcEntityTemplate;
//
//    @GetMapping()
//    public void testing() {
//        List<AccountDTO> vismaAccounts = fetchAllAccounts();
//        List<Account> dataBase = getAllAccounts();
//
//        System.out.println(vismaAccounts.size());
//        System.out.println(dataBase.size());
//
//        for (int i = 0; i < vismaAccounts.size(); i++) {
//            System.out.println("Visma: " + vismaAccounts.get(i).getNumber() + " " + vismaAccounts.get(i).getName());
//            if(i <= dataBase.size()){
//                System.out.println("Visma: " + dataBase.get(i).getNumber() + " " + dataBase.get(i).getName());
//            }
//            System.out.println(" ");
//        }
//
//        System.out.println("DUBBELNUGGE");
//        System.out.println(" ");
//
//        vismaAccounts.forEach(e->{
//            dataBase.forEach(f ->{
//                if(e.getNumber() == f.getNumber()){
//                    System.out.println(e.getNumber() + " " + e.getName());
//                    System.out.println(f.getNumber() + " " + f.getName());
//                    System.out.println(" ");
//                }
//            });
//        });
//
//        Set<Integer> dataBaseNumbers = new HashSet<>();
//        for (Account account : dataBase) {
//            dataBaseNumbers.add(account.getNumber());
//        }
//
//        System.out.println("SINGLENUGGE");
//        System.out.println(" ");
//
//        for (AccountDTO dto : vismaAccounts) {
//            Integer number = dto.getNumber();
//            if (!dataBaseNumbers.contains(number)) {
//                System.out.println(number);
//            }
//        }
//    }
//
//    public List<Account> getAllAccounts() {
//        String sql = "SELECT number, name FROM accounts";
//        return r2dbcEntityTemplate.getDatabaseClient()
//                .sql(sql)
//                .map((row, metadata) -> Account.builder()
//                        .number(row.get("number", Integer.class))
//                        .name(row.get("name", String.class))
//                        .build())
//                .all()  // Fetch all rows
//                .collectList()  // Collect results into a list
//                .block();  // Block to wait for the list (use with caution)
//    }
//
//    private List<AccountDTO> fetchAllAccounts() {
//        return Flux.range(1, 1)  // Generates page numbers from 1 to 52
//                .flatMap(this::fetchPage)  // Fetch each page
//                .flatMapIterable(MetaData::getData)  // Convert each MetaData into a Flux of AccountDTO
//                .collectList()  // Collect all AccountDTOs into a List
//                .block();  // Return the list
//    }
//
//    public Mono<MetaData<AccountDTO>> fetchPage(int page) {
//        String url = String.format("https://eaccountingapi-sandbox.test.vismaonline.com/v2/accounts?$page=%d&$pagesize=50", page);
//        return webClient.get()
//                .uri(url)
//                .header("Authorization", "Bearer " + service.getToken())
//                .retrieve()
//                .onStatus(HttpStatus::isError, response -> {
//                    System.out.println("Error Response: " + response.statusCode());
//                    return Mono.error(new RuntimeException("API error"));
//                })
//                .bodyToMono(new ParameterizedTypeReference<MetaData<AccountDTO>>() {})
//                .doOnNext(metaData -> System.out.println("Received data: " + metaData))
//                .onErrorResume(e -> {
//                    e.printStackTrace();
//                    return Mono.just(new MetaData<>());
//                });
//    }
//}
