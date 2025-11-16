package info5.sar.events;

import java.util.LinkedList;
import java.util.List;

public abstract class Executor extends Thread{
    List<Runnable> queue;
    
    Executor(){
        queue = new LinkedList<Runnable>();
    }

    public void run(){
        Runnable r;
        r= queue.remove(0);
        while(r!=null){
            r.run();
            r=queue.remove(0);
        }
    }

    public void post(Runnable r){
        queue.add(r);
    }
}