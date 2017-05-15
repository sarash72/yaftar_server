package yaftar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Message;

import com.mysql.jdbc.jdbc2.optional.MysqlXid;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.management.ManagementFactory;

/**
 * every client use one thread with MyThread class
 * 
 * @author SHirdel
 */
class MyThread extends Thread {
	SessionFactory factory;
	String date;
	Socket client;

	/**
	 * MyThread contractor
	 * 
	 * @param client
	 * @param factory
	 */
	MyThread(Socket client, SessionFactory factory) {
		this.factory = factory;
		this.client = client;
	}

	/**
	 * run function with MyThread class
	 */
	public void run() {
		DataInputStream is;
		try {
			is = new DataInputStream(client.getInputStream());
			DataOutputStream os = new DataOutputStream(client.getOutputStream());
			/**
			 * get client request
			 */
			String client_request = is.readLine();
			System.out.println(client_request);
			String[] split_client_thread = client_request.split(" ");
			/**
			 * send client date and time to search function
			 */
			search_Source(split_client_thread[0], split_client_thread[1]);
			os.writeBytes(client_request);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Search function for search in databases for find system information in
	 * this time
	 * 
	 * @param date
	 *            for get client request
	 * @param time
	 *            for get client request
	 */
	/* Method to Search all the employees */
	public String search_Source(String date, String time) {
		Session session = factory.openSession();
		String result_search = null;
		/**
		 * if client request not exist in databases flag is 0
		 */
		int flag = 0;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List sources = session.createQuery("FROM Source").list();
			/**
			 * find information
			 */
			for (Iterator iterator = sources.iterator(); iterator.hasNext();) {
				Source choose_source = (Source) iterator.next();
				String source_date_string = choose_source.getDate();
				String source_time_string = choose_source.getTime();
				if (source_date_string.equals(date) && source_time_string.equals(time)) {
					flag = 1;
					result_search = "Disk Usage: " + choose_source.getDisk() + "  " + "Memory Usage: "
							+ choose_source.getMemory() + "  " + "Cpu Usage: " + choose_source.getCpu() + "  "
							+ "IO Usage: " + choose_source.getIO();
					break;
				}
			}
			if (flag == 0) {
				result_search = "this time not exist in databases";
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result_search;
	}
}

/**
 * server class and main
 * 
 * @author SHirdel
 * 
 */
// server class(main)
public class Server {
	static SessionFactory factory;

	public static void main(String[] args) throws IOException {

		// save data in database with hibernate
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		Session session = factory.openSession();
		Server myserver = new Server();
		/**
		 * get information every one min
		 */
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				myserver.addSource();
			}
		}, 0, 1000 * 60 * 1);

		/**
		 * serversocket
		 */
		ServerSocket server = new ServerSocket(12324);
		/**
		 * arraylist for client's thread
		 */
		List<MyThread> thread_client = new ArrayList<MyThread>();
		/**
		 * add to arraylist and start thread
		 */
		while (true) {
			Socket client = server.accept();
			MyThread mythread = new MyThread(client, factory);
			thread_client.add(mythread);
			thread_client.get(thread_client.size() - 1).start();

		}

	}

	/**
	 * add information in databases
	 */
	/* Method to CREATE an employee in the database */
	public void addSource() {
		String memory = null, disk = null, cpu = null, IO = null;

		Session session = factory.openSession();
		/**
		 * get date and time
		 */
		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd kk:mm");
		String string_date = ft.format(date).toString();
		String[] date_split = string_date.split(" ");
		/**
		 * get information fram system
		 */
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
				Object value;
				try {
					value = method.invoke(operatingSystemMXBean);
				} catch (Exception e) {
					value = e;
				} // try
				/**
				 * get memory usage in system
				 */
				if (method.getName().equals("getCommittedVirtualMemorySize")) {
					memory = value.toString();
				}
				/**
				 * get cpu usage in system
				 */
				if (method.getName().equals("getProcessCpuLoad")) {
					cpu = value.toString();
				}
				/**
				 * get disk usage in system
				 */
				if (method.getName().equals("getFreePhysicalMemorySize")) {
					disk = value.toString();
				}
				try {
					/**
					 * get IO usage in system
					 */
					int ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getIndex();
					IO = ni + "";
				} catch (SocketException | UnknownHostException e) {
					e.printStackTrace();
				}
			} // if
		} // for
		Transaction tx = null;
		Integer sourceID = null;
		/**
		 * set information in databases
		 */
		try {
			tx = session.beginTransaction();
			Source source = new Source(date_split[0], date_split[1], disk, memory, cpu, IO);
			sourceID = (Integer) session.save(source);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

}
