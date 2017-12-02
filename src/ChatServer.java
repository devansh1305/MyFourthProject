import java.util.*;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
To test it in bash:
/mnt/c/Users/devan/IdeaProjects/MyFourthProject/src
 */
final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
    private String badwordsfile;


    private ChatServer(int port,String badwordsfile) {
        this.port = port;
        this.badwordsfile=badwordsfile;
    }
    private ChatServer(int port) {
        this.port = port;
        this.badwordsfile="badwords.txt";
    }
    private ChatServer (){
        this.port = 1500;
        this.badwordsfile="badwords.txt";

    }





    /*
         * This is what starts the ChatServer.
         * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
         */
    private void start() {


        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server ON");
            while(true) {
                Socket socket = serverSocket.accept();
                //wait....connected!!
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                clients.add((ClientThread) r);
                clients.get(clients.indexOf(r)).writeMessage("Connection ON");
                //new client thread made....BOOM!!!...there it goes!!
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer(1500);
        server.start();

    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                for(int i=0;i<clients.size();i++){
                    if((clients.get(i).username).equalsIgnoreCase(username)){
                        writeMessage("A1E3B7W0X7");
                    }
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client

            String newMess = "";
            try {

                broadcast(username +" has logged in.");
                boolean isrunning=true;
                while(isrunning) {
                    cm = (ChatMessage) sInput.readObject();
                    String msg = cm.getMessage();
                    if (cm.getMessType() == 0) {
                        newMess = username + ": " + msg;
                        broadcast(newMess);
                    } else if(cm.getMessType()==1){
                        newMess = username + " has logged out";
                        broadcast(newMess);
                        remove(id);
                        sInput.close();
                        sOutput.close();
                        socket.close();
                        isrunning=false;
                    }else if(cm.getMessType()==2){
                        Date date = new Date();
                        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
                        String time1 = time.format(date);
                        newMess = time1+" "+username +"-->"+cm.getRecepient()+": " + msg;
                        if(!username.equalsIgnoreCase(cm.getRecepient())) {
                            directMessage(cm.getRecepient(), newMess);
                        }else if(username.equalsIgnoreCase(cm.getRecepient())){
                            writeMessage("You cannot send message to yourself.");
                            System.out.println(username+"-You cannot send message to yourself.");
                        }
//                        else if(!socket.isConnected()){
//                            directMessage(username,"User does not exist or has already logged out.");
//                        }
                    }else if(cm.getMessType()==3){
                        ArrayList <String> clientName= new ArrayList<>();
                        for (int i = 0; i < clients.size();i++ ) {
                            if((clients.get(i).socket.isConnected()) && (!clients.get(i).username.equals(username))) {
                                clientName.add((clients.get(i)).username);
                            }
                        }
                        String fmess="The active Users are:-\n";
                        for(int i=0;i<clientName.size();i++){
                            fmess+=clientName.get(i)+"\n";
                        }
                        sOutput.writeObject(fmess);
                    }
                }
                remove(id);


            } catch (IOException | ClassNotFoundException e) {
                try {
                    sInput.close();
                    sOutput.close();
                    socket.close();
                } catch (IOException e1) {
                }
            }



            // Send message back to the client
            try {
                sOutput.writeObject((String) newMess);
            } catch (IOException e) {
                try {
                    sInput.close();
                    sOutput.close();
                    socket.close();
                } catch (IOException e1) {
                }
            }

        }

        public boolean writeMessage(String msg){
            if(!socket.isConnected()){
                return false;
            }
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private synchronized void broadcast(String message) {
        Date date = new Date();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        String time1 = time.format(date);
        ChatFilter cf= new ChatFilter(badwordsfile);
        message=cf.filter(message);
        String newMess = time1 + " " + message;
        ClientThread a = null;
        for (int i = 0; i < clients.size();i++ ) {
            a = clients.get(i);
            a.writeMessage(newMess);
        }
        System.out.println(newMess);
    }
    private synchronized void directMessage(String recepient,String message){
//        Date date = new Date();
//        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
//        String time1 = time.format(date);
        ChatFilter cf= new ChatFilter(badwordsfile);
        message=cf.filter(message);
        //String newMess = time1 + " " + message;
        ClientThread a = null;
        boolean b=true;
        for (int i = 0; i < clients.size();i++ ) {
            a = clients.get(i);
            if(a.username.equalsIgnoreCase(recepient)) {
                a.writeMessage(message);
                System.out.println(message);
                b=false;
            }
        }
        if(b){
            message="User does not exist or has already logged out.";
            a.writeMessage(message);
            System.out.println(message);
        }
    }

    private synchronized void remove(int id){
        for(int i=0;i<clients.size();i++){
            ClientThread a=clients.get(i);
            if((a.id)==id){
                clients.remove(i);
            }
        }
    }
}
