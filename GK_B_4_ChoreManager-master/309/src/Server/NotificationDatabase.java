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
/**
 * This class contains all the database calls that pertain to notification handling, such as adding and deleting notifications,
 * as well as increasing and decreasing the number of notifications that a person has connected to them.
 * @author GK_B_4
 *
 */
public class NotificationDatabase {

	static Connection conn;
	public NotificationDatabase(Connection connection) throws SQLException
	{
		conn = connection;
	}
	
	public void closeDB() throws SQLException
	{
		conn.close();
	}
	
	/**
	 * Adds a notification to a table. There can either be one or two members involved with a notification. Member 1 is
	 * the only member in the case of a problem or chore request, while member 2 is left null. If a trade is requested, member 1
	 * is the person who currently has the chore assigned to them(trader), and member 2 is the person who wants to receive the job(tradee).
	 * Returns false if a notification is successfully added to the database, false otherwise.
	 * @param info
	 * @return
	 * @throws ParseException
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public static boolean addNotification(ArrayList<Object> info) throws SQLException, ParseException
	{
		if(info.get(2) != null)
		{
			if(checkIfExistsDouble(info) == true)
			{
				return false;
			}
			ArrayList<Object> mem1 = (ArrayList<Object>) info.get(1);
			ArrayList<Object> mem2 = (ArrayList<Object>) info.get(2);
			String s = "INSERT INTO Notifications (Mem1, Mem2, Username, Job_Name, Job_Name2, Job_Date, Job_Date2, Notif_Type, Message, Job_Time, Job_Time2)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(s);
			ps.setString(1, (String)mem1.get(0));
			ps.setString(2, (String)mem2.get(0));
			ps.setString(3, (String)info.get(0));
			ps.setString(4, (String)mem1.get(1));
			ps.setString(5, (String)mem2.get(1));
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			Date d = new Date(df.parse((String)mem1.get(2)).getTime());
			ps.setDate(6, d);
			Date d2 = new Date(df.parse((String)mem2.get(2)).getTime());
			ps.setDate(7, d2);
			ps.setString(8, (String)mem2.get(3));
			ps.setString(9, (String)mem2.get(4));
			if((String)mem2.get(3) == "Trade")
			{
				ps.setTime(10, (Time)mem1.get(5));
			}
			else
			{
				ps.setTime(10, (Time)mem1.get(3));
			}
			ps.setTime(11, (Time)mem2.get(5));
			int i = ps.executeUpdate();
			if(i < 1)
			{
				return false;
			}
			if(!((String)mem2.get(3)).equals("Trade"))
			{
				addNotificationNum((String)info.get(0), (String)mem1.get(0));
			}
			else
			{
				parentNotifHandler((String)info.get(0), false);
			}
		}
		else
		{
			if(checkIfExistsSingle(info) == true)
			{
				return false;
			}
			ArrayList<Object> mem1 = (ArrayList<Object>)info.get(1);
			String s = "INSERT INTO Notifications (Mem1, Username, Job_Name, Job_Date, Notif_Type, Message, Job_Time)"
					+ " values(?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(s);
			ps.setString(1, (String)mem1.get(0));
			ps.setString(2, (String)info.get(0));
			ps.setString(3, (String)mem1.get(1));
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			Date d = new Date(df.parse((String)mem1.get(2)).getTime());
			ps.setDate(4, d);
			String type = (String)mem1.get(3);
			ps.setString(5, type);
			ps.setString(6, (String)mem1.get(4));
			ps.setTime(7, (Time)mem1.get(5));
			int i = ps.executeUpdate();
			if(i < 1)
			{
				return false;
			}
			if(!type.equals("Problem") && !type.equals("Request") && !type.equals("Late"))
			{
				addNotificationNum((String)info.get(0), (String)mem1.get(0));
			}
			else
			{
				parentNotifHandler((String)info.get(0), false);
			}
		}
		return true;
	}
	
	/**
	 * Returns a list of notifications that are of type "Trade", "Request", or "Problem." Only used for parents
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> retrieveNotifications(String username) throws SQLException
	{
		ArrayList<Object> notifications = new ArrayList<Object>();
		String s = "SELECT Notif_Type, Mem1, Mem2, Job_Name, Job_Date, Notif_Type, Message, Job_Time FROM Notifications WHERE Username=? AND (Notif_Type=? OR Notif_Type=? OR Notif_Type=?)";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, username);
		ps.setString(2, "Request");
		ps.setString(3, "Problem");
		ps.setString(4, "Late");
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			ArrayList<Object> temp = new ArrayList<Object>();
			temp.add(rs.getString(2));
			temp.add(rs.getString(3));
			temp.add(rs.getString(4));
			temp.add(rs.getDate(5));
			temp.add(rs.getString(6));
			temp.add(rs.getString(7));
			temp.add(rs.getTime(8));
			notifications.add(temp);
		}
		s = "SELECT Mem2, Job_Name, Job_Date, Notif_Type, Message, Job_Time, Job_Name2, Job_Date2, Job_Time2, Mem1 FROM Notifications WHERE Username=? AND Notif_Type=?";
		ps = conn.prepareStatement(s);
		ps.setString(1, username);
		ps.setString(2, "Trade");
		rs = ps.executeQuery();
		while(rs.next())
		{
			ArrayList<Object> temp = new ArrayList<Object>();
			temp.add(rs.getString(1)); //Mem2(Who is asking to trade)
			temp.add(rs.getString(2)); //Job_Name(Tradee's chore)
			temp.add(rs.getDate(3)); //Job_Date(Tradee's chore date)
			temp.add(rs.getString(4)); //Notif_Type
			temp.add(rs.getString(5)); //Message
			temp.add(rs.getTime(6)); //Job_Time(Tradee's chore time)
			temp.add(rs.getString(7)); //Job_Name2(Trader's chore name)
			temp.add(rs.getDate(8)); //Job_Date2(Trader's chore date)
			temp.add(rs.getTime(9)); //Job_Time2(Trader's chore time)
			temp.add(rs.getString(10));
			notifications.add(temp);
		}
		return notifications;
	}
	
	/**
	 * Retrieves the notifications for a specific user
	 * @param username
	 * @param member
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> retrieveUserNotifications(String username, String member) throws SQLException
	{
		ArrayList<Object> notifications = new ArrayList<Object>();
		String s = "SELECT Mem2, Job_Name, Job_Date, Notif_Type, Message, Job_Time, Job_Name2, Job_Date2, Job_Time2 FROM Notifications WHERE Username=? AND Mem1=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, username);
		ps.setString(2, member);
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			if(rs.getString(4).equals("MemTrade"))
			{
				ArrayList<Object> temp = new ArrayList<Object>();
				temp.add(rs.getString(1)); //Mem2(Who is asking to trade)
				temp.add(rs.getString(2)); //Job_Name(Tradee's chore)
				temp.add(rs.getDate(3)); //Job_Date(Tradee's chore date)
				temp.add(rs.getString(4)); //Notif_Type
				temp.add(rs.getString(5)); //Message
				temp.add(rs.getTime(6)); //Job_Time(Tradee's chore time)
				temp.add(rs.getString(7)); //Job_Name2(Trader's chore name)
				temp.add(rs.getDate(8)); //Job_Date2(Trader's chore date)
				temp.add(rs.getTime(9)); //Job_Time2(Trader's chore time)
				notifications.add(temp);
			}
			else if(!rs.getString(3).equals("Trade") && !rs.getString(3).equals("Problem") && !rs.getString(3).equals("Request"))
			{
				ArrayList<Object> temp = new ArrayList<Object>();
				temp.add(rs.getString(2)); //Chore name
				temp.add(rs.getDate(3)); //Chore date
				temp.add(rs.getString(4)); //Notif_Type
				temp.add(rs.getString(5)); //Message
				temp.add(rs.getTime(6)); //Job_Time
				notifications.add(temp);
			}
		}
		return notifications;
	}
	
	/**
	 * Removes a notification for the table. In the case of a trade, if the approve = 1, then the notification is deleted, the tradeChore
	 * method is called to switch chores, and a new notification is added saying that the trade was approved. If approve = 0, then the
	 * notification is deleted and a new notification is added saying that the request was denied. If the case of a chore request, if
	 * approve = 1 then the notification is deleted and a new notification is added saying the request was approved. If approve = 0, then
	 * the notification is deleted and a new notification is added saying that the request was denied. In any other case, the notification
	 * is deleted and true is returned. False is returned in the case of any problems
	 * @param info
	 * @return
	 * @throws ParseException
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteNotification(ArrayList<Object> info) throws SQLException, ParseException
	{
		ArrayList<Object> mem1 = (ArrayList<Object>) info.get(1);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date(df.parse((String)mem1.get(2)).getTime());
		if(((String)mem1.get(3)).equals("Trade"))
		{
			ArrayList<Object> mem2 = (ArrayList<Object>) info.get(2);
			Date d2 = new Date(df.parse((String)mem2.get(2)).getTime());
			String s = "DELETE FROM Notifications WHERE Username=? AND Mem1=? AND Mem2=? AND Job_Name=? AND Job_Name2=? AND Job_Date=? AND Job_Date2=? AND Notif_Type=? AND Job_Time=? "+
					"AND Job_Time2=?";
			PreparedStatement ps = conn.prepareStatement(s);
			ps.setString(1, (String)info.get(0));
			ps.setString(2, (String)mem1.get(0));
			ps.setString(3, (String)mem2.get(0));
			ps.setString(4, (String)mem1.get(1));
			ps.setString(5, (String)mem2.get(1));
			ps.setDate(6, d);
			ps.setDate(7, d2);
			ps.setString(8, (String)mem2.get(3));
			ps.setTime(9, (Time)mem1.get(4));
			ps.setTime(10, (Time)mem2.get(4));
			int i = ps.executeUpdate();
			if(i > 0)
			{
				ArrayList<Object> trade = new ArrayList<Object>();
				ArrayList<Object> information = new ArrayList<Object>();
				information.add(info.get(0));
				ArrayList<Object> first = new ArrayList<Object>();
				first.add((String)mem1.get(0));
				first.add((String)mem1.get(1));
				first.add((String)mem1.get(2));
				if((Integer)info.get(3) == 1)
				{
					first.add("Approved");
					first.add("Chore Trade Approved");
				}
				else
				{
					first.add("Denied");
					first.add("Chore Trade Denied");
				}
				first.add((Time)mem1.get(4));
				information.add(first);
				information.add(null);
				addNotification(information);
				first.add(0, info.get(0));
				first.remove(5);
				first.remove(4);
				trade.add(first);
				information.clear();
				information.add((String)info.get(0));
				ArrayList<Object> second = new ArrayList<Object>();
				second.add((String)mem2.get(0));
				second.add((String)mem2.get(1));
				second.add((String)mem2.get(2));
				if((Integer)info.get(3) == 1)
				{
					second.add("Approved");
					second.add("Chore Trade Approved");
				}
				else
				{
					second.add("Denied");
					second.add("Chore Trade Denied");
				}
				second.add((Time)mem2.get(4));
				information.add(second);
				information.add(null);
				addNotification(information);
				second.add(0, info.get(0));
				second.remove(5);
				second.remove(4);
				trade.add(second);
				if((Integer)info.get(3) == 1)
				{
					ChoreManagementDatabase.tradeChore(trade);
				}
				parentNotifHandler((String)info.get(0), true);
				return true;
			}
		}
		else if(((String)mem1.get(3)).equals("MemTrade"))
		{
			ArrayList<Object> mem2 = (ArrayList<Object>) info.get(2);
			Date d2 = new Date(df.parse((String)mem2.get(2)).getTime());
			String s = "DELETE FROM Notifications WHERE Username=? AND Mem1=? AND Mem2=? AND Job_Name=? AND Job_Name2=? AND Job_Date=? AND Job_Date2=? AND Notif_Type=? AND Job_Time=? AND Job_Time2=?";
			PreparedStatement ps = conn.prepareStatement(s);
			ps.setString(1, (String)info.get(0));
			ps.setString(2, (String)mem1.get(0));
			ps.setString(3, (String)mem2.get(0));
			ps.setString(4, (String)mem1.get(1));
			ps.setString(5, (String)mem2.get(1));
			ps.setDate(6, d);
			ps.setDate(7, d2);
			ps.setString(8, (String)mem2.get(3));
			ps.setTime(9, (Time)mem1.get(4));
			ps.setTime(10, (Time)mem2.get(4));
			int i = ps.executeUpdate();
			if(i > 0)
			{
				subtractNotificationNum((String)info.get(0), (String)mem1.get(0));
				if((Integer)info.get(3) == 1)
				{
					ArrayList<Object> tradeInfo = new ArrayList<Object>();
					ArrayList<Object> first = new ArrayList<Object>();
					tradeInfo.add((String)info.get(0));
					first.add((String)mem1.get(0));
					first.add((String)mem1.get(1));
					first.add((String)mem1.get(2));
					first.add("Trade");
					first.add("We would like to trade");
					first.add((Time)mem1.get(4));
					tradeInfo.add(first);
					ArrayList<Object> sec = new ArrayList<Object>();
					sec.add((String)mem2.get(0));
					sec.add((String)mem2.get(1));
					sec.add((String)mem2.get(2));
					sec.add("Trade");
					sec.add("We would like to trade");
					sec.add((Time)mem2.get(4));
					tradeInfo.add(sec);
					addNotification(tradeInfo);
				}
				else
				{
					ArrayList<Object> tradeInfo = new ArrayList<Object>();
					ArrayList<Object> sec = new ArrayList<Object>();
					tradeInfo.add((String)info.get(0));
					sec.add((String)mem2.get(0));
					sec.add((String)mem2.get(1));
					sec.add((String)mem2.get(2));
					sec.add("Denied");
					sec.add("Other user denied request");
					sec.add((Time)mem2.get(4));
					tradeInfo.add(sec);
					tradeInfo.add(null);
					addNotification(tradeInfo);
				}
				return true;
			}
		}
		else
		{
			String s = "DELETE FROM Notifications WHERE Mem1=? AND Username=? AND Job_Name=? AND Job_Date=? AND Notif_Type=? AND Job_Time=?";
			PreparedStatement ps = conn.prepareStatement(s);
			Date d3 = new Date(df.parse((String)mem1.get(2)).getTime());
			ps.setString(1, (String)mem1.get(0));
			ps.setString(2, (String)info.get(0));
			ps.setString(3, (String)mem1.get(1));
			ps.setDate(4, d3);
			ps.setString(5, (String)mem1.get(3));
			ps.setTime(6, (Time)mem1.get(4));
			int i = ps.executeUpdate();
			if(i > 0)
				{
					if(((String)mem1.get(3)).equals("Approved") || ((String)mem1.get(3)).equals("Denied"))
					{
						subtractNotificationNum((String)info.get(0), (String)mem1.get(0));
						return true;
					}
					else if(((String)mem1.get(3)).equals("Request"))
					{
						if((Integer)info.get(3) == 1)
						{
							ArrayList<Object> information = new ArrayList<Object>();
							information.add(info.get(0));
							ArrayList<Object> first = new ArrayList<Object>();
							first.add((String)mem1.get(0));
							first.add((String)mem1.get(1));
							first.add((String)mem1.get(2));
							first.add("Approved");
							first.add("Chore Request Approved");
							first.add((Time)mem1.get(4));
							information.add(first);
							information.add(null);
							addNotification(information);
//							addNotificationNum((String)info.get(0), (String)mem1.get(0));
						}
						else
						{
							ArrayList<Object> information = new ArrayList<Object>();
							information.add(info.get(0));
							ArrayList<Object> first = new ArrayList<Object>();
							first.add((String)mem1.get(0));
							first.add((String)mem1.get(1));
							first.add((String)mem1.get(2));
							first.add("Denied");
							first.add("Chore Request Denied");
							first.add((Time)mem1.get(4));
							information.add(first);
							information.add(null);
							addNotification(information);
//							addNotificationNum((String)info.get(0), (String)mem1.get(0));
						}
					}
					parentNotifHandler((String)info.get(0), true);
					return true;
				}
			}
			return false;
		}
	
	/**
	 * Retrieves the number of notifications that a specific user has
	 * @param username
	 * @param member
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> getNotificationNum(String username, String member) throws SQLException
	{
		ArrayList<Object> notif = new ArrayList<Object>();
		String s = "SELECT notif_num FROM Individuals WHERE Member=? AND Mem_Username=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, member);
		ps.setString(2, username);
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			notif.add(rs.getInt(1));
		}
		return notif;
	}
	
	/**
	 * Takes in a member of a specific family and adds 1 to their total number of notifications
	 * @param username
	 * @param member
	 * @throws SQLException
	 */
	private static void addNotificationNum(String username, String member) throws SQLException
	{
		String s = "SELECT notif_num FROM Individuals WHERE Member=? AND Mem_Username=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, member);
		ps.setString(2, username);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int x = rs.getInt(1) + 1;
		s = "UPDATE Individuals SET notif_num=? WHERE Member=? AND Mem_Username=?";
		ps = conn.prepareStatement(s);
		ps.setInt(1, x);
		ps.setString(2, member);
		ps.setString(3, username);
		ps.executeUpdate();
	}
	
	/**
	 * Takes in a member of a specific family and subtracts 1 from their total number of notifications
	 * @param username
	 * @param member
	 * @throws SQLException
	 */
	private static void subtractNotificationNum(String username, String member) throws SQLException
	{
		String s = "SELECT notif_num FROM Individuals WHERE Member=? AND Mem_Username=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, member);
		ps.setString(2, username);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int x = rs.getInt(1) - 1;
		s = "UPDATE Individuals SET notif_num=? WHERE Member=? AND Mem_Username=?";
		ps = conn.prepareStatement(s);
		ps.setInt(1, x);
		ps.setString(2, member);
		ps.setString(3, username);
		ps.executeUpdate();
	}
	
	/**
	 * Takes in a username and boolean. If delete is false, then each parent within a specific family has their notification
	 * number increase by one. If true, then each parent within a speicific family has their notification number decrease
	 * by one.
	 * @param username
	 * @param delete
	 * @throws SQLException
	 */
	private static void parentNotifHandler(String username, boolean delete) throws SQLException
	{
		String s = "SELECT Member FROM Individuals WHERE Mem_Username=? AND User_Type=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, username);
		ps.setString(2, "Parent");
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			if(!delete)
			{
				addNotificationNum(username, rs.getString(1));
			}
			else
			{
				subtractNotificationNum(username, rs.getString(1));
			}
		}
	}
	
	/**
	 * Checks if there is a notification that already exists in the database that would match
	 * a potential new notification. Done before a new notification is added. Returns true
	 * if a notification already exists, false otherwise.
	 * @param info
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	private static boolean checkIfExistsSingle(ArrayList<Object> info) throws SQLException, ParseException
	{
		ArrayList<Object> mem1 = (ArrayList<Object>)info.get(1);
		String s = "SELECT Mem1 FROM Notifications WHERE Mem1=? AND Username=? AND Job_Name=? AND Job_Date=? AND Notif_Type=? AND Job_Time=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, (String)mem1.get(0));
		ps.setString(2, (String)info.get(0));
		ps.setString(3, (String)mem1.get(1));
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date(df.parse((String)mem1.get(2)).getTime());
		ps.setDate(4, d);
		String type = (String)mem1.get(3);
		ps.setString(5, type);
		ps.setTime(6, (Time)mem1.get(5));
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if there is a notification that already exists in the database that would match
	 * a potential new notification. Done before a new notification is added that is only
	 * of type MemTrade. Returns true if a notification already exists, false otherwise.
	 * @param info
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	private static boolean checkIfExistsDouble(ArrayList<Object> info) throws SQLException, ParseException
	{
		ArrayList<Object> mem1 = (ArrayList<Object>) info.get(1);
		ArrayList<Object> mem2 = (ArrayList<Object>) info.get(2);
		String s = "SELECT Mem1 FROM Notifications WHERE Mem1=? AND Mem2=? AND Username=? AND Job_Name=? AND Job_Name2=? AND Job_Date=? "
				+ "AND Job_Date2=? AND Notif_Type=? AND Message=? AND Job_Time=? AND Job_Time2=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, (String)mem1.get(0));
		ps.setString(2, (String)mem2.get(0));
		ps.setString(3, (String)info.get(0));
		ps.setString(4, (String)mem1.get(1));
		ps.setString(5, (String)mem2.get(1));
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date d = new Date(df.parse((String)mem1.get(2)).getTime());
		ps.setDate(6, d);
		Date d2 = new Date(df.parse((String)mem2.get(2)).getTime());
		ps.setDate(7, d2);
		ps.setString(8, (String)mem2.get(3));
		ps.setString(9, (String)mem2.get(4));
		if((String)mem2.get(3) == "Trade")
		{
			ps.setTime(10, (Time)mem1.get(5));
		}
		else
		{
			ps.setTime(10, (Time)mem1.get(3));
		}
		ps.setTime(11, (Time)mem2.get(5));
		ResultSet rs = ps.executeQuery();
		if(rs.next())
		{
			return true;
		}
		return false;
	}
}