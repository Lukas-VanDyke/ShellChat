import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws Exception{
		try{
			Socket clientSocket = new Socket(args[0], 1995);
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String transmission = "";
			BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

			while (!transmission.equals("/logout")){

				if (userIn.ready())
				{
					transmission = userIn.readLine();
					
					/* Sending files to server */
					if (transmission.split(" ")[0].equals("/send")){
						File fileToSend = new File(transmission.split(" ")[1]);
						FileInputStream fis;
						OutputStream os;
						try{
							int filesize = (int) fileToSend.length();
							out.writeBytes("/SYSFILEINClen " + filesize + " " + fileToSend.getName() + '\n');
							transmission = in.readLine();
							if (!transmission.equals("You need to be in a channel to use this function")){
								byte[] fileByteArray = new byte[1024]; // stream is as huge as file
								fis = new FileInputStream(fileToSend);
								os = clientSocket.getOutputStream();
								int count;
								while ((count = fis.read(fileByteArray)) >= 0){
									os.write(fileByteArray, 0, count);
								}
								transmission = "--rip";
								os.flush();
								fis.close();
							}
							else{
								System.out.println("You need to be in a channel to use this function");
							}
						}catch (Exception e){
							e.printStackTrace();
						}
					}
					
					/* Recieving files from server */
					if (transmission.split(" ")[0].equals("/getfile")){
						out.writeBytes(transmission + '\n');
						String fileName = transmission.split(" ")[1];
						transmission = in.readLine();
						if (!(transmission.equals( "You do not have permission or this file does not exist"))){
							transmission = in.readLine();
							int lengthOfFile = Integer.parseInt(transmission);
							InputStream is = null;
							FileOutputStream fos = null;
							byte[] recieving = new byte[1024];
							try{
								int recieved = 0;
								int remaining;
								is = clientSocket.getInputStream();
								fos = new FileOutputStream(fileName);
								while (recieved < lengthOfFile){
									remaining = lengthOfFile - recieved;
									recieved += is.read(recieving, 0, Math.min(remaining, 1024));
									fos.write(recieving, 0, Math.min(remaining, 1024));
								}
								fos.close ();
								transmission = "--rip";
								System.out.println("The file has sucessfully been transfered");
							}catch (Exception e){
								e.printStackTrace();
							}
						}else{
							System.out.println(transmission);
						}
					}					
					if (!transmission.equals("--rip"))
						out.writeBytes(transmission + '\n');
				}
				if (in.ready())
				{
					transmission = in.readLine();
					System.out.println(transmission);
				}

			}
			clientSocket.close();
		}catch(Exception e){
			System.out.println("Must Establish Connection: Client <Server-IP>");
		}
    }
}
