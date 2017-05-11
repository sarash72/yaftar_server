package yaftar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.management.ManagementFactory;



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
		
		
		// get date every 1 mine
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				Date date = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd kk:mm");
				String string_date = ft.format(date).toString();
				String[] date_split = string_date.split(" ");
				Transaction tx = null;
				Integer sourceID = null;
				Source source = new Source(date_split[0], date_split[1], "1", "2", "3", "4");
				tx = session.beginTransaction();
				sourceID = (Integer) session.save(source);
				tx.commit();
			}
		}, 0, 1000 * 60 * 1);

		
		// sarversocket
		ServerSocket server = new ServerSocket(12324);
		Socket client = server.accept();

		DataInputStream is = new DataInputStream(client.getInputStream());
		DataOutputStream os = new DataOutputStream(client.getOutputStream());
		System.out.println("sarver");
	}

}
