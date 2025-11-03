package info5.sar.events;

import java.nio.ByteBuffer;

import info5.sar.channels.Channel;

public class WriterAutomata {
    private int wrote;
    private int acc;
    private int length;
    private byte[] length_bytes;
    private byte[] message;
    private Channel channel;


    public WriterAutomata(Channel channel,byte[] message ){
        this.wrote=0;
        this.message=message;
        this.channel=channel;
        this.acc=0;
        this.length=message.length;
        this.length_bytes = ByteBuffer.allocate(4).putInt(length).array();
    }

    public void process(){


        switch (sstate) {

            case sState.SEND_LENGTH:

                try {
                    acc = channel.write(length_bytes, wrote, 4-wrote);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("the queue is already closed");
                }
                if(acc==0){
                    throw new IllegalStateException("the queue has been closed");
                }
                wrote+=acc;
                if(wrote==4){
                    sstate= sState.SEND_MESSAGE;
                    wrote=0;
                }else{
                    Runnable r = new Runnable() {
                        public void run(){
                            WriterAutomata.this.process();
                        }
                    };
                    EExecutorManager.get().post(r);
                    break;
                }

                
            case sState.SEND_MESSAGE:

                try {
                    acc = channel.write(message, wrote, length-wrote);
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("the queue is already closed");
                }
                if(acc==0){
                    throw new IllegalStateException("the queue has been closed");
                }
                wrote+=acc;
                if(wrote==length){
                    sstate= sState.SEND_LENGTH; 
                    break;
                    
                }else{
                    Runnable r = new Runnable() {
                        public void run(){
                            WriterAutomata.this.process();
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
