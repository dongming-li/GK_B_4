package Server;

import java.net.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.io.*;
import java.util.*;
import java.sql.Connection;

/**
 * Class for handling calls to a database. Calls come from client code on the users computer and 
 * a thread is spawned to handle each clients request.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class SocketServer implements Runnable 
{
	//Socket setup
	private static ServerSocket socket1;
	protected final static int port = 5000;
	private static Socket connection;
	private static Connection conn;
	private int ID; // Reference to database method to call
	
	/**
	 * Main method that waits for a client to contact it. When contacted deploys a thread to handle the client request.
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		//Connection identifier
		int count = 0;
	  
		//Connect to port and listen for client to connect to it starting new thread when they do
		try
		{
			socket1 = new ServerSocket(port);
			System.out.println("Socket established");
      
			while (true) 
			{
				System.out.println("Waiting for client");
				connection = socket1.accept();
				Runnable runnable = new SocketServer(connection, ++count);
				System.out.println("Client " + count + " connected");
				Thread thread = new Thread(runnable);
				thread.start();
			}
					}
		catch (Exception e) {}
	}
	
	//Thread to handle client request via run() method
	SocketServer(Socket s, int i) 
	{
		SocketServer.connection = s;
		this.ID = i;
	}

/**
* Thread that handles client request. Gets information from client and calls database returning results to client.
*/
@SuppressWarnings("unchecked")
@Override
	public void run() 
	{
		//Array list to handle data to and from client
		ArrayList<Object> fromClient = null;
		ArrayList<Object> sendToClient = null;
		
		//Read data from client, process it, and send requested data back
		try 
	    {
			//Establish input stream and record information from client
			ObjectInputStream ois =  new ObjectInputStream(connection.getInputStream());
			fromClient = (ArrayList<Object>)ois.readObject();
        
	        //For testing, need to process information accordingly
	        System.out.println(fromClient);
	        //sendToClient = new ArrayList<String>(Arrays.asList(new String[] {fromClient.get(0), "Not Used"}));
	        //database d = new database();
	        //sendToClient = new ArrayList<Object>(Arrays.asList(new Object[] {d.login((String)fromClient.get(1), (String)fromClient.get(2))}));
	        sendToClient = ServerToDb(fromClient);
	        
			//Establish output stream and send object to client
			ObjectOutputStream oot = new ObjectOutputStream(connection.getOutputStream());
	        oot.writeObject(sendToClient);
	    }
	    catch (Exception e) 
		{
	        System.out.println(e);
	    }
	    finally 
	    {
	        try 
	        {
	        	connection.close();
	        	System.out.println("Client " + ID + " connection closed");
	        }
	        catch (IOException e){}
	    }
	}

/**
 * Helper method for thread to send client request to database. 
 * Determines and calls correct database method based on information from client.
 * 
 * @param fromClient
 * @return
 * @throws SQLException
 * @throws ParseException
 */
	@SuppressWarnings("unchecked")
	public ArrayList<Object> ServerToDb(ArrayList<Object> fromClient) throws SQLException, ParseException
	{
		// Database connection
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (Exception E) 
		{
			System.err.println("Unable to load JDBC driver");
			E.printStackTrace();
		}
	
		String dbUrl = "jdbc:mysql://mysql.cs.iastate.edu:3306/db309gkb4?useSSL=false";
		String user = "dbu309gkb4";
		String password = "gwdCgt@W";
		conn = DriverManager.getConnection(dbUrl, user, password);
		ChoreDetailsDatabase cdd = new ChoreDetailsDatabase(conn);
		ChoreManagementDatabase cmd = new ChoreManagementDatabase(conn);
		LocationDatabase ld = new LocationDatabase(conn);
		UserDatabase ud = new UserDatabase(conn);
		MainScreenDatabase msd = new MainScreenDatabase(conn);
		NotificationDatabase nd = new NotificationDatabase(conn);
		ResearchDatabase rd = new ResearchDatabase(conn);
//		database d = new database();
		ArrayList<Object> data = new ArrayList<Object>();
		
		switch((int)fromClient.get(0))
		{
		case -1:	//Login screen
			System.out.println("Login Popup");
			data = ud.altLogin((int)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3));
			break;
		case 0:	//Login screen
			System.out.println("Login Screen");
			data = ud.login((String)fromClient.get(1), (String)fromClient.get(2));
			break;
		case 1:	//Registration screen
			System.out.println("Registration Screen");
			data = ud.register((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3), 
					(String)fromClient.get(4), (String)fromClient.get(5), (String)fromClient.get(6), (String)fromClient.get(7));
			break;
		case 2:	//Family creation screen
			System.out.println("Family Creation Screen");
			ArrayList<String> temp = (ArrayList<String>) fromClient.get(2);
			System.out.println(temp);
			data = new ArrayList<Object>(Arrays.asList(new Object[] {ud.addMembers(fromClient)}));
			break;
		case 3:	//Main screen
			System.out.println("Main Screen");
			data = cmd.retrieveJobs((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3));
			//data = new ArrayList<Object>(Arrays.asList(new Object[] {d.retrieveJobs((String)fromClient.get(1), (String)fromClient.get(2))}));	
			break;
		case 100:	//Countries on registration screen
			data = ld.getCountries();
			break;
		case 101:	//States on registration screen
			data = ld.getStates((String)fromClient.get(1));
			break;
		case 102:	//Cities on registration screen
			data = ld.getCities((String)fromClient.get(1));
			break;
		case 298:
			System.out.println("Modify Member");
			data = new ArrayList<Object>(Arrays.asList(new Object[] {ud.modifyMember(fromClient)}));
			break;
		case 299:
			data = ud.getUserInfo((String)fromClient.get(1), (String)fromClient.get(2));
			break;
		case 300:
			data = msd.retrieveMembers((String)fromClient.get(1), (Boolean)fromClient.get(2));
			break;
		case 301:
			data = msd.loginPin((String)fromClient.get(1), (String)fromClient.get(2), (int)fromClient.get(3));
			break;
		case 999:	// Research
			System.out.println("Research");
			data = rd.getResearch(fromClient);
			break;
		//Database methods without calls
		case 9900:
			data = new ArrayList<Object>(Arrays.asList(new Object[] {cdd.defineChore((String)fromClient.get(1), (String)fromClient.get(2), (ArrayList<Object>)fromClient.get(3), (Double)fromClient.get(4), (String)fromClient.get(5))}));
			break;
		case 9901:
			data = new ArrayList<Object>(Arrays.asList(new Object[] {ChoreManagementDatabase.assignJob((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3), (Time)fromClient.get(4), (String)fromClient.get(5), (Double)fromClient.get(6))}));
			break;
		case 9902:
			//d.calculateTime(memName, famName, jobName);
			break;
		case 9903:
			data = cmd.getJobs();
			break;
		case 9904:
			data = new ArrayList<Object>(Arrays.asList(new Object[] {cdd.jobStarted((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3), (String)fromClient.get(4), (Time)fromClient.get(5))}));
			break;
		case 9905:
			data = new ArrayList<Object>(Arrays.asList(new Object[] {cdd.jobFinished((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3), (String)fromClient.get(4), (Time)fromClient.get(5), (String)fromClient.get(6))}));
			break;
//		case 9907:
//			data = new ArrayList<Object>(Arrays.asList(new Object[] {ud.removeMember((String)fromClient.get(1), (String)fromClient.get(2))}));
//			break;
		case 9908:
			ArrayList<Object> chores = new ArrayList<Object>();
			chores.add(fromClient.get(1));
			chores.add(fromClient.get(2));
			data = new ArrayList<Object>(Arrays.asList(new Object[] {ud.addMembers(chores)}));
//			data = new ArrayList<Object>(Arrays.asList(new Object[] {d.tradeChore((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3), (String)fromClient.get(4), (Date)fromClient.get(5))}));
			break;
		case 9909:
			ArrayList<Object> info = new ArrayList<Object>();
			info.add(fromClient.get(1));
			info.add(fromClient.get(2));
			info.add(fromClient.get(3));
			data = new ArrayList<Object>(Arrays.asList(new Object[] {NotificationDatabase.addNotification(info)}));
			break;
		case 9910:
			data = nd.retrieveNotifications((String)fromClient.get(1));
			break;
		case 9911:
			ArrayList<Object> info2 = new ArrayList<Object>();
			info2.add(fromClient.get(1));
			info2.add(fromClient.get(2));
			info2.add(fromClient.get(3));
			info2.add(fromClient.get(4));
			data = new ArrayList<Object>(Arrays.asList(new Object[] {nd.deleteNotification(info2)}));
			break;
		case 9912:
			data = new ArrayList<Object>(Arrays.asList(new Object[] {ChoreManagementDatabase.modifyChore((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3), (String)fromClient.get(4), (String)fromClient.get(5), (Time)fromClient.get(6), (Double)fromClient.get(7))}));
			break;
		case 9913:
			data = cmd.getDefinedChores((String)fromClient.get(1));
			break;
		case 9914:
			data = nd.getNotificationNum((String)fromClient.get(1), (String)fromClient.get(2));
			break;
		case 9915:
			data = cdd.getChoreDef((String)fromClient.get(1), (String)fromClient.get(2));
			break;
		case 9916:
			ArrayList<Object> info3 = new ArrayList<Object>();
			info3.add(fromClient.get(1));
			info3.add(fromClient.get(2));
			info3.add(fromClient.get(3));
			data = new ArrayList<Object>(Arrays.asList(new Object[] {ud.requestUnavailable(info3)}));
			break;
		case 9917:
			data = nd.retrieveUserNotifications((String)fromClient.get(1), (String)fromClient.get(2));
			break;
		case 9918:
			ArrayList<Object> info4 = new ArrayList<Object>();
			info4.add(fromClient.get(1));
			info4.add(fromClient.get(2));
			info4.add(fromClient.get(3));
			data = new ArrayList<Object>(Arrays.asList(new Object[] {ud.modifyUnavailability(info4)}));
			break;
		case 9919:
			data = ud.getAllowances((String)fromClient.get(1));
			break;
		case 9920:
			data = new ArrayList<Object>(Arrays.asList(new Object[] {ud.subtractAllowance((String)fromClient.get(1), (String)fromClient.get(2), (Double)fromClient.get(3))}));
			break;
		case 9921:
			data = new ArrayList<Object>(Arrays.asList(new Object[] {cmd.retrieveJobAllowance((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3), (String)fromClient.get(4))}));
			break;
		case 9922:
			data = new ArrayList<Object>(Arrays.asList(new Object[] {cdd.isStarted((String)fromClient.get(1), (String)fromClient.get(2), (String)fromClient.get(3), (String)fromClient.get(4))}));
			break;
		default:
			System.out.println("Invalid input");
			data = new ArrayList<Object>(Arrays.asList(new Object[] {"Invalid"}));
			break;
			//Database methods
			//d.addMembers(status);
			//d.addPreferences(username, jobName, prereqs);
			//d.altLogin(type, s1, s2);
			//d.assignJob(memName, famName, jobName, desc, incent, t, d);
			//d.calculateTime(memName, famName, jobName);
			//d.getCities(state);
			//d.getCountries();
			//d.getJobs();
			//d.getStates(country);
			//d.jobFinished(memName, famName, jobName, t);
			//d.jobStarted(memName, famName, jobName, t);
			//d.login(s1, s2);
			//d.loginPin(user, indv, pin);
			//d.modifyMember(mod, Family);
			//d.register(user, pass, hint, email, country, state, city);
			//d.removeMember(user, family);
			//d.retrieveJobs(user, d);
			//d.retrieveMembers(famUsername);
			//tradeChore(String username, String trader, String tradee, String jobName, Date d)
			//addNotification(String username, String mem1, String mem2, String jobName, String date, String type, String message)
			//retrieveNotifications(String username)
			//deleteNotification(String username, String mem1, String mem2, String jobName, String date, String type)
			
		}
		
		conn.close();
		return data;		
	}

}