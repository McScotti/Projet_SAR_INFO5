package info5.sar.multicast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PFD {
    public PFD(){

    }

    public static synchronized boolean verify_my_acks(List<Integer> ack_list){
        if (l.keySet().equals(new HashSet<>(ack_list))) {
            return true;
        } else {
            return false;
        }
    }

    public static synchronized void dead(int id){
        l.remove(id);
    }

    public static synchronized void insert(int id,Peer p){
        l.put(id, p);
    }


    static Map<Integer,Peer>  l= new HashMap<>() ;
}
