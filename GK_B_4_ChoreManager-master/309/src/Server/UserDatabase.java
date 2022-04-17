package Server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
/**
 * This class contains all the database calls that relate to specific users. These calls include family logins, add members, modifying members,
 * family registration, setting unavailabilities, and retrieving user information.
 * @author GK_B_4
 *
 */
public class UserDatabase {
	
	static Connection conn;
	public UserDatabase(Connection connection) throws SQLException
	{
		conn = connection;
	}
	
	public void closeDB() throws SQLException
	{
		conn.close();
	}
	
	/**
	 * Used when a person forgets their login or password. If type = 1, then the username for the person is retrieved based
	 * on the email and password that they provided that is tied to their family account. If type = 2, then a password hint is 
	 * retrieved based on the username and email associated with the family account. If incorrect credentials are provided, then
	 * null is returned.
	 * @param type
	 * @param s1
	 * @param s2
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> altLogin(int type, String s1, String s2) throws SQLException
	{
		ArrayList<Object> hint = new ArrayList<Object>();
		String s = null;
		
		try{
			if( type == 1 )
			{
				s = "SELECT Usernames FROM Families where Emails=? and Passwords=?";
			}
			else
			{
				s = "SELECT PasswordHint FROM Families where Usernames=? and Emails=?";
			}
			
			PreparedStatement ps = conn.prepareStatement(s);
			ps.setString(1, s1);
			ps.setString(2, s2);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) 
			{
				hint.add(rs.getString(1));
				return hint;
			}
			}
			catch(SQLException sql)
			{
				System.out.println("Error connecting to database");
			}
		return null;
		
	}
	
		/**
	 * Takes in two strings, a username and a password, and makes a query to the database to see if there is a row that contains
	 * both the username and password
	 * @param s1
	 * @param s2
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> login(String s1, String s2) throws SQLException
	{
		ArrayList<Object> temp = new ArrayList<Object>();
		String s = "SELECT Passwords FROM Families where Usernames=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, s1);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) 
		{
			temp.add(true);
			if(rs.getString(1).equals(s2))
			{
				temp.add(true);
			}
			else
			{
				temp.add(false);
			}
		}
		else
		{
			temp.add(false);
			temp.add(false);
		}
		return temp;
	}
	
	/**
	 * Used to add a new family account to the database. Checks if the desired username or email is already contained in the database. 
	 * Returns an ArrayList containing two booleans. The first index represents the Username, and the second index represents the email.
	 * A false value is added if the username/email is contained within the table, true otherwise.
	 * @param user
	 * @param pass
	 * @param hint
	 * @param email
	 * @param country
	 * @param state
	 * @param city
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> register(String user, String pass, String hint, String email, String country, String state, String city) throws SQLException
	{
		String s = "SELECT Usernames FROM Families where Usernames=?";
		ArrayList<Object> bools = new ArrayList<Object>();
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, user);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) 
		{
		    bools.add(false);
		}
		else
		{
			bools.add(true);
		}
		
		if(bools.contains(false))
		{
			return bools;
		}
		
		s = "SELECT Emails FROM Families where Emails=?";
		ps = conn.prepareStatement(s);
		ps.setString(1, email);
		rs = ps.executeQuery();
		if(rs.next())
		{
			bools.add(false);
		}
		else
		{
			bools.add(true);
		}
		
		s = "INSERT INTO Families (Usernames, Passwords, PasswordHint, Emails, Country, State, City)"
				+ " values(?, ?, ?, ?, ?, ?, ?)";
		ps = conn.prepareStatement(s);
		ps.setString(1, user);
		ps.setString(2, pass);
		ps.setString(3, hint);
		ps.setString(4, email);
		ps.setString(5, country);
		ps.setString(6, state);
		ps.setString(7, city);
		ps.executeUpdate();
		
		return bools;
	}
	
		/**
	 * Used to add members for a specific family. Receives a list of lists, with each list containing information for a specific
	 * individual. Returns true if all members are added, false otherwise.
	 * @param user
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked"})
	public boolean addMembers(ArrayList<Object> status) throws SQLException, ParseException
	{
		for(int i = 2; i < status.size(); i++)
		{
			String s = "INSERT into Individuals (Member, Pin, Mem_Username, Birthday, Sex, User_type, notif_num, Age, inactive, allowance_owed)"
					+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(s);
			ArrayList<String> family = (ArrayList<String>) status.get(i);
			ps.setString(1, (String)family.get(0));
		    ps.setInt(2, Integer.valueOf((String)family.get(1)));
			ps.setString(3, (String)status.get(1));
			String d = (String)family.get(2);
			java.util.Date temp = new SimpleDateFormat("yyyy/MM/dd").parse(d);
			java.sql.Date date = new java.sql.Date(temp.getTime());
			
			ps.setDate(4, date);
			ps.setString(5, (String)family.get(3));
			ps.setString(6,(String)family.get(4));
			ps.setInt(7, 0);
			LocalDate now = LocalDate.now();
			LocalDate birth = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
			Period age = Period.between(birth, now);
			ps.setInt(8, age.getYears());
			ps.setBoolean(9, false);
			ps.setDouble(10, 0.0);
			int x = ps.executeUpdate();
			if(x < 1)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Takes in a list of member's data to modify. A member's pin, birthday, sex, user type, and whether they are inactive
	 * or not can be modified by a parent at one time. Returns true if the modifications were successfully made, false otherwise.
	 * @param info
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public boolean modifyMember(ArrayList<Object> info) throws SQLException, ParseException
	{
		String s = "UPDATE Individuals SET Pin=?, Birthday=?, Sex=?, User_Type=?, inactive=? WHERE Member=? AND Mem_Username=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setInt(1, Integer.valueOf((String)info.get(1)));
		java.util.Date temp = new SimpleDateFormat("yyyy/MM/dd").parse((String)info.get(2));
		java.sql.Date date = new java.sql.Date(temp.getTime());
		ps.setDate(2, date);
		ps.setString(3, (String)info.get(3));
		ps.setString(4, (String)info.get(4));
		ps.setBoolean(5, Boolean.valueOf((String)info.get(5)));
		ps.setString(6, (String)info.get(6));
		ps.setString(7, (String)info.get(7));
		int x = ps.executeUpdate();
		
		if(x < 0)
		{
			return false;
		}

		return true;
	}
	
	/**
	 * This method enters the unavailable times for a user for a given day into the database. An ArrayList is based in that contains the member name,
	 * the day of the week, and the times that the member is unavailable for that day. Up to 13 unavailable times can be entered for a single member.
	 * Return true if successfully entered into the database, false otherwise.
	 * @param info
	 * @return
	 * @throws SQLException
	 * @throws ParseException 
	 */
	//Info contains 3 items, the family name, a list of the member information, and an integer representing if it is reoccuring
	//The list for a member contains this info: (Member name, Date, ArrayList<Object> containing all the times for the day)
	public boolean requestUnavailable(ArrayList<Object> info) throws SQLException, ParseException
	{
		boolean success;
		if((Integer)info.get(2) == 1)
		{
			success = reoccuringUnavailability(info);
		}
		else
		{
			success = singleDayUnavailability(info);
		}
		return success;
	}
	
	/**
	 * Method used to modifiy the unavailability for a specific user for a specific day. The original row of unavailable
	 * times in the database is first deleted, and then the setUnavailable method is called with the given information passed into it.
	 * Returns true if unavailability is successfully modified, false otherwise.
	 * @param info
	 * @return
	 * @throws SQLException
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public boolean modifyUnavailability(ArrayList<Object> info) throws SQLException, ParseException
	{
		if((Integer)info.get(2) == 1)
		{
			//TODO
			return false;
		}
		else
		{
			ArrayList<Object> mem = (ArrayList<Object>) info.get(1);
			String s = "DELETE FROM Unavailabilities WHERE Member=? AND Username=?";
			PreparedStatement ps = conn.prepareStatement(s);
			ps.setString(1, (String)mem.get(0));
			ps.setString(2, (String)info.get(0));
			int x = ps.executeUpdate();
			if(x > 0)
			{
				reoccuringUnavailability(info);
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	/**
	 * Retrieves a specific users information from the database. Used in the modify member screen to populate
	 * the text boxes for a parent to modify. Returns an ArrayList that contains the retrieved information.
	 * @param family
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> getUserInfo(String family, String user) throws SQLException
	{
		ArrayList<Object> result = new ArrayList<Object>();
		String s = "SELECT Pin, Birthday, Sex, User_Type, inactive FROM Individuals WHERE Mem_Username=? AND Member=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, family);
		ps.setString(2, user);
		ResultSet rs = ps.executeQuery();
		
		if(rs.next())
		{
			result.add(rs.getInt(1));
			result.add(rs.getDate(2));
			result.add(rs.getString(3));
			result.add(rs.getString(4));
			result.add(rs.getBoolean(5));
		}
		System.out.println(rs.getBoolean(5));
		return result;
	}
	
	public ArrayList<Object> getAllowances(String family) throws SQLException
	{
		ArrayList<Object> allowances = new ArrayList<Object>();
		String s = "SELECT Member, allowance_owed FROM Individuals WHERE Mem_Username=? AND (User_Type=? OR User_Type=?)";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, family);
		ps.setString(2, "Child");
		ps.setString(3, "Other");
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			ArrayList<Object> info = new ArrayList<Object>();
			info.add(rs.getString(1));
			info.add(rs.getDouble(2));
			allowances.add(info);
		}
		return allowances;
	}
	
	/**
	 * Subtracts a specified amount from an Individual's total allowance owed
	 * @param family
	 * @param member
	 * @param amount
	 * @return
	 * @throws SQLException
	 */
	public boolean subtractAllowance(String family, String member, Double amount) throws SQLException
	{
		String s = "SELECT allowance_owed FROM Individuals WHERE Member=? AND Mem_Username=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, member);
		ps.setString(2, family);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			Double total = rs.getDouble(1);
			total -= amount;
			if(total < 0)
			{
				total = 0.0;
			}
			String s2 = "UPDATE Individuals SET allowance_owed=? WHERE Member=? AND Mem_Username=?";
			PreparedStatement ps2 = conn.prepareStatement(s2);
			ps2.setDouble(1, total);
			ps2.setString(2, member);
			ps2.setString(3, family);
			int x = ps2.executeUpdate();
			if(x > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Inserts a recurring unavailability into the database for a specific user on a specific date
	 * @param info
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	private boolean reoccuringUnavailability(ArrayList<Object> info) throws SQLException, ParseException
	{
		ArrayList<Object> mem1 = (ArrayList<Object>) info.get(1);
		String date = (String)mem1.get(1);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date(df.parse(date).getTime());
		DateFormat format2=new SimpleDateFormat("EEEE"); 
		String day = format2.format(d);
//		if(checkUnavailabilityTable((String)mem1.get(0), (String)info.get(0), day) == true)
//		{
//			return false;
//		}
		
		String s = "INSERT INTO Unavailabilities (Member, Username, DayOfWeek, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13)"
				+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, (String)mem1.get(0));
		ps.setString(2, (String)info.get(0));
		ps.setString(3, day);
		int i = 4;
		for(int j = 2; j < mem1.size(); j++)
		{
			ps.setTime(i, (Time)mem1.get(j));
			i++;
		}
		while(i < 17)
		{
			ps.setTime(i, null);
			i++;
		}
		int x = ps.executeUpdate();
		if(x > 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Sets a single day unavailability for a specific user on a specific date
	 * @param info
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private boolean singleDayUnavailability(ArrayList<Object> info) throws ParseException, SQLException
	{
		ArrayList<Object> mem = (ArrayList<Object>)info.get(1);
		String memName = (String)mem.get(0);
		String family = (String)info.get(0);
		String date = (String)mem.get(1);
		ArrayList<Object> times = (ArrayList<Object>) mem.get(2);
		String jobName = "Unavailable";
		Double allowance = 0.0;
		int i = 0;
		while(i < times.size())
		{	
			Time t = (Time)times.get(i);
			boolean success = ChoreManagementDatabase.assignJob(memName, family, jobName, t, date, allowance);
			if(!success)
			{
				return false;
			}
			i++;
		}
		return true;
	}
	
//	private boolean checkUnavailabilityTable(String day, String family, String member) throws SQLException
//	{
//		String s = "SELECT DayOfWeek FROM Unavailabilities WHERE Member=? AND Username=? AND DayOfWeek=?";
//		PreparedStatement ps = conn.prepareStatement(s);
//		ps.setString(1, member);
//		ps.setString(2, family);
//		ps.setString(3, day);
//		ResultSet rs = ps.executeQuery();
//		if(rs.next())
//		{
//			return true;
//		}
//		return false;
//	}
}