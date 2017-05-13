package yaftar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
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

import com.mysql.jdbc.jdbc2.optional.MysqlXid;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.management.ManagementFactory;

class Threads extends Thread {
	public void run() {
	}

}

// saver class(main)
public class Server {
	private static SessionFactory factory;

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
		// get date every 1 mine
		Timer timer = new Timer();
		/*timer.schedule(new TimerTask() {
			public void run() {
				NetworkInterface ni;
				// com.sun.management.OperatingSystemMXBean os =
				// (com.sun.management.OperatingSystemMXBean)
				// ManagementFactory.getOperatingSystemMXBean();

				myserver.addSource();

			}
		}, 0, 1000 * 60 * 1);
		*/

		myserver.search_Source("2017.05.14", "02:08");

		// sarversocket
		String clint_request = null;
		ServerSocket server = new ServerSocket(12324);
		Socket client = server.accept();

		DataInputStream is = new DataInputStream(client.getInputStream());
		DataOutputStream os = new DataOutputStream(client.getOutputStream());
		clint_request = is.readLine();
		System.out.println(clint_request);
	}

	/* Method to CREATE an employee in the database */
	public Integer addSource() {
		String memory = null, disk = null, cpu = null;

		Session session = factory.openSession();
		// get system sources data
		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd kk:mm");

		String string_date = ft.format(date).toString();
		String[] date_split = string_date.split(" ");
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
				if (method.getName().equals("getCommittedVirtualMemorySize")) {
					memory = value.toString();

				}
				if (method.getName().equals("getProcessCpuLoad")) {
					cpu = value.toString();
				}
				if (method.getName().equals("getTotalPhysicalMemorySize")) {
					disk = value.toString();
				}
				try {
					NetworkInterface ni;

					ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
				} catch (SocketException | UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // if
		} // for
		Transaction tx = null;
		Integer employeeID = null;
		Integer sourceID = null;

		try {
			tx = session.beginTransaction();
			Source source = new Source(date_split[0], date_split[1], disk, memory, cpu, "4");
			sourceID = (Integer) session.save(source);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return employeeID;
	}

	/* Method to READ all the employees */
	public void search_Source(String date, String time) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List sources = session.createQuery("FROM Source").list();

			for (Iterator iterator = sources.iterator(); iterator.hasNext();) {
				Source choose_source = (Source) iterator.next();
				String source_date_string = choose_source.getDate();
				String source_time_string = choose_source.getTime();
				//System.out.println(source_date_string+" ----- "+ date);
				
				if (source_date_string.equals(date) && source_time_string.equals(time)) {
					System.out.println(source_date_string + source_time_string + date + time);

					System.out.print("Disk Usage: " + choose_source.getDisk());
					System.out.print("Memory Usage: " + choose_source.getMemory());
					System.out.println("Cpu Usage: " + choose_source.getCpu());
				}
			}
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
