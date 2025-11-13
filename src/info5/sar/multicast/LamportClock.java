package info5.sar.multicast;

public class LamportClock {
    private int time = 0;

    public synchronized int tick() {
        return ++time;
    }

    public synchronized void update(int other) {
        time = Math.max(time, other) + 1;
    }

    public synchronized int get() {
        return time;
    }
}
