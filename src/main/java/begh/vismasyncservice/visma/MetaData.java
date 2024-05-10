package begh.vismasyncservice.visma;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetaData<T> {
    @JsonProperty("Meta")
    private Meta meta;
    @JsonProperty("Data")
    private List<T> data = new ArrayList<>();

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        @JsonProperty("CurrentPage")
        private int currentPage;
        @JsonProperty("PageSize")
        private int pageSize;
        @JsonProperty("TotalNumberOfPages")
        private int totalNumberOfPages;
        @JsonProperty("TotalNumberOfResults")
        private int totalNumberOfResults;
        @JsonProperty("ServerTimeUtc")
        private ZonedDateTime serverTimeUtc;
    }
}
