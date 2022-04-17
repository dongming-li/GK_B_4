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
import java.util.concurrent.TimeUnit;
/**
 * This class contains database calls that relate to chore details. These calls include setting when a user starts and stops working
 * on a chore, calculating total time to complete a chore, defining a chore, and retreiving chore definitions.
 * @author GK_B_4
 *
 */
public class ChoreDetailsDatabase {

	static Connection conn;
	public ChoreDetailsDatabase(Connection connection) throws SQLException
	{
		conn = connection;
	}
	
	public void closeDB() throws SQLException
	{
		conn.close();
	}

	/**
	 * Adds the time that a specific job is started by a specific member within a family to the database. Returns true if
	 * the time is successfully added, false otherwise.
	 * @param memName
	 * @param famName
	 * @param jobName
	 * @param d
	 * @param t
	 * @return
		 * @throws SQLException 
		 * @throws ParseException 
	 */
	public boolean jobStarted(String memName, String famName, String jobName, String d, Time t) throws SQLException, ParseException
	{
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		String s = "SELECT time_start FROM Job_Assignment WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=?";
		PreparedStatement ps2 = conn.prepareStatement(s);
		ps2.setString(1, memName);
		ps2.setString(2, famName);
		ps2.setString(3, jobName);
		Date date = new Date(df.parse(d).getTime());
		ps2.setDate(4, date);
		ResultSet rs = ps2.executeQuery();
		if(rs.next())
		{
			if(rs.getTime(1) != null)
			{
				return false;
			}
		}
		String s2 = "UPDATE Job_Assignment SET time_start=? WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=?";
		PreparedStatement ps = conn.prepareStatement(s2);
		ps.setTime(1, t);
		ps.setString(2, memName);
		ps.setString(3, famName);
		ps.setString(4, jobName);
		ps.setDate(5, date);
		int i = ps.executeUpdate();
		if(i > 0)
		{
			return true;
		}
		return false;
	}
	
		/**
	 * Adds the time that a specific job is finished by a specific member within a family to the database. Returns true if
	 * the time is successfully added, false otherwise.
	 * @param memName
	 * @param famName
	 * @param jobName
	 * @param d
	 * @param t
	 * @param dd
	 * @return
		 * @throws SQLException 
		 * @throws ParseException 
	 */
	public boolean jobFinished(String memName, String famName, String jobName, String d, Time t, String dd) throws SQLException, ParseException
	{
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		String s2 = "SELECT time_start FROM Job_Assignment WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=?";
		PreparedStatement ps2 = conn.prepareStatement(s2);
		ps2.setString(1, memName);
		ps2.setString(2, famName);
		ps2.setString(3, jobName);
		Date date = new Date(df.parse(d).getTime());
		ps2.setDate(4, date);
		ResultSet rs = ps2.executeQuery();
		if(rs.next())
		{
			if(rs.getTime(1) == null)
			{
				return false;
			}
		}
		String s = "UPDATE Job_Assignment SET time_finish=?, date_complete=?, completed=? WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setTime(1, t);
		Date date2 = new Date(df.parse(dd).getTime());
		ps.setDate(2, date2);
		ps.setBoolean(3, true);
		ps.setString(4, memName);
		ps.setString(5, famName);
		ps.setString(6, jobName);
//		Date date3 = new Date(df.parse(d).getTime());
		ps.setDate(7, date);
		int i = ps.executeUpdate();
		if(i > 0)
		{
			calculateTime(memName, famName, jobName);
			String s3 = "SELECT Job_Time FROM Job_Assignment WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=?";
			PreparedStatement ps3 = conn.prepareStatement(s3);
			ps3.setString(1, memName);
			ps3.setString(2, famName);
			ps3.setString(3, jobName);
			ps3.setDate(4, date);
			ResultSet rs2 = ps3.executeQuery();
			if(rs2.next())
			{
				Time done = rs2.getTime(1);
				if(date2.after(date) || (date.equals(date2) && done.before(t)))
				{
					String s4 = "UPDATE Job_Assignment SET late=? WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=?";
					PreparedStatement ps4 = conn.prepareStatement(s4);
					ps4.setBoolean(1, true);
					ps4.setString(2, memName);
					ps4.setString(3, famName);
					ps4.setString(4, jobName);
					ps4.setDate(5, date);
					i = ps4.executeUpdate();
					if(i < 1)
					{
						return false;
					}
					long timeTaken = howLate(d, dd, done, t);
					createLateNotif(memName, famName, jobName, d, timeTaken, done);
				}
				updateTotalAllowance(famName, memName, jobName, date);
			}
			return true;
		}
		return false;
	}
	
		/**
	 * Updates the Job_Assingment table by calculating the job taken for a certain job and adding it to the table. Is called
	 * within the timeFinish method
	 * @param memName
	 * @param famName
	 * @param jobName
	 * @throws SQLException
	 */
	private void calculateTime(String memName, String famName, String jobName) throws SQLException
	{
		String s = "SELECT time_start, time_finish FROM Job_Assignment WHERE Member=? AND Mem_Username=? AND Job_Name=?";
		ArrayList<Time> times = new ArrayList<Time>();
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, memName);
		ps.setString(2, famName);
		ps.setString(3, jobName);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			times.add(rs.getTime(1));
			times.add(rs.getTime(2));
		}
		Time t1 = times.get(0);
		Time t2 = times.get(1);
		long overall = (t2.getTime() - t1.getTime());
		long seconds = TimeUnit.MILLISECONDS.toSeconds(overall);
		//long minutes = seconds / 60;
		//seconds -= minutes * 60;
		//long hours = minutes / 60;
		//minutes -= hours * 60;
		//Time time = new Time((int)hours, (int)minutes, (int)seconds);
		s = "UPDATE Job_Assignment SET time_taken=? WHERE Member=? AND Mem_Username=? AND Job_Name=?";
		ps = conn.prepareStatement(s);
		ps.setLong(1, seconds); //ps.setTime(1, time);
		ps.setString(2, memName);
		ps.setString(3, famName);
		ps.setString(4, jobName);
		ps.executeUpdate();
	}
	
	/**
	 * Defines a chore for a particular family. Adds the job name, prerequisites, incentive, and description for a specific job. Returns true
	 * if the chore definition is successfully added to the database, false otherwise.	
	 * @param username
	 * @param jobName
	 * @param prereqs
	 * @param incentive
	 * @param desc
	 * @return
	 * @throws SQLException
	 */
	public boolean defineChore(String username, String jobName, ArrayList<Object> prereqs, Double incentive, String desc) throws SQLException
	{
		String s = "INSERT INTO Jobs_Defined(Username, job_name, job_incentive, job_desc)" 
				+ " values(?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, username);
		ps.setString(2, jobName);
		ps.setDouble(3, incentive);
		ps.setString(4, desc);
		int x = ps.executeUpdate();
		if(x < 1)
		{
			return false;
		}
		int count = 1;
		for(int i = 0; i < prereqs.size(); i++)
		{
			s = "UPDATE Jobs_Defined SET Prereq" + count + "=? WHERE Username=? AND Job_Name=?";
			ps = conn.prepareStatement(s);
			ps.setString(1, (String)prereqs.get(i));
			ps.setString(2, username);
			ps.setString(3, jobName);
			x = ps.executeUpdate();
			if(x < 1)
			{
				return false;
			}
			count++;
		}
		return true;
	}
	
	/**
	 * Takes in a family username and a job name, and returns the an ArrayList that contains the job incentive, job description,
	 * and prerequisites that were defined by the family.
	 * @param username
	 * @param jobName
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> getChoreDef(String username, String jobName) throws SQLException
	{
		ArrayList<Object> definition = new ArrayList<Object>();
		String s = "SELECT job_incentive, job_desc, prereq1, prereq2, prereq3, prereq4, prereq5 FROM Jobs_Defined WHERE Username=? AND job_name=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, username);
		ps.setString(2, jobName);
		ResultSet rs = ps.executeQuery();
		ArrayList<Object> prereqs = new ArrayList<Object>();
		if(rs.next())
		{
			definition.add(rs.getDouble(1));
			definition.add(rs.getString(2));
			for(int i = 3; i < 8; i++)
			{
				if(rs.getString(i) != null)
				{
					prereqs.add(rs.getString(i));
				}
			}
			definition.add(prereqs);
		}
		return definition;
	}
	
	/**
	 * Updates the total allowance that an individual is owed after they complete a chore
	 * @param username
	 * @param member
	 * @param jobName
	 * @param jobDate
	 * @return
	 * @throws SQLException
	 */
	private boolean updateTotalAllowance(String username, String member, String jobName, Date jobDate) throws SQLException
	{
//		String s = "SELECT allowance, allowance_owed FROM Job_Assignment INNER JOIN Individuals ON "
//				+ "Job_Assignment.Member = Individuals.Member AND Job_Assignment.Mem_Username = Individuals.Mem_Username"
//				+ " WHERE Job_Assignment.Member=? AND Job_Assignment.Mem_Username=? AND Job_Time=? AND date_done=?";
		String s = "SELECT allowance FROM Job_Assignment WHERE Member=? AND Mem_Username=? AND date_done=? AND completed=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, member);
		ps.setString(2, username);
		ps.setDate(3, jobDate);
		ps.setBoolean(4, true);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			Double allowance = rs.getDouble(1);
			Double total = 0.0;
			String s2 = "SELECT allowance_owed FROM Individuals WHERE Member=? AND Mem_Username=?";
			PreparedStatement ps2 = conn.prepareStatement(s2);
			ps2.setString(1, member);
			ps2.setString(2, username);
			ResultSet rs2 = ps2.executeQuery();
			if(rs2.next())
			{
				total = rs2.getDouble(1);
			}
			else
			{
				return false;
			}
			total += allowance;
			String s3 = "UPDATE Individuals SET allowance_owed=? WHERE Member=? AND Mem_Username=?";
			PreparedStatement ps3 = conn.prepareStatement(s3);
			ps3.setDouble(1, total);
			ps3.setString(2, member);
			ps3.setString(3, username);
			int x = ps3.executeUpdate();
			if(x > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calculates how many minutes a chore was completed late
	 * @param d
	 * @param dd
	 * @param t1
	 * @param t2
	 * @return
	 * @throws ParseException
	 */
	private long howLate(String d, String dd, Time t1, Time t2) throws ParseException
	{
		String initial = d + " " + t1.toString();
		String finished = dd + " " + t2.toString();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		java.util.Date due = null;
		java.util.Date completed = null;
		due = format.parse(initial);
		completed = format.parse(finished);
		long difference = completed.getTime() - due.getTime();
		long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
		return minutes;
	}
	
	/**
	 * Creates a late notification that is sent to Parent members if a chore is completed late.
	 * @param member
	 * @param family
	 * @param jobName
	 * @param date
	 * @param timeTaken
	 * @throws ParseException 
	 * @throws SQLException 
	 */
	private void createLateNotif(String member, String family, String jobName, String date, long timeTaken, Time t) throws SQLException, ParseException
	{
		int hours = 0;
		int minutes = 0;
		
		if (timeTaken >= 60) {
			hours = (int) (timeTaken / 60);
			minutes = (int) (timeTaken - (hours * 60));
		}

		ArrayList<Object> notif = new ArrayList<Object>();;
		notif.add(family);
		ArrayList<Object> mem = new ArrayList<Object>();
		mem.add(member);
		mem.add(jobName);
		mem.add(date);
		mem.add("Late");
		mem.add("This chore was completed " + hours + " hours and " + minutes + " minutes late");
		mem.add(t);
		notif.add(mem);
		notif.add(null);
		NotificationDatabase.addNotification(notif);

	}
	
	/**
	 * Checks if a chore has been started
	 * @param family
	 * @param member
	 * @param jobName
	 * @param d
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	public boolean isStarted(String member, String family, String jobName, String d) throws ParseException, SQLException
	{
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date(df.parse(d).getTime());
		String s = "SELECT time_start FROM Job_Assignment WHERE Member=? AND Mem_Username=? AND Job_Name=? AND date_done=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, member);
		ps.setString(2, family);
		ps.setString(3, jobName);
		ps.setDate(4, date);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			if(rs.getTime(1) != null)
			{
				return true;
			}
		}
		return false;
	}
}