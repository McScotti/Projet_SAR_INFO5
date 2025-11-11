package info5.sar.events;

public abstract class MessageQueue {
    public interface Listener {
        void received(byte[] msg);
        void closed();
    }
    public abstract void setListener(Listener l);
    public abstract boolean send(byte[] bytes);
    public abstract boolean receive(Listener l);
    public abstract boolean send(byte[] bytes,int offset, int length);
    
    public abstract void close(Listener listener);
    public abstract boolean closed();
}
