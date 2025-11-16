package info5.sar.multicast;

import info5.sar.events.EExecutor;
import info5.sar.events.EQueueBroker;
import info5.sar.events.QueueBroker;

public class Test {
    public static void main(String[] args) {
        Runnable R = new Runnable() {

            @Override
            public void run() {
                QueueBroker queueBroker0 = new EQueueBroker("peer0");
                QueueBroker queueBroker1 = new EQueueBroker("peer1");
                QueueBroker queueBroker2 = new EQueueBroker("peer2");
                QueueBroker queueBroker3 = new EQueueBroker("peer3");

                // Runnable R = new Runnable() {
                //     @Override
                //     public void run() {
                //         try {
                //             for (int i = 1; i <= 3; i++) {
                //                 String msg = "Message " + i + " from Peer " + i;
                //                 System.out.println("[Peer " + i + "] multicast → " + msg);
                //                 multicast(msg);

                //                 // petit délai pour éviter que tout parte en même temps
                //                 Thread.sleep(500);
                //             }
                //         } catch (Exception e) {
                //             e.printStackTrace();
                //         }
                //     }
                // };
                

                Peer peer0 = new Peer("peer0", queueBroker0, 0, 4);
                Peer peer1 = new Peer("peer1", queueBroker1, 1, 4);
                Peer peer2 = new Peer("peer2", queueBroker2, 2, 4);
                Peer peer3 = new Peer("peer3", queueBroker3, 3, 4);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //peer0.multicast("Message " + 0 + " from Peer " + 0,0);
                //peer1.multicast("Message " + 1 + " from Peer " + 1,1);
                peer2.multicast("Message " + 2 + " from Peer " + 2,2);
                peer3.multicast("Message " + 3 + " from Peer " + 3,3);
            }
            
        };

        EExecutor.instance().post(R);
        EExecutor.instance().run();
    }
}
