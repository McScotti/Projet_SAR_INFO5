package info5.sar.multicast;

import java.io.Serializable;

public class MessageACK implements Serializable {
    private Timestamp timestamp;
    private int id;

    public MessageACK(Timestamp timestamp,int id){
        this.timestamp=timestamp;
        this.id=id;
        
    }

    public Timestamp get_timestamp(){
        return timestamp;
    }

    public int get_id(){
        return id;
    }
}
