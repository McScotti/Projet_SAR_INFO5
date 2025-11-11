package info5.sar.multicast;

import info5.sar.channels.Broker;
import info5.sar.channels.Task;
import info5.sar.events.MessageQueue;

public class Peer extends Task implements TotallyOrderedMulticast{

    public Peer(String name, Broker broker) {
        super(name, broker);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void set(Listener l) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public void multicast(String message) {
        for (int i=0;i<queues.length;i++){
            queues[i].send(message.getBytes());
        }
        for (int i=0;i<queues.length;i++){
            queues[i].receive(new MessageQueue.Listener(){
                public void received(byte[] msg){

                }

                public void closed(){
                    
                }
            });
        }
    }

    MessageQueue[] queues;
    
}
