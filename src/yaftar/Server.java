package yaftar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.management.ManagementFactory;


class Threads extends Thread {
	public void run(){
		 OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
		    method.setAccessible(true);
		    if (method.getName().startsWith("get") 
		        && Modifier.isPublic(method.getModifiers())) {
		            Object value;
		        try {
		            value = method.invoke(operatingSystemMXBean);
		        } catch (Exception e) {
		            value = e;
		        } // try

		        System.out.println(method.getName() + " = " + value);}
		    } // if
		  } // forsdsd
		
	}


public class Server {
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(12324);
		Socket client = server.accept();

		DataInputStream is = new DataInputStream(client.getInputStream());
		DataOutputStream os = new DataOutputStream(client.getOutputStream());
		Threads thread=new Threads();
		thread.start();
		System.out.println("sarver");
	}

}
