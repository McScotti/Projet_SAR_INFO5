package info5.sar.multicast;

public class Message {
    public Message(String content){
        this.content=content;
    }

    public void set_timestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }

    public Timestamp get_timestamp(){
        return timestamp;
    }

    private String content;
    private Timestamp timestamp;
}
