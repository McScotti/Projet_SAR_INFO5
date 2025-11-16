package info5.sar.events;

import java.util.Arrays;

import info5.sar.channels.Channel;

public class EMessageQueue extends MessageQueue {

    @Override
    public
    void setListener(Listener l) {
        this.listener = l;
    }

    @Override
    public
    boolean send(byte[] bytes) {
        return send(bytes,0,bytes.length);
    }

    @Override
    public
    boolean send(byte[] bytes, int offset, int length) {
        if(offset<0 || length<0 || offset >bytes.length || offset+length >bytes.length+1 || length==0 ){
            throw new IllegalArgumentException("the range indicated is illegal");
        }
        if(this.wr.get_sending()){
            Runnable R = new Runnable() {

                @Override
                public void run() {
                    EMessageQueue.this.send(bytes, offset, length);
                }
                
            };

            EExecutor.instance().post(R);
        }else{
            wr.process(Arrays.copyOfRange(bytes, offset, offset+length));
        }
        //WriterAutomata wr = new WriterAutomata(channel, bytes);
        return true;
    }

    public boolean receive(Listener listener){
        if(this.rd.get_reading()){
            Runnable R = new Runnable() {

                @Override
                public void run() {
                   EMessageQueue.this.receive(listener);
                }
                
            };
            EExecutor.instance().post(R);
        }else{
            //ReaderAutomata rd = new ReaderAutomata(channel);
            rd.process(listener);
        }
        
        return true;
    }

    @Override
    public
    void close(Listener listener) {
        Runnable r = new Runnable() {
            public void run(){
                channel.disconnect();
                listener.closed();
            }
        };
        EExecutor.instance().post(r);
        // Runnable r = new Runnable() {
        //     public void run(){
        //         EMessageQueue.this.listener.closed();
        //     }
        // };
        // EExecutor.instance().post(r);
        
    }

    @Override
    public
    boolean closed() {
        return channel.disconnected();
    }
    
    public EMessageQueue(Channel channel) {
    	this.channel = channel;
        this.wr= new WriterAutomata(channel);
        this.rd = new ReaderAutomata(channel);
    }
    private Channel channel;

    private Listener listener;

    private WriterAutomata wr;
    private ReaderAutomata rd;
}