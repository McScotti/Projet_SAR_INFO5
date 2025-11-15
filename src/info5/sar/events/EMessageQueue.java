package info5.sar.events;

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
        System.out.println("minimum wr");
        return send(bytes,0,bytes.length);
    }

    @Override
    public
    boolean send(byte[] bytes, int offset, int length) {
        if(offset<0 || length<0 || offset >bytes.length || offset+length >bytes.length+1 || length==0 ){
            throw new IllegalArgumentException("the range indicated is illegal");
        }
        WriterAutomata wr = new WriterAutomata(channel, bytes);
        wr.process();
        return true;
    }

    public boolean receive(Listener listener){
        ReaderAutomata rd = new ReaderAutomata(channel, listener);
        rd.process();
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
    }
    private Channel channel;

    private Listener listener;
}
