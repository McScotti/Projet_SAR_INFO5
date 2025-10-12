package info5.sar.queues;
import info5.sar.channels.Broker;

public abstract class QueueBroker {

    Broker broker;
    QueueBroker(Broker broker){
        this.broker=broker;
    }
    abstract String name();
    abstract MessageQueue accept(int port);
    abstract MessageQueue connect(String name, int port);
}
