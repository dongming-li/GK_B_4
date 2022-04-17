package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

import java.text.ParseException;
import java.util.ArrayList;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for displaying the login screen.
 * Class contacts the database via client/server code to log a family in, or send them to the registration screen.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class LoginScreen {

	static Image logo;
	Text familyUsername;
	Text familyPassword;
	Button loginButton;
	Link forgotUser;
	Link forgotPass;
	Link signUp;
	Display display;
	
	/**
	 * Constructor for the screen
	 */
	protected LoginScreen()  {
		
		display = new Display().getDefault();
		logo = display.getSystemImage(SWT.ICON_QUESTION);
		Shell shell = createShell(display);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
	}

	/**
	 * Creates the main LoginScreen UI shell
	 * 
	 * @param display
	 * 
	 * @return The created shell
	 */
	private Shell createShell(final Display display) {
		final Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		layout.marginWidth = 10;
		layout.marginHeight = 75;
		shell.setLayout(layout);
		shell.setText("Chore Manager v0.1");
		shell.setImage(logo);
		
		Label familyLogin = new Label(shell, SWT.NONE);
		familyLogin.setText("Family Login");
		familyLogin.setFont(new Font(display,"Arial", 16, SWT.BOLD ));
		
		Label username = new Label(shell, SWT.NONE);
		username.setText("Family Username: ");
		familyUsername = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label password = new Label(shell, SWT.NONE);
		password.setText("Family Password: ");
		familyPassword = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label NoUsername = new Label(shell, SWT.NONE);
		NoUsername.setText("Username not found");
		Color Red = display.getSystemColor(SWT.COLOR_RED);
		NoUsername.setForeground(Red);
		//NoUsername.setFont(new Font(display,"Arial", 16, SWT.BOLD ));
		
		Label NoPassword = new Label(shell, SWT.NONE);
		NoPassword.setText("username and password do not match");
		NoPassword.setForeground(Red);
		//NoPassword.setFont(new Font(display,"Arial", 16, SWT.BOLD ));
		
		loginButton = new Button(shell, SWT.PUSH);
		loginButton.setText("Log in");
		
		Link forgotUsername = new Link(shell, SWT.DEFAULT);
		forgotUsername.setText("<A>Forgot Username?</A>");
		
		Link forgotPassword = new Link(shell, SWT.DEFAULT);
		forgotPassword.setText("<A>Forgot Password?</A>");
		
		Link newUser = new Link(shell, SWT.DEFAULT);
		newUser.setText("<A>New User? Sign up!</A>");
		
		FormData data = new FormData();	
		data.top = new FormAttachment(20, 0);
		data.left = new FormAttachment(10,0);
		username.setLayoutData(data);
		
		data = new FormData(200, SWT.DEFAULT);
		data.left = new FormAttachment(username, 5);
		data.top = new FormAttachment(20, -2);
		familyUsername.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(username, 11);
		data.left = new FormAttachment(10, 0);
		password.setLayoutData(data);
		
		data = new FormData(200, SWT.DEFAULT);
		data.left = new FormAttachment(password, 8);
		data.top = new FormAttachment(familyUsername, 5);
		familyPassword.setLayoutData(data);
		
		data = new FormData();
		data.bottom = new FormAttachment(username, -20);
		data.left = new FormAttachment(10, 0);
		familyLogin.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(password, 10);
		data.left = new FormAttachment(10, 0);
		loginButton.setLayoutData(data);
		
		data = new FormData();
		data.bottom = new FormAttachment(familyUsername,0);
		data.left = new FormAttachment(30, 20);
		NoUsername.setLayoutData(data);
		NoUsername.setVisible(false);
		
		data = new FormData();
		data.top = new FormAttachment(password, 10);
		data.left = new FormAttachment(30, 20);
		NoPassword.setLayoutData(data);
		NoPassword.setVisible(false);
		
		loginButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {

					String FamUser = familyUsername.getText();
					
					ArrayList<Object> response = client.clientConnection(getData());
					System.out.println(response);
					if( !(boolean)response.get(0))
					{
						NoPassword.setVisible(false);
						NoUsername.setVisible(true);
						
					}
					else if(!(boolean)response.get(1)){
						
						NoPassword.setVisible(true);
						NoUsername.setVisible(false);
					}
					else
					{
						//Need to close login screen
						display.dispose();
						//Open the main screen
						try {
							MainScreen ms = new MainScreen(FamUser);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			});
		
		data = new FormData();
		data.top = new FormAttachment(loginButton, 5);
		data.left = new FormAttachment(10, 0);
		forgotUsername.setLayoutData(data);
		forgotUsername.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				forgot(shell, 1);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(forgotUsername, 5);
		data.left = new FormAttachment(10, 0);
		forgotPassword.setLayoutData(data);
		forgotPassword.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				forgot(shell, 2);
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(forgotPassword, 20);
		data.left = new FormAttachment(10, 0);
		newUser.setLayoutData(data);
		newUser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				display.dispose();
				RegistrationScreen reg = new RegistrationScreen();
			}
		});
		
		shell.pack();
		
		return shell;
	}	
	
	/**
	 * Gets the login data to be sent to the server
	 * 
	 * @return The login data array
	 */
	protected ArrayList<Object> getData() {
		ArrayList<Object> data = new ArrayList<Object>();
		
		data.add(0); //FamilyCreation ID
		data.add(familyUsername.getText());
		data.add(familyPassword.getText());
		
		return data;
	}
	
	/**
	 * Pops up the forgot username or password dialog.
	 * 
	 * @param popupShell
	 * @param listner
	 */
	protected void forgot(Shell popupShell, int listner) {
		String forgot = null;
		String textField1 = null;
		String textField2 = null;
		ArrayList<Object> login = new ArrayList<Object>();
		
		if( listner == 1 )
		{
			forgot = "Forgot Username";
			textField1 = "Family Email:";
			textField2 = "Password:";
		}
		else
		{
			forgot = "Forgot Password";
			textField1 = "Family Username:";
			textField2 = "Family Email:";
		}
		
		final Shell dialog = new Shell (popupShell);
		dialog.setText(forgot);
		FormLayout popupLayout = new FormLayout ();
		popupLayout.marginWidth = 10;
		popupLayout.marginHeight = 10;
		popupLayout.spacing = 10;
		dialog.setLayout (popupLayout);

		FormData data = new FormData ();
		data.top = new FormAttachment(0,0);
		data.left = new FormAttachment(0,0);
		Label label = new Label (dialog, SWT.NONE);
		label.setText (textField1);
		label.setLayoutData (data);
		
		final Text userText1 = new Text (dialog, SWT.BORDER);
		data = new FormData ();
		data.width = 200;
		data.left = new FormAttachment (label, 0, SWT.DEFAULT);
		data.right = new FormAttachment (100, 0);
		data.top = new FormAttachment (label, -5, SWT.CENTER);
		data.bottom = new FormAttachment (label, 5, SWT.CENTER);
		userText1.setLayoutData (data);

		data = new FormData ();
		data.top = new FormAttachment(label, 30, SWT.TOP);
		data.right = new FormAttachment(label, 0 , SWT.RIGHT);
		Label label2 = new Label (dialog, SWT.NONE);
		label2.setText (textField2);
		label2.setLayoutData (data);
		
		final Text userText2 = new Text (dialog, SWT.BORDER);
		data = new FormData ();
		data.width = 200;
		data.left = new FormAttachment (label2, 0, SWT.DEFAULT);
		data.right = new FormAttachment (100, 0);
		data.top = new FormAttachment (label2, -5, SWT.CENTER);
		data.bottom = new FormAttachment (label2, 5, SWT.CENTER);
		userText2.setLayoutData (data);
		
		Button cancel = new Button (dialog, SWT.PUSH);
		cancel.setText ("Cancel");
		data = new FormData ();
		data.width = 60;
		data.right = new FormAttachment (100, 0);
		data.top = new FormAttachment (userText2, 0, SWT.DEFAULT);
		cancel.setLayoutData (data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event1) {
				dialog.close ();
			}});
		
		Button ok = new Button (dialog, SWT.PUSH);
		ok.setText ("OK");
		data = new FormData ();
		data.width = 60;
		data.right = new FormAttachment (cancel, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment (100, 0);
		ok.setLayoutData (data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event2) {
				//Check if correct format before closing	
				login.add(-1);
				
				if( listner == 1 )
				{
					login.add(1);
				}
				else
				{
					login.add(2);
				}
				
				login.add(userText1.getText());
				login.add(userText2.getText());
				System.out.println("login: " + login);
				dialog.close ();
				
				ArrayList<Object> response = client.clientConnection(login);
				
				if( response == null)
				{
					//Inform user of error
				}
				else if( listner == 1 )
				{
					//Need to close login screen
					display.dispose();
					//Open the main screen
					try {
						MainScreen ms = new MainScreen((String)response.get(0));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				else
				{
					MessageBox dialog1 = new MessageBox(popupShell, SWT.OK);
			        dialog1.setText("Your Password Hint:");
			        dialog1.setMessage((String)response.get(0));
			        dialog1.open();
				}
		}});

		dialog.setDefaultButton (ok);
		dialog.pack ();
		dialog.open ();
	}
}