# use cases

- connect to a task
- accept a connection
- write a message
- Read a message
- Disconnect a channel


## Broker

- Each Broker has a Hahmap (thread-safe) where it holds as key the ports on wich an accept operation is running and as value the status. The status is actually an integer that could take three values: 0,1 and 2.

- There is a static register where is registered all the brokers.

- Each broker also has a semaphore in order to synchronize access to ressources like its hashmap and its buffer.

## Channel 

A Channel's object got two buffer , a writing one and a reading one. since that a Channel is actually a tuple of two paired Channel's object, each of them matching a end of the channel . The two Channel'object are paired because the writing buffer of one is the reading of the other , and vice versa. Channel will be synchronize with semaphore since Java's monitors do not guarantee the FIFO trait between tasks.

# Connecting and accepting
```
Algorithme fonction connect(int port,string name):
    broker = Broker.getBroker(name) 
    While(t<T ){
        if(broker.map(port)!=0){
            wait(10s)
            t+=10;
        }else{
            sem.P()
            broker.sem.P()
            //buffer1 = new Buffer
            broker.map(port)=1
            broker.sem.V()
            while(Broker.map(port)=0){
                wait
            }
            //buffer2 = broker.buffer
            channel = Channel(broker.buffer2,broker.buffer1)
            broker.map(port)=2
            sem.V()
            return channel
        }
    }
```

```
Algorithme fonction accept(int port):
    while(t<T){
        if(this.port!=1){
            wait(10s)
            t+=10
        }else{
            sem.p()
            buffer1 = new Buffer()
            buffer2 = new Buffer()
            channel = new Channel(buffer1,buffer2)
            map(port)=0
            while(map(port)!=2){
                wait
            }
            sem.V()
            return channel
        }
    }

```
# Writing and Reading










# Disconnecting


    
