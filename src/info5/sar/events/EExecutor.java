package info5.sar.events;

import java.util.LinkedList;
import java.util.List;

public class EExecutor extends Executor{
    
    private EExecutor(String name){
        super();
        this.name = name;
        this.queue= new LinkedList<Runnable>();
    }

    public String name;

    final private static EExecutor instance = new EExecutor("scott");
    
    public static EExecutor instance(){
        return instance;
    }

    // public void run(){
    //     Runnable r;
    //     r= queue.remove(0);
    //     while(r!=null){
    //         r.run();
    //         System.out.println("lsjflkjslfalnfonoajfojaofoiajfjanv v oajfoajofjoajfoanfoajfojaofoaf9");
    //         r=queue.remove(0);
    //     }
    // }

    // public void post(Runnable r){
    //     queue.add(r);
    // }

    public synchronized void post(Runnable r) {
        queue.add(r);
        notify(); // réveille le thread s’il attend
    }

    @Override
    public void run() {
        while (true) {
            Runnable r;
            synchronized (this) {
                while (queue.isEmpty()) {
                    try {
                        wait(); // attend qu'une tâche arrive
                    } catch (InterruptedException e) {
                        return; // thread arrêté
                    }
                }
                r = queue.remove(0);
            }
            r.run();
        }
    }


    List<Runnable> queue;
    
}
