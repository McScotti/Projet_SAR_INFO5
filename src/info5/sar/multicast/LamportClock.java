package info5.sar.multicast;

public class LamportClock {
    private long time = 0;

    public synchronized long tick() {
        return ++time;
    }

    public synchronized void update(long other) {
        time = Math.max(time, other) + 1;
    }

    public synchronized long get() {
        return time;
    }
}
