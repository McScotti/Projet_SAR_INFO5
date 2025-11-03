package info5.sar.events;
import info5.sar.channels.*;

public class EQueueBroker extends QueueBroker{

    @Override
    boolean bind(int port, AcceptListener listener) {
    	Task task = new Task("",this.broker);
    	Runnable r = new Runnable() {
    		public void run() {
    			MessageQueue Mq = new EMessageQueue(broker.accept(port));
    			Runnable r = new Runnable(){
                    public void run(){
                        listener.accepted(Mq);
                    }
                };
                Executor ex = EExecutorManager.get();
                ex.post(r);
                
    		}
    	};
    	task.start(r);
        
        throw new UnsupportedOperationException("Unimplemented method 'bind'");
    }

    @Override
    boolean unbind(int port) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unbind'");
    }

    @Override
    boolean connect(String name, int port, ConnectListener listener) {
        Task task = new Task("",this.broker);
        Runnable r = new Runnable(){
            public void run(){
                MessageQueue Mq = new EMessageQueue(broker.connect(name, port));
                Runnable r = new Runnable() {
                    public void run(){
                        listener.connected(Mq);
                    }
                };
                Executor ex = EExecutorManager.get();
                ex.post(r);
            }
        };
        task.start(r);
    	
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connect'");
    }
    
    private Broker broker;
    
}
