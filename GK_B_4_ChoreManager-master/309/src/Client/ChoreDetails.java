package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for displaying the details of a selected chore.
 * Class contacts the database via client/server code to pull a chore's details, report issues, and stop/start chores.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class ChoreDetails {

	static Image logo;
	
	private String familyUsername;
	private String username;
	private String chore;
	private String choreDate;
	private String dateString;
	private String choreTime;
	private Boolean parent;
	private Double allowance;
	private String description;
	private String currentUsername;
	private ArrayList<Object> prereqs; 
	
	private ArrayList<Object> data = new ArrayList<Object>();
	
	/**
	 * Constructor for the ChoreDetails screen.
	 * 
	 * @param familyName
	 * @param selectedUser
	 * @param choreName
	 * @param date
	 * @param time
	 * @param currentUser
	 * @param isParent
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected ChoreDetails(String familyName, String selectedUser, String choreName, String date, String time, String currentUser, Boolean isParent, Double choreAllowance) throws ParseException {
		
		familyUsername = familyName;
		username = selectedUser;
		parent = isParent;
		chore = choreName;
		choreTime = time;
		choreDate = parseDate(date);
		dateString = date;
		currentUsername = currentUser;
		allowance = choreAllowance;
		
		if(chore.equals("Unavailable"))
		{
			return;
		}
		
		//Retrieve details for the requested chore.
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9915);
		send.add(familyUsername);
		send.add(chore);
		data = client.clientConnection(send);
		
		description = (String)data.get(1);
		prereqs = (ArrayList<Object>)data.get(2);
		
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
	 * Parses the date from a given date String.
	 * 
	 * @param date
	 * 
	 * @return The parsed date string
	 */
	private String parseDate(String date) throws ParseException {
		String parsedDate = "";
		
		String pattern = "yyyy/MM/dd";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		
		Date newDate = format.parse(date);
		
		parsedDate = newDate.toString();
		
		return parsedDate;
	}

	/**
	 * Creates the main ChoreDetails UI shell
	 * 
	 * @param display
	 * 
	 * @return The created shell
	 */
	private Shell createShell(Display display) {
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(500, 750);
		shell.setText("Chore Details");
		shell.setImage(logo);
		
		Label choreLabel = new Label(shell, SWT.NONE);
		choreLabel.setText(username + ": " + chore);
		
		Label dateLabel = new Label(shell, SWT.NONE);
		dateLabel.setText(choreDate);
		
		Label dueLabel = new Label(shell, SWT.NONE);
		dueLabel.setText("Due By: " + choreTime);
		
		Label descLabel = new Label(shell, SWT.NONE);
		descLabel.setText("Full Description:");
		
		Label desc = new Label(shell, SWT.WRAP);
		desc.setSize(200, 100);
		desc.setText(description);
		
		Label prereqLabel = new Label(shell, SWT.NONE);
		prereqLabel.setText("Prerequisites:");
		
		Label prereq1 = new Label(shell, SWT.NONE);;
		prereq1.setVisible(false);
		Label prereq2 = new Label(shell, SWT.NONE);;
		prereq2.setVisible(false);
		Label prereq3 = new Label(shell, SWT.NONE);;
		prereq3.setVisible(false);
		Label prereq4 = new Label(shell, SWT.NONE);;
		prereq4.setVisible(false);
		Label prereq5 = new Label(shell, SWT.NONE);;
		prereq5.setVisible(false);
		
		/*
		 * 
		 */
		
		switch (prereqs.size()) {
		case 1:
			prereq1.setText("1.) " + (String)prereqs.get(0));
			prereq1.setVisible(true);
			break;
		case 2:
			prereq1.setText("1.) " + (String)prereqs.get(0));
			prereq1.setVisible(true);
			prereq2.setText("2.) " + (String)prereqs.get(1));
			prereq2.setVisible(true);
			break;
		case 3:
			prereq1.setText("1.) " + (String)prereqs.get(0));
			prereq1.setVisible(true);
			prereq2.setText("2.) " + (String)prereqs.get(1));
			prereq2.setVisible(true);
			prereq3.setText("3.) " + (String)prereqs.get(2));
			prereq3.setVisible(true);
			break;
		case 4:
			prereq1.setText("1.) " + (String)prereqs.get(0));
			prereq1.setVisible(true);
			prereq2.setText("2.) " + (String)prereqs.get(1));
			prereq2.setVisible(true);
			prereq3.setText("3.) " + (String)prereqs.get(2));
			prereq3.setVisible(true);
			prereq4.setText("4.) " + (String)prereqs.get(3));
			prereq4.setVisible(true);
			break;
		case 5:
			prereq1.setText("1.) " + (String)prereqs.get(0));
			prereq1.setVisible(true);
			prereq2.setText("2.) " + (String)prereqs.get(1));
			prereq2.setVisible(true);
			prereq3.setText("3.) " + (String)prereqs.get(2));
			prereq3.setVisible(true);
			prereq4.setText("4.) " + (String)prereqs.get(3));
			prereq4.setVisible(true);
			prereq5.setText("5.) " + (String)prereqs.get(4));
			prereq5.setVisible(true);
			break;
		default:
			
		}
		
		Label allowanceLabel = new Label(shell, SWT.NONE);
		allowanceLabel.setText("Allowance:");
		
		Label allowanceNum = new Label(shell, SWT.NONE);
		allowanceNum.setText("$" + allowance);
		
		Button startButton = new Button(shell, SWT.PUSH);
		startButton.setText("Start");
		
		Button stopButton = new Button(shell, SWT.PUSH);
		stopButton.setText("Stop");
		
		if(parent) {
			startButton.setVisible(false);
			stopButton.setVisible(false);
		} else {
			startButton.setVisible(true);
			stopButton.setVisible(true);
		}
		
		Button returnButton = new Button(shell, SWT.PUSH);
		returnButton.setText("Return");
		
		Button reportButton = new Button(shell, SWT.PUSH);
		reportButton.setText("Report a Problem");
		
		if(username.equals(currentUsername)) {
			reportButton.setVisible(true);
		} else {
			reportButton.setVisible(false);
		}
		
		/*
		 * ---------------------------------------------------------------------------------
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		choreLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(60, 0);
		dateLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(dateLabel, 10);
		data.left = new FormAttachment(60, 0);
		dueLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(choreLabel, 15);
		data.left = new FormAttachment(5, 0);
		descLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(descLabel, 5);
		data.left = new FormAttachment(5, 0);
		desc.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(desc, 15);
		data.left = new FormAttachment(5, 0);
		prereqLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(dueLabel, 15);
		data.left = new FormAttachment(60, 0);
		reportButton.setLayoutData(data);
		reportButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Shell rshell = reportShell(display);
				rshell.open();
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(prereqLabel, 5);
		data.left = new FormAttachment(5, 0);
		prereq1.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(prereq1, 5);
		data.left = new FormAttachment(5, 0);
		prereq2.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(prereq2, 5);
		data.left = new FormAttachment(5, 0);
		prereq3.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(prereq3, 5);
		data.left = new FormAttachment(5, 0);
		prereq4.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(prereq4, 5);
		data.left = new FormAttachment(5, 0);
		prereq5.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(prereq5, 15);
		data.left = new FormAttachment(5, 0);
		allowanceLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(allowanceLabel, 5);
		data.left = new FormAttachment(5, 0);
		allowanceNum.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(allowanceNum, 20);
		data.left = new FormAttachment(5, 0);
		returnButton.setLayoutData(data);
		returnButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(allowanceNum, 20);
		data.left = new FormAttachment(returnButton, 10);
		startButton.setLayoutData(data);
		startButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("deprecation")
			public void widgetSelected(SelectionEvent event) {
				Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
				Time time = new Time(t.getHours(), t.getMinutes(), t.getSeconds());
				ArrayList<Object> send = new ArrayList<Object>();
				send.add(9904);
				send.add(username);
				send.add(familyUsername);
				send.add(chore);
				send.add(dateString);
				send.add(time);
				ArrayList<Object> response = client.clientConnection(send);
				 if( !(boolean)response.get(0))
				 {
					 //Inform user of error
				 }
				else
				{
					shell.dispose();
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(allowanceNum, 20);
		data.left = new FormAttachment(startButton, 10);
		stopButton.setLayoutData(data);
		stopButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("deprecation")
			public void widgetSelected(SelectionEvent event) {
				Timestamp t = new Timestamp(Calendar.getInstance().getTime().getTime());
				Date date = new Date(t.getTime());
		        SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd");
		        String dateText = df2.format(date);
				Time time = new Time(t.getHours(), t.getMinutes(), t.getSeconds());
				ArrayList<Object> send = new ArrayList<Object>();
				send.add(9905);
				send.add(username);
				send.add(familyUsername);
				send.add(chore);
				send.add(dateString);
				send.add(time);
				send.add(dateText);
				ArrayList<Object> response = client.clientConnection(send);
				 if( !(boolean)response.get(0))
				 {
					 //Inform user of error
				 }
				else
				{
					shell.dispose();
				}
			}
		});
		
		return shell;
	}

	/**
	 * Creates the shell for reporting an issue with completing a chore.
	 * 
	 * @param display
	 * @return The created UI shell.
	 */
	private Shell reportShell(Display display) {
		Shell rshell = new Shell(display);
		FormLayout layout = new FormLayout();
		rshell.setLayout(layout);
		rshell.setSize(300, 500);
		rshell.setText("Report a Problem");
		rshell.setImage(logo);
		
		Label mainLabel = new Label(rshell, SWT.NONE);
		mainLabel.setText("Report a Problem");
		
		Label enterLabel = new Label(rshell, SWT.NONE);
		enterLabel.setText("Please explain the problem you are having:");
		Text enterText = new Text(rshell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		enterText.setText("");
		
		Button submitButton = new Button(rshell, SWT.PUSH);
		submitButton.setText("Submit");
		
		Button cancelButton = new Button(rshell, SWT.PUSH);
		cancelButton.setText("Cancel");
		
		/*
		 * -------------------------------------------------------------------------------------------------
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		mainLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 10);
		data.left = new FormAttachment(5, 0);
		enterLabel.setLayoutData(data);
		
		data = new FormData(200, 200);
		data.top = new FormAttachment(enterLabel, 5);
		data.left = new FormAttachment(5, 0);
		enterText.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(enterText, 20);
		data.left = new FormAttachment(5, 0);
		submitButton.setLayoutData(data);
		submitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (enterText.getText() != "") {
					
					Time time;
					if(choreTime.length() == 7)
					{
						int hour;
						if(choreTime.contains("PM"))
						{
							hour = Integer.parseInt(choreTime.substring(0, 1)) + 12;
						}
						else
						{
							hour = Integer.parseInt(choreTime.substring(0, 1));
						}
						int minutes = Integer.parseInt(choreTime.substring(2, 4));
						int seconds = 00;
						Time t = new Time(hour, minutes, seconds);
						time = t;
					}
					else
					{
						int hour;
						if(choreTime.contains("PM") && !choreTime.substring(0, 2).equals("12"))
						{
							hour = Integer.parseInt(choreTime.substring(0, 2)) + 12;
						}
						else
						{
							hour = Integer.parseInt(choreTime.substring(0, 2));
						}
						int minutes = Integer.parseInt(choreTime.substring(3, 5));
						int seconds = 00;
						Time t = new Time(hour, minutes, seconds);
						time = t;
					}
					
					ArrayList<Object> send = new ArrayList<Object>();
					ArrayList<Object> info = new ArrayList<Object>();
					send.add(9909);
					send.add(familyUsername);
					info.add(username);
					info.add(chore);
					info.add(dateString);
					info.add("Problem");
					info.add(enterText.getText());
					info.add(time);
					send.add(info);
					send.add(null);
					ArrayList<Object> result = client.clientConnection(send);
					
					if((Boolean)result.get(0) == true) {
						rshell.dispose();
					} else {
						//TODO error message here
					}
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(enterText, 20);
		data.left = new FormAttachment(submitButton, 10);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				rshell.dispose();
			}
		});
		
		return rshell;
	}
	
}