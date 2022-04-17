package Client;

import java.text.ParseException;

/**
 * This is the launch class for the application, also used for testing screens
 * 
 * @author GK_B_4ChoreManager
 *
 */
public class Main 
{

	public static void main(String[] args) throws ParseException 
	{
		String family = "DemoFamily";
		String parent = "Bob";
		String child = "Charlie";
		String chore = "Walk Dog";
		String date = "2017-11-28";
		String time = "14:00:00";
		String user = "";
		Boolean isParent = false;
		
		LoginScreen log = new LoginScreen();
		//AssignChore as = new AssignChore(family, parent);
		//ChildScreen cs = new ChildScreen(family, child); //String familyName, String userName
		//ChoreDetails cd = new ChoreDetails(family, child, chore, date, time, parent, isParent);
		//DefineChore dc = new DefineChore(family, parent);
		//FamilyCreation asdf = new FamilyCreation(family); //String familyName
		//LoginScreen log = new LoginScreen();

		//RegistrationScreen reg = new RegistrationScreen();

		
		//FamilyCreation familyc = new FamilyCreation("Test");
		//MainScreen ms = new MainScreen("Test");
		//ParentScreen ps = new ParentScreen("Test", "John");
		//ChildScreen cs = new ChildScreen(family,child);//pull
		//OtherScreen os = new OtherScreen("Test", "Zack");		

		//Availability Brian = new Availability(family, child);
		//MainScreen ms = new MainScreen(family);	//String FamUser
		//ModifyChore mc = new ModifyChore(family, parent);
		//NotificationsScreen ns = new NotificationsScreen(family, parent, isParent);
		//OtherLocation o = new OtherLocation();		
		//OtherScreen os = new OtherScreen(family, child); //String familyName, String userName
		//ParentScreen ps = new ParentScreen(family, child);	//String familyName, String userName	
		//RegistrationScreen reg = new RegistrationScreen();

	}
}

