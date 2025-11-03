package info5.sar.events;

import java.nio.ByteBuffer;

import info5.sar.channels.Channel;

public class ReaderAutomata {
    
    private int readed;
    private int acc;
    private int length;
    private byte[] message_bytes;
    private byte[] length_bytes;
    private Channel channel;
    private EMessageQueue.Listener listener;


    public ReaderAutomata(Channel channel, EMessageQueue.Listener listener ){
        this.readed=0;
        this.channel=channel;
        this.acc=0;
        this.length=0;
        this.message_bytes=new byte[2];
        this.length_bytes = new byte[4];
        this.listener=listener;
    }

    public void process(){


        switch (rstate) {

            case rState.RECEIVE_LENGTH:

                try {
                    acc = channel.read(length_bytes, readed, 4-readed);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("the queue is already closed");
                }
                if(acc==0){
                    throw new IllegalStateException("the queue has been closed");
                }
                readed+=acc;
                if(readed==4){
                    length= ByteBuffer.wrap(length_bytes).getInt();
                    rstate= rState.RECEIVE_MESSAGE;
                    message_bytes = new byte[length];
                    readed=0;
                }else{
                    Runnable r = new Runnable() {
                        public void run(){
                            ReaderAutomata.this.process();
                        }
                    };
                    EExecutorManager.get().post(r);
                }

                
            case rState.RECEIVE_MESSAGE:

                try {
                    acc = channel.read(message_bytes, readed, length-readed);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("the queue is already closed");
                }
                if(acc==0){
                    throw new IllegalStateException("the queue has been closed");
                }
                readed+=acc;
                if(readed==length){
                    rstate= rState.RECEIVE_LENGTH; 
                    listener.received(message_bytes);
                    break;
                    
                }else{
                    Runnable r = new Runnable() {
                        public void run(){
                            ReaderAutomata.this.process();
                        }
                    };
                    EExecutorManager.get().post(r);
                }
                break;
        }

    }

    private enum sState{SEND_LENGTH,SEND_MESSAGE}

    private enum rState{RECEIVE_LENGTH, RECEIVE_MESSAGE}

    private sState sstate;
    private rState rstate;
}


// while(readed!=4){
                //     try {
                //         acc = channel.read(length_bytes, readed, 4-readed);
                //     } catch (IllegalStateException e) {
                //         throw new IllegalStateException("the queue is already closed");
                //     }
                //     if(acc==0){
                //         throw new IllegalStateException("the queue has been closed");
                //     }
                //     readed+=acc;
                // }
                // length= ByteBuffer.wrap(length_bytes).getInt();
                // rstate = rState.RECEIVE_MESSAGE;