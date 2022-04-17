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
 * Class for Displaying the user screen for a Child User
 * Class contacts the database via client/server code to pull user data/chores and call to other screens.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class ChildScreen {
	static Image logo;
	
	private String familyUsername;
	private String username;
	
	private String selectedUsername;
	private Double allowanceTotal;
	
	private Button notifications;
	private Integer numNotifications = 0;
	
	private Table scheduleTable;
	
	/**
	 * Child Screen constructor
	 * 
	 * @param familyName
	 * @param userName
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected ChildScreen(String familyName, String userName) throws ParseException {
		familyUsername = familyName;
		username = userName;
		
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9919);
		send.add(familyUsername);
		ArrayList<Object> response = client.clientConnection(send);
		
		for (int i = 0; i < response.size(); i++) {
			if (((String)(((ArrayList<Object>)response.get(i)).get(0))).equals(username)) {
				allowanceTotal = (Double)((ArrayList<Object>)response.get(i)).get(1);
			}
		}
		
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
	 * Creates the main child user-type UI shell
	 * 
	 * @param display
	 * 
	 * @return The created shell
	 */
	private Shell createShell(Display display) throws ParseException {
		
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(750, 750);
		shell.setText("Child Screen");
		shell.setImage(logo);
		
		/*
		 * Screen Buttons (Preferences, Chore Trader, Notifications, Chore Request, Availability, Research)
		 */
		
		Button choreTrader = new Button(shell, SWT.PUSH);
		choreTrader.setText("Chore Trader");
		
		Button research = new Button(shell, SWT.PUSH);
		research.setText("Research");
		
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9914);
		send.add(familyUsername);
		send.add(username);
		ArrayList<Object> response = client.clientConnection(send);
		
		numNotifications = (Integer) response.get(0);
		
		notifications = new Button(shell, SWT.PUSH);
		notifications.setText("Notifications (" + numNotifications + ")");
		
		Button requestChore = new Button(shell, SWT.PUSH);
		requestChore.setText("Request a Chore");
		
		Button changeAvailability = new Button(shell, SWT.PUSH);
		changeAvailability.setText("Change Availability");
		
		Label totalAllowance = new Label(shell, SWT.NONE);
		totalAllowance.setText("Total allowance owed: $" + allowanceTotal);
		
		Button returnButton = new Button(shell, SWT.PUSH);
		returnButton.setText("Return");
		
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
		 * Hourly Schedule - very similar to MainScreen, but only shows the current user's chores
		 */
		
		Group dailySchedule = new Group(shell, SWT.NONE);
		dailySchedule.setText("Daily Schedule");
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		notifications.setLayoutData(data);
		
		//Notifications listener - used to open the notifications screen so long as a user has open notifications
		notifications.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (numNotifications > 0) {
					NotificationsScreen notifications = new NotificationsScreen(familyUsername, username, false);
					updateNotificationCount();
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(notifications, 10);
		changeAvailability.setLayoutData(data);
		changeAvailability.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
				try {
					Availability sdt = new Availability(familyUsername, username);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	);
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(changeAvailability, 10);
		requestChore.setLayoutData(data);
		requestChore.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
				try {
					RequestChore rc = new RequestChore(familyUsername, username);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		);
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(requestChore, 10);
		choreTrader.setLayoutData(data);
		choreTrader.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
				ChoreTrader ct = new ChoreTrader(familyUsername, username, 1);
			}
		}
	);

		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(choreTrader, 10);
		research.setLayoutData(data);
		research.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Research r = new Research(familyUsername);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(notifications, 10);
		data.left = new FormAttachment(5, 0);
		calendar.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(calendar, 10);
		data.left = new FormAttachment(5, 0);
		totalAllowance.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(totalAllowance, 20);
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
		data.top = new FormAttachment(notifications, 10);
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
				
				selectedUsername = ((TableItem)event.item).getText(1);
				if (!selectedUsername.equals("")) {
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
						ChoreDetails details = new ChoreDetails(familyUsername, selectedUsername, choreName, date, time, username, false, allowance);
						updateAllowanceTotal();
						
						calendar.getParent().setRedraw(false);
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
						
					} catch (ParseException e) {
						e.printStackTrace();
					}	
				}
			}

			@SuppressWarnings("unchecked")
			private void updateAllowanceTotal() {
				ArrayList<Object> send = new ArrayList<Object>();
				send.add(9919);
				send.add(familyUsername);
				ArrayList<Object> response = client.clientConnection(send);
				
				for (int i = 0; i < response.size(); i++) {
					if (((String)(((ArrayList<Object>)response.get(i)).get(0))).equals(username)) {
						allowanceTotal = (Double)((ArrayList<Object>)response.get(i)).get(1);
					}
				}
				
				totalAllowance.setText("Total allowance owed: $" + allowanceTotal);
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
		notifications.setText("Notifications (" + numNotifications + ")");
	}
	
	/**
	 * 
	 * Takes in a given data array of all chores belonging to a family and creates the schedule for the current user.
	 * 
	 * @param data
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void createSchedule(ArrayList<Object> data) {
		scheduleTable.removeAll();
		
		ArrayList<Integer> times = new ArrayList<Integer>();
		ArrayList<Integer> dataPoints = new ArrayList<Integer>();
		
		if (data != null && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				Time temp = (Time)(((ArrayList<Object>)data.get(i)).get(3));
				String name = (String) ((ArrayList<Object>) data.get(i)).get(0);
				
				int time = temp.getHours();
				
				if(name.equals(username)) {
					times.add(time);
					dataPoints.add(i);
				}
				
			}
		
			if (times.size() > 0) {
				for (int i = 8; i < 21; i++) {
					boolean multi = false;
					for (int j = 0; j < times.size(); j++) {
						
						if (times.get(j) == i) {
							createItem(i, j, true, data, dataPoints);
							multi = true;
						} else if (j == times.size() - 1 && !multi) {
							createItem(i, j, false, data, dataPoints);
							
						}
						
					}
					
				}
			} else {
				createEmpty();
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
	 * Uses the given DataPoints array and position j to identify a user's chore positions within the data array, 
	 * then using the time (1 - 12) i to create a Chore item within the Schedule table, or creates an empty item if isJob is false.
	 * 
	 * @param i, j, isJob, data, dataPoints
	 */
	@SuppressWarnings("unchecked")
	private void createItem(int i, int j, boolean isJob, ArrayList<Object> data, ArrayList<Integer> dataPoints) {
		
		int pos = dataPoints.get(j);
		
		TableItem item = new TableItem(scheduleTable, SWT.NULL);
		String name = (String) ((ArrayList<Object>) data.get(pos)).get(0);
		
		if(isJob && name.equals(username)) {
			String job = (String) ((ArrayList<Object>) data.get(pos)).get(1);
			Double allowance = (Double) ((ArrayList<Object>) data.get(pos)).get(4);
			
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