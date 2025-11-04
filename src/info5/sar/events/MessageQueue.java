package info5.sar.events;

public abstract class MessageQueue {
    interface Listener {
        void received(byte[] msg);
        void closed();
    }
    abstract void setListener(Listener l);
    abstract boolean send(byte[] bytes);
    abstract boolean receive(Listener l);
    abstract boolean send(byte[] bytes,int offset, int length);
    
    abstract void close(Listener listener);
    abstract boolean closed();
}
