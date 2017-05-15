package yaftar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * client class for get date and time
 * 
 * @author SHirdel
 */
public class Client {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		Socket client = null;
		try {
			Logger logger = Logger.getLogger("MyLog");  
		    FileHandler fh; 
			client = new Socket("localhost", 12324);
			/**
			 * give date
			 */
			System.out.println("plz enter date same 2017.05.11");
			String date = scanner.next();
			/**
			 * give time
			 */
			System.out.println("plz enter time same 19:02");
			String time = scanner.next();

			/**
			 * send date to server
			 */
			DataOutputStream os = new DataOutputStream(client.getOutputStream());
			//os.writeBytes(date + " " + time);
			/**
			 * get and print system information
			 */
			DataInputStream is = new DataInputStream(client.getInputStream());
			//String input = is.readLine();
			//System.out.println(input);
			 fh = new FileHandler("D:/log/MyLogFile.log");  
		        logger.addHandler(fh);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter); 

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.close();

		}

	}
}