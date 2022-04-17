package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

import java.util.ArrayList;
import java.util.Objects;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for displaying the Registration screen.
 * Class contacts the database via client/server code to register a family and pass on to the Family creation screen after registration.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class RegistrationScreen {
	
	static Image logo;
	boolean usernameBoo=false;
	boolean passwordBoo=false;
	boolean passwordTestBoo=false;
	boolean emailBoo=false;
	
	Text username;
	Text password;
	Text passwordTest;
	Text passwordHint;
	Text email;
	
	Button submit;
	
	Combo country;
	Combo state;
	Combo city;
	
	/**
	 * Checks whether the entered email is of the correct format.
	 * 
	 * @param email2
	 * @return
	 */
	public boolean isValidEmailAddress(String email2) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher( email2);
        return m.matches();
	}
	
	/**
	 * Checks the given string to ensure it contains only letters.
	 * 
	 * @param blah
	 * @return
	 */
	boolean Alphacheck(String blah){
		boolean allLetters = (blah).chars().allMatch(Character::isLetter);
		return allLetters;
	}
	
	/**
	 * Checks whether the two passowrds match.
	 * 
	 * @param abba
	 * @param Babba
	 * @return
	 */
	boolean Passwordmatch(String abba, String Babba){
		return Objects.equals(abba,Babba);
	}
	
	/**
	 * Constructor for the registration screen.
	 */
	protected RegistrationScreen() {
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
	 * Creates the main registration UI shell
	 * 
	 * @param display
	 * 
	 * @return The created shell
	 */
	@SuppressWarnings("unchecked")
	private Shell createShell(final Display display) {
		
		Shell shell = new Shell(display);	
		FormLayout layout = new FormLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		shell.setLayout(layout);
		shell.setSize(500, 550);
		shell.setText("Family Registration");
		shell.setImage(logo);
		Color Red = display.getSystemColor(SWT.COLOR_RED);
		Label userLabel = new Label(shell, SWT.NONE);
		userLabel.setText("Choose a Family Username");
		username = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label passLabel = new Label(shell, SWT.NONE);
		passLabel.setText("Enter a Password");
		password = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label ErrorMessage = new Label(shell, SWT.NONE);
		ErrorMessage.setText("Oops, something went wrong.");
		Color sdfed = display.getSystemColor(SWT.COLOR_RED);
		ErrorMessage.setForeground(sdfed);
		ErrorMessage.setVisible(false);
		Label passTestLabel = new Label(shell, SWT.NONE);
		passTestLabel.setText("Re-Enter Password");
		passwordTest = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label passHintLabel = new Label(shell, SWT.NONE);
		passHintLabel.setText("Enter a Password Hint");
		passwordHint = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label emailLabel = new Label(shell, SWT.NONE);
		emailLabel.setText("Email for Lost Credentials");
		email = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label location = new Label(shell, SWT.UNDERLINE_SINGLE);
		location.setText("Location:");
		
		Label countryLabel = new Label(shell, SWT.NONE);
		countryLabel.setText("Country");
		
		Label stateLabel = new Label(shell, SWT.NONE);
		stateLabel.setText("State");
		
		Label cityLabel = new Label(shell, SWT.NONE);
		cityLabel.setText("City");
		
		submit = new Button(shell, SWT.PUSH);
		submit.setText("Submit");

		country = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
		state = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
		city = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
		
		state.add("Select a country");
		city.add("Select a state");
		
		/*
		 * 
		 */
		ArrayList<Object> countryReq = new ArrayList<Object>();
		countryReq.add(100);
		ArrayList<Object> response = client.clientConnection(countryReq);
		for (int i = 0; i < response.size(); i++) 
		{
			country.add((String)response.get(i));
		}
		
		
		FormData data = new FormData();
		data.top = new FormAttachment(1, 0);
		data.left = new FormAttachment(20,0);
		userLabel.setLayoutData(data);
				
		data = new FormData(200, SWT.DEFAULT);
		data.top = new FormAttachment(userLabel, 5);
		data.left = new FormAttachment(20,0);
		username.setLayoutData(data);
		Label usernameProblem = new Label(shell, SWT.NONE);
		usernameProblem.setText("Not a valid name.");
		usernameProblem.setForeground(Red);
		data = new FormData();
		data.left=new FormAttachment(username,5);
		data.top= new FormAttachment(6,0);
		usernameProblem.setLayoutData(data);
		usernameProblem.setVisible(false);
		username.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if(Alphacheck(username.getText())){
					usernameProblem.setVisible(false);
					usernameBoo=true;
				}
				else{
					usernameProblem.setVisible(true);
					usernameBoo=false;
				}
				enableDisableSubmit();
			}
		});
		
		
		
		data = new FormData();
		data.top = new FormAttachment(username, 5);
		data.left = new FormAttachment(20,0);
		passLabel.setLayoutData(data);
				
		data = new FormData(200, SWT.DEFAULT);
		data.top = new FormAttachment(passLabel, 5);
		data.left = new FormAttachment(20,0);
		password.setLayoutData(data);
		
		Label passwordProblem = new Label(shell, SWT.NONE);
		passwordProblem.setText("Not a valid password");
		passwordProblem.setForeground(Red);
		
		data = new FormData();
		data.left = new FormAttachment(password,5);
		data.top = new FormAttachment(14,0);
		passwordProblem.setLayoutData(data);
		passwordProblem.setVisible(false);
		password.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if((password.getText().length())>=5){
					passwordProblem.setVisible(false);
					passwordBoo=true;
				}
				else{
					passwordProblem.setVisible(true);
					passwordBoo=false;
				}
				enableDisableSubmit();
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(password, 5);
		data.left = new FormAttachment(20,0);
		passTestLabel.setLayoutData(data);
		
		data = new FormData(200, SWT.DEFAULT);
		data.top = new FormAttachment(passTestLabel, 5);
		data.left = new FormAttachment(20,0);
		passwordTest.setLayoutData(data);
		
		Label passwordMatchProblem = new Label(shell, SWT.NONE);
		passwordMatchProblem.setText("Passwords don't match.");
		passwordMatchProblem.setForeground(Red);
		
		data = new FormData();
		data.left = new FormAttachment(passwordTest,5);
		data.top = new FormAttachment(24,0);
		passwordMatchProblem.setLayoutData(data);
		passwordMatchProblem.setVisible(false);
		passwordTest.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if(Passwordmatch(password.getText(),passwordTest.getText())){
					passwordMatchProblem.setVisible(false);
					passwordTestBoo=true;
				}
				else{
					passwordMatchProblem.setVisible(true);
					passwordTestBoo=false;
				}
				enableDisableSubmit();
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(passwordTest, 5);
		data.left = new FormAttachment(20,0);
		passHintLabel.setLayoutData(data);
		
		data = new FormData(200, SWT.DEFAULT);
		data.top = new FormAttachment(passHintLabel, 5);
		data.left = new FormAttachment(20,0);
		passwordHint.setLayoutData(data);
		passwordHint.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//password hint, doen't need anything
				
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(passwordHint, 5);
		data.left = new FormAttachment(20,0);
		emailLabel.setLayoutData(data);
		
		data = new FormData(200, SWT.DEFAULT);
		data.top = new FormAttachment(emailLabel, 5);
		data.left = new FormAttachment(20,0);
		email.setLayoutData(data);
		
		Label emailProblem = new Label(shell, SWT.NONE);
		emailProblem.setText("Email isn't valid");
		emailProblem.setForeground(Red);
		
		data = new FormData();
		data.left=new FormAttachment(email,5);
		data.top= new FormAttachment(42,0);
		emailProblem.setLayoutData(data);
		emailProblem.setVisible(false);
		email.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if(isValidEmailAddress(email.getText())){
					emailProblem.setVisible(false);
					emailBoo=true;
				}
				else{
					emailProblem.setVisible(true);
					emailBoo=false;
				}
				enableDisableSubmit();
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(email, 5);
		data.left = new FormAttachment(20,0);
		location.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(location, 5);
		data.left = new FormAttachment(20,0);
		countryLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(countryLabel, 5);
		data.left = new FormAttachment(20,0);
		country.setLayoutData(data);
		country.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("Country: " + country.getText());
				ArrayList<Object> stateReq = new ArrayList<Object>();
				stateReq.add(101);
				stateReq.add(country.getText());
				ArrayList<Object> response = client.clientConnection(stateReq);
				state.removeAll();
				
				for (int i = 0; i < response.size(); i++) {
					state.add((String)response.get(i));
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(country, 5);
		data.left = new FormAttachment(20, 0);
		stateLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(stateLabel, 5);
		data.left = new FormAttachment(20, 0);
		state.setLayoutData(data);
		state.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("State: " + state.getText());
				ArrayList<Object> cityReq = new ArrayList<Object>();
				cityReq.add(102);
				cityReq.add(state.getText());
				ArrayList<Object> response = client.clientConnection(cityReq);
				city.removeAll();
				
				for (int i = 0; i < response.size(); i++) {
					city.add((String)response.get(i));
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(state, 5);
		data.left = new FormAttachment(20, 0);
		cityLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(cityLabel, 5);
		data.left = new FormAttachment(20, 0);
		city.setLayoutData(data);
		city.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("City: " + city.getText());
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(city, 20);
		data.left = new FormAttachment(20,0);
		submit.setLayoutData(data);
		submit.setEnabled(false);
		data = new FormData();
		data.top = new FormAttachment(submit, 10);
		data.left = new FormAttachment(5, 0);
		ErrorMessage.setLayoutData(data);
		submit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				String FamUser = username.getText();
				
				ArrayList<Object> response = client.clientConnection(getData());
				System.out.println(response);
				
				if( !(boolean)response.get(0))
				{
					ErrorMessage.setVisible(true);
				}
				else
				{
					//Need to close registration screen
					display.dispose();
					//Open the Family Creation screen
					FamilyCreation family = new FamilyCreation(FamUser);
				}
			}
		});
		
		return shell;
	}
	
	/**
	 * Retrieves the data to be passed to the server to register the family from the screen.
	 * 
	 * @return The retireved data array.
	 */
	protected ArrayList<Object> getData() {
		//Structure will be (ID, Username, Password, Password confirmation, email, Country, State, City)
		ArrayList<Object> data = new ArrayList<Object>();
		
		data.add(1); //Registration ID
		
		data.add(username.getText());
		data.add(password.getText());
		data.add(passwordHint.getText());
		data.add(email.getText());
		data.add(country.getText());
		data.add(state.getText());
		data.add(city.getText());
		
		return data;
	}
	
	/**
	 * Enables and disables the submit button depending on correct user inputs in the registration fields.
	 */
	protected void enableDisableSubmit() {
		if(usernameBoo&&passwordBoo&&passwordTestBoo&&emailBoo) {
			submit.setEnabled(true);
		} else {
			submit.setEnabled(false);
		}
	}
}
