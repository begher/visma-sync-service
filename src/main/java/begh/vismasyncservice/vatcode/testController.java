package begh.vismasyncservice.vatcode;

import begh.vismasyncservice.type.AccountTypeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
@RequiredArgsConstructor
public class testController {

    private final AccountTypeWriter writer;

    @GetMapping("try")
    public String test(){
        writer.start().subscribe();
        return "complete";
    }
}
