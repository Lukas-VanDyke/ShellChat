import java.io.*;
import java.net.*;
import java.lang.Math.*;
import java.util.ArrayList;

public class ClientThread extends Thread{
    private Socket socket;
    private String input = "";
    private BufferedReader inBuff;
    private DataOutputStream outBuff;
    private Server server;
    private Chat currentChat = null;
    private Account user = null;

    private String[] commands = {"/login", "/register", "/editaccount",
            "/send", "/cancel", "/accept", "/invite", "/getpublic",
            "/join", "/newpublic", "/whisper", "/addfriend", "/removefriend",
            "/files", "/getfile", "/getchats", "/logs", "/log", "/onlinefriends", 
			"/friendstatus", "/help", "/logout", "/newprivate", "/SYSFILEINClen"};

    public ClientThread(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try{
            inBuff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outBuff = new DataOutputStream(socket.getOutputStream());

            while (!input.equals("/logout")){
				input = inBuff.readLine();
                interpret(input);
            }
            socket.close();
        }catch (IOException e){
        }catch (NullPointerException e){
			if (user != null)
				interpret("/logout");
		}
    }

    protected void setServer(Server server){
        this.server = server;
    }

    public synchronized void sendMessage(String message){
        try {
            outBuff.writeBytes(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getChannel(){
        if (currentChat != null){
            return currentChat.getName();
        }
        return "NONE";
    }

    public Chat getChat(){
        return currentChat;
    }

    public Account getUser(){
        return user;
    }

    public int getChannelType(){
        if (currentChat != null){
            return currentChat.getType();
        }
        return -1;
    }

    public void setChannel(Chat newChat){
        if (currentChat != null){
            leftChat();
        }
        currentChat = newChat;
        joinedChat();
    }

    public boolean validAccess(String x, String y){
        if (currentChat != null){
            return (currentChat.getName().equals(x) && currentChat.getPassword().equals(y));
        }
        return false;
    }

    private void interpret(String reading){
		if (reading.equals("") || reading.equals("\n")){}
		else if ('/' == reading.charAt(0)){
            int invalid = 1;
            for (int x = 0; x < commands.length; x++){
                if (reading.split(" ")[0].equals(commands[x])){
                    invalid = 0;
                    commandPick(x);
                }
            }
            if (invalid == 1){
                commandPick(-1);
            }
        }
        else {
            if (user == null){
                sendMessage("Please /login or /register to continue.");
            }
            else{
                if(currentChat == null){
                    sendMessage("Please join a chat cannel or whisper a user. Type \"/join <Channel-name> <password>\" (password only if channel is private)" +
                            " or \"/whisper <username>\" (without the quotes). You can also type \"/newpublic <channel-name>\" or \"/newprivate <channel-name> <pass-word>\" to create public or private chatrooms.");
                }
                else{
                    server.sendMessage(user.getUserName() + ": " + reading, this);
                }
            }
        }
    }

    public void leftChat(){
        server.sendMessage(user.getUserName() + " has left the chat", this);
    }

    public void joinedChat(){
		user.addChat(currentChat.getName());
        server.sendMessage(user.getUserName() + " has joined the chat", this);
    }

    public void loggedOutMessage(){
        server.sendMessage(user.getUserName() + " has logged out", this);
    }

    private void commandPick(int index){
        if (user != null){
            switch (index) {
                case 0:
                    sendMessage("You're already logged in!");
                    break;
                case 1:
                    sendMessage("You already have an account.");
                    break;
                case 2: //function call editaccount
                    break;
                case 3: //function call send
                    break;
                case 4: //function call cancel
                    break;
                case 5: //function call accept
                    break;
                case 6: //function call invite
                    inviteUser(input.split(" ")[1]);
                    break;
                case 7: // /getpublic
                    getpublic();
                    break;
                case 8: // /join
                    joinchat();
                    break;
                case 9: // /newpublic
                    makepublic();
                    break;
                case 10: //function call whisper
                    break;
                case 11: //function call addfriend
                    addFriend(input.split(" ")[1]);
                    break;
                case 12: //function call removefriend
                    removeFriend(input.split(" ")[1]);
                    break;
                case 13: //function call files
                    listFiles();
                    break;
                case 14: //function call getfile
					if (!user.filePermission(input.split(" ")[1])){
						System.out.println("Transfer request denied.");
						sendMessage("You do not have permission or this file does not exist");
					}else{
						//System.out.println("wat");
						sendMessage("");
						sendFile(input.split(" ")[1]);
                    }
					break;
                case 15: //function call getchats
                    break;
                case 16: //function call logs
					listLogs();
                    break;
                case 17: //function call log
					showLog(input.split(" ")[1]);
                    break;
                case 18: //function call onlinefriends
                    checkOnlineFriends();
                    break;
                case 19: //function call friendstatus
                    checkFriendStatus();
                    break;
                case 20: //function call help
                    break;
                case 21: // logout call
                    logout();
                    break;
                case 22: //newprivate
                    makeprivate();
                    break;
                case 23: //incoming file (gets file length) SYSTEM USE ONLY
					if (currentChat == null){
						System.out.println("hi");
						sendMessage("You need to be in a channel to use this function");
					}else{
						String len = input.split(" ")[1];
						sendMessage("");
						getFile(Integer.parseInt(len), input.split(" ")[2]);
					}
					break;
                default: // invalid command issued
                    sendMessage(input + " is not a valid command. Type \"/help\" (without the quotes) for a list of valid commands");
                    break;
            }

        }
        else{
            switch (index) {
                case 0:// code to log in
                    try{
                        Login log = new Login();
                        String username;
                        sendMessage("Username: ");
                        username = inBuff.readLine();
                        sendMessage("Password: ");
                        String password;
                        password = inBuff.readLine();
                        if(!server.isOnline(username)){
                            user = log.log(username, password);
                            if (user == null){
                                sendMessage("Invalid username or password, please try again or use /register if you do not have an account.");
                            }
                            else{
                                sendMessage("Welcome " + user.getUserName() + "!");
                            }
                        }
                        else{
                            sendMessage("That user is already logged on.");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 1:// code to register
                    try{
                        Login log = new Login();
                        String username;
                        sendMessage("Username: ");
                        username = inBuff.readLine();
                        sendMessage("Password: ");
                        String password;
                        password = inBuff.readLine();
                        if (log.checkName(username)){
                            Account acc = new Account(username, password);
                            acc.storeAccount();
                            System.out.println("An account has been created on the server, Username: "+ username);
                            user = log.log(username, password);
                            sendMessage("Welcome " + user.getUserName() + "!");
                        }
                        else{
                            sendMessage("There is already a user with that username, please try a different username.");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 19:
                    sendMessage("Type /login or /register to begin.");
                default:
                    sendMessage("You must login to use that command.");
                    break;
            }
        }
    }

	private void sendFile(String fileName){
		File fileToSend = new File(fileName);
		FileInputStream fis;
		OutputStream os;
		try{
			int filesize = (int) fileToSend.length();
			sendMessage(Integer.toString(filesize));
			byte[] fileByteArray = new byte[1024]; 
			fis = new FileInputStream(fileToSend);
			os = socket.getOutputStream();
			int count;
			while ((count = fis.read(fileByteArray)) >= 0){
				os.write(fileByteArray, 0, count);
			}
			os.flush();
			fis.close();
		}catch (Exception e){
			e.printStackTrace();
		}		
	}
	
    private void getFile(int lengthOfFile, String fileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        byte[] recieving = new byte[1024]; // stream is as huge as file
        try{
			int recieved = 0;
			int remaining;
			is = socket.getInputStream();
			fos = new FileOutputStream(fileName);
			while (recieved < lengthOfFile){
				remaining = lengthOfFile - recieved;
				recieved += is.read(recieving, 0, Math.min(remaining, 1024));
				fos.write(recieving, 0, Math.min(remaining, 1024));
			}
			server.addPermission(fileName, this);
			
            fos.close();
			sendMessage("Transmssion complete");
            System.out.println("File has been created on the server");
			
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void joinchat(){
        String[] in = input.split(" ");
        if (server.checkChannel(in[1])){
            try{
                String temp = in[2];
                if (server.privateJoinable(in[1], temp) != null){
                    setChannel(server.privateJoinable(in[1], temp));
                    ArrayList<String> history = currentChat.getHistory();
                    for (String message : history){
                        sendMessage(message);
                    }
                }else{
                    sendMessage("Invalid password or something");
                }
            }catch(ArrayIndexOutOfBoundsException e){
                setChannel(server.privateJoinable(in[1], "NONE"));
                ArrayList<String> history = currentChat.getHistory();
                for (String message : history){
                    sendMessage(message);
                }
            }
        }else{
            sendMessage("Couldn't find a channel with the name " + (in[1]) + ".");
        }
    }

    private void makepublic(){
        String[] in = input.split(" ");
        if (server.checkChannel(in[1])){
            sendMessage("This channel name already exists.");
        }
        else{
            Chat newChat = new Chat(in[1], "NONE");
            setChannel(newChat);
        }
    }

    private void makeprivate(){
        String[] in = input.split(" ");
        if (server.checkChannel(in[1])){
            sendMessage("This channel name already exists.");
        }
        else{
            Chat newChat = new Chat(in[1], in[2]);
            setChannel(newChat);
        }
    }

    private void addFriend(String friendName){
        boolean exists = true;
        String fileName = friendName + ".txt";
        BufferedReader streamInput = null;
        try{
            streamInput = new BufferedReader(new FileReader(fileName));
            String password = streamInput.readLine();
            streamInput.close();
        } catch (IOException e) {exists = false;}

        if (exists){
            user.addFriend(friendName);
            sendMessage("Added friend: " + friendName);
            user.storeAccount();
        }
        else{
            sendMessage("Unable to add friend: " + friendName);
        }
    }

    private void removeFriend(String friendName){
        boolean haveFriend = false;
        ArrayList<String> tempFriends = user.getFriends();
        for (String friend : tempFriends){
            if (friend.equals(friendName)){
                haveFriend = true;
            }
        }
        if (haveFriend){
            user.removeFriend(friendName);
            sendMessage("Removed friend: " + friendName);
            user.storeAccount();
        }
        else{
            sendMessage("There is no friend with username: " + friendName);
        }
    }

    private void checkFriendStatus(){
        ArrayList<String> status = server.friendStatus(user.getFriends(), this);
        for (String state : status){
            sendMessage(state);
        }
    }

    private void checkOnlineFriends(){
        ArrayList<String> online = server.onlineFriends(user.getFriends(), this);
        for (String state : online){
            sendMessage(state);
        }
    }

    private void listFiles(){
        ArrayList<String> files = user.getFiles();
        for (String file : files){
            sendMessage(file);
        }
    }
	
	public void listLogs(){
		ArrayList<String> logs = user.getChats();
		sendMessage("You can view these logs: ");
		for (String log : logs){
			sendMessage(log);
		}
		sendMessage("Use the /viewlog command followed by the name of a log you can view to view a log");
	}
	
	public void showLog(String logName){
		ArrayList<String> logs = user.getChats();
		boolean displayed = false;
		for (String log: logs){
			if (log.equals(logName)){
				server.displayLog(logName, this);
				displayed = true;
			}
		}
		if (!displayed){
			sendMessage("You do not have access to view a log by that name.");
		}
	}

    private void inviteUser(String username){
        if (currentChat != null){
            if (currentChat.getType() == 1){
                server.sendInvite(user.getUserName() + " has invited you to join public channel " + currentChat.getName(), username, this);
            }
            else {
                server.sendInvite(user.getUserName() + " has invited you to join private channel " + currentChat.getName() + " with password " + currentChat.getPassword(), username, this);
            }
        }
        else {
            sendMessage("You are not in a chat channel and cannot invite another user.");
        }
    }

    private void logout(){
        try{
            user.storeAccount();
            if (currentChat != null){
                loggedOutMessage();
            }
            server.logoff(this);
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getpublic(){
        String publist = server.publicList();
        if (publist.equals("")){
            publist = "No public channels available";
        }
        sendMessage(publist);
    }

}
