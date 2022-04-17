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
 * Class for displaying the Request chore screen.
 * Class contacts the database via client/server code to retrieve available chores and times as well as requesting the selected chore.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class RequestChore {
	
	static Image logo;
	
	private String familyUsername;
	private String childUser;
	
	@SuppressWarnings("unused")
	private Combo monthCombo;
	@SuppressWarnings("unused")
	private Combo dayCombo;
	@SuppressWarnings("unused")
	private Combo yearCombo;
	private Combo timeCombo;
	
	private Time selectedTime = null;
	private String selectedDate = "";
//	private String selectedUser = "";
	private String selectedTask = "";
	
	private Table scheduleTable;
	
	/**
	 * Constructor for the request chore screen.
	 *  
	 * @param familyName
	 * @param childName
	 * @throws ParseException
	 */
	protected RequestChore(String familyName, String childName) throws ParseException {
		
		familyUsername = familyName;
		childUser = childName;
		
		Display display = Display.getDefault();
		
		logo = display.getSystemImage(SWT.ICON_QUESTION);
		Shell shell = createShell(display);
		shell.open();
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				shell.dispose();
				try {
					@SuppressWarnings("unused")
					ChildScreen childScreen  = new ChildScreen(familyUsername, childUser);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
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
	 * Creates the main request UI shell
	 * 
	 * @param display
	 * @return
	 * @throws ParseException
	 */
	private Shell createShell(Display display) throws ParseException {
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(750, 750);
		shell.setText("Request Chores");
		shell.setImage(logo);
		
		Label selectTask = new Label(shell, SWT.NONE);
		selectTask.setText("Select Task:");
		
		Combo taskCombo = new Combo(shell, SWT.NONE);
		ArrayList<Object> send2 = new ArrayList<Object>();
		send2.add(9913);
		send2.add(familyUsername);
		ArrayList<Object> response2 = client.clientConnection(send2);
		for (int i = 0; i < response2.size(); i++) {
			taskCombo.add((String)response2.get(i));
		}
		
		Label selectDateTime = new Label(shell, SWT.NONE);
		selectDateTime.setText("Select a Time:");
		
		timeCombo = new Combo(shell, SWT.NONE);
		timeCombo.setText("Time");
		
		createDateTimeCombos();
		
		Label calendarLabel = new Label(shell, SWT.NONE);
		calendarLabel.setText("Select a Date:");
		Label ErrorMessage = new Label(shell, SWT.NONE);
		ErrorMessage.setText("Oops, something went wrong.");
		Color Red = display.getSystemColor(SWT.COLOR_RED);
		ErrorMessage.setForeground(Red);
		ErrorMessage.setVisible(false);
		DateTime calendar = new DateTime (shell, SWT.CALENDAR);
		
		Button submitButton = new Button(shell, SWT.NONE);
		submitButton.setText("Submit");
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		
		Group dailySchedule = new Group(shell, SWT.NONE);
		dailySchedule.setText("Selected User's Schedule:");
		
		/*
		 * ------------------------------------------ Layout setup
		 */
		
		scheduleTable = new Table(dailySchedule, SWT.BORDER | SWT.V_SCROLL
		        | SWT.H_SCROLL);
		
		createScheduleGroup(dailySchedule);
		
		initializeTable(calendar);
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		
		data = new FormData();
		data.top = new FormAttachment(10, 5);
		data.left = new FormAttachment(5, 0);	
		
		data = new FormData();
		data.bottom = new FormAttachment(10, -5);
		data.left = new FormAttachment(5, 10);
		selectTask.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(selectTask, 5);
		data.left = new FormAttachment(5, 10);
		taskCombo.setLayoutData(data);
		taskCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				selectedTask = taskCombo.getText();
			}
		});
		
		data = new FormData();
		data.bottom = new FormAttachment(taskCombo, -5);
		data.left = new FormAttachment(taskCombo, 10);
		selectDateTime.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(selectDateTime, 5);
		data.left = new FormAttachment(taskCombo, 10);
		timeCombo.setLayoutData(data);
		timeCombo.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("deprecation")
			public void widgetSelected(SelectionEvent event) {
				if(timeCombo.getText().length() == 7)
				{
					int hour;
					if(timeCombo.getText().contains("PM"))
					{
						hour = Integer.parseInt(timeCombo.getText().substring(0, 1)) + 12;
					}
					else
					{
						hour = Integer.parseInt(timeCombo.getText().substring(0, 1));
					}
					int minutes = Integer.parseInt(timeCombo.getText().substring(2, 4));
					int seconds = 00;
					Time t = new Time(hour, minutes, seconds);
					selectedTime = t;
				}
				else
				{
					int hour;
					if(timeCombo.getText().contains("PM") && !timeCombo.getText().substring(0, 2).equals("12"))
					{
						hour = Integer.parseInt(timeCombo.getText().substring(0, 2)) + 12;
					}
					else
					{
						hour = Integer.parseInt(timeCombo.getText().substring(0, 2));
					}
					int minutes = Integer.parseInt(timeCombo.getText().substring(3, 5));
					int seconds = 00;
					Time t = new Time(hour, minutes, seconds);
					selectedTime = t;
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(15, 10);
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
			    
			    selectedDate = year + "/" + month + "/" + day;
			    
			    //TODO fill the schedule with the selected user's data or all user's data if no user is selected
			    
			    String dateString = String.format("%d-%d-%d", Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
			    Date d = null;
				try {
					d = new SimpleDateFormat("yyyy-M-d").parse(dateString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(d);
			    
			    ArrayList<Object> send = new ArrayList<Object>();
				send.add(3);
				send.add(familyUsername);
				send.add(selectedDate);
				send.add(dayOfWeek);
				ArrayList<Object> response = client.clientConnection(send);
				createSchedule(response);
				
				calendar.getParent().setRedraw(true);
			}
		});
		
		data = new FormData();
		data.left = new FormAttachment(calendar, 10);
		data.top = new FormAttachment(calendarLabel, 0);
		dailySchedule.setLayoutData(data);
		
		initializeTable(calendar);
		
		data = new FormData();
		data.top = new FormAttachment(calendar, 10);
		data.left = new FormAttachment(5, 0);
		submitButton.setLayoutData(data);
		data = new FormData();
		data.top = new FormAttachment(submitButton, 10);
		data.left = new FormAttachment(5, 0);
		ErrorMessage.setLayoutData(data);
		submitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
					 ArrayList<Object> send = new ArrayList<Object>();
					 send.add(9909);
					 send.add(familyUsername);
					 ArrayList<Object> info = new ArrayList<Object>();
					 info.add(childUser);
//					 info.add(familyUsername);
					 info.add(selectedTask);
					 info.add(selectedDate);
					 info.add("Request");
					 //TODO eventually will let user put in a personalized message for error check
					 info.add("I would like to do this chore");
					 info.add(selectedTime);
					 send.add(info);
					 send.add(null);
				
					 ArrayList<Object> response = client.clientConnection(send);
					 if( !(boolean)response.get(0))
					 {
						 ErrorMessage.setVisible(true);
					 }
					else
					{
						shell.dispose();
					}
				}
				
		});
		
		data = new FormData();
		data.top = new FormAttachment(calendar, 10);
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
	 * Initializes the schedule table with the current day's chores.
	 * 
	 * @param calendar
	 * @throws ParseException
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
	    selectedDate= date;
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
	 * Creates the headers for the daily schedule
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

	/**
	 * Creates the schedule for a selected date
	 * 
	 * @param data
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	protected void createSchedule(ArrayList<Object> data) {
		scheduleTable.removeAll();
		
		ArrayList<Integer> times = new ArrayList<Integer>();
		
		if (data != null && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				Time temp = (Time)(((ArrayList<Object>)data.get(i)).get(3));
				
				int time = temp.getHours();
				
				times.add(time);
				
			}
		
		
			for (int i = 8; i < 21; i++) {
				
				for (int j = 0; j < times.size(); j++) {
					
					if (times.get(j) == i) {
						createItem(i, j, true, data);
						break;
					} else if (j == times.size() - 1) {
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
		
		scheduleTable.setBounds(25, 25, 300, 285);
		
	}
	/**
	 * Creates an empty daily schedule.
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
	 * Uses the given data array, position j and time i to create a Chore item within the Schedule table, or creates an empty item if isJob is false.
	 * 
	 * @param i
	 * @param j
	 * @param isJob
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	private void createItem(int i, int j, boolean isJob, ArrayList<Object> data) {
		
		TableItem item = new TableItem(scheduleTable, SWT.NULL);
		
		if ((isJob)) {
			
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
	 * Fills the time combo.
	 */
	private void createDateTimeCombos() {
		
		timeCombo.add("8:00 AM");
		timeCombo.add("9:00 AM");
		timeCombo.add("10:00 AM");
		timeCombo.add("11:00 AM");
		timeCombo.add("12:00 PM");
		timeCombo.add("1:00 PM");
		timeCombo.add("2:00 PM");
		timeCombo.add("3:00 PM");
		timeCombo.add("4:00 PM");
		timeCombo.add("5:00 PM");
		timeCombo.add("6:00 PM");
		timeCombo.add("7:00 PM");
		timeCombo.add("8:00 PM");
		
	}
}