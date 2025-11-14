package info5.sar.multicast;

import java.util.HashSet;
import java.util.List;

public class PFD {
    public PFD(){

    }

    public static synchronized boolean verify_my_acks(List<Integer> ack_list){
        if (new HashSet<>(l).equals(new HashSet<>(ack_list))) {
            return true;
        } else {
            return false;
        }
    }

    public static synchronized void dead(int id){
        l.remove(id);
    }


    static List<Integer>  l ;
}
