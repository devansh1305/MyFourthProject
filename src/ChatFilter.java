//package chatapplication;
import java.io.*;

public class ChatFilter {

    private String badWordsFileName;
    public ChatFilter(String badWordsFileName) {
        this.badWordsFileName=badWordsFileName;
    }

    public String filter(String msg){
        File f=new File(badWordsFileName);
        FileReader fr=null;
        try {
            fr=new FileReader(f);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        BufferedReader br=new BufferedReader(fr);
        String bad="";
        String line;
        try {
            while ((line = br.readLine()) != null) {
                bad += line + " ";
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //System.out.println(bad);    //print 1
        String[] badArray=bad.split(" ");


        String oldmess=msg;
        //System.out.println(oldmess);    //print 2
        String[] oldmessArray=oldmess.split(" ");

        for(int i=0;i<oldmessArray.length;i++){
            String word1=oldmessArray[i].toLowerCase();
            for(int j=0;j<badArray.length;j++){
                String word2=badArray[j].toLowerCase();
                if(word1.equals(word2)){
                    String rep="";
                    for(int k=0;k<oldmessArray[i].length();k++){
                        rep=rep+"*";
                    }
                    oldmessArray[i]=rep;
                }
            }
        }

        oldmess="";
        for(int i=0;i<oldmessArray.length;i++){
            oldmess+=oldmessArray[i]+ " ";
        }
        //System.out.println(oldmess);    //print 3


        return oldmess;
    }

//    public static void main(String[] args) throws IOException {
//        ChatFilter cf=new ChatFilter("badwords.txt");
//        String line=cf.filter("I love IU");
//        //System.out.println(line);
//    }
}
