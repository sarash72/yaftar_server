package yaftar;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

 

public class Source_Manage {
	   private static SessionFactory factory; 

	public static void main(String[] args)  {
			
				   try{
						factory = new Configuration().configure().buildSessionFactory();
				      }catch (Throwable ex) { 
				         System.err.println("Failed to create sessionFactory object." + ex);
				         throw new ExceptionInInitializerError(ex); 
				      }
				     Session session = factory.openSession();
				      Transaction tx = null;
				      Integer sourceID = null;
				      try{
				         tx =  session.beginTransaction();
				         Source source= new Source("sar", "alzahr","1","2","5","6");
				         sourceID = (Integer) session.save(source); 
				         tx.commit();
				      }catch (HibernateException e) {
				         if (tx!=null) tx.rollback();
				         e.printStackTrace(); 
				      }finally {
				         session.close(); 
				      }
			}

}
 

