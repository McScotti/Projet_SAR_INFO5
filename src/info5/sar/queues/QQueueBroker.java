package info5.sar.queues;

import info5.sar.channels.Broker;

public class QQueueBroker extends QueueBroker{

    public QQueueBroker(Broker broker){
        super(broker);
    }

    @Override
    String name() {
        return this.broker.getName();
    }

    @Override
    MessageQueue accept(int port) {
        return new QMessageQueue(this.broker.accept(port));
        
    }

    @Override
    MessageQueue connect(String name, int port) {

        return new QMessageQueue(this.broker.connect(name, port));
        
    }

    
}
