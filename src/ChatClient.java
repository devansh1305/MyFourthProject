import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    private ChatClient(String username, int port, String server) {
        this.server = server;
        this.port = port;
        this.username = username;
    }
    private ChatClient (String username, int port){
        this.server = "localhost";
        this.username = username;
        this.port = port;
    }
    private ChatClient (String username){
        this.server = "localhost";
        this.port = 1500;
        this.username = username;
    }
//    private ChatClient (){
//        this.server = "localhost";
//        this.port = 1500;
//        this.username = "anonymous";
//    }

    /*
     * This starts the Chat Client
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            System.out.println("Server has not started yet. Please try again later.");
            System.exit(0);
        }

        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername(){
        return this.username;
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        // Get proper arguments and override defaults
        // Create your client and start it
        ChatClient client= new ChatClient(args[0]); //= new ChatClient("dev");
        if(args.length == 3){
            client = new ChatClient(args[0],Integer.parseInt(args[1]),args[2]);
            client.start();
        }
        else if (args.length == 2){
            client = new ChatClient(args[0],Integer.parseInt(args[1]),"localhost");
            client.start();
        }
        else if (args.length==1){
            client = new ChatClient(args[0],1500,"localhost");
            client.start();
        }else if(args.length==0){
            //client=new ChatClient();
            client.start();
        }
        Scanner scan = new Scanner(System.in);
        boolean isrunning=true;
        while(isrunning){
            String msg=scan.nextLine();
            //String[] part=msg.split(" ");
            //String recp=part[1];
            System.out.print(">");
            String part="";
            if(msg.length()>3) {
                part = msg.substring(0, 4);
            }

            if(msg.equals("/logout")){
                ChatMessage cm=new ChatMessage(1,"");
                client.sendMessage(cm);
                isrunning=false;
            }
            else if(msg.equals("/list")){
                ChatMessage cm=new ChatMessage(3,"");
                client.sendMessage(cm);
            }
            else if(part.equals("/msg")) {
                String msg2=msg.substring(5);
                int pos=msg2.indexOf(" ");
                String newMess=msg2.substring(pos);
                String msg3=msg2.substring(0,pos);
                ChatMessage cm = new ChatMessage(2,newMess,msg3);
                client.sendMessage(cm);
            }
            else{
                ChatMessage cm=new ChatMessage(0,msg);
                client.sendMessage(cm);
            }
        }
        client.close();

        // Send an empty message to the server
        //ChatMessage cm=new ChatMessage();
        //client.sendMessage(cm);
    }
    public void close(){
        try {
            sInput.close();
            sOutput.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            try {
                //ServerSocket serverSocket=new ServerSocket(8500);
                while(true) {
                    int type;
                    String msg = (String) sInput.readObject();
                    if(msg.equals("A1E3B7W0X7")){
                        System.out.println("The username already exists! Try again!");
                        System.exit(0);
                    }else {
                        System.out.println(msg);
                    }


                    //Socket socket=serverSocket.accept();
                }
            } catch (IOException | ClassNotFoundException e) {
                try {
                    sInput.close();
                    sOutput.close();
                    socket.close();
                } catch (IOException e1) {

                }
            }

        }
    }
}
