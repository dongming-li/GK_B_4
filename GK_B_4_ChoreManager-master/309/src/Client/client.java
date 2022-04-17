package Client;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

/**
 * Class to connect user interface to server. Specific user actions on each screen will call 
 * this class to contact the database via a server. The client will wait for a server response to return to the user screen.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class client
{
	/**
	 * Contacts the specified server to run a database query and return the result from the server to the user interface.
	 * 
	 * @param sendToServer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> clientConnection(ArrayList<Object> sendToServer)
	{
		//Server information
		String host = "localhost"; //"proj-309-gk-b-4.cs.iastate.edu";	//Define host ip
		int port = 5000;	//Define port to connect to
		ArrayList<Object> serverResponse = null;
		
		//Establish a connection
		try
		{
			//Obtain an address for the server
			InetAddress address = InetAddress.getByName(host);

			//Establish a socket connection
			Socket connection = new Socket(address, port);
			System.out.println("Connection to server established");

			//Establish output stream and send object to server
			ObjectOutputStream oot = new ObjectOutputStream(connection.getOutputStream());
	        oot.writeObject(sendToServer);
	        System.out.println("Waiting for server response");
	        
	        //Establish input stream and record server response
			ObjectInputStream ois =  new ObjectInputStream(connection.getInputStream());
			serverResponse = (ArrayList<Object>)ois.readObject();
			System.out.println("Server responded closing connection");
			
		    //Close connection.  check on closing streams.  may need to send bye msg.
		    connection.close();
		    System.out.println("Connection closed");
		}
		catch (IOException f) 
		{
			System.out.println("IOException: " + f);
		}
		catch (Exception g) 
		{
		    System.out.println("Exception: " + g);
		}
		
		return serverResponse;
	}
}