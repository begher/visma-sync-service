package begh.vismasyncservice.account;

import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.SyncDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/account")
public class AccountController {
    private final AccountService service;
    @GetMapping("/sync")
    public ResponseEntity<String> syncAccount(
            @RequestParam(required = false, defaultValue = "1") Integer startPage,
            @RequestParam(required = false, defaultValue = "15") Integer limit,
            @RequestParam(required = false, defaultValue = "10") Integer callsPerSecond
    ) {
        service.syncAccount(startPage, limit, callsPerSecond).subscribe();
        return ResponseEntity.ok("Started");
    }

    @GetMapping("/info")
    public ResponseEntity<Mono<InfoDTO>> infoAccount(){
        return ResponseEntity.ok(service.getInfo());
    }

    @GetMapping("/sync-check")
    public ResponseEntity<Mono<SyncDTO>> checkIfSyncIsNeeded(){
        return ResponseEntity.ok(service.checkIfSyncIsNeeded(1, 1));
    }
}