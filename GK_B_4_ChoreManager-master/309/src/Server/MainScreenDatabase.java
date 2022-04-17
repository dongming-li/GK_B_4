package Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * This class contains the database calls that are associated with the main screen of this application. These calls include
 * retrieving member names and individual member logins.
 * @author GK_B_4
 *
 */
public class MainScreenDatabase {
	
	static Connection conn;
	public MainScreenDatabase(Connection connection) throws SQLException
	{
		conn = connection;
	}
	
	public void closeDB() throws SQLException
	{
		conn.close();
	}
	
	/**
	 * Retrieves the names all the members of a specific family, which is displayed on the main screen
	 * @param famUsername
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> retrieveMembers(String famUsername, boolean all) throws SQLException
	{
		ArrayList<Object> members = new ArrayList<Object>();
		String s = "";
		if(!all)
		{
			s = "SELECT Member FROM Individuals WHERE Mem_Username=? AND inactive=?";
		}
		else
		{
			s = "SELECT Member FROM Individuals WHERE Mem_Username=?";
		}
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, famUsername);
		if(!all)
		{
			ps.setBoolean(2, false);
		}
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			members.add(rs.getString(1));
		}
		return members;
	}
	
	/**
	 * Used when a specific user is trying to login to their specific account within the family. Takes in the family username,
	 * the individual name, and the pin. Returns an ArrayList of objects that contains the user type if the pin is correct,
	 * and returns an empty list if the pin is incorrect.
	 * @param user
	 * @param indv
	 * @param pin
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> loginPin(String user, String indv, Integer pin) throws SQLException
	{
		ArrayList<Object> type = new ArrayList<Object>();
		String s = "SELECT User_Type FROM Individuals WHERE Mem_Username=? AND Member=? AND Pin=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, user);
		ps.setString(2, indv);
		ps.setInt(3, pin);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			type.add(rs.getString(1));
		}
		return type;
	}
}