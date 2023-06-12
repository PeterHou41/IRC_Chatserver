import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
* Represents a IrcServer which can have multiple clients
* (from the corresponding connection) connected and multiple
* channel opened on.
*/
public class IrcServer {

    private String serverName;
    private ExecutorService scalableThreadPool;
    private ServerSocket ircS;
    private List<ConnectionHandler> allConnections;
    private List<Channel> allChannels;
    private List<String> registeredUsers;
    private List<String> registeredChannels;

    /**
    * Open an IrcServer on specified port with specified server name,
    * ready to listen for connection.
    * @param serverName Name of the server.
    * @param port Port number.
    */
    public IrcServer(String serverName, int port) {
        if (port < Configuration.PORTNUM_MIN || port > Configuration.PORTNUM_MAX) {
            throw new IllegalArgumentException("Out of the range of port numbers!");
        }
        try {
            this.serverName = serverName;
            ircS = new ServerSocket(port);
            /*Scalable thread pool that can run multiple threads.*/
            this.scalableThreadPool = Executors.newCachedThreadPool();
            allConnections = new ArrayList<>();
            allChannels = new ArrayList<>();
            registeredUsers = new ArrayList<>();
            registeredChannels = new ArrayList<>();

            while (true) {
                Socket connection = ircS.accept();
                // A pair of ClientThread and ConnectionHandler
                // will only be opened until the connection was returned
                ConnectionHandler newClient = new ConnectionHandler(connection);
                newClient.setIrcServer(this);
                allConnections.add(newClient);
                scalableThreadPool.execute(newClient);
            }
        }
        catch (IOException ioe) {
            System.out.println("Unknown IOException thrown when invoking IrcServer() :"
                    + ioe.getMessage());
        }
    }

    /**
     * Public getter method to return the name of this server.
     * @return The name of this server.
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Public method to add a reference of a registered user to this server.
     * @param nickname The nickname of the user.
     */
    public void addRegisteredUser(String nickname) {
        registeredUsers.add(nickname);
    }

    /**
     * Public method to handle the request of quitting this server from registered client.
     * @param text The message to be sent to all connected clients.
     * @param registeredClient Client that send the request.
     */
    public void handleQuitRequest(String text, ConnectionHandler registeredClient) {
        for (ConnectionHandler poll: allConnections) {
            poll.printToWriter(text);
        }
        String allNicksInAChannel;
        String clientNick = registeredClient.getNickname();
        for (Channel poll: allChannels) {
            allNicksInAChannel = poll.getCurrentClientsNicks();
            if (allNicksInAChannel.contains(clientNick)) {
                poll.forcedQuitJoinedClient(registeredClient);
            }
        }
    }

    /**
     * Public method to handle the JOIN command from a registered user.
     * @param channelName The name of the channel.
     * @param client Client who wishes to join the channel.
     */
    public void handleJoinRequest(String channelName, ConnectionHandler client) {
        //Add client to the channel if the channel is already opened.
        if (checkChannelExistence(channelName)) {
            for (Channel poll: allChannels) {
                if (channelName.equals(poll.getChannelname())) {
                    poll.addJoinedClient(client);
                    return;
                }
            }
        }
        //Open a new channel on this server and add this client to the channel.
        else {
            Channel newRoom = new Channel(channelName);
            newRoom.setIrcServer(this);
            newRoom.addJoinedClient(client);
            registeredChannels.add(channelName);
            allChannels.add(newRoom);
        }
    }

    /**
     * Public method to handle the PART command from a registered user.
     * @param channelName The name of the channel.
     * @param client Client who wishes to leave the channel.
     */
    public void handlePartRequest(String channelName, ConnectionHandler client) {
        for (Channel poll: allChannels) {
            if (channelName.equals(poll.getChannelname())) {
                poll.partJoinedClient(client);
                //Remove the reference of the channel from this server if it's empty.
                if (poll.getCurrentClientsNum() == 0) {
                    allChannels.remove(poll);
                    registeredChannels.remove(poll.getChannelname());
                }
                return;
            }
        }
    }

    /**
     * Public method to process the request of sending private message to a user.
     * @param target The nickname of the user.
     * @param text String sent to the user.
     */
    public void handlePrivateMsgRequest(String target, String text) {
        for (ConnectionHandler poll: allConnections) {
            if (target.equals(poll.getNickname())) {
                poll.printToWriter(text);
                return;
            }
        }
    }

    /**
     * Public method to process the request of sending private(broadcast) message to a channel.
     * @param target The name of the channel.
     * @param text String to broadcast to all joined clients of the channel.
     */
    public void handleBroadcastMsgRequest(String target, String text) {
        for (Channel poll: allChannels) {
            if (target.equals(poll.getChannelname())) {
                poll.broadcastMsg(text);
                return;
            }
        }
    }

    /**
     * Public method to handle the request of getting all joined users' nickname of a channel.
     * @param channelName The name of the channel.
     * @return  String contains nicknames of all users in the channel.
     */
    public String handleNamesRequest(String channelName) {
        String allUsers = "";
        for (Channel poll: allChannels) {
            if (channelName.equals(poll.getChannelname())) {
                allUsers =  poll.getCurrentClientsNicks();
                break;
            }
        }
        return allUsers;
    }

    /**
     * Public method to return the name of all channels opened on this server.
     * @return The String contains all valid Channel's name.
     */
    public String handleListRequest() {
        String allChannelsNm = "";
        for (Channel poll: allChannels) {
            allChannelsNm += poll.getChannelname();
        }
        return allChannelsNm;
    }

    /**
    * Public method to return whether the channel with the specified
    * channel name opened on this server.
    * @param channelName Name of the channel.
    * @return Whether the channel exist.
    */
    public boolean checkChannelExistence(String channelName) {
        for (Channel poll: allChannels) {
            if (channelName.equals(poll.getChannelname())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Public method to return whether there's such registered user with
     * specified nickname on this server.
     * @param nickname Name of the channel.
     * @return Whether the registered user exist.
     */
    public boolean checkUserExistence(String nickname) {
        return registeredUsers.contains(nickname);
    }

    /**
     * Public method to remove the reference of a connection stored to this server instance.
     * @param connection The disconnected ConnectionHandler.
     */
    public void removeConnectionReference(ConnectionHandler connection) {
        this.allConnections.remove(connection);
        System.out.println("A reference of a connection was removed successfully");
    }

}
