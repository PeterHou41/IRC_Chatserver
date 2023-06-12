/**Stores constants that can be used by different clients(ConnectionHandler).*/
public abstract class Configuration {

    /**String used for checking the legality of the input strings via args[].*/
    public static final String INVALID_ARGUMENT_PROMPT = "Usage: java IrcServerMain <server_name> <port>";

    /**String used for matching if client's input is this commands.*/
    public static final String QUIT = "QUIT";

    /**{@link #QUIT readDocHere}.*/
    public static final String NICK = "NICK";

    /**{@link #QUIT readDocHere}.*/
    public static final String USER = "USER";

    /**{@link #QUIT readDocHere}.*/
    public static final String JOIN = "JOIN";

    /**{@link #QUIT readDocHere}.*/
    public static final String PART = "PART";

    /**{@link #QUIT readDocHere}.*/
    public static final String NAMES = "NAMES";

    /**{@link #QUIT readDocHere}.*/
    public static final String LIST = "LIST";

    /**{@link #QUIT readDocHere}.*/
    public static final String TIME = "TIME";

    /**{@link #QUIT readDocHere}.*/
    public static final String INFO = "INFO";

    /**{@link #QUIT readDocHere}.*/
    public static final String PING = "PING";

    /**{@link #QUIT readDocHere}.*/
    public static final String PONG = "PONG";

    /**{@link #QUIT readDocHere}.*/
    public static final String PRIVMSG = "PRIVMSG";

    /**A collection of all valid Constants used for matching
     * if client's input contains one of the commands.*/
    public static final String[] COMMANDSET = {QUIT, NICK, USER, JOIN, PART, PRIVMSG, NAMES, LIST, TIME, INFO, PING};



    /**Regular expression to check the validity of the nickname.*/
    public static final String NICK_VALIDITY_REGEX = "^[a-zA-z_][a-zA-Z0-9_]{0,8}";

    /**Regular expression as delimiter to split the argument of USER command.*/
    public static final String USER_PERMISSION_REGEX = "\\s0\\s\\*\\s:";

    /**Regular expression to check the validity of the channelName.*/
    public static final String CHANNELNAME_VALIDITY_REGEX = "#[a-zA-Z0-9_]*";

    /**Regular expression as delimiter to split the argument of PRIVMSG command.*/
    public static final String PRIVMSG_REGEX = "\\s:";


    /**<text> used for structural reply(error) message sent by server.*/
    public static final String NO_NICKNAME = "You need to set your nickname first";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String NOT_REGISTERED = "You need to register first";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String CHANNEL_NOT_FOUND = "No channel exists with that name";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String USER_NOT_FOUND = "No user exists with that name";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String USER_ALREADY_REGISTERED = "You are already registered";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String INVALID_NICK = "Invalid nickname";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String INVALID_USER_ARG = "Invalid arguments to USER command";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String INVALID_CHA_NM = "Invalid channel name";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String INVALID_PRIVMSG_ARG = "Invalid arguments to PRIVMSG command";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String LACKED_USER_ARG = "Not enough arguments";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String USER_WELCOME = "Welcome to the IRC network";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String END_OF_LIST = "End of LIST";

    /**{@link #NO_NICKNAME readDocHere}.*/
    public static final String SERVER_INFO = "This server was implemented by student 220021270\n"
            + "which supports simple multiuser realtime communication via TCP.";



    /**<reply_code> as constant string used for structural reply(error) message sent by server.*/
    public static final String ERROR_CODE = "400";

    /**{@link #ERROR_CODE readDocHere}.*/
    public static final String TIME_CODE = "391";

    /**{@link #ERROR_CODE readDocHere}.*/
    public static final String USER_CODE = "001";

    /**{@link #ERROR_CODE readDocHere}.*/
    public static final String INFO_CODE = "371";

    /**{@link #ERROR_CODE readDocHere}.*/
    public static final String NAMES_EXIST_CODE = "353";

    /**{@link #ERROR_CODE readDocHere}.*/
    public static final String LIST_CHAN_CODE = "322";

    /**{@link #ERROR_CODE readDocHere}.*/
    public static final String LIST_END_CODE = "323";

    /**The minimum value of the port number.*/
    public static final int PORTNUM_MIN = 0;

    /**The maximum value of the port number.*/
    public static final int PORTNUM_MAX = 65536;
}
