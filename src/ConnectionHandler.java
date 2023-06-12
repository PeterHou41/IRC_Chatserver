import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
* Represents the synthesis of a connected client and
* the input processing unit for the connection.
*/
public class ConnectionHandler implements Runnable {

    private IrcServer ircS;
    private InputStream clientIs;
    private OutputStream clientOs;
    private BufferedReader clientBr;
    private PrintWriter clientPw;
    private String nickname;
    private String realname;
    private String username;
    private boolean quitStatus;
    private boolean registered;

    /**
    * Instantiate a synthesis.
    * The function of the input processing
    * via acquiring InputStream and OutputStream via socket(connection).
    * Client's information would also be recorded to this instance.
    * @param socket Connection accepted by the ServerSocket.
    */
    public ConnectionHandler(Socket socket) {
        try {
            clientIs = socket.getInputStream(); // Get data from client on this InputStream.
            clientOs = socket.getOutputStream(); // Send data back to client via this OutputStream.
            clientBr = new BufferedReader(new InputStreamReader(clientIs));
            clientPw = new PrintWriter(new OutputStreamWriter(clientOs), true);
            nickname = "*"; // Initialise with this for structural reply to be sent.
            registered = false;
            quitStatus = false;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
    * Entrance of the client message handling method, processing client's input
    * recurrently until "QUIT" was detected or unknown interruption occur.
    */
    private void processClientMessage() throws IOException {
        String originalLine;
        while (!quitStatus) {
            originalLine = clientBr.readLine();
            String[] partitionedLine;
            String command, arguments;
            /* Connection will not be closed instantly
               if client hit "Enter" directly. */
            if (originalLine == null || originalLine.equals("")) {
                continue;
            }
            else {
                partitionedLine = partitionKeyboardConditionally(originalLine, " ");
                command = partitionedLine[0];
                boolean validCommand = checkCommandValidity(command);
                /* Check if the input contains one of the valid commands
                 specified in the protocol. */
                if (!validCommand) {
                    //clientPw.println(command);
                    printToWriter(ircS.getServerName() + ": Usage: " + "<command> <arguments>");
                }
                else {
                    /* Branch for "<command>-only operation",
                       can be performed if the match is successful*/
                    if (command.equals(Configuration.QUIT)) {
                        requestQuit();
                    }
                    else if (command.equals(Configuration.TIME)) {
                        printTime();
                    }
                    else if (command.equals(Configuration.INFO)) {
                        requestInfo();
                    }
                    else if (command.equals(Configuration.LIST)) {
                        requestList();
                    }

                   /* Branch for "<command> <arguments>-operation"
                      <arguments> part might need further partition
                      according to the specification of <command> */
                    else {
                        try{
                            arguments = partitionedLine[1];
                            if (command.equals(Configuration.NICK)) {
                                setNickname(arguments);
                            }
                            else if (command.equals(Configuration.JOIN)) {
                                requestJoin(arguments);
                            }
                            else if (command.equals(Configuration.PART)) {
                                requestLeave(arguments);
                            }
                            else if (command.equals(Configuration.NAMES)) {
                                requestNames(arguments);
                            }
                            else if (command.equals(Configuration.PRIVMSG)) {
                                sendPrivateMsg(arguments);
                            }
                            else if (command.equals(Configuration.PING)) {
                                requestPong(arguments);
                            }
                            else if (command.equals(Configuration.USER)) {
                                setUser(arguments);
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException e) {
                            printServerReply(Configuration.ERROR_CODE, Configuration.LACKED_USER_ARG);
                        }

                    }
                }
            }
        }
    }

    /**
     * Public method to return the nickname of this client.
     * @return The nickname of this client.
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Public method to set the IRC Server this client connected to.
     * @param ircS The IRC Server this client connected to.
     */
    public void setIrcServer(IrcServer ircS) {
        this.ircS = ircS;
    }

    /**
     * Public method to print some text to this client, designed to implement communication.
     * @param text String to be sent.
     */
    public void printToWriter(String text) {
        this.clientPw.println(text);
    }

    private boolean checkCommandValidity(String command) {
        for (int counter = 0; counter < Configuration.COMMANDSET.length; counter++) {
            //System.out.println(Configuration.COMMANDSET[counter]);
            if (command.equals(Configuration.COMMANDSET[counter])) {
                return true;
            }
        }
        return false;
    }

    private void printTime() {
        LocalDateTime ldt = LocalDateTime.now();
        printServerReply(Configuration.TIME_CODE, ldt.toString());
    }

    private void requestJoin(String channelName) {
        if (!registered) {
            printServerReply(Configuration.ERROR_CODE, Configuration.NOT_REGISTERED);
        }
        else {
            if (channelName.matches(Configuration.CHANNELNAME_VALIDITY_REGEX)) {
                ircS.handleJoinRequest(channelName, this);
            }
            else {
                printServerReply(Configuration.ERROR_CODE, Configuration.INVALID_CHA_NM);
            }
        }
    }

    private void requestLeave(String channelName) {
        if (!registered) {
            printServerReply(Configuration.ERROR_CODE, Configuration.NOT_REGISTERED);
        }
        else {
            if (!ircS.checkChannelExistence(channelName)) {
                printServerReply(Configuration.ERROR_CODE, Configuration.CHANNEL_NOT_FOUND);
            }
            else {
                ircS.handlePartRequest(channelName, this);
            }
        }
    }

    private void requestNames(String channelName) {
        if (!registered) {
            printServerReply(Configuration.ERROR_CODE, Configuration.NOT_REGISTERED);
        }
        else {
            if (!ircS.checkChannelExistence(channelName)) {
                printServerReply(Configuration.ERROR_CODE, Configuration.CHANNEL_NOT_FOUND);
            }
            else {
                String text = " " + channelName + " :" + ircS.handleNamesRequest(channelName);
                printServerReply(Configuration.NAMES_EXIST_CODE, text);
            }
        }
    }

    private void requestList() {
        if (!registered) {
            printServerReply(Configuration.ERROR_CODE, Configuration.NOT_REGISTERED);
        }
        else {
            String originalChannelNames = ircS.handleListRequest();
            printServerReply(Configuration.LIST_CHAN_CODE, originalChannelNames);
        }
    }

    private void requestQuit() {
        if (registered) {
            ircS.handleQuitRequest(":" + nickname + " " + Configuration.QUIT, this);
        }
        ircS.removeConnectionReference(this);
        this.setIrcServer(null);
        closeCloseable(clientBr);
        closeCloseable(clientIs);
        closeCloseable(clientPw);
        closeCloseable(clientOs);
        quitStatus = true;
    }

    private void requestPong(String text) {
        printToWriter(Configuration.PONG + " " + text);
    }

    private void sendPrivateMsg(String originalArguments) {
        if (!registered) {
            printServerReply(Configuration.ERROR_CODE, Configuration.NOT_REGISTERED);
        }
        else {
            String[] partitionedArguments
                    = partitionKeyboardConditionally(originalArguments, Configuration.PRIVMSG_REGEX);
            String target = partitionedArguments[0];
            if (partitionedArguments.length < 2 || target.contains(" ")) {
                printServerReply(Configuration.ERROR_CODE, Configuration.INVALID_PRIVMSG_ARG);
            }
            else {
                String text = ":" + this.nickname + " " + Configuration.PRIVMSG
                        + " " + target + " :" + partitionedArguments[1];
                if (target.matches(Configuration.CHANNELNAME_VALIDITY_REGEX)) {
                    if (ircS.checkChannelExistence(target)) {
                        ircS.handleBroadcastMsgRequest(target, text);
                    }
                    else {
                        printServerReply(Configuration.ERROR_CODE, Configuration.CHANNEL_NOT_FOUND);
                    }

                }
                else {
                    if (ircS.checkUserExistence(target)) {
                        ircS.handlePrivateMsgRequest(target, text);
                    }
                    else {
                        printServerReply(Configuration.ERROR_CODE, Configuration.USER_NOT_FOUND);
                    }
                }
            }
        }
    }
    private void setNickname(String nickname) {
        if (nickname.matches(Configuration.NICK_VALIDITY_REGEX)) {
            this.nickname = nickname;
        }
        else {
            printServerReply(Configuration.ERROR_CODE, Configuration.INVALID_NICK);
        }
    }

    private void requestInfo() {
        printServerReply(Configuration.INFO_CODE, Configuration.SERVER_INFO);
    }

    private void setUser(String originalArguments) {
        String[] partitionedArguments = partitionKeyboardConditionally(originalArguments, Configuration.USER_PERMISSION_REGEX);
        //clientPw.println(partitionedArguments[0]);
        if (partitionedArguments.length < 2) {
            printServerReply(Configuration.ERROR_CODE, Configuration.LACKED_USER_ARG);
        }
        else {
            if (registered) {
                printServerReply(Configuration.ERROR_CODE, Configuration.USER_ALREADY_REGISTERED);
            }
            else if (nickname.equals("*")) {
                printServerReply(Configuration.ERROR_CODE, Configuration.NO_NICKNAME);
            }
            else {
                String username = partitionedArguments[0];
                String realname = partitionedArguments[1];
                if (username.contains(" ")) {
                    printServerReply(Configuration.ERROR_CODE, Configuration.INVALID_USER_ARG);
                }
                else {
                    this.username = username;
                    this.realname = realname;
                    this.registered = true;
                    ircS.addRegisteredUser(this.nickname);
                    printServerReply(Configuration.USER_CODE, Configuration.USER_WELCOME);
                }
            }
        }
    }

    /**
    * Integrated method that print the structural system reply
    * message(String) to the current client(ConnectionHandler)
    * depending on the needs of different methods.
    */
    private void printServerReply(String replyCode, String text) {
        String reply = ":" + ircS.getServerName() + " " + replyCode
                + " " + nickname;
        String spaceColonPlusText = " :" + text;
        String spaceEqualPlusText = " =" + text;
        if (replyCode.equals(Configuration.USER_CODE)) {
            this.printToWriter(reply + spaceColonPlusText + ", " + nickname);
        }
        else if (replyCode.equals(Configuration.NAMES_EXIST_CODE)) {
            this.printToWriter(reply + spaceEqualPlusText);
        }
        else if (replyCode.equals(Configuration.LIST_CHAN_CODE)) {
            String intermediate = text.replaceAll("\\#", "\n" + reply + " #");
            intermediate = intermediate.replaceFirst("\n", "") + "\n"
                    + reply.replace(replyCode, Configuration.LIST_END_CODE) + " :" + Configuration.END_OF_LIST;
            this.printToWriter(intermediate);
        }
        else {
            this.printToWriter(reply + spaceColonPlusText);
        }
    }


    private void closeCloseable(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }


    private String[] partitionKeyboardConditionally(String keyboard, String delimiter) {
        String[] partitionedKeyboard = keyboard.split(delimiter, 2);
        return partitionedKeyboard;
    }

    /**
    * Implementing the run() of the interface,
    * the task is running processClientMessage() to
    * read client's input recurrently until "QUIT" was entered or unknown exception thrown.
    */
    @Override
    public void run() {
        try {
            processClientMessage();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
