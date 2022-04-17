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
 * Class for displaying the "Home" screen for a family.
 * Class contacts the database via client/server code to pull family members, log in to user screens, pull chores, and access research.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class MainScreen {
	
	static Image logo;
	private String familyUsername;
	
	private Table scheduleTable;
	
	private String selectedUser = "";
	
	/**
	 * Main screen constructor.
	 * 
	 * @param FamUser
	 * @throws ParseException
	 */
	protected MainScreen(String FamUser) throws ParseException {
		familyUsername = FamUser;
		
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
	 * Creates the main MainScreen UI shell
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
		shell.setText("Home");
		shell.setImage(logo);
		
		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(layout);
		
		Label userLabel = new Label(comp, SWT.NONE);
		userLabel.setText("Sign in as a User:");
		
		Combo userCombo = new Combo(comp, SWT.NONE);

		ArrayList<Object> send = new ArrayList<Object>();
		send.add(300);
		send.add(familyUsername);
		send.add(false);
		ArrayList<Object> response = client.clientConnection(send);
		for (int i = 0; i < response.size(); i++) {
			userCombo.add((String)response.get(i));
		}
		
		
		DateTime calendar = new DateTime (comp, SWT.CALENDAR);
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
		
		Group dailySchedule = new Group(comp, SWT.NONE);
		dailySchedule.setText("Daily Schedule");
		
		Label selectUser = new Label(comp, SWT.NONE);
		selectUser.setText("Select a User:");
		
		Label enterPin = new Label(comp, SWT.NONE);
		enterPin.setText("Enter User's PIN:");
		Text pinText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		
		Button signInButton = new Button(comp, SWT.PUSH);
		signInButton.setText("Sign In");
		
		Button research = new Button(comp, SWT.PUSH);
		research.setText("Research");
		
		FormData data = new FormData();
		data.top = new FormAttachment(10, 0);
		data.left = new FormAttachment(10, 0);
		calendar.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(calendar, 10);
		data.left = new FormAttachment(10, 0);
		userLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(userLabel, 5);
		data.left = new FormAttachment(10, 0);
		selectUser.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(selectUser, 5);
		data.left = new FormAttachment(10, 0);
		userCombo.setLayoutData(data);
		userCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedUser = userCombo.getText();
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(userCombo, 5);
		data.left = new FormAttachment(10, 0);
		enterPin.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(userCombo, 5);
		data.left = new FormAttachment(enterPin, 5);
		pinText.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(enterPin, 10);
		data.left = new FormAttachment(10, 0);
		signInButton.setLayoutData(data);
		signInButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//Add the pin-sign-in call here with selectedUser as the input for the User's name, familyUsername for the family user, and pinText.getText() for the pin
				int pin = Integer.parseInt(pinText.getText());
				
				//Enter the rest here - I think this is how zach is sending info - correct it if necessary
				ArrayList<Object> send = new ArrayList<Object>();
				send.add(301);
				send.add(familyUsername);
				send.add(selectedUser);
				send.add(pin);
				ArrayList<Object> response = client.clientConnection(send);
				
				if (response.size() == 0) {
					//Eventually add in a "failed to sign in" notification
				} else {
					switch ((String)response.get(0)) {
					case "Parent":
						shell.dispose();
						try {
							ParentScreen parentScreen  = new ParentScreen(familyUsername, selectedUser);
						} catch (ParseException e2) {
							e2.printStackTrace();
						}
						break;
					case "Child":
						shell.dispose();
						try {
							ChildScreen childScreen  = new ChildScreen(familyUsername, selectedUser);
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						break;
					case "Other":
						shell.dispose();
						try {
							OtherScreen otherScreen  = new OtherScreen(familyUsername, selectedUser);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			}
		});
		
    	data = new FormData();
		data.top = new FormAttachment(signInButton, 10);
		data.left = new FormAttachment(10, 0);
		research.setLayoutData(data);
		research.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Research r = new Research(familyUsername);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(10, 0);
		data.left = new FormAttachment(calendar, 5);
		dailySchedule.setLayoutData(data);

		/*
		 * Schedule table + group initialization
		 */
		
		scheduleTable = new Table(dailySchedule, SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		
		createScheduleGroup(dailySchedule);
		scheduleTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
			}
		});
		
		initializeTable(calendar);

		return shell;
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
	    	month = "0" + (calendar.getMonth() + 1);
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