package begh.vismasyncservice.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {
    @JsonProperty("AccountNumber")
    int accountNumber;
    @JsonProperty("AccountName")
    String accountName;
    @JsonProperty("Balance")
    double balance;
    @JsonProperty("AccountType")
    int accountType;
    @JsonProperty("AccountTypeDescription")
    String accountTypeDescription;
}
