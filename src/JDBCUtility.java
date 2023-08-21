
import java.sql.Connection;
import java.sql.DriverManager;
public class JDBCUtility 
{
	//private Connection conn  ;
	private static JDBCUtility utilityInstance;
	
    
    private JDBCUtility()
    {
    	try 
    	{
		Class.forName("com.mysql.jdbc.Driver");  
		System.out.println("Driver is lode");
		}
    	catch(Exception e)
    	{ 
    		System.out.println(e);
    		e.printStackTrace();
    	}  
    }
     
    public static synchronized JDBCUtility getInstance(){
        if(utilityInstance == null){
        	utilityInstance = new JDBCUtility();
        }
        return utilityInstance;
    }
    
    public Connection getConnection()
    {
    	Connection conn = null ;
    	try 
    	{
    		 conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/empdb","root","root");
    	}
    	catch(Exception e)
    	{ 
    		System.out.println("ERROR IN OPENING CONNECTION");
    		System.out.println(e);
    	}
    	return conn;
        		//here  is database name, root is username and password  
    }
    public boolean closeConnection(Connection conn)
    {
    	try {
    		if(conn!= null)
    			conn.close();
    		return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return false;
    }
    
    public static void main(String[] args) {
    	JDBCUtility jdbc = JDBCUtility.getInstance();
    	jdbc.getConnection();
	}
}
