package info5.sar.events;

import info5.sar.channels.Channel;

public class EMessageQueue extends MessageQueue {

    @Override
    void setListener(Listener l) {
        this.listener = l;
    }

    @Override
    boolean send(byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'send'");
    }

    @Override
    boolean send(byte[] bytes, int offset, int length) {
        if(offset<0 || length<0 || offset >bytes.length || offset+length >bytes.length+1 || length==0 ){
            throw new IllegalArgumentException("the range indicated is illegal");
        }
        WriterAutomata wr = new WriterAutomata(channel, bytes);
        wr.process();
        return true;
    }

    boolean receive(Listener listener){
        ReaderAutomata rd = new ReaderAutomata(channel, listener);
        rd.process();
        return true;
    }

    @Override
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
    boolean closed() {
        return channel.disconnected();
    }
    
    public EMessageQueue(Channel channel) {
    	this.channel = channel;
    }
    private Channel channel;

    private Listener listener;
}
