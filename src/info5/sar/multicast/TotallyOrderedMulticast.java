package info5.sar.multicast;

public interface TotallyOrderedMulticast {
    interface Listener{
        void delivered(String message);
    }

    void set(Listener l);
    void multicast(String message);
}
