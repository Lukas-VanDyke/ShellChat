import java.io.*;

public class Login{

    public Account acc = null;

    public Login(){

    }

    public Account log(String username, String pass){
        String inputUserName = username;
        String inputPassword = pass;

        String fileName = inputUserName + ".txt";
        BufferedReader streamInput;

        try{
            streamInput = new BufferedReader(new FileReader(fileName));
            String password = streamInput.readLine();
            if (password.equals(inputPassword)){
				acc = new Account(username, pass);
				String friend = streamInput.readLine();
				while (!friend.equals("FILES:")){
					acc.addFriend(friend);
					friend = streamInput.readLine();
				}
				String file = streamInput.readLine();
				while (!file.equals("CHATS:")){
					acc.addFile(file);
					file = streamInput.readLine();
				}
				String chat = streamInput.readLine();
				while (chat != null){
					acc.addChat(chat);
					chat = streamInput.readLine();
				}
			}
            streamInput.close();
        } catch (IOException e) {System.out.println("Login failed");}

        return acc;
    }

    public boolean checkName(String username){
        String fileName = username + ".txt";
        BufferedReader streamInput = null;

        try{
            streamInput = new BufferedReader(new FileReader(fileName));
            String password = streamInput.readLine();
            streamInput.close();
        } catch (IOException e) {return true;}

        return false;
    }
}
