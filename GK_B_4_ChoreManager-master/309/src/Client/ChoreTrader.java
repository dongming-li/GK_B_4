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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for Displaying the Chore trader screen to allow users to trade chores.
 * Class contacts the database via client/server code to access assigned chores for a user and a family, and send the trade chore notifications.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class ChoreTrader {
	
	static Image logo;
	private ArrayList<Object> mem1;
	private ArrayList<Object> mem2;
	
	private String familyUsername;
	private String username;
	private String username2;
	
	private Table scheduleTable;
	private Table scheduleTable2;
	
	private String chore1 = "";
	private String chore2 = "";
	private String time1 = "";
	private String time2 = "";
	private String date1 = "";
	private String date2 = "";
	private Time t1;
	private Time t2;
	
	/**
	 * Constructor for the ChoreTrader screen.
	 * 
	 * @param familyName
	 * @param userName
	 * @param userType
	 */
	protected ChoreTrader(String familyName, String userName, Integer userType) {
		
		familyUsername = familyName;
		username = userName;
		mem1 = new ArrayList<Object>();
		mem2 = new ArrayList<Object>();
		
		Display display = Display.getDefault();
		
		logo = display.getSystemImage(SWT.ICON_QUESTION);
		Shell shell = createShell(display);
		shell.open();
		shell.addDisposeListener(new DisposeListener() {
			@SuppressWarnings("unused")
			public void widgetDisposed(DisposeEvent event) {
				shell.dispose();
				
				if (userType == 0) {
					try {
						ParentScreen parentScreen  = new ParentScreen(familyUsername, username);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (userType == 1) {
					try {
						ChildScreen childScreen = new ChildScreen(familyUsername, username);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					try {
						OtherScreen otherScreen = new OtherScreen(familyUsername, username);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}
	
	/**
	 * Creates the main ChoreTrader UI shell
	 * 
	 * @param display
	 * 
	 * @return The created shell
	 */
	private Shell createShell(Display display) {

		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(1500, 750);
		shell.setText("Trade Chores");
		shell.setImage(logo);
		
		Label mainLabel = new Label(shell, SWT.NONE);
		mainLabel.setText("Select the Chores you wish to trade:");
		
		Label ErrorLabel = new Label(shell, SWT.NONE);
		ErrorLabel.setText("There is some sort of error");
		Color Red = display.getSystemColor(SWT.COLOR_RED);
		ErrorLabel.setForeground(Red);
		ErrorLabel.setVisible(false);
		
		DateTime calendar = new DateTime (shell, SWT.CALENDAR);
		
		DateTime calendar2 = new DateTime (shell, SWT.CALENDAR);
		
		Group dailySchedule = new Group(shell, SWT.NONE);
		dailySchedule.setText("Select one of your Chores to trade:");
		
		Group dailySchedule2 = new Group(shell, SWT.NONE);
		dailySchedule2.setText("Select the Chore you wish to trade with:");
		
		Button submitButton = new Button(shell, SWT.NONE);
		submitButton.setText("Submit");
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		
		/*
		 * --------------------------------------------------------------------------------------------
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		mainLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 10);
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
				createSchedule(response);
			    
				calendar.getParent().setRedraw(true);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 10);
		data.left = new FormAttachment(calendar, 5);
		dailySchedule.setLayoutData(data);
		
		scheduleTable = new Table(dailySchedule, SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		
		scheduleTable.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("deprecation")
			public void widgetSelected(SelectionEvent event) {
				time1 = ((TableItem)event.item).getText(0);
				if(time1.length() == 7)
				{
					int hour;
					if(time1.contains("PM"))
					{
						hour = Integer.parseInt(time1.substring(0, 1)) + 12;
					}
					else
					{
						hour = Integer.parseInt(time1.substring(0, 1));
					}
					int minutes = Integer.parseInt(time1.substring(2, 4));
					int seconds = 00;
					Time t = new Time(hour, minutes, seconds);
					t1 = t;
				}
				else
				{
					int hour;
					if(time1.contains("PM") && !time1.substring(0, 2).equals("12"))
					{
						hour = Integer.parseInt(time1.substring(0, 2)) + 12;
					}
					else
					{
						hour = Integer.parseInt(time1.substring(0, 2));
					}
					int minutes = Integer.parseInt(time1.substring(3, 5));
					int seconds = 00;
					Time t = new Time(hour, minutes, seconds);
					t1 = t;
				}
				chore1 = ((TableItem)event.item).getText(2);
			}
		});
		
		createScheduleGroup(true);
		initializeTable(calendar, true);
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 10);
		data.left = new FormAttachment(dailySchedule, 20);
		calendar2.setLayoutData(data);
		calendar2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				calendar2.getParent().setRedraw(false);
				
				String month;
			    String day;

			    if (calendar.getMonth() < 9) {
			    	month = "0" + (calendar2.getMonth() + 1); //DateTime's months start at 0 for some reason
			      } else {
			    	month = "" + (calendar2.getMonth() + 1);
			      }
			    

			      if (calendar.getDay() < 10) {
			    	  day = "0" + calendar2.getDay();
			      } else {
			    	  day = "" + calendar2.getDay();
			      }
			    
			    
			    String year = "" + calendar2.getYear();
			    
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
			    
				calendar2.getParent().setRedraw(true);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 10);
		data.left = new FormAttachment(calendar2, 5);
		dailySchedule2.setLayoutData(data);
		
		scheduleTable2 = new Table(dailySchedule2, SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		
		scheduleTable2.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("deprecation")
			public void widgetSelected(SelectionEvent event) {
				time2 = ((TableItem)event.item).getText(0);
				if(time2.length() == 7)
				{
					int hour;
					if(time2.contains("PM"))
					{
						hour = Integer.parseInt(time2.substring(0, 1)) + 12;
					}
					else
					{
						hour = Integer.parseInt(time2.substring(0, 1));
					}
					int minutes = Integer.parseInt(time2.substring(2, 4));
					int seconds = 00;
					Time t = new Time(hour, minutes, seconds);
					t2 = t;
				}
				else
				{
					int hour;
					int minutes;
					if(time2.contains("PM") && !time2.substring(0, 2).equals("12"))
					{
						hour = Integer.parseInt(time2.substring(0, 1)) + 12;
						minutes = Integer.parseInt(time2.substring(2, 4));
					}
					else
					{
						hour = Integer.parseInt(time2.substring(0, 2));
						minutes = Integer.parseInt(time2.substring(3, 5));
					}
					int seconds = 00;
					Time t = new Time(hour, minutes, seconds);
					t2 = t;
				}
				chore2 = ((TableItem)event.item).getText(2);
				username2 = ((TableItem)event.item).getText(1);
			}
		});
		
		createScheduleGroup(false);
		initializeTable(calendar2, false);
		
		data = new FormData();
		data.top = new FormAttachment(dailySchedule, 20);
		data.left = new FormAttachment(5, 0);
		submitButton.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(submitButton, 20);
		data.left = new FormAttachment(5, 0);
		ErrorLabel.setLayoutData(data);
		submitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (!time1.equals("") && !time2.equals("")) {
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
				    
				    date1 = year + "/" + month + "/" + day;
					
				    if (calendar2.getMonth() < 9) {
				    	month = "0" + (calendar2.getMonth() + 1); //DateTime's months start at 0 for some reason
				      } else {
				    	month = "" + (calendar2.getMonth() + 1);
				      }
				    
	
				      if (calendar2.getDay() < 10) {
				    	  day = "0" + calendar2.getDay();
				      } else {
				    	  day = "" + calendar2.getDay();
				      }
				      
				     year = "" + calendar.getYear();
				     
				     date2 = year + "/" + month + "/" + day;
				     
				     Shell cShell = createConfirmationShell(display);
				     cShell.open();
				     cShell.addDisposeListener(new DisposeListener() {
							public void widgetDisposed(DisposeEvent event) {
								cShell.dispose();
								shell.dispose();
							}
						});
				     
				} else {
					ErrorLabel.setVisible(true);
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(dailySchedule, 20);
		data.left = new FormAttachment(submitButton, 10);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});
		
		return shell;
	}
	
	/**
	 * Creates the Headers for either the current user or family's schedule groups, based on the boolean passed in.
	 * 
	 * @param b
	 */
	private void createScheduleGroup(Boolean b) {
		if (b) {
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
	 * Creates the schedule for the family for the selected day.
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
				
				int time = temp.getHours();
				
				times.add(time);
				
			}
		
		
			for (int i = 8; i < 21; i++) {
				boolean multi = false;
				for (int j = 0; j < times.size(); j++) {
					
					if (times.get(j) == i) {
						createItem(i, j, true, response, true, null);
						multi = true;
					} else if (j == times.size() - 1 && !multi) {
						createItem(i, j, false, response, true, null);
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
	 * Creates the schedule for the current user for the selected day.
	 * 
	 * @param response
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	protected void createSchedule(ArrayList<Object> response) {
		scheduleTable.removeAll();
		
		ArrayList<Integer> times = new ArrayList<Integer>();
		ArrayList<Integer> dataPoints = new ArrayList<Integer>();
		
		if (response != null && response.size() > 0) {
			for (int i = 0; i < response.size(); i++) {
				Time temp = (Time)(((ArrayList<Object>)response.get(i)).get(3));
				String name = (String) ((ArrayList<Object>) response.get(i)).get(0);
				
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
							createItem(i, j, true, response, false, dataPoints);
							multi = true;
						} else if (j == times.size() - 1 && !multi) {
							createItem(i, j, false, response, false, dataPoints);
							
						}
						
					}
					
				}
			} else {
				createEmpty(true);
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
	 * Creates and empty day's schedule.
	 * 
	 * @param b
	 */
	private void createEmpty(boolean b) {
		if (b) {
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
	 * Uses the given DataPoints array and position j to identify a user's chore positions within the data array, 
	 * then using the time (1 - 12) i to create a Chore item within the correct table based on the table boolean, or creates an empty item if isJob is false.
	 * 
	 * @param i
	 * @param j
	 * @param isJob
	 * @param response
	 * @param table
	 * @param dataPoints
	 */
	@SuppressWarnings("unchecked")
	private void createItem(int i, int j, boolean isJob, ArrayList<Object> response, boolean table, ArrayList<Integer> dataPoints) {
		if (table) {
			TableItem item = new TableItem(scheduleTable2, SWT.NULL);
			
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
			
			int pos = dataPoints.get(j);
			
			TableItem item = new TableItem(scheduleTable, SWT.NULL);
			String name = (String) ((ArrayList<Object>) response.get(pos)).get(0);
			
			if(isJob && name.equals(username)) {
				String job = (String) ((ArrayList<Object>) response.get(pos)).get(1);
				Double allowance = (Double) ((ArrayList<Object>) response.get(pos)).get(4);
				
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
	 * Initializes the schedule table with the current day's chores.
	 * 
	 * @param calendar
	 * @param table
	 */
	private void initializeTable(DateTime calendar, Boolean table) {
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
			if (table) {
				createSchedule(response);
			} else {
				createSchedule2(response);
			}
	}

	/**
	 * Creates the trade confirmation shell, allowing the user to double check their entries before requesting the trade.
	 * 
	 * @param display
	 * @return The created shell.
	 */
	private Shell createConfirmationShell(Display display) {
		
		Shell cShell = new Shell(display);
		FormLayout layout = new FormLayout();
		cShell.setLayout(layout);
		cShell.setSize(500, 200);
		cShell.setText("Confirm Trade");
		cShell.setImage(logo);
		
		Label mainLabel = new Label(cShell, SWT.NONE);
		mainLabel.setText("Are you sure you want to request to trade: ");
		
		Label chore1Label = new Label(cShell, SWT.NONE);
		chore1Label.setText(chore1 + " at " + time1);
		
		Label forLabel = new Label(cShell, SWT.NONE);
		forLabel.setText("for: ");
		
		Label chore2Label = new Label(cShell, SWT.NONE);
		chore2Label.setText(chore2 + " at " + time2 + "?");
		
		Button confirmButton = new Button(cShell, SWT.NONE);
		confirmButton.setText("Confirm");
		
		Button cancelButton = new Button(cShell, SWT.NONE);
		cancelButton.setText("Cancel");
		
		/*
		 * --------------------------------------------------------------------------------
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		mainLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 10);
		data.left = new FormAttachment(5, 0);
		chore1Label.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(chore1Label, 10);
		data.left = new FormAttachment(5, 0);
		forLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(forLabel, 10);
		data.left = new FormAttachment(5, 0);
		chore2Label.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(chore2Label, 20);
		data.left = new FormAttachment(5, 0);
		confirmButton.setLayoutData(data);
		confirmButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				boolean validChores = checkChores();
				if(!validChores || chore1.equals("Unavailable") || chore2.equals("Unavailable") || username.equals(username2))
				{
					//Inform user of error
					System.out.println("Invalid chore");
				}
				else
				{
					ArrayList<Object> send = new ArrayList<Object>();
					send.add(9909);
					send.add(familyUsername);
					mem1.add(username);
					mem1.add(chore1);
					mem1.add(date1);
					mem1.add("MemTrade");
					mem1.add("Would you like to trade?");
					mem1.add(t1);
					mem2.add(username2);
					mem2.add(chore2);
					mem2.add(date2);
					mem2.add(t2);
					send.add(mem2);
					send.add(mem1);
					ArrayList<Object> response = client.clientConnection(send);
					if( !(boolean)response.get(0))
					 {
						 //Inform user of error
					 }
					else
					{
						cShell.dispose();
					}
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(chore2Label, 20);
		data.left = new FormAttachment(confirmButton, 10);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				cShell.dispose();
			}
		});
		
		return cShell;
	}
	
	/**
	 * Checks whether the selected chores have already been started
	 * 
	 * @return A boolean representing whether either chore has been started.
	 */
	private boolean checkChores()
	{
		ArrayList<Object> check1 = new ArrayList<Object>();
		check1.add(9922);
		check1.add(username);
		check1.add(familyUsername);
		check1.add(chore1);
		check1.add(date1);
		ArrayList<Object> response = client.clientConnection(check1);
		if((Boolean) response.get(0))
		{
			return false;
		}
		ArrayList<Object> check2 = new ArrayList<Object>();
		check2.add(9922);
		check2.add(username2);
		check2.add(familyUsername);
		check2.add(chore2);
		check2.add(date2);
		response = client.clientConnection(check1);
		if((Boolean) response.get(0))
		{
			return false;
		}
		return true;
	}
}