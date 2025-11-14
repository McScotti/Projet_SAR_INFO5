package info5.sar.multicast;

import java.util.Objects;

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

    int get_id(){
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timestamp)) return false;
        Timestamp other = (Timestamp) o;
        return id == other.id ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

