package info5.sar.events;

import info5.sar.events.MessageQueue;
import info5.sar.events.QueueBroker;

public class TestClientServer {
    public static void main(String[] args) throws Exception {
        
        Runnable r = new Runnable() {
            public void run(){

                int port = 5555;

                // Création de ton broker (à remplacer par ton implémentation)
                QueueBroker serverBroker = new EQueueBroker("server");
                QueueBroker clientBroker = new EQueueBroker("client");

                // --- SERVEUR ---
                serverBroker.bind(port, new QueueBroker.AcceptListener() {
                    @Override
                    public void accepted(MessageQueue queue) {
                        System.out.println("[SERVER] Client connecté !");
                        queue.receive(new MessageQueue.Listener() {
                            @Override
                            public void received(byte[] msg) {
                                String message = new String(msg);
                                System.out.println("[SERVER] Reçu : " + message);
                                queue.send("ACKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK".getBytes(),0,"ACKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK".getBytes().length);
                            }

                            @Override
                            public void closed() {
                                System.out.println("[SERVER] Connexion fermée");
                            }
                        });
                    }
                });

                // --- CLIENT ---
                clientBroker.connect("server", port, new QueueBroker.ConnectListener() {
                    @Override
                    public void connected(MessageQueue queue) {
                        System.out.println("[CLIENT] Connecté au serveur !");
                        queue.send("Hellooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo".getBytes(),0,"Hellooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo".getBytes().length);
                        queue.receive(new MessageQueue.Listener() {
                            @Override
                            public void received(byte[] msg) {
                                System.out.println("[CLIENT] Réponse : " + new String(msg));
                                queue.close(new MessageQueue.Listener(){

                                    @Override
                                    public void received(byte[] msg) {
                                        
                                    }

                                    @Override
                                    public void closed() {
                                        System.out.println("[CLIENT] Connexion fermée");
                                    }
                                    
                                });
                            }

                            @Override
                            public void closed() {
                                System.out.println("[CLIENT] Connexion fermée");
                            }
                        });
                    }

                    @Override
                    public void refused() {
                        System.err.println("[CLIENT] Connexion refusée !");
                    }
                });
                System.out.println("Hello depuis le thread : ");
            }
        };

        
        EExecutor.instance().post(r);
        EExecutor.instance().run();

    }
}


// [SERVER] Client connecté !
// [SERVER] Reçu : Hello
// [CLIENT] Connecté au serveur !
// [CLIENT] Réponse : ACK
// [SERVER] Connexion fermée
// [CLIENT] Connexion fermée
