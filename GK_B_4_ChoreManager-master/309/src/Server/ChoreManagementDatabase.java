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
import java.util.ArrayList;
import java.util.Calendar;
/**
 * This class contains database calls that relate to managing chore information. Methods in this class include retrieving jobs,
 * assigning chores, modifying chores, trading chores, and retrieving unavailable times for users.
 * @author GK_B_4
 *
 */
public class ChoreManagementDatabase {
	
	static Connection conn;
	public ChoreManagementDatabase(Connection connection) throws SQLException
	{
		conn = connection;
	}
	
	public void closeDB() throws SQLException
	{
		conn.close();
	}

	/**
 * Retrieves all of the jobs that are assigned for a specific on the selected date. Takes in the username and specific date
 * as a parameter. Returns an ArrayList of ArrayLists that contain the information for each job assigned that day.
 * @param user
 * @param d
 * @return
 * @throws SQLException
 * @throws ParseException
 */
	public ArrayList<Object> retrieveJobs(String user, String d, String day) throws SQLException, ParseException
    {
		ArrayList<Object> jobs = new ArrayList<Object>();
		ArrayList<Object> prereqs = new ArrayList<Object>();
		ArrayList<Object> unavail = unavailableTimes(user, day);
		
		for(int i = 0; i < unavail.size(); i++)
		{
			jobs.add(unavail.get(i));
		}
		
		java.util.Date temp = new SimpleDateFormat("yyyy/MM/dd").parse(d);
		java.sql.Date date = new java.sql.Date(temp.getTime());
		String s = "SELECT Job_Assignment.Member, Job_Name, Job_Time, allowance FROM Job_Assignment INNER JOIN Individuals "
				+ "ON Job_Assignment.Member = Individuals.Member AND Job_Assignment.Mem_Username = Individuals.Mem_Username "
				+ "WHERE Job_Assignment.Mem_Username=? AND date_done=? AND completed=? AND inactive=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, user);
		ps.setDate(2, date);
		ps.setBoolean(3, false);
		ps.setBoolean(4, false);
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			ArrayList<Object> info = new ArrayList<Object>();
			info.add(0, rs.getString(1));
			info.add(1, rs.getString(2));
			info.add(rs.getTime(3));
			info.add(rs.getDouble(4));
			if(info.get(1).equals("Unavailable"))
			{
				info.add(new ArrayList<Object>());
				info.add(2, "Unavailable");
				jobs.add(info);
			}
			else
			{
				String s2 = "SELECT job_desc, prereq1, prereq2, prereq3, prereq4, prereq5 FROM Jobs_Defined WHERE Username=? AND job_name=?";
				PreparedStatement ps2 = conn.prepareStatement(s2);
				ps2.setString(1, user);
				ps2.setString(2, rs.getString(2));
				ResultSet rs2 = ps2.executeQuery();
				prereqs = new ArrayList<Object>();
				if(rs2.next())
				{
	    			info.add(2, rs2.getString(1));
					for(int i = 2; i < 11; i++)
					{
						if(rs2.getString(i) == null)
						{
							break;
						}
						else
						{
							prereqs.add(rs2.getString(i));
						}
					}
				}
				info.add(prereqs);
				jobs.add(info);
			}
		}
		return jobs;
	}
	
	/**
	 * Returns a list of all available jobs that can be assigned to someone.
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> getJobs() throws SQLException
	{
		ArrayList<Object> jobs = new ArrayList<Object>();
		String s = "SELECT job_name from Job_List";
		PreparedStatement ps = conn.prepareStatement(s);
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			jobs.add(rs.getString(1));
		}
		return jobs;
	}
	
	/**
	 * Used to assign a job to a family member. The member name, the family name, the job name, job description, incentive, time due, and date
	 * due are all passed in and added to the table. Returns true if the job is successfully added to the database, false otherwise.
	 * @param memName
	 * @param famName
	 * @param jobName
	 * @param t
	 * @param d
	 * @return
	 * @throws SQLException
	 * @throws ParseException 
	 */
	public static boolean assignJob(String memName, String famName, String jobName, Time t, String d, Double allowance) throws SQLException, ParseException
	{
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date(df.parse(d).getTime());
		if(checkUnavailable(famName, memName, date, t) == false || checkIsValid(d, t) == false)
		{
			return false;
		}
		String s = "INSERT INTO Job_Assignment (Member, Mem_Username, Job_Name, Job_Time, date_done, completed, late, allowance)"
				+ " values(?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, memName);
		ps.setString(2, famName);
		ps.setString(3, jobName);
		ps.setTime(4, t);
		ps.setDate(5, date);
		ps.setBoolean(6, false);
		ps.setBoolean(7, false);
		ps.setDouble(8, allowance);
		int i = ps.executeUpdate();
		if(i > 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Method called when a chore trade request has been approved. Takes in the username of the family, the trader, the tradee,
	 * the job name, and the date that the chore is due. Swaps the name of the trader with the name of the tradee within
	 * the database. Returns true if successfully executed, false otherwise.
	 * @param chores
	 * @return
	 * @throws SQLException 
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public static boolean tradeChore(ArrayList<Object> chores) throws SQLException, ParseException
	{
		ArrayList<Object> chore1 = (ArrayList<Object>) chores.get(0);
		ArrayList<Object> chore2 = (ArrayList<Object>) chores.get(1);
		String s = "UPDATE Job_Assignment SET Member=? WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=? AND Job_Time=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, (String)chore2.get(1));
		ps.setString(2, (String)chore1.get(1));
		ps.setString(3, (String)chore1.get(0));
		ps.setString(4, (String)chore1.get(2));
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date(df.parse((String)chore1.get(3)).getTime());
		ps.setDate(5, d);
		ps.setTime(6, (Time)chore1.get(4));
		int i = ps.executeUpdate();
		if(i < 1)
		{
			return false;
		}
		PreparedStatement ps2 = conn.prepareStatement(s);
		ps2.setString(1, (String)chore1.get(1));
		ps2.setString(2, (String)chore2.get(1));
		ps2.setString(3, (String)chore2.get(0));
		ps2.setString(4, (String)chore2.get(2));
		DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd");
		Date d2 = new Date(df2.parse((String)chore2.get(3)).getTime());
		ps2.setDate(5, d2);
		ps2.setTime(6, (Time)chore2.get(4));
		i = ps2.executeUpdate();
		if(i > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Updates a current chore in the case of a problem occurring in which the parent must change the date and time
	 * of when a chore is due. Returns true if successfully modified, false otherwise.
	 * @param username
	 * @param member
	 * @param jobName
	 * @param oldDate
	 * @param newDate
	 * @param newTime
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public static boolean modifyChore(String username, String member, String jobName, String oldDate, String newDate, Time newTime, Double allowance) throws SQLException, ParseException
	{
		String s = "UPDATE Job_Assignment SET Job_Time=?, date_done=?, allowance=? WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=?";
		PreparedStatement ps = conn.prepareStatement(s);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date oldD = new Date(df.parse(oldDate).getTime());
		Date newD = new Date(df.parse(newDate).getTime());
		ps.setTime(1, newTime);
		ps.setDate(2, newD);
		ps.setDouble(3, allowance);
		ps.setString(4, member);
		ps.setString(5, username);
		ps.setString(6, jobName);
		ps.setDate(7, oldD);
		int i = ps.executeUpdate();
		if(i > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * A family username is passed in to this method, and a list consisting of the names of only chores that have been defined by the family
	 * is returned. Used for assigning chores since only chores than have been defined can be assigned.
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> getDefinedChores(String username) throws SQLException
	{
		ArrayList<Object> chores = new ArrayList<Object>();
		String s = "SELECT job_name FROM Jobs_Defined WHERE Username=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			chores.add(rs.getString(1));
		}
		return chores;
	}
	
	/**
	 * Retrieves the unavailable times for a specific day for a given family. Returns a list that contains lists with the name of the member and their 
	 * unavailable times for the given day. Used to populate calendar on main, parent, child, and other screens
	 * @param username
	 * @param day
	 * @return
	 * @throws SQLException
	 */
	private ArrayList<Object> unavailableTimes(String username, String day) throws SQLException
	{
		ArrayList<Object> unavailable = new ArrayList<Object>();
		String s = "SELECT Member, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13 FROM Unavailabilities WHERE Username=? and DayOfWeek=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, username);
		ps.setString(2, day);
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			ArrayList<Object> times = new ArrayList<Object>();
			for(int i = 2; i < 15; i++)
			{
				if(rs.getTime(i) == null)
				{
					break;
				}
				else
				{
					times.add(rs.getTime(i));
				}
			}
			for(int j = 0; j < times.size(); j++)
			{
				String member = rs.getString(1);
				boolean inactive = isInactive(username, member);
				if(!inactive)
				{
					ArrayList<Object> info = new ArrayList<Object>();
					info.add(rs.getString(1));
					info.add("Unavailable");
					info.add("Not available to work");
					info.add(times.get(j));
					info.add(0.0);
					info.add(new ArrayList<Object>());
					unavailable.add(info);
				}
			}
		}
		return unavailable;
	}
	
	/**
	 * Takes in a member, date, and time and checks with the database to see if the member is unavailable at that time and day. Used when
	 * assigning chores to make sure the member can take on that chore.
	 * @param username
	 * @param mem
	 * @param d
	 * @param t
	 * @return
	 * @throws SQLException
	 */
	private static boolean checkUnavailable(String username, String mem, java.util.Date d, Time t) throws SQLException
	{
		DateFormat format2=new SimpleDateFormat("EEEE"); 
		String day = format2.format(d);
		String s = "SELECT t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13 FROM Unavailabilities WHERE Member=? AND Username=? AND DayOfWeek=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, mem);
		ps.setString(2, username);
		ps.setString(3, day);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			int i = 1;
			while(i < 14 && rs.getTime(i) != null)
			{
				if(rs.getTime(i).equals(t))
				{
					return false;
				}
				i++;
			}
		}
		String s2 = "SELECT Job_Name FROM Job_Assignment WHERE Member=? AND Mem_Username=? AND date_done=? AND Job_Time=?";
		PreparedStatement ps2 = conn.prepareStatement(s2);
		ps2.setString(1, mem);
		ps2.setString(2, username);
		ps2.setDate(3, (Date) d);
		ps2.setTime(4, t);
		ResultSet rs2 = ps2.executeQuery();
		if(rs2.next())
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieves the allowance for a single chore
	 * @param family
	 * @param member
	 * @param d
	 * @param jobName
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	public double retrieveJobAllowance(String family, String member, String d, String jobName) throws ParseException, SQLException
	{
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date(df.parse(d).getTime());
		Double allowance = 0.0;
		String s = "SELECT allowance FROM Job_Assignment WHERE Member=? AND Mem_Username=? AND date_done=? AND Job_Name=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, member);
		ps.setString(2, family);
		ps.setDate(3, date);
		ps.setString(4, jobName);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			allowance = rs.getDouble(1);
		}
		return allowance;
	}
	
	/**
	 * Checks if an assigned chore date and time is before or after the current time. Used to make sure a job is not assigned to be due in the past.
	 * Returns true if the chore date and time is after the current time, false otherwise.
	 * @param d
	 * @param t
	 * @return
	 * @throws ParseException
	 */
	private static boolean checkIsValid(String d, Time t) throws ParseException
	{
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = d + " " + t;
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		java.util.Date assigned = format.parse(date);
		java.util.Date current = format.parse(timeStamp);
		if(assigned.before(current))
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if a family member is active. Returns true if the member is inactive, false otherwise
	 * @param family
	 * @param member
	 * @return
	 * @throws SQLException
	 */
	private static boolean isInactive(String family, String member) throws SQLException
	{
		String s = "SELECT inactive FROM Individuals WHERE Member=? AND Mem_Username=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, member);
		ps.setString(2, family);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			boolean inactive = rs.getBoolean(1);
			if(inactive)
			{
				return true;
			}
		}
		return false;
	}
}