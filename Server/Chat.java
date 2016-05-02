import java.io.*;
import java.util.ArrayList;

public class Chat{
    private String chatName;
    private String password;
    private int type;

    public Chat(String name, String pass){
        chatName = name;
        password = pass;
        if (pass.equals("NONE")){
            type = 1;
        }
        else{
            type = 0;
        }
        String fileName = chatName + ".txt";
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
            writer.write(pass);
            writer.newLine();
            if (type == 0){
                writer.write("ZERO");
                writer.newLine();
            }
            else{
                writer.write("ONE");
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {e.printStackTrace();}
    }

    public String getName(){
        return chatName;
    }

    public String getPassword(){
        return password;
    }

    public int getType(){
        return type;
    }

    public void storeMessage(String newMessage){
        String fileName = chatName + ".txt";

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"));
            writer.append(newMessage);
            writer.newLine();
            writer.close();
        } catch (IOException ex) {ex.printStackTrace();}
    }

    public ArrayList<String> getHistory(){
        String fileName = chatName + ".txt";
        ArrayList<String> chatHistory = new ArrayList<String>();

        BufferedReader streamInput = null;
        try{
            streamInput = new BufferedReader(new FileReader(fileName));
            String chatLine = streamInput.readLine();
            chatLine = streamInput.readLine();
            chatLine = streamInput.readLine();
            while (chatLine != null){
                chatHistory.add(chatLine);
                chatLine = streamInput.readLine();
            }
            streamInput.close();
        } catch (IOException e) {e.printStackTrace();}

        return chatHistory;
    }
}
