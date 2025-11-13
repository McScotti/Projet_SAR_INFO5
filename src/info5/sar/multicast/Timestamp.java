package info5.sar.multicast;

public class Timestamp implements Comparable<Timestamp> {
    private int id;
    private int lamportClock;

    public Timestamp(int id,int lamportClock){
        this.id=id;
        this.lamportClock=lamportClock;
    }


    @Override
    public int compareTo(Timestamp o) {
        if(this.lamportClock>o.lamportClock){
            return 1;
        }else if(this.lamportClock<o.lamportClock){
            return -1;
        }else{
            return Integer.compare(this.id, o.id);
        }
    }
}
