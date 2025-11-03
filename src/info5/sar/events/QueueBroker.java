package info5.sar.events;

//import info5.sar.queues.MessageQueue;

public abstract class QueueBroker {
    //abstract QueueBroker(String name);
    interface AcceptListener {
        void accepted(MessageQueue queue);
    }
    abstract boolean bind(int port, AcceptListener listener);
    abstract boolean unbind(int port);
    
    interface ConnectListener {
        void connected(MessageQueue queue);
        void refused();
    }
    abstract boolean connect(String name, int port,ConnectListener listener);
}