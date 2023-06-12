import java.util.List;
import java.util.ArrayList;


/**Represents a channel opened on the IrcServer.*/
public class Channel {

    private IrcServer ircS;
    private String channelName;
    private List<ConnectionHandler> joinedClients;

     /**
     * Open a channel on the IrcServer with specified channel name.
     * @param channelName Name of this channel.
     */
    public Channel(String channelName) {
        this.channelName = channelName;
        joinedClients = new ArrayList<>();
    }

    /**
     * Public getter to return the name of this channel.
     * @return The name of this channel.
     */
    public String getChannelname() {
        return channelName;
    }

    /**
     * Public getter to return the number of joined clients of this channel.
     * @return The number of currently joined clients.
     */
    public int getCurrentClientsNum() {
        return joinedClients.size();
    }

    /**
     * Public getter to return the nickname of all joined clients of this channel.
     * @return String contains space-separated list of the nicknames of all joined clients.
     */
    public String getCurrentClientsNicks() {
        String allNicks = "";
        for (ConnectionHandler poll: joinedClients) {
            allNicks += poll.getNickname() + " ";
        }
        allNicks = allNicks.substring(0, allNicks.length() - 1);
        return allNicks;
    }

    /**
     * Public method to set the IRC Server that this channel opened on.
     * @param ircS The IRC Server this channel opened on.
     */
    public void setIrcServer(IrcServer ircS) {
        this.ircS = ircS;
    }

    /**
     * Public method to add a client to this channel.
     * @param client A connection from a client.
     */
    public void addJoinedClient(ConnectionHandler client) {
        joinedClients.add(client);
        this.broadcastMsg(":" + client.getNickname()
                + " " + Configuration.JOIN + " " + channelName);
    }

    /**
     * Public method to remove a client from this channel and sending PART information to other clients.
     * @param client A connection from a client.
     */
    public void partJoinedClient(ConnectionHandler client) {
        this.broadcastMsg(":" + client.getNickname()
                + " " + Configuration.PART + " " + channelName);
        joinedClients.remove(client);
    }

    /**
     * Public method to remove a client from this channel directly without sending PART to other clients.
     * @param client A connection from a client.
     */
    public void forcedQuitJoinedClient(ConnectionHandler client) {
        joinedClients.remove(client);
    }

    /**
     * Public method to print some text to all joined users of this channel.
     * @param text String to be sent.
     */
    public void broadcastMsg(String text) {
        ConnectionHandler poll;
        for (int counter = 0; counter < joinedClients.size(); counter++) {
            poll = joinedClients.get(counter);
            poll.printToWriter(text);
        }
    }
}
