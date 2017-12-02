//package chatapplication;
import java.io.*;

final class ChatMessage implements Serializable {

    private static final long serialVersionUID = 6898543889087L;

    private String message;
    private int messType;
    private String recepient;
    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    public ChatMessage(int messType,String message){
        this.message=message;
        this.messType=messType;
    }
    public ChatMessage(int messType,String message, String recepient){
        this.message=message;
        this.messType=messType;
        this.recepient=recepient;
    }

    public String getMessage() {
        return message;
    }

    public int getMessType() {
        return messType;
    }

    public String getRecepient() {
        return recepient;
    }
}
