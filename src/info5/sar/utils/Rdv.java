package info5.sar.utils;



import info5.sar.channels.Broker;
import info5.sar.channels.CChannel;

public class Rdv {
    
    private Broker broker;

    public Rdv(Broker b){
        broker=b;
        in=null;
        Ccch=null;
        Acch=null;
        out= new CircularBuffer(5);
    }

    public synchronized CChannel connect_meet() {
        in = new CircularBuffer(5);
        Ccch = new CChannel(in, out, broker);
        notifyAll();
        while(Acch==null){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        Ccch.set_remote_end(Acch);
        return Ccch;
        
    }

    public synchronized CChannel accept_meet() {
        while (Ccch==null) {
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Acch= new CChannel(out, in, this.broker);
        notifyAll();
        Acch.set_remote_end(Ccch);
        return Acch;
    }

    public boolean met(){
        return in!=null;
    }
    private CChannel Acch;
    private CChannel Ccch;
    
    private CircularBuffer in ;
    private CircularBuffer out ;
}
