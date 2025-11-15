package info5.sar.events;
import info5.sar.channels.*;

public class EQueueBroker extends QueueBroker{

    public EQueueBroker(String name){
        this.broker = new CBroker(name);
    }

    @Override
    public
    boolean bind(int port, AcceptListener listener) {
    	Task task = new Task("",this.broker);
    	Runnable r = new Runnable() {
    		public void run() {
    			try {
                    MessageQueue Mq = new EMessageQueue(EQueueBroker.this.broker.accept(port));
                    Runnable r = new Runnable(){
                        public void run(){
                            listener.accepted(Mq);
                        }
                    };
                    EExecutor.instance().post(r);
                } catch (IllegalArgumentException e) {
                    System.out.print(broker.getName()+" faute sur "+port);
                }
                
    		}
    	};
    	task.start(r);
        return true;
        
    }

    @Override
    public
    boolean unbind(int port) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unbind'");
    }

    @Override
    public
    boolean connect(String name, int port, ConnectListener listener) {
        Task task = new Task("",this.broker);
        Runnable r = new Runnable(){
            public void run(){
                MessageQueue Mq = new EMessageQueue(EQueueBroker.this.broker.connect(name, port));
                Runnable r = new Runnable() {
                    public void run(){
                        listener.connected(Mq);
                    }
                };
                EExecutor.instance().post(r);
            }
        };
        task.start(r);
    	
        return true;
    }
    
    private Broker broker;
    
}
