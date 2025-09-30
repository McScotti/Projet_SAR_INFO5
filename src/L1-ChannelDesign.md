# use cases

- se connecter to a task
- accepter a connection
- ecrire sur un channel
- lire sur un Channel
- deconnecter un channel


## Broker

- Chaque broker de messagerie possède une table de hachage (à sécurité de thread) dans laquelle il stocke, en tant que clé, les ports sur lesquels une opération d'acceptation est en cours, et en tant que valeur, le statut. Ce statut est un entier qui peut prendre trois valeurs : 0, 1 et 2.

- Un registre statique répertorie tous les brokers.

- Chaque breker dispose également d'un sémaphore pour synchroniser l'accès aux ressources, telles que sa table de hachage et les variables buffer ...
## Channel 

Un objet de type Channel possède deux buffers : un pour l'écriture et un pour la lecture. Un canal est en réalité un couple de deux objets Channel, chacun correspondant à une extrémité du canal. Ces deux objets sont associés car le buffer d'écriture de l'un correspond au buffer de lecture de l'autre, et vice versa. La synchronisation des canaux se fait à l'aide de sémaphores, car les mécanismes de synchronisation de Java ne garantissent pas le comportement FIFO (premier arrivé, premier servi) entre les tâches..

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
sem: est un semaphore que le broker utilise pour synchroniser l'access a ses variables
map: correspond a la table de hashage ou les broker stocke les numero de port avec les statuts correspondants

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

## writing

Lorsque une tache veut ecrire un message a travers le Channel , elle envoit tout d'abord la taille du message , ce qui permet a la tache a l'autre bout de savoir quand est ce que la lecture du message est terminee. Deux semaphores de chaque cote du Channel assurent que l'on ne puisse pas avoir deux taches qui ecrivent du meme bout du Channel , ni deux taches qui lisent du meme bout du Channel simultanement.

Dans une operation d'ecriture si l'on ne reussit plus a transmettre d'octets , on teste si le buffer d'ecriture est plein et si tel est le cas , la tache reste bloque jusqu'a ce que un place se libere dans le buffer d'ecriture .
Algorithme fonction write(bytes,offset,length):

## Reading








# Disconnecting




# use cases

- se connecter  
- accepter une connection
- ecrire sur un channel
- lire sur un Channel
- deconnecter un channel

## Broker

- Each Broker has a Hahmap (thread-safe) where it holds as key the ports on wich an accept operation is running and as value the status. The status is actually an integer that could take three values: 0,1 and 2.

- There is a static register where is registered all the brokers.

- Each broker also has a semaphore in order to synchronize access to ressources like its hashmap and its buffer.

## Channel 

A Channel's object got two buffers , a writing one and a reading one. since that, a Channel is actually a tuple of two paired Channel's object, each of them matching a end of the channel . The two Channel'object are paired as the writing buffer of one is the reading of the other , and vice versa. Channel will be synchronize with semaphore since Java's monitors do not guarantee the FIFO trait between tasks.

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

## writing

Lorsque une tache veut ecrire un message a travers le Channel , elle envoit tout d'abord la taille du message , ce qui permet a la tache a l'autre bout de savoir quand est ce que la lecture du message est terminee. Deux semaphores de chaque cote du Channel assurent que l'on ne puisse pas avoir deux taches qui ecrivent du meme bout du Channel , ni deux taches qui lisent du meme bout du Channel simultanement.

Dans une operation d'ecriture si l'on ne reussit plus a transmettre d'octets , on teste si le buffer d'ecriture est plein et si tel est le cas , la tache reste bloque jusqu'a ce que un place se libere dans le buffer d'ecriture .




# Disconnecting


    
