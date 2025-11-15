package info5.sar.multicast;

import java.io.Serializable;
import java.util.Objects;

public class Timestamp implements Comparable<Timestamp> , Serializable {
    private int id;
    private int clock;

    public Timestamp(int id,int clock){
        this.id=id;
        this.clock=clock;
    }


    @Override
    public int compareTo(Timestamp o) {
        if(this.clock>o.clock){
            return 1;
        }else if(this.clock<o.clock){
            return -1;
        }else{
            return Integer.compare(this.id, o.id);
        }
    }

    int get_id(){
        return id;
    }

    int get_clock(){
        return clock;
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

