package paste;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** A clock you can move by hand: demos and tests (L38). */
public class SettableClock extends Clock {
    public Instant now;
    public SettableClock(Instant now) { this.now = now; }
    @Override public Instant instant() { return now; }
    @Override public ZoneId getZone() { return ZoneOffset.UTC; }
    @Override public Clock withZone(ZoneId zone) { return this; }
}
