package info5.sar.multicast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info5.sar.channels.Broker;
import info5.sar.channels.Task;
import info5.sar.events.MessageQueue;

public class Peer extends Task implements TotallyOrderedMulticast{

    public Peer(String name, Broker broker) {
        super(name, broker);
        
    }

    @Override
    public void set(Listener l) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public void multicast(String msg) {
        Message message = new Message(msg);
        Timestamp timestamp = new Timestamp(this.id, lamportClock.tick());
        message.set_timestamp(timestamp);
        byte[] b = MessageSerializer.serialize(message);
        for (int i=0;i<queues.length;i++){
            queues[i].send(b);
        }
        
    }

    public void listen(MessageQueue queue){
        queue.receive(new MessageQueue.Listener() {

            @Override
            public void received(byte[] msg) {
                Message message = (Message)MessageSerializer.deserialize(msg);
                // ack
                
                received_ack.put(message.get_timestamp(), new ArrayList<Integer>());
                received_messages.put(message.get_timestamp(), message);
                deliver();
            }

            @Override
            public void closed() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'closed'");
            }
            
        });
    }

    public void deliver(){
        Timestamp mintTimestamp = Collections.min(received_messages.keySet());
        if(PFD.verify_my_acks(received_ack.get(mintTimestamp))){

        }
        
    }

    private Map<Timestamp,List<Integer>> received_ack;

    private Map<Timestamp,Message> received_messages;

    MessageQueue[] queues;

    LamportClock lamportClock;

    int id;
    
}
