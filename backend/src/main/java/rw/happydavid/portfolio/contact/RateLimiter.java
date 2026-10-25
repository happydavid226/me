package rw.happydavid.portfolio.contact;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class RateLimiter {
    private static final int LIMIT = 5;
    private static final long WINDOW_MILLIS = Duration.ofMinutes(15).toMillis();
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final Clock clock = Clock.systemUTC();

    public boolean allow(String key) {
        long now = clock.millis();
        Window updated = windows.compute(key, (ignored, current) -> {
            if (current == null || now - current.startedAt() >= WINDOW_MILLIS) return new Window(now, 1);
            return new Window(current.startedAt(), current.count() + 1);
        });
        if (windows.size() > 10_000) windows.entrySet().removeIf(e -> now - e.getValue().startedAt() >= WINDOW_MILLIS);
        return updated.count() <= LIMIT;
    }
    private record Window(long startedAt, int count) { }
}
