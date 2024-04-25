package begh.vismasyncservice.type;

import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.SyncDTO;
import begh.vismasyncservice.visma.TokenDTO;
import begh.vismasyncservice.visma.VismaTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/account-type")
public class AccountTypeController {

    private final AccountTypeService service;
    private final VismaTokenService tokenService;
    @GetMapping("/sync")
    public ResponseEntity<String> syncAccount(
            @RequestParam(required = false, defaultValue = "0") Integer startPage,
            @RequestParam(required = false, defaultValue = "15") Integer limit,
            @RequestParam(required = false, defaultValue = "10") Integer callsPerSecond
    ) {
        TokenDTO token = tokenService.getToken();
        service.syncAccount(startPage, limit, callsPerSecond, token.getAccessToken()).subscribe();
        return ResponseEntity.ok("Started");
    }

    @GetMapping("/info")
    public ResponseEntity<Mono<InfoDTO>> infoAccount(){
        return ResponseEntity.ok(service.getInfo());
    }

    @GetMapping("/sync-check")
    public ResponseEntity<Mono<SyncDTO>> checkIfSyncIsNeeded(){
        TokenDTO token = tokenService.getToken();
        return ResponseEntity.ok(service.checkIfSyncIsNeeded(1, 1, token.getAccessToken()));
    }
}
