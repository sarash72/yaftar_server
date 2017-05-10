package yaftar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException {
		
			Socket client = new Socket("192.168.1.127", 12324);

			DataInputStream is = new DataInputStream(client.getInputStream());
			DataOutputStream os = new DataOutputStream(client.getOutputStream());
	System.out.println("client");

}
}