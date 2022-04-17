package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for displaying the user screen for Parent users.
 * Class contacts the database via client/server code to pull user data/chores and call to other screens.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class ParentScreen {
	static Image logo;
	
	private String familyUsername;
	private String username;
	
	private String selectedUsername;
	
	private Button notificationButton;
	private Integer numNotifications = 0;
	
	private Table scheduleTable;

	/**
	 * Parent Screen constructor, takes in the given family name and username for the Parent user.
	 * 
	 * @param familyName
	 * @param userName
	 * @throws ParseException
	 */
	protected ParentScreen(String familyName, String userName) throws ParseException {
		familyUsername = familyName;
		username = userName;
		
		Display display = Display.getDefault();
		
		logo = display.getSystemImage(SWT.ICON_QUESTION);
		Shell shell = createShell(display);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}

	/**
	 * Creates the main Parent user-type UI shell
	 * 
	 * @param display
	 * 
	 * @return The created shell
	 */
	private Shell createShell(Display display) throws ParseException {
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(1000, 750);
		shell.setText("Parent Screen");
		shell.setImage(logo);
		
		/*
		 * Screen Buttons (Preferences, Chore Trader, Notifications, Chore Request, Availability)
		 */
		
		Button defineChore = new Button(shell, SWT.PUSH);
		defineChore.setText("Define A New Chore");
		
		Button addUser = new Button(shell, SWT.PUSH);
		addUser.setText("Add New User");
		
		Button updateInfo = new Button(shell, SWT.PUSH);
		updateInfo.setText("Modify User");
		
		Button assignChore = new Button(shell, SWT.PUSH);
		assignChore.setText("Assign Chores");
		
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9914);
		send.add(familyUsername);
		send.add(username);
		ArrayList<Object> response = client.clientConnection(send);
		
		numNotifications = (Integer) response.get(0);
		
		notificationButton = new Button(shell, SWT.PUSH);
		notificationButton.setText("Notifications (" + numNotifications + ")");
		
		Button modifyChore = new Button(shell, SWT.PUSH);
		modifyChore.setText("Modify a Chore");
		
		Button research = new Button(shell, SWT.PUSH);
		research.setText("Research");
		
		Button returnButton = new Button(shell, SWT.PUSH);
		returnButton.setText("Return");
		
		Button allowance = new Button(shell, SWT.PUSH);
		allowance.setText("Pay Allowances");
		
		/*
		 * Calendar
		 */
		
		DateTime calendar = new DateTime (shell, SWT.CALENDAR);
		calendar.addSelectionListener (new SelectionAdapter () {
		    public void widgetSelected (SelectionEvent e) {
		      
		      calendar.getParent().setRedraw(false);
		    	
		      String month;
		      String day;
		      String year = "" + ((DateTime)e.widget).getYear();
		      
		      if (((DateTime)e.widget).getMonth() < 10) {
		    	  month = "0" + (((DateTime)e.widget).getMonth() + 1);
		      } else {
		    	  month = "" + (((DateTime)e.widget).getMonth() + 1);
		      }
		      
		      if (((DateTime)e.widget).getDay() < 10) {
		    	  day = "0" + ((DateTime)e.widget).getDay();
		      } else {
		    	  day = "" + ((DateTime)e.widget).getDay();
		      }
		      
		      //When a date is selected, call the server with the date to get the data and then call createSchedule with that data
		      String date = year + "/" + month + "/" + day;
		      String dateString = String.format("%d-%d-%d", Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
			    Date d = null;
				try {
					d = new SimpleDateFormat("yyyy-M-d").parse(dateString);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(d);
		      ArrayList<Object> send = new ArrayList<Object>();
				send.add(3);
				send.add(familyUsername);
				send.add(date);
				send.add(dayOfWeek);
				ArrayList<Object> response = client.clientConnection(send);
				createSchedule(response);
		    
				
				calendar.getParent().setRedraw(true);
		    }
		  });
		
		/*
		 * Hourly Schedule - very similar to MainScreen, parents will likely be able to set who they want to see
		 */
		
		Group dailySchedule = new Group(shell, SWT.NONE);
		dailySchedule.setText("Daily Schedule");	
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		notificationButton.setLayoutData(data);
		notificationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//Open notifications screen for the user
				if (numNotifications > 0) {
					NotificationsScreen notifications = new NotificationsScreen(familyUsername, username, true);
					updateNotificationCount();
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(notificationButton, 10);
		addUser.setLayoutData(data);
		addUser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FamilyCreation family = new FamilyCreation(familyUsername);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(addUser, 10);
		updateInfo.setLayoutData(data);
		updateInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				UpdateInfo update = new UpdateInfo(familyUsername);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(updateInfo, 10);
		defineChore.setLayoutData(data);
		defineChore.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
				DefineChore defineScreen = new DefineChore(familyUsername, username);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(defineChore, 10);
		assignChore.setLayoutData(data);
		assignChore.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
				try {
					AssignChore assignScreen = new AssignChore(familyUsername, username);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(assignChore, 10);
		modifyChore.setLayoutData(data);
		modifyChore.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
				try {
					ModifyChore modifyScreen = new ModifyChore(familyUsername, username);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(modifyChore, 10);
		research.setLayoutData(data);
		research.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Research r = new Research(familyUsername);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(research, 10);
		allowance.setLayoutData(data);
		allowance.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				AllowancePayScreen all = new AllowancePayScreen(familyUsername);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(notificationButton, 10);
		data.left = new FormAttachment(5, 0);
		calendar.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(calendar, 20);
		data.left = new FormAttachment(5, 0);
		returnButton.setLayoutData(data);
		returnButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
				try {
					MainScreen main = new MainScreen(familyUsername);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(notificationButton, 10);
		data.left = new FormAttachment(calendar, 10);
		dailySchedule.setLayoutData(data);

		/*
		 * Schedule table + group initialization
		 */
		
		scheduleTable = new Table(dailySchedule, SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		
		createScheduleGroup(dailySchedule);
		scheduleTable.addListener(SWT.Selection, new Listener()  {
			public void handleEvent(Event event) {
				//when a user selects an item, collect the selected chore's user's name and set "username" to it - for use later
				selectedUsername = ((TableItem)event.item).getText(1);
				if (!selectedUsername.equals("")) {
					//when a user selects an item, collect the selected chore's date + time
					String month;
				    String day;
				    String year = "" + calendar.getYear();
				      
				    if (calendar.getMonth() < 10) {
				    	month = "0" + (calendar.getMonth() + 1);
				    } else {
				    	month = "" + (calendar.getMonth() + 1);
				    }
				      
				    if (calendar.getDay() < 10) {
				    	day = "0" + calendar.getDay();
				    } else {
				    	day = "" + calendar.getDay();
				    }
				    
				    String date = year + "/" + month + "/" + day;
					String time = ((TableItem)event.item).getText(0);
					
					String choreName = ((TableItem)event.item).getText(2);
					
					StringBuilder allowanceStringFull = new StringBuilder(((TableItem)event.item).getText(3));
					allowanceStringFull.deleteCharAt(0);
					String allowanceString = allowanceStringFull.toString();
					
					Double allowance = Double.parseDouble(allowanceString);
					
					try {
						ChoreDetails details = new ChoreDetails(familyUsername, selectedUsername, choreName, date, time, username, true, allowance);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		initializeTable(calendar);

		
		return shell;
	}
	
	/**
	 * 
	 * Updates the number of notifications the current user has - pulled from the Database
	 * 
	 */
	protected void updateNotificationCount() {
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9914);
		send.add(familyUsername);
		send.add(username);
		ArrayList<Object> response = client.clientConnection(send);
		
		numNotifications = (Integer) response.get(0);
		notificationButton.setText("Notifications (" + numNotifications + ")");
	}
	
	/**
	 * 
	 * Takes in a given data array of all chores belonging to a family and creates the schedule for the all users.
	 * 
	 * @param data
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void createSchedule(ArrayList<Object> data) {
		scheduleTable.removeAll();
		
		ArrayList<Integer> times = new ArrayList<Integer>();
		
		if (data != null && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				Time temp = (Time)(((ArrayList<Object>)data.get(i)).get(3));
				
				int time = temp.getHours();
				
				times.add(time);
				
			}
		
			for (int i = 8; i < 21; i++) {
				boolean multi = false;
				for (int j = 0; j < times.size(); j++) {
					
					if (times.get(j) == i) {
						createItem(i, j, true, data);
						multi = true;
					} else if (j == times.size() - 1 && !multi) {
						createItem(i, j, false, data);
					}
					
				}
				
			}
		} else {
			createEmpty();
		}
		
		for (int i = 0; i < 4; i++) {
			scheduleTable.getColumn(i).pack();
		}
		
		scheduleTable.setBounds(25, 25, 350, 500);
	}
	
	/**
	 * 
	 * Creates an empty schedule table
	 * 
	 */
	private void createEmpty() {
		
		for (int i = 8; i < 21; i++) {
			TableItem item = new TableItem(scheduleTable, SWT.NULL);
			
			if (i < 12) {
				item.setText("" + i + ":00 AM");
			    item.setText(0, "" + i + ":00 AM");
			    item.setText(1, "");
			    item.setText(2, "");
			    item.setText(3, "");
			} else if (i == 12) {
				item.setText("" + i + ":00 PM");
				item.setText(0, "" + i + ":00 PM");
				item.setText(1, "");
				item.setText(2, "");
				item.setText(3, "");
			} else {
				item.setText("" + (i-12) + ":00 PM");
				item.setText(0, "" + (i-12) + ":00 PM");
				item.setText(1, "");
				item.setText(2, "");
				item.setText(3, "");
			}
		}
	}
	
	/**
	 * 
	 * Uses the given data array, position j and time i to create a Chore item within the Schedule table, or creates an empty item if isJob is false.
	 * 
	 * @param i, j, isJob, data
	 */
	@SuppressWarnings("unchecked")
	private void createItem(int i, int j, boolean isJob, ArrayList<Object> data) {
		
		TableItem item = new TableItem(scheduleTable, SWT.NULL);
		
		if (isJob) {
			
			String name = (String) ((ArrayList<Object>) data.get(j)).get(0);
			String job = (String) ((ArrayList<Object>) data.get(j)).get(1);
			Double allowance = (Double) ((ArrayList<Object>) data.get(j)).get(4);
			
			if (i < 12) {
				item.setText("" + i + ":00 AM");
			    item.setText(0, "" + i + ":00 AM");
			    item.setText(1, name);
			    item.setText(2, job);
			    item.setText(3, "$" + allowance);
			} else if (i == 12) {
				item.setText("" + i + ":00 PM");
				item.setText(0, "" + i + ":00 PM");
				item.setText(1, name);
				item.setText(2, job);
				item.setText(3, "$" + allowance);
			} else {
				item.setText("" + (i-12) + ":00 PM");
				item.setText(0, "" + (i-12) + ":00 PM");
				item.setText(1, name);
				item.setText(2, job);
				item.setText(3, "$" + allowance);
			}
		} else {
			if (i < 12) {
				item.setText("" + i + ":00 AM");
			    item.setText(0, "" + i + ":00 AM");
			    item.setText(1, "");
			    item.setText(2, "");
			    item.setText(3, "");
			} else if (i == 12) {
				item.setText("" + i + ":00 PM");
				item.setText(0, "" + i + ":00 PM");
				item.setText(1, "");
				item.setText(2, "");
				item.setText(3, "");
			} else {
				item.setText("" + (i-12) + ":00 PM");
				item.setText(0, "" + (i-12) + ":00 PM");
				item.setText(1, "");
				item.setText(2, "");
				item.setText(3, "");
			}
		}
	}
	
	/**
	 * 
	 * Initializes the schedule table with the current day's chores, using the given calendar widget.
	 * 
	 * @param calendar
	 */
	private void initializeTable(DateTime calendar) throws ParseException {
		String month;
	    String day;

	    if (calendar.getMonth() < 9) {
	    	month = "0" + (calendar.getMonth() + 1); //DateTime's months start at 0 for some reason
	      } else {
	    	month = "" + (calendar.getMonth() + 1);
	      }
	    

	      if (calendar.getDay() < 10) {
	    	  day = "0" + calendar.getDay();
	      } else {
	    	  day = "" + calendar.getDay();
	      }
	    
	    
	    String year = "" + calendar.getYear();
	    
	    String date = year + "/" + month + "/" + day;
	    String dateString = String.format("%d-%d-%d", Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
	    Date d = new SimpleDateFormat("yyyy-M-d").parse(dateString);
	    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(d);
	      ArrayList<Object> send = new ArrayList<Object>();
			send.add(3);
			send.add(familyUsername);
			send.add(date);
			send.add(dayOfWeek);
			ArrayList<Object> response = client.clientConnection(send);
			createSchedule(response);
	}
	
	/**
	 * 
	 * Creates the headers for the chore schedule table
	 * 
	 * @param dailySchedule
	 */
	private void createScheduleGroup(Group dailySchedule) {
		
		scheduleTable.setHeaderVisible(true);
		
		String[] headers = { "Time", "Family Member", "Chore", "Allowance"};
		
		for (int i = 0; i < headers.length; i++) {
			TableColumn column = new TableColumn(scheduleTable, SWT.NONE);
			column.setText(headers[i]);
			
		}

	}
		
}