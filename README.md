# 5001p3 - IRC chatserver

**_This mini-project is developed under jdk 17.0.3, and it has been confirmed that 
all the implemented functions can be
run and tested in this environment._**

## Instructions to run the program

### Server side:

At the ``/src`` level of the project, compile all the source code using the command 
 ``javac *.java``. Then, the name of the server and the port number should be specified
in order to run the server: ``java IrcServerMain <server_name> <port>``. 

**Note: Exception
will be thrown if the entered port was already used by other service.**

## Interact as client

The ``telnet`` could be used to interact with / test the server, and this is available on most operating systems.
While running your server on port 12345 in one terminal window, (for instance, the server was run by
```java IrcServerMain server_one 12345```)
another terminal window could be run on the same machine and type

```
telnet localhost 12345
```

If your server program accepts the connection,
you can then type a message directly into telnet (such as ```TIME```)
and the response from the server will be printed immediately below it in the terminal.

## Commands
_Interactive commands supported by the server._

### NICK

This message is sent by the client in order to declare what nickname the user wants to be known by.
The whole message should have the following format:

```
NICK <nickname>
```

where ``<nickname>`` is the chosen nickname. A valid nickname has 1–9 characters,
and contains only letters, numbers and underscores. It cannot start with a number.

If the nickname is valid, the server should remember that this is the nickname for this client,
and send no reply.
If the nickname is invalid, the server should reject it by sending the reply:

```
:<server_name> 400 * :Invalid nickname
```

### USER

This message should be sent by the client after they have sent a **NICK** message (see **NICK**).
The **USER** message allows the client to specify their username and their real name. 
It has the following form:

```
USER <username> 0 * :<real_name>
```

where

 * ``<username>`` is the username, which should not include spaces (the username is stored internally but not used for anything else);
 * ``0`` and ``*`` are meaningless (they are used for permissions in full IRC, but this is ignored in this mini-project); and
 * ``<real_name>``is the user’s real full name, which may include spaces.

If the message is valid, and a nickname has already been set,
then the server should store all these details and regard the client as **_registered_**.
A registered client can send and receive private messages, and join and leave channels.
A reply should be sent of the form

```
:<server_name> 001 <nickname> :Welcome to the IRC network, <nickname>
```

If the message is invalid, one of the following replies should be sent:

```
:<server_name> 400 * :Not enough arguments
:<server_name> 400 * :Invalid arguments to USER command
:<server_name> 400 * :You are already registered
```

### QUIT

This message indicates that the user wants to end their connection to the server. It has no arguments,
and the message looks like:

```
QUIT
```

If the client is _registered_(see **USER**), then the server should send the message

```
:<nickname> QUIT
```

to _all_ connected clients. The quitting user should also be removed from any channels they may be in (see **JOIN**).

Finally, the connection to the quitting client should be closed. 

### JOIN

This message is sent by a client in order to join a channel. 
A channel is like a chat room that users can join, 
and any messages sent to a channel will be seen by all users that are in the channel (see **PRIVMSG**).

This message should have the form:

```
JOIN <channel_name>
```

where ``<channel_name>`` is the name of the channel to join.
A channel name must be a single ``#`` symbol followed by any number of letters, numbers and underscores.

If the channel name is valid, this user should be added to that channel.
If no channel with this name exists on the server, it should be created.
All users in the channel (including the joining user) should be sent the message

```
:<nick> JOIN <channel_name>
```

to indicate that a new user has joined. 
That user should then receive all chat messages sent to that channel until
they leave the channel or quit the server.

If the channel name is invalid or the user is not registered, one of the following error messages should be sent:

```
:<server_name> 400 * :Invalid channel name
:<server_name> 400 * :You need to register first
```

### PART

This message is sent by a client when that user wishes to leave a channel they are in. It has the form:

```
PART <channel_name>
```

where ```<channel_name>``` is the name of the channel they want to leave. If successful, the message

```
:<nickname> PART <channel_name>
```

should be sent to all users in the channel,
and the user should be removed from the channel.
If the channel is now empty, it should be deleted from the server.
If the channel exists, but the user is not in it, the server should do nothing.

If unsuccessful, one of the following error replies might be necessary:

```
:<server_name> 400 * :You need to register first
:<server_name> 400 * :No channel exists with that name
```

### PRIVMSG

Perhaps the most important command as it allows registered users to send chat messages to each other.
It has the form:

```
PRIVMSG <target> : <message>
```

where ``<target>`` is either a channel name or a user's nickname, and ``<message>`` is the full chat message
they want to send, which may include spaces.

If ``<target>`` is a channel, this should be sent to all users in that channel;
if it is a user’s nickname, it should be sent to that user only.

A message of the form

```
:<sender_nickname> PRIVMSG <target> :<message>
```

should be sent to all appropriate users, if the message is valid.

If the message is invalid, one of the following error messages may be sent:

```
:<server_name> 400 * :No channel exists with that name
:<server_name> 400 * :No user exists with that name
:<server_name> 400 * :Invalid arguments to PRIVMSG command
:<server_name> 400 * :You need to register first
```

### NAMES

This message is sent by a registered client to request the nicknames of all users in a given channel. 
It has the form:

```
NAMES <channel_name>
```

If a channel with the given name exists, the server should send a reply of the following form:

```
:<server_name> 353 <nickname> = <channel_name> :<nicks>
```

where

 * ```<server_name>``` is the server name;
 * ```<nickname>``` is the nickname of the user that sent the request;
 * ```<channel_name>``` is the channel being queried;
 * ```<nicks>``` is a space-separated list of the nicknames of all users in the channel, for example ```moeen zak jos ben jofra jonny```.


The server might need to reply with one of the following error replies:

```
:<server_name> 400 * :You need to register first
:<server_name> 400 * :No channel exists with that name
```

### LIST

This message allows a registered client to request the names of all channels on the server.
It has no arguments, so the whole message is just:

```
LIST
```

and the server should reply with one line for each channel, of the form:

```
:<server_name> 322 <nick> <channel_name>
```
followed by one final line of the form:

```
:<server_name> 323 <nick> :End of LIST
```

where ```<nick>``` is the nickname of the user who sent the ```LIST``` command.

If the user is not registered, they should receive the same error reply as they would for ```NAMES```.



### TIME

Clients can send the simple message:

```
TIME
```

to ask the server to respond with the current date and time. The server should send a reply of the form:

```
:<server_name> 391 * :<time>
```

where ```<time>``` is the server’s local time, in the standard **ISO 8601** format, something like

```
2023-06-13T18:02:42.370473509
```

### INFO

The user can request some basic information about the server by sending the message:

```
INFO
```

The server should send a reply of the form:

```
:<server_name> 371 * :<message>
```

where ```<message>``` is a short string saying what the server is and who wrote it.

### PING

Finally, any client can send a message of the form

```
PING <text>
```

where ```<text>``` is any string of characters. The server should respond with

```
PONG <text>
```

where ```<text>``` is the exact same string sent back.
Clients could use it to make sure their connection is still active.

## Project Structure

| Folder name   | Information                                                                        |
|:--------------|------------------------------------------------------------------------------------|
| ./            | Root directory of this project.                                                    |
| ./README.md   | This file.                                                                         |
| ./src         | Contains the main method and and other object programs that implements the server. |
| ./Tests       | Contains a configuration file and a sub-directory which stores the test files.     |
| ./Tests/basic | Contains 17 tests as folders.                                                      |