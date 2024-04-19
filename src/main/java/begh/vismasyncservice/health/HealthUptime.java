package begh.vismasyncservice.health;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Endpoint(id = "uptime")
public class HealthUptime {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ss:mm:HH:dd:MM:yyyy");

    @ReadOperation
    public ResponseEntity<UptimeDTO> uptime() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long startTimeMillis = runtimeMXBean.getStartTime();
        long uptimeMillis = runtimeMXBean.getUptime();

        Instant startInstant = Instant.ofEpochMilli(startTimeMillis);
        ZonedDateTime startDateTime = startInstant.atZone(ZoneId.systemDefault());

        Duration uptimeDuration = Duration.ofMillis(uptimeMillis);

        long seconds = uptimeDuration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "Y: %02d M: %02d D: %02d H: %02d M: %02d S: %02d",
                absSeconds / 86400 / 365,
                (absSeconds / 86400 / 30) % 12,
                (absSeconds / 86400) % 30,
                (absSeconds / 3600) % 24,
                (absSeconds / 60) % 60,
                absSeconds % 60);

        String formattedStartTime = FORMATTER.format(startDateTime);

        return ResponseEntity.ok(
                UptimeDTO.builder()
                        .startedAt(formattedStartTime)
                        .upTime(positive)
                        .upTimeInSeconds(seconds)
                        .build()
        );
    }
}
