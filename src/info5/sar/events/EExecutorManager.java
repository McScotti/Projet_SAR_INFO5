package info5.sar.events;

import java.util.Hashtable;
import java.util.Map;



public class EExecutorManager {
    
    public static synchronized EExecutor get(String name){
        return ExecutorRegister.get(name);
            
    }

    public static synchronized EExecutor get(){
        return ExecutorRegister.getOrDefault(ExecutorRegister, get());
    }

    public static synchronized void post(EExecutor executor){
        if(EExecutorManager.get(executor.name)==null){
            ExecutorRegister.put(executor.name, executor);
        }else{
            throw new IllegalArgumentException("A broker with the same name already exits");
        }
    }

    private static Map<String, EExecutor> ExecutorRegister = new Hashtable<>();
}
