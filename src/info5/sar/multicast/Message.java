package info5.sar.multicast;

import java.io.Serializable;

public class Message implements Serializable {
    public Message(String content){
        this.content=content;
    }

    public void set_timestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }

    public Timestamp get_timestamp(){
        return timestamp;
    }

    public String content;
    private Timestamp timestamp;
}
