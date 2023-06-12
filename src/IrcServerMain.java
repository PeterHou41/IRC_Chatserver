/**Entrance of the small project.*/
public class IrcServerMain {

    private IrcServer ircS;

    /**
    * An instance of IrcServer will be instantiated
    * and ready to listen for client connection
    * with the specified server name and port number.
    * @param args Array of strings stores user's input.
    */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(Configuration.INVALID_ARGUMENT_PROMPT);
        }
        else {
            try {
                String serverName = args[0];
                int portNum = Integer.parseInt(args[1]);
                IrcServer ircS = new IrcServer(serverName, portNum);
            }
            catch (Exception e) {
                // Catch Exception when handling the port number,
                // including NumberFormatException and IllegalArgumentException
                System.out.println(Configuration.INVALID_ARGUMENT_PROMPT);
            }
        }
    }
}
