package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for displaying the Notifications screen.
 * Class contacts the database via client/server code to pull notifications accessible to a user as well as acknowledging and carrying out notification functions.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class NotificationsScreen {

	static Image logo;
	
	private String familyUsername;
	private String username;
	private Boolean isParent;
	
	private ArrayList<Group> notifications = new ArrayList<Group>();
	private ArrayList<Object> data = new ArrayList<Object>();
	
	/**
	 * Constructor for the screen - pulls different types of notifications for a parent user. 
	 * 
	 * @param familyName
	 * @param memberName
	 * @param parent
	 */
	protected NotificationsScreen(String familyName, String memberName, Boolean parent) {
		
		familyUsername = familyName;
		username = memberName;
		isParent = parent;
		
		if (isParent) {
			ArrayList<Object> send = new ArrayList<Object>();
			send.add(9910);
			send.add(familyUsername);
			data = client.clientConnection(send);
		} else {
			ArrayList<Object> send = new ArrayList<Object>();
			send.add(9917);
			send.add(familyUsername);
			send.add(username);
			data = client.clientConnection(send);
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
	 * Creates the main Notifications UI shell
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
		shell.setText("Notifications");
		shell.setImage(logo);
		
		ScrolledComposite sc1 = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc1.setLayout(layout);
		FormData test = new FormData();
		test.top = new FormAttachment(1, 0);
		test.left = new FormAttachment(1, 0);
		sc1.setLayoutData(test);
		sc1.setExpandVertical(true);
		sc1.setExpandHorizontal(true);
		
	    Composite comp = new Composite(sc1, SWT.NONE);
	    sc1.setContent(comp);
	    FormLayout layout2 = new FormLayout();
	    comp.setLayout(layout2);
		
	    Label mainLabel = new Label(comp, SWT.NONE);
		mainLabel.setText("Notifications");
		
		Button returnButton = new Button(comp, SWT.PUSH);
		returnButton.setText("Return");
	    
		/*
		 * 
		 */
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9914);
		send.add(familyUsername);
		send.add(username);
		ArrayList<Object> response = client.clientConnection(send);
		
		int numNotifications = (Integer) response.get(0);
		
		for (int i = 0; i < numNotifications; i++) {
			Group newGroup = newNotification(display, comp, i);
			
			if (notifications.isEmpty()) {
				FormData data = new FormData();
				data.top = new FormAttachment(mainLabel, 2);
				data.left = new FormAttachment(1, 0);
				newGroup.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(newGroup, 2);
				data.left = new FormAttachment(1, 0);
				returnButton.setLayoutData(data);
				
				shell.layout();
				
			} else {
				FormData data = new FormData();
				data.top = new FormAttachment(notifications.get(notifications.size() - 1), 2);
				data.left = new FormAttachment(1, 0);
				newGroup.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(newGroup, 2);
				data.left = new FormAttachment(1, 0);
				returnButton.setLayoutData(data);
				
				shell.layout();
				
			}
			
			notifications.add(newGroup);
			newGroup.setText("" + (notifications.size()));
			comp.setSize(comp.computeSize(250, 750));
			comp.layout();
			sc1.layout();
		}
		
		/*
		 * ---------------------------------------------------------------------------------------------------------
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(1, 0);
		data.left = new FormAttachment(1, 0);
		mainLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 2);
		data.left = new FormAttachment(1, 0);
		returnButton.setLayoutData(data);
		returnButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});
		
		return shell;
	}
	
	Boolean improper = false;
	Double allowance = 0.0;
	Double origAllowance = 0.0;
	/**
	 * Creates a group for a given notification - also adding the button functionality for each type of notification.
	 * 
	 * @param display
	 * @param comp
	 * @param i
	 * @return The created Notification group.
	 */
	@SuppressWarnings("unchecked")
	private Group newNotification(Display display, Composite comp, Integer i) {
		Group newGroup = new Group(comp, SWT.NONE);
		newGroup.setLayout(new FormLayout());
		
		ArrayList<Object> notif = (ArrayList<Object>) data.get(i);
		String notifType;
		String parsedTime;
		String parsedTime2 = null;
		String parsedDate;
		String parsedDate2 = null;
		
		if(notif.size() == 7) {
			notifType = (String) notif.get(4);
			
			Time time = (Time)notif.get(6);
			parsedTime = parseTime(time);
			
			Date date = (Date)notif.get(3);
			parsedDate = parseDate(date);
			
			String selectedTask = (String) notif.get(2);
			ArrayList<Object> send = new ArrayList<Object>();
			send.add(9915);
			send.add(familyUsername);
			send.add(selectedTask);
			ArrayList<Object> response = client.clientConnection(send);
			allowance = (Double)response.get(0);
			origAllowance = allowance;
			
		} else if (notif.size() == 9 || notif.size() == 10) {
			notifType = (String) notif.get(3);
			
			Time time1 = (Time)notif.get(8);
			parsedTime = parseTime(time1); //Trader's time
			
			Time time2 = (Time)notif.get(5);
			parsedTime2 = parseTime(time2); //Tradee's time
			
			Date date = (Date)notif.get(7); //Trader's date
			parsedDate = parseDate(date);
			
			Date date2 = (Date)notif.get(2); //Tradee's date
			parsedDate2 = parseDate(date2);
		} else {
			notifType = (String) notif.get(2);
			
			Time time = (Time)notif.get(4);
			parsedTime = parseTime(time);
			
			Date date = (Date)notif.get(1);
			parsedDate = parseDate(date);
		}
		
		Label typeLabel = new Label(newGroup, SWT.NONE);
		typeLabel.setText(notifType);
		
		Label userLabel = new Label(newGroup, SWT.NONE);
		
		Label userLabel2 = new Label(newGroup, SWT.NONE);
		
		Label choreLabel = new Label(newGroup, SWT.NONE);
		
		Label choreLabel2 = new Label(newGroup, SWT.NONE);
		
		Label messageLabel = new Label(newGroup, SWT.NONE);
		
		Button button1 = new Button(newGroup, SWT.PUSH);
		
		Button button2 = new Button(newGroup, SWT.PUSH);
		/*
		 * Creates the notification based on notification type.
		 */
		switch(notifType) {
		case "Problem":
		case "Request":
			Label allowanceLabel = new Label(newGroup, SWT.NONE);
			Text allowanceText = new Text(newGroup, SWT.SINGLE | SWT.BORDER);
			allowanceText.setVisible(false);
			
			userLabel.setText((String) notif.get(0));
			choreLabel.setText((String) notif.get(2) + ": " + parsedDate + " at " + parsedTime);
			messageLabel.setText((String) notif.get(5));
			button1.setText("Accept");
			button2.setText("Deny");
			
			FormData data = new FormData();
			data.top = new FormAttachment(1, 0);
			data.left = new FormAttachment(1, 0);
			typeLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(typeLabel, 2);
			data.left = new FormAttachment(1, 0);
			userLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(userLabel, 2);
			data.left = new FormAttachment(1, 0);
			choreLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(choreLabel, 2);
			data.left = new FormAttachment(1, 0);
			messageLabel.setLayoutData(data);
			
			if (notifType.equals("Request")) {
				data = new FormData();
				data.top = new FormAttachment(messageLabel, 2);
				data.left = new FormAttachment(1, 0);
				allowanceLabel.setLayoutData(data);
				allowanceLabel.setText("Enter an allowance (Leave blank for default)");
				
				data = new FormData();
				data.top = new FormAttachment(messageLabel, 2);
				data.left = new FormAttachment(allowanceLabel, 5);
				allowanceText.setLayoutData(data);
				allowanceText.setVisible(true);
				allowanceText.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
							try {
								improper = false;
								allowance = Double.parseDouble(allowanceText.getText());
							} catch (NumberFormatException n) {
								if (allowanceText.getText().equals("")) {
									improper = false;
									allowance = origAllowance;
								} else {
									improper = true;
									System.out.println("Improper input");
								}
							}
					}	
				});
				
				data = new FormData();
				data.top = new FormAttachment(allowanceLabel, 2);
				data.left = new FormAttachment(1, 0);
				button1.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(allowanceLabel, 2);
				data.left = new FormAttachment(button1, 5);
				button2.setLayoutData(data);
				
			} else {
				data = new FormData();
				data.top = new FormAttachment(messageLabel, 2);
				data.left = new FormAttachment(1, 0);
				button1.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(messageLabel, 2);
				data.left = new FormAttachment(button1, 5);
				button2.setLayoutData(data);
			}
			
			//Accepts the problem
			button1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (!improper) {
						//Remove the notification + modify the chore + notify the user
						ArrayList<Object> send = new ArrayList<Object>();
						ArrayList<Object> info = new ArrayList<Object>();
						send.add(9911);
						send.add(familyUsername);
						
						info.add(notif.get(0));
						info.add(notif.get(2));
						info.add(parsedDate);
						info.add(notif.get(4));
						info.add(notif.get(6));
						
						send.add(info);
						send.add(null);
						send.add(1);
						ArrayList<Object> response = client.clientConnection(send);
						
						if ((Boolean)response.get(0) && notifType.equals("Problem")) {
							//Modify the chore
							ModifyChore modify = new ModifyChore(display, familyUsername, username, (String) notif.get(0), (String) notif.get(2), parsedDate, parsedTime);
							newGroup.getShell().dispose();
						} else if ((Boolean)response.get(0) && notifType.equals("Request")) {
								//Assign the chore
							Time selectedTime;
							if(parsedTime.length() == 7)
							{
								int hour;
								if(parsedTime.contains("PM"))
								{
									hour = Integer.parseInt(parsedTime.substring(0, 1)) + 12;
								}
								else
								{
									hour = Integer.parseInt(parsedTime.substring(0, 1));
								}
								int minutes = Integer.parseInt(parsedTime.substring(2, 4));
								int seconds = 00;
								Time t = new Time(hour, minutes, seconds);
								selectedTime = t;
							}
							else
							{
								int hour;
								if(parsedTime.contains("PM") && !parsedTime.substring(0, 2).equals("12"))
								{
									hour = Integer.parseInt(parsedTime.substring(0, 2)) + 12;
								}
								else
								{
									hour = Integer.parseInt(parsedTime.substring(0, 2));
								}
								int minutes = Integer.parseInt(parsedTime.substring(3, 5));
								int seconds = 00;
								Time t = new Time(hour, minutes, seconds);
								selectedTime = t;
							}
							
								ArrayList<Object> send2 = new ArrayList<Object>();
								send2.add(9901);
								send2.add((String) notif.get(0));
								send2.add(familyUsername);
								send2.add((String) notif.get(2));
								send2.add(selectedTime);
								send2.add(parsedDate);
								send2.add(allowance);
								ArrayList<Object> response2 = client.clientConnection(send2);
								if( !(boolean)response2.get(0))
								{
									//Inform user of error
								}
									else {
										newGroup.getShell().dispose();						
									}
							} else {
								//Error checking - error with call
							}
					} else {
						//Error with input
					}
				}
			});
			
			//Denies the problem
			button2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					//Remove the notification
					ArrayList<Object> send = new ArrayList<Object>();
					ArrayList<Object> info = new ArrayList<Object>();
					send.add(9911);
					send.add(familyUsername);
					
					info.add(notif.get(0));
					info.add(notif.get(2));
					info.add(parsedDate);
					info.add(notif.get(4));
					info.add(notif.get(6));
					
					send.add(info);
					send.add(null);
					send.add(0);
					ArrayList<Object> response = client.clientConnection(send);
					
					if ((Boolean)response.get(0)) {
						newGroup.getShell().dispose();
					} else {
						//Error checking
					}
				}
			});
			
			break;
		case "Trade":
			if(isParent)
			{
				userLabel.setText((String) notif.get(0) + " wants to trade:");
				choreLabel.setText((String) notif.get(6) + " on: " + parsedDate + " at: " + parsedTime + " with:"); //Trader's Job
				userLabel2.setText((String)notif.get(9) + "'s chore:");
				choreLabel2.setText((String) notif.get(1)+ " on: " + parsedDate2 + " at: " + parsedTime2 + "."); //Tradee's Job
				button1.setText("Approve");
				button2.setText("Decline");
				String dateTemp = parsedDate;
				String dateTemp2 = parsedDate2;
				
				data = new FormData();
				data.top = new FormAttachment(1, 0);
				data.left = new FormAttachment(1, 0);
				typeLabel.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(typeLabel, 2);
				data.left = new FormAttachment(1, 0);
				userLabel.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(userLabel, 2);
				data.left = new FormAttachment(1, 0);
				choreLabel.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(choreLabel, 2);
				data.left = new FormAttachment(1, 0);
				userLabel2.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(userLabel2, 2);
				data.left = new FormAttachment(1, 0);
				choreLabel2.setLayoutData(data);
				
				data = new FormData();
				data.top = new FormAttachment(choreLabel2, 2);
				data.left = new FormAttachment(1, 0);
				button1.setLayoutData(data);
				//Approve the trade
				button1.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ArrayList<Object> send = new ArrayList<Object>();
						ArrayList<Object> mem1 = new ArrayList<Object>();
						ArrayList<Object> mem2 = new ArrayList<Object>();
						send.add(9911);
						send.add(familyUsername);
						mem2.add(notif.get(9));
						mem2.add(notif.get(1));
						mem2.add(dateTemp2);
						mem2.add("Trade");
						mem2.add(notif.get(5));
						send.add(mem2);
						mem1.add(notif.get(0));
						mem1.add(notif.get(6));
						mem1.add(dateTemp);
						mem1.add("Trade");
						mem1.add(notif.get(8));
						send.add(mem1);
						send.add(1);
						ArrayList<Object> response = client.clientConnection(send);
						if(!(Boolean)response.get(0))
						{
							//Error
						}
						else
						{
							newGroup.getShell().dispose();
						}
					}
				});
				
				data = new FormData();
				data.top = new FormAttachment(choreLabel2, 2);
				data.left = new FormAttachment(button1, 5);
				button2.setLayoutData(data);
				//Decline the trade
				button2.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						ArrayList<Object> send = new ArrayList<Object>();
						ArrayList<Object> mem1 = new ArrayList<Object>();
						ArrayList<Object> mem2 = new ArrayList<Object>();
						send.add(9911);
						send.add(familyUsername);
						mem2.add(notif.get(9));
						mem2.add(notif.get(1));
						mem2.add(dateTemp2);
						mem2.add("Trade");
						mem2.add(notif.get(5));
						send.add(mem2);
						mem1.add(notif.get(0));
						mem1.add(notif.get(6));
						mem1.add(dateTemp);
						mem1.add("Trade");
						mem1.add(notif.get(8));
						send.add(mem1);
						send.add(0);
						ArrayList<Object> response = client.clientConnection(send);
						if(!(Boolean)response.get(0))
						{
							//Error
						}
						else
						{
							newGroup.getShell().dispose();
						}
					}
				});
			}
			
			break;
		case "MemTrade":
			userLabel.setText((String) notif.get(0) + " wants to trade:");
			choreLabel.setText((String) notif.get(6) + " on: " + parsedDate + " at: " + parsedTime + " for:"); //Trader's Job
			choreLabel2.setText((String) notif.get(1)+ " on: " + parsedDate2 + " at: " + parsedTime2 + "."); //Tradee's Job
			messageLabel.setText((String) notif.get(4)); //"Would you like to trade?"
			button1.setText("Accept Trade");
			button2.setText("Deny Trade");
			String dateTemp = parsedDate;
			String dateTemp2 = parsedDate2;
			
			data = new FormData();
			data.top = new FormAttachment(1, 0);
			data.left = new FormAttachment(1, 0);
			typeLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(typeLabel, 2);
			data.left = new FormAttachment(1, 0);
			userLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(userLabel, 2);
			data.left = new FormAttachment(1, 0);
			choreLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(choreLabel, 2);
			data.left = new FormAttachment(1, 0);
			choreLabel2.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(choreLabel2, 2);
			data.left = new FormAttachment(1, 0);
			messageLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(messageLabel, 2);
			data.left = new FormAttachment(1, 0);
			button1.setLayoutData(data);
			//Accept the trade
			button1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ArrayList<Object> mem1 = new ArrayList<Object>();
					ArrayList<Object> mem2 = new ArrayList<Object>();
					ArrayList<Object> send = new ArrayList<Object>();
					send.add(9911);
					send.add(familyUsername);
					mem1.add(username);
					mem1.add(notif.get(1));
					mem1.add(dateTemp2);
					mem1.add("MemTrade");
					mem1.add(notif.get(5));
					send.add(mem1);
					mem2.add(notif.get(0));
					mem2.add(notif.get(6));
					mem2.add(dateTemp);
					mem2.add("MemTrade");
					mem2.add(notif.get(8));
					send.add(mem2);
					send.add(1);
					ArrayList<Object> response = client.clientConnection(send);
					if(!(Boolean)response.get(0))
					{
						//Error
					}
					else
					{
						newGroup.getShell().dispose();
					}
				}
			});
			
			data = new FormData();
			data.top = new FormAttachment(messageLabel, 2);
			data.left = new FormAttachment(button1, 5);
			button2.setLayoutData(data);
			//Deny the trade
			button2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ArrayList<Object> mem1 = new ArrayList<Object>();
					ArrayList<Object> mem2 = new ArrayList<Object>();
					ArrayList<Object> send = new ArrayList<Object>();
					send.add(9911);
					send.add(familyUsername);
					mem1.add(username);
					mem1.add(notif.get(1));
					mem1.add(dateTemp2);
					mem1.add("MemTrade");
					mem1.add(notif.get(5));
					send.add(mem1);
					mem2.add(notif.get(0));
					mem2.add(notif.get(6));
					mem2.add(dateTemp);
					mem2.add("MemTrade");
					mem2.add(notif.get(8));
					send.add(mem2);
					send.add(0);
					ArrayList<Object> response = client.clientConnection(send);
					if(!(Boolean)response.get(0))
					{
						//Error
					}
					else
					{
						newGroup.getShell().dispose();
					}
				}
			});
			
			break;
		case "Approved":
		case "Denied":
			messageLabel.setText((String) notif.get(3) + " for:");
			choreLabel.setText((String) notif.get(0) + " on: " + parsedDate + " at: " + parsedTime);
			button1.setText("Okay");
			
			data = new FormData();
			data.top = new FormAttachment(1, 0);
			data.left = new FormAttachment(1, 0);
			typeLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(typeLabel, 2);
			data.left = new FormAttachment(1, 0);
			messageLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(messageLabel, 2);
			data.left = new FormAttachment(1, 0);
			choreLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(choreLabel, 2);
			data.left = new FormAttachment(1, 0);
			button1.setLayoutData(data);
			//Close the notification
			button1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ArrayList<Object> send = new ArrayList<Object>();
					ArrayList<Object> mem1 = new ArrayList<Object>();
					send.add(9911);
					send.add(familyUsername);
					mem1.add(username);
					mem1.add(notif.get(0));
					mem1.add(parsedDate);
					mem1.add(notifType);
					mem1.add(notif.get(4));
					send.add(mem1);
					send.add(null);
					send.add(1);
					ArrayList<Object> response = client.clientConnection(send);
					if(!(Boolean)response.get(0))
					{
						//Error
					}
					else
					{
						newGroup.getShell().dispose();
					}
				}
			});
			
			button2.setVisible(false);
			
			break;
		case "Late":
			userLabel.setText((String) notif.get(0));
			choreLabel.setText((String) notif.get(2) + ": " + parsedDate + " at " + parsedTime);
			messageLabel.setText((String) notif.get(5));
			button1.setText("No");
			button2.setText("Reduce Allowance");
			
			Double allowance2 = retrieveAllowance(parsedDate, (String) notif.get(0), (String) notif.get(2));
			
			Label reduceAllowance = new Label(newGroup, SWT.NONE);
			reduceAllowance.setText("Original allowance was $" + allowance2 + ". Would you like to reduce it?");
			
			data = new FormData();
			data.top = new FormAttachment(1, 0);
			data.left = new FormAttachment(1, 0);
			typeLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(typeLabel, 2);
			data.left = new FormAttachment(1, 0);
			userLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(userLabel, 2);
			data.left = new FormAttachment(1, 0);
			choreLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(choreLabel, 2);
			data.left = new FormAttachment(1, 0);
			messageLabel.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(messageLabel, 2);
			data.left = new FormAttachment(1, 0);
			reduceAllowance.setLayoutData(data);
			
			data = new FormData();
			data.top = new FormAttachment(reduceAllowance, 2);
			data.left = new FormAttachment(1, 0);
			button1.setLayoutData(data);
			button1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					//TODO remove the notification
					ArrayList<Object> send = new ArrayList<Object>();
					ArrayList<Object> mem1 = new ArrayList<Object>();
					send.add(9911);
					send.add(familyUsername);
					mem1.add(notif.get(0));
					mem1.add(notif.get(2));
					mem1.add(parsedDate);
					mem1.add(notifType);
					mem1.add(notif.get(6));
					send.add(mem1);
					send.add(null);
					send.add(0);
					ArrayList<Object> response = client.clientConnection(send);
					if(!(Boolean)response.get(0))
					{
						//Error
					}
					else
					{
						newGroup.getShell().dispose();
					}
				}
			});
			
			data = new FormData();
			data.top = new FormAttachment(reduceAllowance, 2);
			data.left = new FormAttachment(button1, 5);
			button2.setLayoutData(data);
			button2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					//remove the notification + reduce allowance
					//Reduce allowance
					reduceAllowance(display, allowance2, (String) notif.get(0));
					
					//TODO remove the notification
					ArrayList<Object> send = new ArrayList<Object>();
					ArrayList<Object> mem1 = new ArrayList<Object>();
					send.add(9911);
					send.add(familyUsername);
					mem1.add(notif.get(0));
					mem1.add(notif.get(2));
					mem1.add(parsedDate);
					mem1.add(notifType);
					mem1.add(notif.get(6));
					send.add(mem1);
					send.add(null);
					send.add(0);
					ArrayList<Object> response = client.clientConnection(send);
					if(!(Boolean)response.get(0))
					{
						//Error
					}
					else
					{
						newGroup.getShell().dispose();
					}
				}
				
				Boolean improperInput = true;
				Boolean improperAmount = true;
				Double toRemove = 0.0;
				
				private void reduceAllowance(Display display, Double originalAllowance, String user) {
					Shell shell = new Shell(display);
					FormLayout layout = new FormLayout();
					shell.setLayout(layout);
					shell.setSize(200, 150);
					shell.setText("Reduce allowance");
					shell.setImage(logo);
					
					Label allowanceLabel = new Label(shell, SWT.NONE);
					allowanceLabel.setText("Original Allowance was $" + originalAllowance);
					
					Label newAllowanceLabel = new Label(shell, SWT.NONE);
					newAllowanceLabel.setText("New allowance:");
					
					Text allowanceText = new Text(shell, SWT.SINGLE | SWT.BORDER);
					
					Button submitButton = new Button(shell, SWT.PUSH);
					submitButton.setText("Submit");
					
					Button cancelButton = new Button(shell, SWT.PUSH);
					cancelButton.setText("Cancel");
					
					FormData data = new FormData();
					data.top = new FormAttachment(10, 0);
					data.left = new FormAttachment(5, 0);
					allowanceLabel.setLayoutData(data);
					
					data = new FormData();
					data.top = new FormAttachment(allowanceLabel, 5);
					data.left = new FormAttachment(5, 0);
					newAllowanceLabel.setLayoutData(data);
					
					data = new FormData();
					data.top = new FormAttachment(allowanceLabel, 5);
					data.left = new FormAttachment(newAllowanceLabel, 2);
					allowanceText.setLayoutData(data);
					allowanceText.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent e) {
							try {
								improperInput = false;
								toRemove = Double.parseDouble(allowanceText.getText());
							} catch (NumberFormatException n) {
								improperInput = true;
								System.out.println("Improper input");
							}
							
							if (toRemove > originalAllowance || toRemove < 0.0) {
								improperAmount = true;
							} else {
								improperAmount = false;
							}
						}
					});
					
					data = new FormData();
					data.top = new FormAttachment(newAllowanceLabel, 10);
					data.left = new FormAttachment(5, 0);
					submitButton.setLayoutData(data);
					submitButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							if (improperInput || improperAmount) {
								//Error
							} else {
								ArrayList<Object> send = new ArrayList<Object>();
								send.add(9920);
								send.add(familyUsername);
								send.add(user);
								send.add(toRemove);
								ArrayList<Object> response = client.clientConnection(send);
								
								if ((Boolean)response.get(0)) {
									shell.dispose();
								} else {
									//Error
								}
							}
						}
					});
					
					data = new FormData();
					data.top = new FormAttachment(newAllowanceLabel, 10);
					data.left = new FormAttachment(submitButton, 10);
					cancelButton.setLayoutData(data);
					cancelButton.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							shell.dispose();
						}
					});
					
					shell.open();
				}
			});
			
			break;
		}
		
		return newGroup;
	}

	/**
	 * Parses the date to the correct YYY/MM/DD format.
	 * 
	 * @param date
	 * @return The parsed date.
	 */
	@SuppressWarnings("deprecation")
	private String parseDate(Date date) {
		String parsedDate = "";
		String monthConv;
		String dayConv;
		
		int month = 1 + date.getMonth();
		int day = date.getDate();
		int year = date.getYear() + 1900;
		
		if (day < 10) {
			dayConv = "0" + day;
		} else {
			dayConv = "" + day;
		}
		
		if (month < 10) {
			monthConv = "0" + month;
		} else {
			monthConv = "" + month;
		}
		
		parsedDate = year + "/" + monthConv + "/" + dayConv;
		
		return parsedDate;
	}

	/**
	 * Parses the given time object to the correct H:MM XM format
	 * 
	 * @param time
	 * @return The parsed time
	 */
	@SuppressWarnings("deprecation")
	private String parseTime(Time time) {
		String parsedTime = "";
		String hourConv;
		String AmPm;
		
		int hour = time.getHours();
		if(hour < 12) {
			hourConv = "" + hour;
			AmPm = "AM";
		} else if (hour == 12) {
			hourConv = "" + hour;
			AmPm = "PM";
		} else {
			hourConv = "" + (hour - 12);
			AmPm = "PM";
		}
		
		parsedTime = hourConv + ":00 " + AmPm;
		
		return parsedTime;
	}
	
	/**
	 * Method that calls the database to get the allowance for a single chore
	 * 
	 * @param d
	 * @param user
	 * @param chore
	 * @return The retrieved allowance
	 */
	private double retrieveAllowance(String d, String user, String chore)
	{
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9921);
		send.add(familyUsername);
		send.add(user);
		send.add(d);
		send.add(chore);
		ArrayList<Object> response = client.clientConnection(send);
		return (Double)response.get(0);
	}
	
}