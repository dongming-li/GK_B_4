package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for modifying user information such as pin, birthday, sex, user type, and active status.
 * Class contacts the database via client/server code to obtain and update family members data.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class UpdateInfo 
{
	static Image logo;
	
	// Reference for server to make correct database call
	final private int modifyID = 298;
	final private int userInfoID = 299;
	final private int getMemID = 300;
	
	private int pin;	// Users pin
	private String familyUsername;	// Your family name
	private ArrayList<Object> update = new ArrayList<Object>();	// Holder for information to update
	
	// Birthday
	private int month;
	private int day;
	private int year;
	
	// Combo boxes hold possible choices to select when modifying the specified data for a user.
	private Combo user;
	private Combo sexCombo;
	private Combo bDayMonth;
	private Combo bDayDay;
	private Combo bDayYear;
	private Combo typeCombo;
	private Combo activeCombo;

	// Screen controls
	private Button modifyButton;
	private Button cancelButton;
	
	/**
	 * Constructor for creating the update info window
	 * 
	 * @param familyName
	 */
	protected UpdateInfo(String familyName) 
	{
		familyUsername = familyName;
		update.add(familyName);
		
		// Add positions so set() function for list can be used
		for (int i = 1; i < 9; i++) 
		{
			  update.add(0);
		}
		
		Display display = Display.getDefault();
		
		logo = display.getSystemImage(SWT.ICON_QUESTION);
		Shell shell = createShell(display);
		shell.open();
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}
	
	/**
	 * Creates the shell within the display window. This shell displays choices for data that can be modified for each user.
	 * 
	 * @param display
	 * @return
	 */
	private Shell createShell(Display display) 
	{
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(300, 350);
		shell.setText("Modify User");
		shell.setImage(logo);
		
		Group user = defineUser(shell);	// Area containing user information
		
		modifyButton = new Button(shell, SWT.PUSH);
		modifyButton.setText("MODIFY");
		
		cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("CANCEL");
		
		setLayout(user);	//Screen layout
		modify(shell);	//Screen control either modify or cancel
		
		return shell;
	}
	
	/**
	 * Defines an area containing a user's information. The information is laid out for display and 
	 * listeners added to record changes to user data.
	 * 
	 * @param shell
	 * @return the group that was created
	 */
	private Group defineUser(Shell shell)
	{
		// Creates a group to contain all location options/buttons
		Group userGroup = new Group(shell, SWT.NONE);
		userGroup.setLayout(new FormLayout());
        userGroup.setText("User Information");
        
        // Labels and combo boxes for displaying user data
		Label nameLabel = new Label(userGroup, SWT.NONE);
		nameLabel.setText("Family Member Name");
		createUserCombo(userGroup, shell);
		
		Label pinLabel = new Label(userGroup, SWT.NONE);
		pinLabel.setText("Pin");
		Text pinText = new Text(userGroup, SWT.SINGLE | SWT.BORDER);
		
		Label bDayLabel = new Label(userGroup, SWT.NONE);
		bDayLabel.setText("Birthday");		
		createBDayCombos(userGroup);
		
		Label sexLabel = new Label(userGroup, SWT.NONE);
		sexLabel.setText("Sex");
		createSexCombo(userGroup);

		Label typeLabel = new Label(userGroup, SWT.NONE);
		typeLabel.setText("User Type");
		createTypeCombo(userGroup);

		Label activeLabel = new Label(userGroup, SWT.NONE);
		activeLabel.setText("Inactive");
		createActiveCombo(userGroup);
		
		// Layout within the group
		setGroupLayout(nameLabel, pinLabel, pinText, bDayLabel, sexLabel, typeLabel, activeLabel);
		
		// Listeners for each combo box to perform a action if selected
		user.setText("Select A User");
		user.addModifyListener( new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				// Record information displayed in combo box
				String test = ((Combo) e.widget).getText();
				update.set(1, test);
				 
				// Get info for selected user
				ArrayList<Object> userReq = new ArrayList<Object>();
				userReq.add(userInfoID);
				userReq.add(familyUsername);
				userReq.add(test);
				ArrayList<Object> userInfo = client.clientConnection(userReq);
				            
				// Display user info or warning message if information was not obtained
            	if(userInfo.size() == 0)
            	{
                	MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK);
                	dialog.setText("Warning");
            		dialog.setMessage("Couldn't get user info.\n\nTry again or cancel to close screen.");
            		dialog.open();
            	}
            	else
            	{       	
					pin = (int) userInfo.get(0);
					pinText.setText(Integer.toString(pin));
					Date birthday = (Date) userInfo.get(1);
					Calendar cal = Calendar.getInstance();
					cal.setTime(birthday);
					month = cal.get(Calendar.MONTH) + 1;
					
					// Add 0 for MM format for month
					if(month < 10)
					{
						bDayMonth.setText("0" + Integer.toString(month));
					}
					else
					{
						bDayMonth.setText(Integer.toString(month));
					}
					
					day = cal.get(Calendar.DAY_OF_MONTH);
					bDayDay.setText(Integer.toString(day));
					year = cal.get(Calendar.YEAR);
					bDayYear.setText(Integer.toString(year));
					sexCombo.setText((String) userInfo.get(2));
					typeCombo.setText((String) userInfo.get(3));
					activeCombo.setText(Boolean.toString((boolean) userInfo.get(4)));
            	}
			}
			
		});
		
		pinText.addModifyListener( new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				// Record any changes to pin
				String test = ((Text) e.widget).getText();
				update.set(2, test);
			}
			
		});
		
		bDayMonth.addModifyListener( new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				// Record any changes to birthday month
				String test = ((Combo) e.widget).getText();
				update.set(3, test);
			}
			
		});
		
		bDayDay.addModifyListener( new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				// Record changes to birthday day
				String test = ((Combo) e.widget).getText();
				update.set(4, test);
			}
			
		});
		
		bDayYear.addModifyListener( new ModifyListener()
		{
			public void modifyText(ModifyEvent e) 
			{
				// Record changes to birthday year
				String test = ((Combo) e.widget).getText();
				update.set(5, test);
			}
			
		});
		
		sexCombo.addModifyListener( new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				// Record changes to user's sexuality
				String test = ((Combo) e.widget).getText();
				update.set(6, test);
			}
			
		});

		typeCombo.addModifyListener( new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				// Record changes to user type
				String test = ((Combo) e.widget).getText();
				update.set(7, test);
			}
			
		});

		activeCombo.addModifyListener( new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				// Record changes to user's active status
				String test = ((Combo) e.widget).getText();
				update.set(8, test);
			}
			
		});
		
		return userGroup;
	}
	
	/**
	 * Creates a combo box containing a list of members for the family. 
	 * Calls database via client/server code to obtain members.
	 * 
	 * @param userGroup
	 * @param shell
	 */
	private void createUserCombo(Group userGroup, Shell shell)
	{
		user = new Combo(userGroup, SWT.DROP_DOWN | SWT.BORDER);
		
		// Database call
		ArrayList<Object> userReq = new ArrayList<Object>();
		userReq.add(getMemID);
		userReq.add(familyUsername);
		userReq.add(true);
		ArrayList<Object> response = client.clientConnection(userReq);

		// Display a warning if member list cannot be obtained else add members to combo box
    	if(response.size() == 0)
    	{
        	MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK);
        	dialog.setText("Warning");
    		dialog.setMessage("Couldn't get list of users.\n\nClick cancel to close screen and try again.");
    		dialog.open();
    	}
    	else
    	{ 
			for (int i = 0; i < response.size(); i++) 
			{
				user.add((String)response.get(i));
			}
    	}
	}
	
	/**
	 * Creates combo boxes containing the year, month, and day to choose a member's birthday.
	 * 
	 * @param userGroup
	 */
	private void createBDayCombos(Group userGroup)
	{
		// Combo box creation
		bDayMonth = new Combo(userGroup, SWT.DROP_DOWN | SWT.BORDER);
		bDayDay = new Combo(userGroup, SWT.DROP_DOWN | SWT.BORDER);
		bDayYear = new Combo(userGroup, SWT.DROP_DOWN | SWT.BORDER);
		
		// Add months to month combo box
		bDayMonth.add("01");
		bDayMonth.add("02");
		bDayMonth.add("03");
		bDayMonth.add("04");
		bDayMonth.add("05");
		bDayMonth.add("06");
		bDayMonth.add("07");
		bDayMonth.add("08");
		bDayMonth.add("09");
		bDayMonth.add("10");
		bDayMonth.add("11");
		bDayMonth.add("12");		
		
		// Add days to day combo box adding 0 to meet dd format if needed
		for (int i = 1; i < 32; i++) 
		{
			if (i < 10) {
				bDayDay.add("0" + i);
			} else {
				bDayDay.add("" + i);
			}
		}
		
		// Add list of years to year combo box
		for (int i = 1950; i < 2000; i++) 
		{
			bDayYear.add("" + i);
		}
		
		for (int i = 2000; i < 2018; i++) 
		{
			bDayYear.add("" + i);
		}
	}
	
	/**
	 * Creates a combo box containing a list of choices for member's sexuality. 
	 * 
	 * @param userGroup
	 */
	private void createSexCombo(Group userGroup)
	{
		sexCombo = new Combo(userGroup, SWT.DROP_DOWN | SWT.BORDER);
		sexCombo.add("Male");
		sexCombo.add("Female");
	}
	
	/**
	 * Creates a combo box containing a list of user types for a member. 
	 * 
	 * @param userGroup
	 */
	private void createTypeCombo(Group userGroup)
	{
		typeCombo = new Combo(userGroup, SWT.DROP_DOWN | SWT.BORDER);
		typeCombo.add("Parent");
		typeCombo.add("Child");
		typeCombo.add("Other");
	}
	
	/**
	 * Create a combo box containing a choice for setting a member's active status. 
	 * 
	 * @param userGroup
	 */
	private void createActiveCombo(Group userGroup)
	{
		activeCombo = new Combo(userGroup, SWT.DROP_DOWN | SWT.BORDER);
		activeCombo.add("True");
		activeCombo.add("False");
	}
	
	/**
	 * Positions items within the window being displayed.
	 * 
	 * @param userGroup
	 */
	private void setLayout(Group userGroup)
	{
		FormData data = new FormData(220, 250);
		data.top = new FormAttachment(1, 0);
		data.left = new FormAttachment(10, 0);
		userGroup.setLayoutData(data);
		
		data = new FormData(SWT.DEFAULT, SWT.DEFAULT);
		data.top = new FormAttachment(userGroup, 5);
		data.left = new FormAttachment(10, 0);
		modifyButton.setLayoutData(data);
		
		data = new FormData(SWT.DEFAULT, SWT.DEFAULT);
		data.top = new FormAttachment(userGroup, 5);
		data.left = new FormAttachment(modifyButton, 5);
		cancelButton.setLayoutData(data);
	}
	
	/**
	 * Layout of the users information within the area defined for the group.
	 * 
	 * @param nameLabel
	 * @param pinLabel
	 * @param pinText
	 * @param bDayLabel
	 * @param sexLabel
	 * @param typeLabel
	 * @param activeLabel
	 */
	private void setGroupLayout(Label nameLabel, Label pinLabel, Text pinText, Label bDayLabel, Label sexLabel, Label typeLabel, Label activeLabel)
	{
		FormData data = new FormData();
		data.top = new FormAttachment(10, 0);
		data.left = new FormAttachment(1, 0);
		nameLabel.setLayoutData(data);
		
		data = new FormData(180, SWT.DEFAULT);
		data.top = new FormAttachment(nameLabel, 5);
		data.left = new FormAttachment(1, 0);
		user.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(user, 5);
		data.left = new FormAttachment(1, 0);
		bDayLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(bDayLabel, 5);
		data.left = new FormAttachment(1, 0);
		bDayMonth.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(bDayLabel, 5);
		data.left = new FormAttachment(bDayMonth, 5);
		bDayDay.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(bDayLabel, 5);
		data.left = new FormAttachment(bDayDay, 5);
		bDayYear.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(bDayYear, 5);
		data.left = new FormAttachment(1, 0);
		activeLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(activeLabel, 5);
		data.left = new FormAttachment(1, 0);
		activeCombo.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(activeCombo, 5);
		data.bottom = new FormAttachment(activeCombo, -5);
		pinLabel.setLayoutData(data);
		
		data = new FormData(80, 17);
		data.top = new FormAttachment(pinLabel, 5);
		data.left = new FormAttachment(activeCombo, 5);
		pinText.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(activeCombo, 5);
		data.left = new FormAttachment(1, 0);
		sexLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(sexLabel, 5);
		data.left = new FormAttachment(1, 0);
		sexCombo.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(sexCombo, 5);
		data.bottom = new FormAttachment(sexCombo, -5);
		typeLabel.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(sexCombo, 5);
		data.top = new FormAttachment(typeLabel, 5);
		typeCombo.setLayoutData(data);
	}
	
	/**
	 * Creates listeners for the control buttons, Modify and Cancel. 
	 * Modify will call the database to update user information via client/server code.
	 * Cancel will close the screen to return to the calling screen.
	 * 
	 * @param shell
	 */
	private void modify(Shell shell)
	{
		// Contact database to update information 
        modifyButton.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	ArrayList<Object> send = new ArrayList<Object>();
            	ArrayList<Object> response = new ArrayList<Object>();
            	String date = update.get(5) + "/" + update.get(3) + "/" + update.get(4);
            	send.add(modifyID);
            	send.add(update.get(2));
            	send.add(date);         	
            	send.add(update.get(6));
            	send.add(update.get(7));
            	send.add(update.get(8));
            	send.add(update.get(1));
            	send.add(update.get(0));
            	response = client.clientConnection(send);
            	
            	//Message box displayed if modification was successful or if error occured
            	MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK);
            	
            	if(response.size() > 0 && (Boolean)response.get(0))
            	{
            		dialog.setText("Success");
            		dialog.setMessage("User Data modified succesfully.\n\nModify another user or cancel to close screen.");
            		dialog.open();
            	}
            	else
            	{
            		dialog.setText("Warning");
            		dialog.setMessage("User data couldn't be modified\n\nTry again or cancel to close screen.");
            		dialog.open();
            	}
            }            
        });
		
		// Close screen 
        cancelButton.addSelectionListener(new SelectionAdapter() 
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	shell.dispose();
            }             
        });
	}
}