package begh.vismasyncservice.voucher;

import begh.vismasyncservice.models.dto.InfoDTO;
import begh.vismasyncservice.models.dto.SyncDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/vouchers")
public class VoucherController {
    private final VoucherService service;
    @GetMapping("/sync")
    public ResponseEntity<String> syncAccount(
            @RequestParam(required = false, defaultValue = "1") Integer startPage,
            @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        service.syncVouchers(startPage, limit).subscribe();
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
