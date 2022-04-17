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
 * Class for displaying the modify chore screen.
 * Class contacts the database via client/server code to pull chores for a family and modify a selected chore.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class ModifyChore {

	static Image logo;
	
	private String familyUsername;
	private String username;
	private String parentUser;
	
	private Table scheduleTable;
	private Table scheduleTable2;
	
	private String newTime = "";
	private String choreName = "";
	
	private Boolean improperInput = false;
	private Boolean allowanceChanged = false;
	
	private Double allowance;
	private Double newAllowance = null;
	
	/**
	 * Constructor #1 for the screen, takes in a family name and parent who is defining the chore.
	 * 
	 * @param familyName
	 * @param parentName
	 * @throws ParseException
	 */
	protected ModifyChore(String familyName, String parentName) throws ParseException {
		
		familyUsername = familyName;
		parentUser = parentName;
		
		Display display = Display.getDefault();
		
		logo = display.getSystemImage(SWT.ICON_QUESTION);
		Shell shell = createShell(display);
		shell.open();
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				shell.dispose();
				try {
					ParentScreen parentScreen  = new ParentScreen(familyUsername, parentUser);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}

	/**
	 * Constructor #2 for the modify chore screen, used by other screens to modify chores.
	 * 
	 * @param display
	 * @param familyName
	 * @param parentName
	 * @param childName
	 * @param chore
	 * @param date
	 * @param time
	 */
	public ModifyChore(Display display, String familyName, String parentName, String childName, String chore, String date, String time) {
		familyUsername = familyName;
		parentUser = parentName;
		username = childName;
		choreName = chore;
		allowance = retrieveAllowance(date);
		logo = display.getSystemImage(SWT.ICON_QUESTION);
		
		Shell shell = null;
		try {
			shell = modifyShell(display, date, time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	/**
	 * Creates the main ModifyChore UI shell
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
		shell.setText("Modify Chores");
		shell.setImage(logo);
		
		Label calendarLabel = new Label(shell, SWT.NONE);
		calendarLabel.setText("Select a Date:");
		
		DateTime calendar = new DateTime (shell, SWT.CALENDAR);
		
		Group dailySchedule = new Group(shell, SWT.NONE);
		dailySchedule.setText("Select Chore to modify:");
		
		Button returnButton = new Button(shell, SWT.NONE);
		returnButton.setText("Return");
		/*
		 * ------------------------------------------------------------------------------
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		calendarLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(calendarLabel, 5);
		data.left = new FormAttachment(5, 0);
		calendar.setLayoutData(data);
		calendar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				calendar.getParent().setRedraw(false);
				
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
			    Date d = null;
				try {
					d = new SimpleDateFormat("yyyy-M-d").parse(dateString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(d);
			    ArrayList<Object> send = new ArrayList<Object>();
				send.add(3);
				send.add(familyUsername);
				send.add(date);
				send.add(dayOfWeek);
				ArrayList<Object> response = client.clientConnection(send);
				createSchedule1(response);
			    
				calendar.getParent().setRedraw(true);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(5, 5);
		data.left = new FormAttachment(calendar, 10);
		dailySchedule.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(calendar, 20);
		data.left = new FormAttachment(5, 0);
		returnButton.setLayoutData(data);
		returnButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});
		
		/*
		 * ---------------------------------------------------
		 */
		
		scheduleTable = new Table(dailySchedule, SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		
		createScheduleGroup(dailySchedule, true);
		
		scheduleTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println(((TableItem)event.item).getText(1));
				//when a user selects an item, collect the selected chore's user's name and set "username" to it - for use later
				username = ((TableItem)event.item).getText(1);
				
				if (!username.equals("")) {
					//when a user selects an item, collect the selected chore's date + time
					String month;
				    String day;
				    String year = "" + calendar.getYear();
				      
				    if (calendar.getMonth() < 10) {
				    	month = "0" + (calendar.getMonth() + 1); //DateTime's months start at 0 for some reason
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
					
					choreName = ((TableItem)event.item).getText(2);
					if(choreName.equals("Unavailable"))
					{
						return;
					}
					try
					{
						
						StringBuilder allowanceStringFull = new StringBuilder(((TableItem)event.item).getText(3));
						allowanceStringFull.deleteCharAt(0);
						String allowanceString = allowanceStringFull.toString();
						
						allowance = Double.parseDouble(allowanceString);
						
						Shell mshell = modifyShell(display, date, time);
						mshell.open();
						mshell.addDisposeListener(new DisposeListener() {
							public void widgetDisposed(DisposeEvent event) {
								mshell.dispose();
								shell.dispose();
							}
						});
					}
					catch(ParseException e)
					{
						System.out.println("Parse Exception");
					}
				}
			}
		});
		
		initializeTable(calendar, true);
		
		return shell;
	}

	/**
	 * Creates the modify chore shell, allowing a parent to choose modifications for the selected chore.
	 * 
	 * @param display
	 * @param date
	 * @param time
	 * @return THe created shell
	 * @throws ParseException
	 */
	private Shell modifyShell(Display display, String date, String time) throws ParseException {
		Shell mshell = new Shell(display);
		FormLayout layout = new FormLayout();
		mshell.setLayout(layout);
		mshell.setSize(1000, 750);
		mshell.setText("Choose a new Date/Time");
		mshell.setImage(logo);
		
		newTime = time;
		
		Label originalLabel = new Label(mshell, SWT.NONE);
		originalLabel.setText("Original Chore Date/Time: " + date + " " + time);
		
		Label calendarLabel = new Label(mshell, SWT.NONE);
		calendarLabel.setText("Select a new Date:");
		
		DateTime calendar = new DateTime (mshell, SWT.CALENDAR);

		Label originalAllowance = new Label(mshell, SWT.NONE);
		originalAllowance.setText("Original Allowance: $" + allowance);
		
		Label modifyAllowance = new Label(mshell, SWT.NONE);
		modifyAllowance.setText("Would you like to modify this value? (Default No)");
		
		Combo modifyCombo = new Combo(mshell, SWT.NONE);
		modifyCombo.add("Yes");
		modifyCombo.add("No");
		
		Label modifyLabel = new Label(mshell, SWT.NONE);
		modifyLabel.setText("New Value:");
		
		Text modifyText = new Text(mshell, SWT.SINGLE | SWT.BORDER);
		modifyText.setEnabled(false);
		
		Label scheduleLabel = new Label(mshell, SWT.NONE);
		scheduleLabel.setText("Select a new Time:");
		
		Group dailySchedule = new Group(mshell, SWT.NONE);
		dailySchedule.setText(username + "'s Schedule on selected date:");
		
		Button submitButton = new Button(mshell, SWT.NONE);
		submitButton.setText("Submit");
		
		Button cancelButton = new Button(mshell, SWT.NONE);
		cancelButton.setText("Cancel");
		
		/*
		 * -------------------------------------------------------------------------------
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		originalLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(originalLabel, 20);
		data.left = new FormAttachment(5, 0);
		calendarLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(calendarLabel, 5);
		data.left = new FormAttachment(5, 0);
		calendar.setLayoutData(data);
		calendar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				calendar.getParent().setRedraw(false);
				
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
			    Date d = null;
				try {
					d = new SimpleDateFormat("yyyy-M-d").parse(dateString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(d);
			    
			    ArrayList<Object> send = new ArrayList<Object>();
				send.add(3);
				send.add(familyUsername);
				send.add(date);
				send.add(dayOfWeek);
				ArrayList<Object> response = client.clientConnection(send);
				createSchedule2(response);
			    
				calendar.getParent().setRedraw(true);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(calendar, 10);
		data.left = new FormAttachment(5, 0);
		originalAllowance.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(originalAllowance, 5);
		data.left = new FormAttachment(5, 0);
		modifyAllowance.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(originalAllowance, 5);
		data.left = new FormAttachment(modifyAllowance, 5);
		modifyCombo.setLayoutData(data);
		modifyCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (modifyCombo.getText().equals("Yes")) {
					improperInput = true;
					newAllowance = allowance;
					allowanceChanged = true;
					modifyText.setEnabled(true);
				} else {
					improperInput = false;
					newAllowance = null;
					allowanceChanged = false;
					modifyText.setEnabled(false);
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(modifyAllowance, 5);
		data.left = new FormAttachment(5, 0);
		modifyLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(modifyAllowance, 5);
		data.left = new FormAttachment(modifyLabel, 5);
		modifyText.setLayoutData(data);
		modifyText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					improperInput = false;
					newAllowance = Double.parseDouble(modifyText.getText());
				} catch (NumberFormatException n) {
					improperInput = true;
					System.out.println("Improper input");
				}
			}
		});
		/*
		 * 
		 */
		data = new FormData();
		data.top = new FormAttachment(originalLabel, 20);
		data.left = new FormAttachment(modifyCombo, 10);
		dailySchedule.setLayoutData(data);
		
		data = new FormData();
		data.bottom = new FormAttachment(dailySchedule, -5);
		data.left = new FormAttachment(modifyCombo, 10);
		scheduleLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(modifyLabel, 25);
		data.left = new FormAttachment(5, 0);
		submitButton.setLayoutData(data);
		submitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				if (improperInput) {
				 	//Error message
				} else {
					//add the parameters properly for the popup
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
				    
				    String newDate = year + "/" + month + "/" + day;
					
					Shell cshell = confirmShell(display, date, newDate, time, newTime);
					cshell.open();
					cshell.addDisposeListener(new DisposeListener() {
						public void widgetDisposed(DisposeEvent event) {
							cshell.dispose();
							mshell.dispose();
						}
					});
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(modifyLabel, 25);
		data.left = new FormAttachment(submitButton, 10);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				mshell.dispose();
			}
		});
		
		scheduleTable2 = new Table(dailySchedule, SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		
		createScheduleGroup(dailySchedule, false);
		
		scheduleTable2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				newTime = ((TableItem)event.item).getText(0);
			}
		});
		
		initializeTable(calendar, false);
		
		return mshell;
	}
	
	/**
	 * Creates the confirmation sell allowing Parents the confirm their modifications to the chore.
	 * 
	 * @param display
	 * @param oldDate
	 * @param newDate
	 * @param oldTime
	 * @param newTime
	 * @return
	 */
	private Shell confirmShell(Display display, String oldDate, String newDate, String oldTime, String newTime) {
		Shell cshell = new Shell(display);
		FormLayout layout = new FormLayout();
		cshell.setLayout(layout);
		cshell.setSize(500, 500);
		cshell.setText("Confirm modification");
		cshell.setImage(logo);
		
		Label mainLabel = new Label(cshell, SWT.NONE);
		mainLabel.setText("Are you sure you want to change " + choreName + " from: ");
		
		Label changeLabel = new Label(cshell, SWT.NONE);
		changeLabel.setText(oldDate + " at " + oldTime + " to " + newDate + " at " + newTime);
		
		Label allowanceLabel = new Label(cshell, SWT.NONE);
		if (allowanceChanged) {
			allowanceLabel.setText("As well as the allowance from $" + allowance + " to $" + newAllowance);
		}
		
		Button submitButton = new Button(cshell, SWT.NONE);
		submitButton.setText("Submit");
		
		Button cancelButton = new Button(cshell, SWT.NONE);
		cancelButton.setText("Cancel");
		
		/*
		 * --------------------------------------------------------------------------
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		mainLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 10);
		data.left = new FormAttachment(5, 0);
		changeLabel.setLayoutData(data);
		
		if (allowanceChanged) {
			data = new FormData();
			data.top = new FormAttachment(changeLabel, 10);
			data.left = new FormAttachment(5, 0);
			allowanceLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(allowanceLabel, 10);
			data.left = new FormAttachment(5, 0);
			submitButton.setLayoutData(data);
			submitButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					//submit all necessary modify data to the server/DB
					Time selectedTime;
					if(newTime.length() == 7)
					{
						int hour;
						if(newTime.contains("PM"))
						{
							hour = Integer.parseInt(newTime.substring(0, 1)) + 12;
						}
						else
						{
							hour = Integer.parseInt(newTime.substring(0, 1));
						}
						int minutes = Integer.parseInt(newTime.substring(2, 4));
						int seconds = 00;
						Time t = new Time(hour, minutes, seconds);
						selectedTime = t;
					}
					else
					{
						int hour;
						if(newTime.contains("PM") && !newTime.substring(0, 2).equals("12"))
						{
							hour = Integer.parseInt(newTime.substring(0, 2)) + 12;
						}
						else
						{
							hour = Integer.parseInt(newTime.substring(0, 2));
						}
						int minutes = Integer.parseInt(newTime.substring(3, 5));
						int seconds = 00;
						Time t = new Time(hour, minutes, seconds);
						selectedTime = t;
					}
					
					ArrayList<Object> send = new ArrayList<Object>();
					send.add(9912);
					send.add(familyUsername);
					send.add(username);
					send.add(choreName);
					send.add(oldDate);
					send.add(newDate);
					send.add(selectedTime);
					send.add(newAllowance);
					ArrayList<Object> response = client.clientConnection(send);
					
					if ((Boolean)response.get(0)) {
						cshell.dispose();
					} else {
						//TODO error checking here
						System.out.println("Error with modification");
					}
				}
			});
			
			data = new FormData();
			data.top = new FormAttachment(allowanceLabel, 10);
			data.left = new FormAttachment(submitButton, 10);
			cancelButton.setLayoutData(data);
			cancelButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					cshell.dispose();
				}
			});
			
		} else {
			data = new FormData();
			data.top = new FormAttachment(changeLabel, 10);
			data.left = new FormAttachment(5, 0);
			submitButton.setLayoutData(data);
			submitButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					//submit all necessary modify data to the server/DB
					Time selectedTime;
					if(newTime.length() == 7)
					{
						int hour;
						if(newTime.contains("PM"))
						{
							hour = Integer.parseInt(newTime.substring(0, 1)) + 12;
						}
						else
						{
							hour = Integer.parseInt(newTime.substring(0, 1));
						}
						int minutes = Integer.parseInt(newTime.substring(2, 4));
						int seconds = 00;
						Time t = new Time(hour, minutes, seconds);
						selectedTime = t;
					}
					else
					{
						int hour;
						if(newTime.contains("PM") && !newTime.substring(0, 2).equals("12"))
						{
							hour = Integer.parseInt(newTime.substring(0, 2)) + 12;
						}
						else
						{
							hour = Integer.parseInt(newTime.substring(0, 2));
						}
						int minutes = Integer.parseInt(newTime.substring(3, 5));
						int seconds = 00;
						Time t = new Time(hour, minutes, seconds);
						selectedTime = t;
					}
					
					ArrayList<Object> send = new ArrayList<Object>();
					send.add(9912);
					send.add(familyUsername);
					send.add(username);
					send.add(choreName);
					send.add(oldDate);
					send.add(newDate);
					send.add(selectedTime);
					send.add(allowance);
					ArrayList<Object> response = client.clientConnection(send);
					
					if ((Boolean)response.get(0)) {
						cshell.dispose();
					} else {
						//TODO error checking here
						System.out.println("Error with modification");
					}
				}
			});
			
			data = new FormData();
			data.top = new FormAttachment(changeLabel, 10);
			data.left = new FormAttachment(submitButton, 10);
			cancelButton.setLayoutData(data);
			cancelButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					cshell.dispose();
				}
			});
		}
		
		
		
		return cshell;
	}
	
	/**
	 * Initializes the schedule table with the current day's chores.
	 * 
	 * @param calendar
	 * @param table
	 * @throws ParseException
	 */
	private void initializeTable(DateTime calendar, Boolean table) throws ParseException {
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
			if (table) {
				createSchedule1(response);
			} else {
				createSchedule2(response);
			}
	}

	/**
	 * Creates the Headers for either the current selected user or family's schedule groups, based on the boolean passed in.
	 * 
	 * @param dailySchedule
	 * @param table
	 */
	private void createScheduleGroup(Group dailySchedule, Boolean table) {
		if (table) {
			scheduleTable.setHeaderVisible(true);
			
			String[] headers = { "Time", "Family Member", "Chore", "Allowance"};
			
			for (int i = 0; i < headers.length; i++) {
				TableColumn column = new TableColumn(scheduleTable, SWT.NONE);
				column.setText(headers[i]);
				
			}
		} else {
			scheduleTable2.setHeaderVisible(true);
			
			String[] headers = { "Time", "Family Member", "Chore", "Allowance"};
			
			for (int i = 0; i < headers.length; i++) {
				TableColumn column = new TableColumn(scheduleTable2, SWT.NONE);
				column.setText(headers[i]);
				
			}
		}
	}

	/**
	 * Creates the daily schedule for the family based on the data.
	 * 
	 * @param response
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	protected void createSchedule1(ArrayList<Object> response) {
		scheduleTable.removeAll();
		
		ArrayList<Integer> times = new ArrayList<Integer>();
		
		if (response != null && response.size() > 0) {
			for (int i = 0; i < response.size(); i++) {
				Time temp = (Time)(((ArrayList<Object>)response.get(i)).get(3));
				
				int time = temp.getHours();
				
				times.add(time);
				
			}
		
		
			for (int i = 8; i < 21; i++) {
				boolean multi = false;
				for (int j = 0; j < times.size(); j++) {
					
					if (times.get(j) == i) {
						createItem(i, j, true, response, true);
						multi = true;
					} else if (j == times.size() - 1 && !multi) {
						createItem(i, j, false, response, true);
					}
					
				}
				
			}
		} else {
			createEmpty(true);
		}
		
		for (int i = 0; i < 4; i++) {
			scheduleTable.getColumn(i).pack();
		}
		
		scheduleTable.setBounds(25, 25, 350, 500);
		
	}
	
	/**
	 * Creates the daily schedule for the selected user, based on the data.
	 * 
	 * @param response
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	protected void createSchedule2(ArrayList<Object> response) {
		scheduleTable2.removeAll();
		
		ArrayList<Integer> times = new ArrayList<Integer>();
		
		if (response != null && response.size() > 0) {
			for (int i = 0; i < response.size(); i++) {
				Time temp = (Time)(((ArrayList<Object>)response.get(i)).get(3));
				String name = (String) ((ArrayList<Object>) response.get(i)).get(0);
				
				int time = temp.getHours();
				
				if(name.equals(username)) {
					times.add(time);
				}
				
			}
		
		
			for (int i = 8; i < 21; i++) {
				boolean multi = false;
				for (int j = 0; j < times.size(); j++) {		
					if (times.get(j) == i) {
						createItem(i, j, true, response, false);
						multi = true;
					} else if (j == times.size() - 1 && !multi) {
						createItem(i, j, false, response, false);
					}
					
				}
				
			}
		} else {
			createEmpty(false);
		}
		
		for (int i = 0; i < 4; i++) {
			scheduleTable2.getColumn(i).pack();
		}
		
		scheduleTable2.setBounds(25, 25, 350, 500);
		
	}
	
	/**
	 * Creates an empty schedule
	 * 
	 * @param table
	 */
	private void createEmpty(boolean table) {
		if (table) {
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
		} else {
			for (int i = 8; i < 21; i++) {
				TableItem item = new TableItem(scheduleTable2, SWT.NULL);
				
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
		
	}

	/**
	 * Uses the given data array, position j and time i to create a Chore item within the correct Schedule table, or creates an empty item if isJob is false.
	 * 
	 * @param i
	 * @param j
	 * @param isJob
	 * @param response
	 * @param table
	 */
	@SuppressWarnings("unchecked")
	private void createItem(int i, int j, boolean isJob, ArrayList<Object> response, boolean table) {
		
		if (table) {
			TableItem item = new TableItem(scheduleTable, SWT.NULL);
			
			if (isJob) {
				
				String name = (String) ((ArrayList<Object>) response.get(j)).get(0);
				String job = (String) ((ArrayList<Object>) response.get(j)).get(1);
				Double allowance = (Double) ((ArrayList<Object>) response.get(j)).get(4);
				
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
		} else {
			TableItem item = new TableItem(scheduleTable2, SWT.NULL);
			String name = (String) ((ArrayList<Object>) response.get(j)).get(0);
			
			if(isJob && name.equals(username)) {
				String job = (String) ((ArrayList<Object>) response.get(j)).get(1);
				Double allowance = (Double) ((ArrayList<Object>) response.get(j)).get(4);
				
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
	}
	
	/**
	 * Method that calls the database to get the allowance for a single chore
	 * @param d
	 * @return
	 */
	private double retrieveAllowance(String d)
	{
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9921);
		send.add(familyUsername);
		send.add(username);
		send.add(d);
		send.add(choreName);
		ArrayList<Object> response = client.clientConnection(send);
		return (Double)response.get(0);
	}
}