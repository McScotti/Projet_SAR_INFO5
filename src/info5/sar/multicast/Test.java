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
                

                Peer peer0 = new Peer("peer0", queueBroker0, 0, 3);
                Peer peer1 = new Peer("peer1", queueBroker1, 1, 3);
                Peer peer2 = new Peer("peer2", queueBroker2, 2, 3);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                peer1.multicast(" ", 1);
                //peer0.multicast("Message " + 0 + " from Peer " + 0,0);
                //peer1.multicast("Message " + 1 + " from Peer " + 1,1);
                //peer2.multicast("Message " + 2 + " from Peer " + 2,2);
            }
            
        };

        EExecutor.instance().post(R);
        EExecutor.instance().run();
    }
}
