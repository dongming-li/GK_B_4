package Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/**
 * This class contains database calls used for retrieving world locations. These calls include retrieving countries, states, and cities for
 * registration purposes.
 * @author GK_B_4
 *
 */
public class LocationDatabase {
	
	static Connection conn;
	public LocationDatabase(Connection connection) throws SQLException
	{
		conn = connection;
	}
	
	public void closeDB() throws SQLException
	{
		conn.close();
	}

	/**
	 * Returns a list of all the countries in the world.
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> getCountries() throws SQLException
	{
		ArrayList<Object> countries = new ArrayList<Object>();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("select * from Countries");
		while(rs.next())
		{
			countries.add(rs.getString(2));
		}
		return countries;
	}
	
		/**
	 * Returns a list of all the states that are contained within the passed in country name.
	 * @param country
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> getStates(String country) throws SQLException
	{
		ArrayList<Object> states = new ArrayList<Object>();
		String s = "SELECT state_name FROM States INNER JOIN Countries ON States.country_id = Countries.country_id WHERE Countries.country_name=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, country);
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			states.add(rs.getString(1));
		}
		return states;
	}

		/**
	 * Returns a list of all the cities that are contained within the passed in state name.
	 * @param state
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Object> getCities(String state) throws SQLException
	{
		ArrayList<Object> cities = new ArrayList<Object>();
		String s = "SELECT city_name FROM Cities INNER JOIN States ON Cities.state_id = States.state_id WHERE States.state_name=?";
		PreparedStatement ps = conn.prepareStatement(s);
		ps.setString(1, state);
		ResultSet rs = ps.executeQuery();
		while(rs.next())
		{
			cities.add(rs.getString(1));
		}
		return cities;
	}
}
