package info5.sar.queues;

import java.nio.ByteBuffer;

import info5.sar.channels.Channel;

public class QMessageQueue extends MessageQueue {

    QMessageQueue(Channel ch){
        this.channel=ch;
        sstate=sState.SEND_LENGTH;
        rstate=rState.RECEIVE_LENGTH;
    }

    @Override
    void send(byte[] bytes, int offset, int length) {

        if(offset<0 || length<0 || offset >bytes.length || offset+length >bytes.length+1 || length==0 ){
            throw new IllegalArgumentException("the range indicated is illegal");
        }
        int wrote;
        int acc;
        
        switch (sstate) {

            case sState.SEND_LENGTH:
                
                 wrote=0;
                byte[] length_bytes = ByteBuffer.allocate(4).putInt(length).array();
                while(wrote!=4){
                    try {
                        acc= channel.write(length_bytes, wrote, 4-wrote);
                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("the queue is already closed");
                    }
                    if(acc==0){
                        throw new IllegalStateException("the queue has been closed");
                    }
                    wrote+=acc;
                }
                sstate = sState.SEND_MESSAGE;
                

            case sState.SEND_MESSAGE:

                wrote=0;
                acc=0;
                while(wrote!=length){
                    try {
                        acc = channel.write(bytes, wrote, length-wrote);
                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("the queue is already closed");
                    }
                    if(acc==0){
                        throw new IllegalStateException("the queue has been closed");
                    }
                    wrote+=acc;
                }
                sstate = sState.SEND_LENGTH;
                break;

            default:
                break;
        }
        
    }

    @Override
    byte[] receive() {

        int readed;
        int acc;
        int length=0;
        byte[] message_bytes=new byte[2];

        switch (rstate) {

            case rState.RECEIVE_LENGTH:

                byte[] length_bytes = new byte[4];
                readed=0;
                acc=0;
                while(readed!=4){
                    try {
                        acc = channel.read(length_bytes, readed, 4-readed);
                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("the queue is already closed");
                    }
                    if(acc==0){
                        throw new IllegalStateException("the queue has been closed");
                    }
                    readed+=acc;
                }
                length= ByteBuffer.wrap(length_bytes).getInt();
                rstate = rState.RECEIVE_MESSAGE;

            case rState.RECEIVE_MESSAGE:

                message_bytes = new byte[length];
                readed=0;
                acc=0;
                while(readed!=length){
                    try {
                        acc = channel.read(message_bytes, readed, 4-readed);
                    } catch (IllegalStateException e) {
                        throw new IllegalStateException("the queue is already closed");
                    }
                    if(acc==0){
                        throw new IllegalStateException("the queue has been closed");
                    }
                    readed+=acc;
                }
                rstate = rState.RECEIVE_LENGTH;
                return message_bytes;
                
            default:
                break;
        }

        return message_bytes;
    }

    @Override
    void close() {
        channel.disconnect();
    }

    @Override
    boolean closed() {
        return channel.disconnected();
        
    }

    private Channel channel;

    private enum sState{SEND_LENGTH,SEND_MESSAGE}

    private enum rState{RECEIVE_LENGTH, RECEIVE_MESSAGE}

    private sState sstate;
    private rState rstate;
    
}
