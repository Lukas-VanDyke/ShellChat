import java.io.*;
import java.util.ArrayList;

public class Account{
    private String userName;
    private String password;
    private ArrayList<String> friends;
    private ArrayList<String> files;
	private ArrayList<String> chats;

    public Account(String newUserName, String newPassword){
        userName = newUserName;
        password = newPassword;
        friends = new ArrayList<String>();
        files = new ArrayList<String>();
		chats = new ArrayList<String>();
    }

    public String getUserName(){
        return userName;
    }

    public ArrayList<String> getFriends(){
        return friends;
    }

    public ArrayList<String> getFiles(){
        return files;
    }
	
	public ArrayList<String> getChats(){
		return chats;
	}

    public boolean filePermission(String filename){
        for (String file : files){
            if (file.equals(filename)){
                return true;
            }
        }
        return false;
    }

    public void changePassword(String newPassword){
        password = newPassword;
    }

    public void addFriend(String friendName){
        friends.add(friendName);
    }
	
	public void addChat(String chatName){
		if (!chats.contains(chatName)){
			chats.add(chatName);
		}
	}

    public void removeFriend(String friendName){
        friends.remove(friendName);
    }

    public void addFile(String fileName){
        if (!files.contains(fileName)){
			files.add(fileName);
		}
    }

    public void storeAccount(){
        String fileName = userName + ".txt";
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
			writer.write(password);
			writer.newLine();
			for(String friend:friends){
				writer.write(friend);
				writer.newLine();
			}
			writer.write("FILES:");
			writer.newLine();
			for(String file:files){
				writer.write(file);
				writer.newLine();
			}
			writer.write("CHATS:");
			writer.newLine();
			for(String chat:chats){
				writer.write(chat);
				writer.newLine();
			}
            writer.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
