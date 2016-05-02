import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

    ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();

    private static final int port = 1995;
    static Server server;

    public static void main(String[] args) {
        server = new Server();
    }

    public Server(){
        ServerSocket serverSocket = null;
        Socket socket = null;
        ClientThread thread;

        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Server Online");
        }catch (IOException e){
            e.printStackTrace();
        }
        while(true){
            try {
                socket = serverSocket.accept();
                System.out.println("Connection request accepted.");
            }catch (IOException e){
                e.printStackTrace();
            }
            thread = new ClientThread(socket);
            thread.start();
            thread.setServer(this);
            clientThreads.add(thread);
        }
    }

    public synchronized void logoff(ClientThread sender){
        clientThreads.remove(sender);
    }

    public synchronized void sendMessage(String message, ClientThread sender){
        clientThreads.remove(sender);
        System.out.println(message);
        for (ClientThread thread : clientThreads){
            if (thread.getChannel().equals(sender.getChannel())){
                thread.sendMessage(message);
            }
        }
        clientThreads.add(sender);
        sender.getChat().storeMessage(message);
    }

    public synchronized void sendInvite(String invite, String username, ClientThread sender){
        clientThreads.remove(sender);
        for (ClientThread thread : clientThreads){
            if (thread.getUser() != null){
                if (thread.getUser().getUserName().equals(username)){
                    thread.sendMessage(invite);
                }
            }
        }
        clientThreads.add(sender);
    }

    // checkChannel - will return true of a channel with name provided already exists
    public synchronized boolean checkChannel(String chname){
        for (ClientThread thread : clientThreads){
            if (thread.getChannel().equals(chname)){
                return true;
            }
        }

        String fileName = chname + ".txt";
        BufferedReader streamInput = null;

        try{
            streamInput = new BufferedReader(new FileReader(fileName));
            String password = streamInput.readLine();
            streamInput.close();
        } catch (IOException e) {return false;}
        return true;
    }

    public synchronized ArrayList<String> friendStatus(ArrayList<String> friends, ClientThread sender){
        clientThreads.remove(sender);
        ArrayList<String> status = new ArrayList<String>();
        ArrayList<String> online = new ArrayList<String>();
        ArrayList<String> tempFriends = new ArrayList<String>();
        for (String friend : friends){
            tempFriends.add(friend);
        }
        for (ClientThread thread : clientThreads){
            if (thread.getUser() != null){
                String username = thread.getUser().getUserName();
                for (String friend : friends){
                    if (username.equals(friend)){
                        online.add(friend);
                        status.add(friend + " is online");
                    }
                }
            }
        }
        for (String on : online){
            tempFriends.remove(on);
        }
        for (String friend : tempFriends){
            status.add(friend + " is offline");
        }
        clientThreads.add(sender);
        return status;
    }

    public synchronized ArrayList<String> onlineFriends(ArrayList<String> friends, ClientThread sender){
        clientThreads.remove(sender);
        ArrayList<String> online = new ArrayList<String>();
        for (ClientThread thread : clientThreads){
            if (thread.getUser() != null){
                String username = thread.getUser().getUserName();
                for (String friend : friends){
                    if (username.equals(friend)){
                        online.add(friend + " is online");
                    }
                }
            }
        }
        clientThreads.add(sender);
        return online;
    }

    public synchronized boolean isOnline(String newName){
        for (ClientThread thread : clientThreads){
            if (thread.getUser() != null){
                String username = thread.getUser().getUserName();
                if (username.equals(newName)){
                    return true;
                }
            }
        }
        return false;
    }
	
	public synchronized void addPermission(String fileName, ClientThread sender){
		Chat currentChat = sender.getChat();
		for (ClientThread thread:clientThreads){
			if (currentChat.equals(thread.getChat())){
				thread.getUser().addFile(fileName);
				thread.getUser().storeAccount();
			}
		}
	}

    // privateJoinable - will return true if the channel name is joinable with the password provided
    public synchronized Chat privateJoinable(String x, String y){
        Chat newChat = null;
        ArrayList<String> chatHistory = new ArrayList<String>();
        for (ClientThread thread : clientThreads){
            if (thread.validAccess(x, y)){
                return thread.getChat();
            }
        }

        String fileName = x + ".txt";
        BufferedReader streamInput = null;
        try{
            streamInput = new BufferedReader(new FileReader(fileName));
            String password = streamInput.readLine();
            String type = streamInput.readLine();
            String history = streamInput.readLine();
            while (history != null){
                chatHistory.add(history);
                history = streamInput.readLine();
            }
            if (y.equals(password)){
                newChat = new Chat(x, y);
                for (String newHistory : chatHistory){
                    newChat.storeMessage(newHistory);
                }
                return newChat;
            }
            streamInput.close();
        } catch (IOException e) {return null;}
        return null; //unreachable
    }
	
	public synchronized void displayLog(String chatName, ClientThread sender){
		String fileName = chatName + ".txt";
		BufferedReader streamInput = null;
		try{
			streamInput = new BufferedReader(new FileReader(fileName));
			String password = streamInput.readLine();
			String type = streamInput.readLine();
			String history = streamInput.readLine();
			while (history != null){
				sender.sendMessage(history);
				history = streamInput.readLine();
			}
			streamInput.close();
		} catch (IOException e) {e.printStackTrace();}
	}

    public synchronized String publicList(){
        String publist = "";
        for (ClientThread thread : clientThreads){
            if (thread.getChannelType() == 1){
                publist = publist.concat(thread.getChannel() + "\n");
            }
        }
        return publist;
    }
}