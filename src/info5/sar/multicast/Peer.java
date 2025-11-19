package info5.sar.multicast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info5.sar.events.EExecutor;
import info5.sar.events.EMessageQueue;
import info5.sar.events.EQueueBroker;
import info5.sar.events.MessageQueue;
import info5.sar.events.QueueBroker;

public class Peer extends Task implements TotallyOrderedMulticast{

    private int neighbors;

    public Peer(String name, QueueBroker queueBroker, int id, int Number_of_peer) {
        super(name, queueBroker);
        
        this.queueBroker= queueBroker;
        received_ack = new HashMap<>();
        received_messages = new HashMap<>();
        queues = new ArrayList<>();
        lamportClock = new LamportClock();
        this.id = id;
        this.neighbors=Number_of_peer-1;


        bootstrap_bind(id);
        int i=id+1;
        while(i<Number_of_peer){
            if(i!=id){
                queueBroker.connect("peer"+i, 1000+i+id, new QueueBroker.ConnectListener() {

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
            i++;
        }

        PFD.insert(id,this);
    }

    
    @Override
    public void set(Listener l) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    }

    @Override
    public void multicast(String msg,int id) {
        if(queues.size()<neighbors){
            Runnable R = new Runnable() {

                @Override
                public void run() {
                    Peer.this.multicast(msg, id);
                }
            };
            EExecutor.instance().post(R);
        }else{
            Message message = new Message(msg);
            Timestamp timestamp = new Timestamp(this.id, lamportClock.tick());
            message.set_timestamp(timestamp);
            byte[] b = MessageSerializer.serialize(message);
            //queues.get(0).send(b);
            for(MessageQueue q : queues){
                q.send(b);
                //System.out.println("j'ai  ecrit pour un "+id);
            } 
            handleMessage(message);
        }
        
    }

    public void listen(MessageQueue queue){
        queue.receive(new MessageQueue.Listener() {

            @Override
            public void received(byte[] msg) {
                Object message = MessageSerializer.deserialize(msg);
                if(message instanceof MessageACK){
                    MessageACK ack = (MessageACK)message;
                    handleACK(ack);
                    listen(queue);
                }else if(message instanceof Message){
                    Message m = (Message)message;
                    handleMessage(m);
                    listen(queue);
                }
                
            }

            @Override
            public void closed() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'closed'");
            }
            
        });
    }

    public void bootstrap_bind( int i){
        
        for (int j=0;j<i;j++){
            queueBroker.bind(1000+j+i, new QueueBroker.AcceptListener() {

            @Override
            public void accepted(MessageQueue queue) {
                queues.add(queue);
                listen(queue);
            }
            
        });
        }
    }


    public void deliver(){
        Timestamp mintTimestamp = Collections.min(received_messages.keySet());
        if(PFD.verify_my_acks(received_ack.get(mintTimestamp))){
            System.out.println(received_messages.get(mintTimestamp).content + " "+id);
            received_messages.remove(mintTimestamp);
            received_ack.remove(mintTimestamp);
            if(received_messages.size()>=1){
                deliver();
            }
        }else{
            String h = "";
            for(Integer i:received_ack.get(mintTimestamp)){
                h = h+" "+i;
            }
            //System.out.println("j n'ai pas pu delivrer ce message: "+received_messages.get(mintTimestamp).content+ " "+this.id);
        }
        
    }

    public void suicide(){
        for(MessageQueue q: queues){
            q.close(new MessageQueue.Listener() {

                @Override
                public void received(byte[] msg) {
                   
                }

                @Override
                public void closed() {
                    PFD.dead(Peer.this.id);
                }
                
            });
        }
    }

    public void handleACK(MessageACK ack){
        if(received_messages.get(ack.get_timestamp())!=null){
                received_ack.get(ack.get_timestamp()).add(ack.get_id());
                deliver();
            
        }else{
            Runnable R = new Runnable() {

                @Override
                public void run() {
                    Peer.this.handleACK(ack);;
                }
                
            };
            EExecutor.instance().post(R);
        }
        // received_ack.get(ack.get_timestamp()).add(ack.get_id());
        // System.out.println("j'ai recu un message");
        // deliver();
    }

    public void handleMessage(Message m){
        received_ack.put(m.get_timestamp(), new ArrayList<Integer>());
        received_ack.get(m.get_timestamp()).add(Peer.this.id);
        MessageACK ack = new MessageACK(m.get_timestamp(), Peer.this.id);
        //ack
        for(MessageQueue q: queues){
            q.send(MessageSerializer.serialize(ack));
        }
        received_messages.put(m.get_timestamp(), m);
        lamportClock.update(m.get_timestamp().get_clock());
        deliver();
    }

    private Map<Timestamp,List<Integer>> received_ack;

    private Map<Timestamp,Message> received_messages;

    List<MessageQueue> queues;

    LamportClock lamportClock;

    int id;

    QueueBroker queueBroker;

    int received,acked;
    
}