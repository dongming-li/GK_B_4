package Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Contains methods for determining minimum, maximum, average, and median time taken and allowance paid for a chore.
 * Research is based on user criteria for location, chore, and user.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class ResearchDatabase 
{

	static Connection conn;	// Database connection
	String s; // String for database call used to get location and also min, max, and avg time and allowance.
	String mts;	// String for database call to get median time
	String mas;	// String for database call to get median allowance
	String country = null;
	String state = null;
	String city = null;
	int count = 0; // number of completed jobs in results from string s used to find median
	PreparedStatement ps;	// database statement
	ResultSet rs;	// database results
	
	/**
	 * Constructor passing parameter for the database connection
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public ResearchDatabase(Connection connection) throws SQLException
	{
		conn = connection;
	}
	
	/**
	 * Closes the database connection
	 * 
	 * @throws SQLException
	 */
	public void closeDB() throws SQLException
	{
		conn.close();
	}
	
	/**
	 * Queries the database based on user criteria for location, chores, and user to determine minimum, 
	 * maximum, average, and median time taken for each chore specified.
	 * 
	 * @param data	user criteria
	 * @return	minimum, maximum, average, and time taken for each chore specified
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	public ArrayList<Object> getResearch(ArrayList<Object> data) throws SQLException
	{
		ArrayList<Object> results = new ArrayList<Object>(); //contains results for each chore to be returned
		String familyName = (String)data.get(1);
		String location = (String)data.get(2);
		@SuppressWarnings("unchecked")
		ArrayList<String> chores = (ArrayList<String>) data.get(3);
		
		// define 2 gender variables that are the same if gender is male or female and different if don't care about gender
		String gender = (String)data.get(4);
		String gender2 = (String)data.get(4);
		
		if(gender.equalsIgnoreCase("Both"))
		{
			gender = "Female";
			gender2 = "Male";
		}
		
		int minAge = (int)data.get(5);
		int maxAge = (int)data.get(6);
		String userType = (String)data.get(7);
		
		// Determine location if not world
		if(!location.equalsIgnoreCase("world"))
		{
			location = getLocation(location, familyName, data);
		}
		
		// For each chore requested find the min, max, avg, and mean time taken and allowance
		for(int c = 0; c < chores.size(); c++)
		{
			ArrayList<Object> result = new ArrayList<Object>();	// Single chore results
			result.add(chores.get(c));
			
			// Set database call strings s, mts and mas
			dbCalls(location, country, state, city);
			
			// Call database for each string and process results
			for(int l = 0; l < 3; l++)
			{
				// Set database statement and values
				if(l == 0)
				{
					ps = conn.prepareStatement(s);
				}
				else if(l == 1)
				{
					ps = conn.prepareStatement(mts);
				}
				else
				{
					ps = conn.prepareStatement(mas);
				}
				
				ps.setString(1, chores.get(c));
				ps.setString(2, gender);
				ps.setString(3, gender2);
				ps.setString(4, userType);
				ps.setInt(5, minAge);
				ps.setInt(6, maxAge);
				
				// Determine the rest of the statement values
				getStatement(location, l);
						
				// Query the database and get result
				rs = ps.executeQuery();
				result = getResult(result, l);		
			}			
			results.add(result);
		}		
		return results;		
	}
	
	/**
	 * Determines the location to base research on if world was not specified
	 * 
	 * @param local
	 * @param familyName
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	private String getLocation(String local, String familyName, ArrayList<Object> data) throws SQLException
	{
		String location = "World";
		
		// If location is other determine if desired location is country, state, or city and set variables
		// else if location is not other or world get users location from database
		if(local.equalsIgnoreCase("other"))
		{
			if(data.get(10) != null)
			{
				country = (String) data.get(8);
				state = (String) data.get(9);
				city = (String) data.get(10);
				location = "City";
			}
			else if(data.get(9) != null)
			{
				country = (String) data.get(8);
				state = (String) data.get(9);
				location = "State";
			}
			else
			{
				country = (String) data.get(8);
				location = "Country";
			}
		}
		else
		{
			s = "SELECT Country, State, City FROM Families WHERE Usernames=?";
			ps = conn.prepareStatement(s);
			ps.setString(1, familyName);
			rs = ps.executeQuery();
		
			if(rs.next())
			{
				country = rs.getString(1);
				state = rs.getString(2);
				city = rs.getString(3);
			}
		}
		return location;
	}
	
	/**
	 * Determines strings for database calls based on location
	 * 
	 * @param location
	 * @param country
	 * @param state
	 * @param city
	 */
	private void dbCalls(String location, String country, String state, String city)
	{
		// Determine strings to use for database calls based on location
		if( location.equalsIgnoreCase("Country") && country != null )
		{
			s = "SELECT COUNT(DISTINCT Families.Usernames), COUNT(DISTINCT Job_Assignment.Member), SUM(Job_Assignment.completed), MIN(time_taken), AVG(time_taken), "
					+ "MAX(time_taken), MIN(allowance), AVG(allowance), MAX(allowance) FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=?";
		
			mts = "SELECT time_taken FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=? ORDER BY time_taken LIMIT ?";
		
			mas = "SELECT allowance FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=? ORDER BY allowance LIMIT ?";
		}
		else if( location.equalsIgnoreCase("State") && state != null )
		{
			s = "SELECT COUNT(DISTINCT Families.Usernames), COUNT(DISTINCT Job_Assignment.Member), SUM(Job_Assignment.completed), MIN(time_taken), AVG(time_taken), " 
					+ "MAX(time_taken), MIN(allowance), AVG(allowance), MAX(allowance) FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=? AND State=?";

			mts = "SELECT time_taken FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=? AND State=? ORDER BY time_taken LIMIT ?";
		
			mas = "SELECT allowance FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=? AND State=? ORDER BY allowance LIMIT ?";
		}
		else if( location.equalsIgnoreCase("City") && city != null )
		{
			s = "SELECT COUNT(DISTINCT Families.Usernames), COUNT(DISTINCT Job_Assignment.Member), SUM(Job_Assignment.completed), MIN(time_taken), AVG(time_taken), " 
					+ "MAX(time_taken), MIN(allowance), AVG(allowance), MAX(allowance) FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=? AND State=? AND City=?";
		
			mts = "SELECT time_taken FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=? AND State=? AND City=? ORDER BY time_taken LIMIT ?";
		
			mas = "SELECT allowance FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? AND Country=? AND State=? AND City=? ORDER BY allowance LIMIT ?";
		}
		else
		{
			s = "SELECT COUNT(DISTINCT Families.Usernames), COUNT(DISTINCT Job_Assignment.Member), SUM(Job_Assignment.completed), "
					+ "MIN(time_taken), AVG(time_taken), MAX(time_taken), MIN(allowance), AVG(allowance), MAX(allowance) FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ?";

			mts = "SELECT time_taken FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? ORDER BY time_taken LIMIT ?";
		
			mas = "SELECT allowance FROM Families INNER JOIN Individuals "
					+ "ON Families.Usernames=Individuals.Mem_Username INNER JOIN Job_Assignment ON Individuals.Member=Job_Assignment.Member "
					+ "WHERE Job_Name=? AND completed=1 AND Sex BETWEEN ? AND ? AND User_Type=? AND Age BETWEEN ? AND ? ORDER BY allowance LIMIT ?";
		}		
	}
	
	/**
	 * Determines the values for the remaining part of the database statement
	 * 
	 * @param location
	 * @param l
	 * @throws SQLException
	 */
	private void getStatement(String location, int l) throws SQLException
	{
		// For string s if location is not the world determine location and set values
		// Else for string mts and mas if location is not the world determine location and set values also set median position
		if(!location.equalsIgnoreCase("World") && l == 0)
		{
			ps.setString(7, country);
			
			if(!location.equalsIgnoreCase("Country"))
			{
				ps.setString(8, state);
				
				if(location.equalsIgnoreCase("City"))
				{
					ps.setString(9, city);
				}
			}
			
		}
		else if(l > 0)
		{
			if(!location.equalsIgnoreCase("World"))
			{
				ps.setString(7, country);
				
				if(!location.equalsIgnoreCase("Country"))
				{
					ps.setString(8, state);
					
					if(location.equalsIgnoreCase("City"))
					{
						ps.setString(9, city);
						ps.setInt(10, (count+1)/2);
					}
					else
					{
						ps.setInt(9, (int)Math.ceil((count+1)/2.0));
					}
				}
				else
				{
					ps.setInt(8, (int)Math.ceil((count+1)/2.0));
				}
				
			}
			else
			{
				ps.setInt(7, (int)Math.ceil((count+1)/2.0));
			}
		}
	}
	
	/**
	 * Convert time taken to Time format and allowance to a 2 decimal format and add results for chore
	 * 
	 * @param result
	 * @param l
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	private ArrayList<Object> getResult(ArrayList<Object> res, int l) throws SQLException
	{
		ArrayList<Object> result = res;
		
		if(rs.next())
		{
			// If string s process accordingly
			// else if string mts or msa process accordingly
			if(l == 0 && rs.getInt(1) != 0)
			{
				result.add(rs.getInt(1));
				result.add(rs.getInt(2));
				result.add(rs.getInt(3));
				count = rs.getInt(3);
	
				// For min, max and avg time convert to Time format
				for(int i = 4; i < 7; i++)
				{
					long seconds = rs.getLong(i);
					long minutes = seconds / 60;
					seconds -= minutes * 60;
					long hours = minutes / 60;
					minutes -= hours * 60;
					Time time = new Time((int)hours, (int)minutes, (int)seconds);
					result.add(time);
				}
				
				//Convert allowance to 2 decimal places
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(2);
				result.add(df.format(rs.getFloat(7)));
				result.add(df.format(rs.getFloat(8)));
				result.add(df.format(rs.getFloat(9)));
			}
			else if(l > 0)
			{
				long addTime = 0; // Time of median position if odd or right median position if even 
				long addTimeTemp = 0; // Time of left median position if even
				float addAllow = 0;	// Allowance of median position if odd or right median position if even
				float addAllowTemp = 0;	// Allowance of left median position if even
				
				do
				{
					if(l == 1)
					{
						addTimeTemp = addTime;
						addTime = rs.getLong(1);
					}
					else
					{
						addAllowTemp = addAllow;
						addAllow = rs.getFloat(1);
					}
				}while(rs.next());
				
				if(l == 1)
				{
					long seconds = 0;
					
					if(count % 2 == 0)
					{
						seconds = (addTimeTemp + addTime) / 2;
					}
					else
					{
						seconds = addTime;
					}
					
					long minutes = seconds / 60;
					seconds -= minutes * 60;
					long hours = minutes / 60;
					minutes -= hours * 60;
					Time time = new Time((int)hours, (int)minutes, (int)seconds);addTime = (addTimeTemp + addTime)/2;
					result.add(time);
				}
				else
				{
					if(count % 2 == 0)
					{
						addAllow = (addAllowTemp + addAllow)/2;
					}
						
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(2);
					result.add(df.format(addAllow));
				}
			}
		}
		return result;
	}
}
