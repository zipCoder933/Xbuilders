# Multiplayer
https://youtu.be/KBBJqPL5-eU?feature=shared

## tcp vs udp protocols
### udp
faster, but the order of messages could be jumbled

in order to order the messages properly, we use a buffer. a buffer waits for X amount of messages to be sent before rearrranging them and playing the timeline starting from the first message sent

### tcp
slower, but message order and data are kept intact

## adress
* ip adress
    * computer adress
* port
    * process adress within a computer


## server model
server model is a multiplayer layout where there is a centralized server keeping track of everything in the game. when someone makes a change to the game in any way, including to themselves, that change is sent to server and transmittted to other players
* sometimes due to latency, or refresh rate, the clients must predict and move some objects themselves without waiting for the server to send an update
    * this applies to host and p2p models too

## host model
like a server model but the server is also a player

## peer to peer model
all computers are servers.