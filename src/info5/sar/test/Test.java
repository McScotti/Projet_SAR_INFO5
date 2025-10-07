package info5.sar.test;

import info5.sar.channels.*;
import java.util.ArrayList;
import java.util.Arrays;


public class Test {

    
    public static byte[] getMessageSize(int size) {
        byte[] sizeBytes = new byte[4];
        sizeBytes[0] = (byte) (size >> 24);
        sizeBytes[1] = (byte) (size >> 16);
        sizeBytes[2] = (byte) (size >> 8);
        sizeBytes[3] = (byte) size;
        return sizeBytes;
    }

    public static int getSizeFromMessage(byte[] sizeBytes) {
        return (sizeBytes[0] << 24) | (sizeBytes[1] << 16) | (sizeBytes[2] << 8) | sizeBytes[3];
    }

    public static int readMessageSize(Channel channel) {
        byte[] sizeBytes = new byte[4];
        // We need to use a While loop to make sure we read all 4 bytes
        int bytesRead = 0;
        int response = 0;
        while (bytesRead < 4) {
            response = channel.read(sizeBytes, bytesRead, 4 - bytesRead);
            if (response == -1) {
                return -1;
            }
            bytesRead += response;
            System.out.print(" j ai lu "+response+" byte de taille ");
            System.out.println(Arrays.toString(sizeBytes));
        }
        System.out.println(Arrays.toString(sizeBytes));
        return getSizeFromMessage(sizeBytes);
    }

    public static byte[] readSizeAndMessage(Channel channel) {
        int messageSize = readMessageSize(channel);
        if (messageSize <= 0) {
            return null;
        }

        byte[] buffer = new byte[messageSize];
        int bytesRead = 0;

        while (bytesRead < messageSize) {
            int response = channel.read(buffer, bytesRead, messageSize - bytesRead);

            if (response == -1) {
                return null;
            }

            bytesRead += response;
        }

        if (VERBOSE) {
            System.out.println("Received message: " + new String(buffer, 0, buffer.length));
        }

        return buffer;
    }

    public static void writeSizeAndMessage(Channel channel, byte[] message) {
        byte[] sizeBytes = getMessageSize(message.length);
        byte[] buffer = new byte[sizeBytes.length + message.length];
        System.arraycopy(sizeBytes, 0, buffer, 0, sizeBytes.length);
        System.arraycopy(message, 0, buffer, sizeBytes.length, message.length);

        int bytesWritten = 0;
        while (bytesWritten < buffer.length) {
            int response = channel.write(buffer, bytesWritten, buffer.length - bytesWritten);
            if (response == -1) {
                return;
            }
            bytesWritten += response;
            //System.out.print(" j ai ecrit "+response+" bytes ");
        }

        if (VERBOSE) {
            System.out.println("Sent message: " + new String(message, 0, message.length));
        }
    }

    protected static Boolean VERBOSE = true;
    public static void main(String[] args) {
        // Create a new test object
        Test test = new Test();
        // Run the test
        test.test();

    }

    protected class EchoServer implements Runnable {
        protected Broker broker;
        protected boolean isAccept;
        protected String brokerName;
        protected int port;

        public EchoServer(Broker broker, boolean isAccept, String brokerName, int port) {
            this.broker = broker;
            this.isAccept = isAccept;
            this.brokerName = brokerName;
            this.port = port;
        }
        @Override
        public void run() {
            try {
                Channel serverChannel;
                int nbMessages = 0;
                if (isAccept) {
                    serverChannel = (Channel) broker.accept(this.port);
                
                    while (nbMessages < 10) {
                        byte[] buffer = readSizeAndMessage(serverChannel);
    
                        writeSizeAndMessage(serverChannel, buffer);
    
                        nbMessages++;
                    }

                } else {
                    serverChannel = (Channel) broker.connect(this.brokerName, this.port);

                    while (nbMessages < 10) {

                        String message = "Broker " + brokerName + " message number " + nbMessages;
                        writeSizeAndMessage(serverChannel, message.getBytes());

                        readSizeAndMessage(serverChannel);
    
                        nbMessages++;
                    }
                }
                
                serverChannel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    public void test() {
        Broker broker = new CBroker("Broker1");

        Runnable rs = new Runnable() {
            @Override
            public void run() {
                try {
                    // Listen on port 8080 for incoming connections
                    Channel serverChannel = (Channel) broker.accept(8080);
                    if (serverChannel == null) {
                        return;
                    }
                    int nbMessages = 0;

                    while (nbMessages < 10) {
                        // Read message
                        byte[] buffer = readSizeAndMessage(serverChannel);


                        // Echo the message back to the client
                        try {
                            writeSizeAndMessage(serverChannel, buffer);
                        } catch (Exception e) {
                            System.out.print("le serveur ");
                            e.printStackTrace();
                        }
                        //writeSizeAndMessage(serverChannel, buffer);


                        nbMessages++;
                    }
                    serverChannel.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        Runnable rc = new Runnable() {
            @Override
            public void run() {
                try {
                    // Connect to the server on port 8080
                    Channel clientChannel = (Channel) broker.connect("Broker1", 8080);
                    

                    int nbMessages = 0;

                    while (nbMessages < 10) {
                        String message = "Message " + nbMessages;

                        //writeSizeAndMessage(clientChannel, message.getBytes());
                        try {
                            writeSizeAndMessage(clientChannel, message.getBytes());
                        } catch (Exception e) {
                            System.out.print("le client ");
                            e.printStackTrace();
                        }


                        // Read the message from the channel

                        byte[] echoBuffer = readSizeAndMessage(clientChannel);

                        assert echoBuffer != null;
                        assert new String(echoBuffer, 0, echoBuffer.length).equals(message);
                        assert echoBuffer.length == message.length();

                        nbMessages++;
                    }

                    clientChannel.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Task serverTask = new Task("serverTask",broker);

        Task clientTask = new Task("clientTask",broker );
        try {
            serverTask.start(rs);
            clientTask.start(rc);
            serverTask.join();
            clientTask.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}













