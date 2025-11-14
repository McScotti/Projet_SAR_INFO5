package info5.sar.multicast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info5.sar.channels.Broker;
import info5.sar.channels.Task;
import info5.sar.events.EQueueBroker;
import info5.sar.events.MessageQueue;
import info5.sar.events.QueueBroker;

public class Peer extends Task implements TotallyOrderedMulticast{

    public Peer(String name, Broker broker, int id, int Number_of_peer) {
        super(name, broker);
        queueBroker= new EQueueBroker(broker.getName()); 
        bootstrap_bind(queueBroker);
        for(int i=0;i<Number_of_peer;i++){
            if(i!=id){
                queueBroker.connect("peer"+i, 1000, new QueueBroker.ConnectListener() {

                    @Override
                    public void connected(MessageQueue queue) {
                        queues.add(queue);
                        listen(queue);
                    }

                    @Override
                    public void refused() {
                        // TODO Auto-generated method stub
                        throw new UnsupportedOperationException("Unimplemented method 'refused'");
                    }
                    
                });
            }
        }
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
        // for (int i=0;i<queues.length;i++){
        //     queues[i].send(b);
        // }
        for(MessageQueue q : queues){
            q.send(b);
        }
        
    }

    public void listen(MessageQueue queue){
        queue.receive(new MessageQueue.Listener() {

            @Override
            public void received(byte[] msg) {
                Object message = MessageSerializer.deserialize(msg);
                if(message instanceof Timestamp){
                    Timestamp timestamp = (Timestamp)message;
                    received_ack.get(timestamp).add(timestamp.get_id());
                    deliver();
                }else if(message instanceof Message){
                    Message m = (Message)message;
                    received_ack.put(m.get_timestamp(), new ArrayList<Integer>());
                    received_messages.put(m.get_timestamp(), m);
                    deliver();
                }
                
            }

            @Override
            public void closed() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'closed'");
            }
            
        });
    }

    public void bootstrap_bind(QueueBroker queueBroker){
        queueBroker.bind(1000, new QueueBroker.AcceptListener() {

            @Override
            public void accepted(MessageQueue queue) {
                //listen(queue);
                bootstrap_bind(queueBroker);
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

    List<MessageQueue> queues;

    LamportClock lamportClock;

    int id;

    QueueBroker queueBroker;
    
}
