package info5.sar.channels;

import java.util.Hashtable;
import java.util.Map;

public class  CBrokerManager {
    
    
    public static synchronized CBroker get(String name){
        return BrokerRegister.get(name);
            
    }

    public static synchronized void post(CBroker broker){
        if(CBrokerManager.get(broker.name)==null){
            BrokerRegister.put(broker.name, broker);
        }else{
            throw new IllegalArgumentException("A broker with the same name already exits");
        }
    }

    private static Map<String, CBroker> BrokerRegister = new Hashtable<>();

}