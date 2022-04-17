package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
//import org.eclipse.jface.dialogs.ErrorDialog;

/**
 * Class for displaying the Family creation screen.
 * Class contacts the database via client/server code to add members to a family.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class FamilyCreation {
	static Image logo;

	private Combo sexCombo;
	private Combo bDayMonth;
	private Combo bDayDay;
	private Combo bDayYear;
	private Combo typeCombo;
	private Group startGroup;
	
	//List of all of the family members
	private ArrayList<Group> groups = new ArrayList<Group>();
	
	@SuppressWarnings("rawtypes")
	//Used to collect the data from the groups - not the final array to be sent to server (thats in getData)
	private ArrayList<ArrayList> StringData = new ArrayList<ArrayList>();
	
	final private int ID = 2;
	private String familyUsername;
	
	/**
	 * Checks if everything within a given string is either a number or letter
	 * 
	 * @param blah
	 * @return True if String is all numbers or letters.
	 */
	boolean AlphaNumcheck(String blah){
		String ePattern = "[a-z0-9]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher( blah);
        return m.matches();
	}
	
	/**
	 * Constructor for the FamilyCreation screen
	 * 
	 * @param familyName
	 */
	protected FamilyCreation(String familyName) {
		familyUsername = familyName;
		
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
	 * Creates the main FamilyCreation UI shell
	 * 
	 * @param display
	 * 
	 * @return The created shell
	 */
	private Shell createShell(Display display) {
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(1000, 1000);
		shell.setText("Family Creation");
		shell.setImage(logo);
		
		ScrolledComposite sc1 = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc1.setLayout(layout);
		FormData test = new FormData();
		test.top = new FormAttachment(1, 0);
		test.left = new FormAttachment(1, 0);
		sc1.setLayoutData(test);
		sc1.setExpandVertical(true);
		
	    Composite comp = new Composite(sc1, SWT.NONE);
	    sc1.setContent(comp);
	    FormLayout layout2 = new FormLayout();
	    comp.setLayout(layout2);
	    
		Label mainLabel = new Label(comp, SWT.NONE);
		mainLabel.setText("Add Family Members");
		Label ErrorMessage = new Label(shell, SWT.NONE);
		ErrorMessage.setText("Oops, something went wrong.");
		Color Red = display.getSystemColor(SWT.COLOR_RED);
		ErrorMessage.setForeground(Red);
		ErrorMessage.setVisible(true);
		/*
		Label badPin  = new Label(comp, SWT.NONE);
		badPin.setText("Use Only numbers");
		//badPin.setForeground(Red);
		*/
		startGroup = newMember(comp);
		startGroup.setText("0");
		newMember();
		
		Button addButton = new Button(comp, SWT.NONE);
		addButton.setText("Add Another Member");
		
		Button submitButton = new Button(comp, SWT.NONE);
		submitButton.setText("Submit");

		FormData data = new FormData();
		data.top = new FormAttachment(1, 0);
		data.left = new FormAttachment(1, 0);
		mainLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(mainLabel, 2);
		data.left = new FormAttachment(1, 0);
		startGroup.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(startGroup, 2);
		data.left = new FormAttachment(1, 0);

		addButton.setLayoutData(data);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				Group newGroup = newMember(comp);
				
				if (groups.isEmpty()) {
					FormData data = new FormData();
					data.top = new FormAttachment(startGroup, 2);
					data.left = new FormAttachment(1, 0);
					newGroup.setLayoutData(data);
					
					data = new FormData();
					data.top = new FormAttachment(newGroup, 2);
					data.left = new FormAttachment(1, 0);
					addButton.setLayoutData(data);
					
					shell.layout();
					
				} else {
					FormData data = new FormData();
					data.top = new FormAttachment(groups.get(groups.size() - 1), 2);
					data.left = new FormAttachment(1, 0);
					newGroup.setLayoutData(data);
					
					data = new FormData();
					data.top = new FormAttachment(newGroup, 2);
					data.left = new FormAttachment(1, 0);
					addButton.setLayoutData(data);
					
					shell.layout();
					
				}
				
				groups.add(newGroup);
				newGroup.setText("" + (groups.size()));
				newMember();
				comp.setSize(comp.computeSize(750, 750));
				comp.layout();
				sc1.layout();
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(addButton, 2);
		data.left = new FormAttachment(1, 0);
		submitButton.setLayoutData(data);
		data = new FormData();
		data.top = new FormAttachment(submitButton, 10);
		data.left = new FormAttachment(5, 0);
		ErrorMessage.setLayoutData(data);
		submitButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("rawtypes")
			public void widgetSelected(SelectionEvent event) {
				//Send data then open main screen
				
				//Send data via a new arrayList set to a call to getData() + the int indicator
				ArrayList<Object> response = client.clientConnection(getData());
				System.out.println(response);
				
				if( !(boolean)response.get(0))
				{
					//TODO get this to work
					//MessageBox dialog= new MessageDialog.openError(shell, "Error", "Error occured");
					ErrorMessage.setVisible(true);
				}
				else
				{
					//Need to close registration screen
					display.dispose();
					//Open the Family Creation screen
					try {
						MainScreen main = new MainScreen(familyUsername);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		comp.setSize(comp.computeSize(750, 750));
		
		return shell;
	}
	
	/**
	 * Creates a new member group within the given composite
	 * 
	 * @param comp
	 * @return The new Group object
	 */
	private Group newMember(Composite comp) {
		Group newGroup = new Group(comp, SWT.NONE);
		
		newGroup.setLayout(new FormLayout());
		
		Label nameLabel = new Label(newGroup, SWT.NONE);
		nameLabel.setText("Family Member Name");
		//Label badUsername = new Label(newGroup, SWT.NONE);
		//badUsername.setText("Username error");
		//Color Red =getSystemColor(SWT.COLOR_RED);
		//badUsername.setForeground(Red);
		//Label badPin  = new Label(newGroup, SWT.NONE);
		//badPin.setText("Use Only numbers");
		Text nameText = new Text(newGroup, SWT.SINGLE | SWT.BORDER);
		nameText.addModifyListener( new ModifyListener() {
			@SuppressWarnings("unchecked")
			public void modifyText(ModifyEvent e) {
				String test = ((Text) e.widget).getText();
				StringData.get(Integer.parseInt(((Group)((Text)e.widget).getParent()).getText())).set(0, test);
					
			}
			
		});
		
		
		Label pinLabel = new Label(newGroup, SWT.NONE);
		pinLabel.setText("Pin");
		Text pinText = new Text(newGroup, SWT.SINGLE | SWT.BORDER);
		pinText.addModifyListener( new ModifyListener() {
			@SuppressWarnings("unchecked")
			public void modifyText(ModifyEvent e) {
				String test = ((Text) e.widget).getText();
				StringData.get(Integer.parseInt(((Group)((Text)e.widget).getParent()).getText())).set(1, test);
			}
			
		});
		
		Label bDayLabel = new Label(newGroup, SWT.NONE);
		bDayLabel.setText("Birthday");
		
		bDayMonth = new Combo(newGroup, SWT.DROP_DOWN | SWT.BORDER);
		bDayMonth.addModifyListener( new ModifyListener() {
			@SuppressWarnings("unchecked")
			public void modifyText(ModifyEvent e) {
				String test = ((Combo) e.widget).getText();
				StringData.get(Integer.parseInt(((Group)((Combo)e.widget).getParent()).getText())).set(2, test);
			}
			
		});
		
		bDayDay = new Combo(newGroup, SWT.DROP_DOWN | SWT.BORDER);
		bDayDay.addModifyListener( new ModifyListener() {
			@SuppressWarnings("unchecked")
			public void modifyText(ModifyEvent e) {
				String test = ((Combo) e.widget).getText();
				StringData.get(Integer.parseInt(((Group)((Combo)e.widget).getParent()).getText())).set(3, test);
			}
			
		});
		
		bDayYear = new Combo(newGroup, SWT.DROP_DOWN | SWT.BORDER);
		bDayYear.addModifyListener( new ModifyListener() {
			@SuppressWarnings("unchecked")
			public void modifyText(ModifyEvent e) {
				String test = ((Combo) e.widget).getText();
				StringData.get(Integer.parseInt(((Group)((Combo)e.widget).getParent()).getText())).set(4, test);
			}
			
		});
		createBDayCombos();
		
		Label sexLabel = new Label(newGroup, SWT.NONE);
		sexLabel.setText("Sex");
		sexCombo = new Combo(newGroup, SWT.DROP_DOWN | SWT.BORDER);
		createsexCombo();
		sexCombo.addModifyListener( new ModifyListener() {
			@SuppressWarnings("unchecked")
			public void modifyText(ModifyEvent e) {
				String test = ((Combo) e.widget).getText();
				StringData.get(Integer.parseInt(((Group)((Combo)e.widget).getParent()).getText())).set(5, test);
			}
			
		});
		
		Label typeLabel = new Label(newGroup, SWT.NONE);
		typeLabel.setText("User Type");
		typeCombo = new Combo(newGroup, SWT.DROP_DOWN | SWT.BORDER);
		createTypeCombo();
		typeCombo.addModifyListener( new ModifyListener() {
			@SuppressWarnings("unchecked")
			public void modifyText(ModifyEvent e) {
				String test = ((Combo) e.widget).getText();
				StringData.get(Integer.parseInt(((Group)((Combo)e.widget).getParent()).getText())).set(6, test);
			}
			
		});
		
		/*
		 * Layout
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment();
		nameLabel.setLayoutData(data);
		/*
		data = new FormData();
		data.top = new FormAttachment();
		data.left= new FormAttachment(nameLabel,5);
		//badUsername.setLayoutData(data);
		*/
		data = new FormData(200, SWT.DEFAULT);
		data.top = new FormAttachment(nameLabel, 5);
		nameText.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(nameText, 5);
		data.bottom = new FormAttachment(pinText, -5);
		pinLabel.setLayoutData(data);
		/*
		data = new FormData();
		data.left = new FormAttachment(pinLabel, 5);
		data.bottom = new FormAttachment();
		//badPin.setLayoutData(data);
		*/
		data = new FormData(100, SWT.DEFAULT);
		data.left = new FormAttachment(nameText, 5);
		data.top = new FormAttachment(pinLabel, 5);
		pinText.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(pinText, 5);
		data.bottom = new FormAttachment(pinText, -5);
		bDayLabel.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(pinText, 5);
		data.top = new FormAttachment(bDayLabel, 5);
		bDayMonth.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(bDayMonth, 5);
		data.top = new FormAttachment(bDayLabel, 5);
		bDayDay.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(bDayDay, 5);
		data.top = new FormAttachment(bDayLabel, 5);
		bDayYear.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(bDayYear, 5);
		data.bottom = new FormAttachment(bDayYear, -5);
		sexLabel.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(bDayYear, 5);
		data.top = new FormAttachment(sexLabel, 5);
		sexCombo.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(sexCombo, 5);
		data.bottom = new FormAttachment(sexCombo, -5);
		typeLabel.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(sexCombo, 5);
		data.top = new FormAttachment(typeLabel, 5);
		typeCombo.setLayoutData(data);
		
		return newGroup;
	}
	
	/**
	 * Fills the Birthday combos with the Months and days.
	 */
	private void createBDayCombos() {
		bDayMonth.add("January");
		bDayMonth.add("February");
		bDayMonth.add("March");
		bDayMonth.add("April");
		bDayMonth.add("May");
		bDayMonth.add("June");
		bDayMonth.add("July");
		bDayMonth.add("August");
		bDayMonth.add("September");
		bDayMonth.add("October");
		bDayMonth.add("November");
		bDayMonth.add("December");
		
		for (int i = 1; i < 32; i++) {
			if (i < 10) {
				bDayDay.add("0" + i);
			} else {
				bDayDay.add("" + i);
			}
		}
		
		for (int i = 1950; i < 2000; i++) {
			bDayYear.add("" + i);
		}
		for (int i = 2000; i < 2018; i++) {
			bDayYear.add("" + i);
		}
		
	}
	
	/**
	 * Fills the sex combo
	 */
	private void createsexCombo() {
		sexCombo.add("Male");
		sexCombo.add("Female");
	}
	
	/**
	 * Fills the uset type combo
	 */
	private void createTypeCombo() {
		typeCombo.add("Parent");
		typeCombo.add("Child");
		typeCombo.add("Other");
	}
	
	/**
	 * Fills the default values for a new member to be sent to the server
	 */
	private void newMember() {
		ArrayList<String> temp = new ArrayList<String>();
		
		temp.add("Name");
		temp.add("Pin");
		temp.add("BdayMonth");
		temp.add("BdayDay");
		temp.add("BdayYear");
		temp.add("Sex");
		temp.add("Type");
		
		StringData.add(groups.size(), temp);
		
	}
	
	/**
	 * Gets all added user data to be sent to the server.
	 * 
	 * @return The arraylist of data.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ArrayList<Object> getData() {
		//Structure in each arraylist will be (Name, Pin, Bday, sex, type)
		
		ArrayList<Object> toSend = new ArrayList<Object>();
		
		toSend.add(2);
		toSend.add(familyUsername);
		
		for (int i = 0; i <= groups.size(); i++) {
			String bday = parseBday(StringData.get(i));
			
			ArrayList<String> temp = new ArrayList<String>();
			
			temp.add((String) StringData.get(i).get(0));
			temp.add((String) StringData.get(i).get(1));
			temp.add(bday);
			temp.add((String) StringData.get(i).get(5));
			temp.add((String) StringData.get(i).get(6));
			
			toSend.add(temp);
		}
		
		
		return toSend;
	}
	
	/**
	 * Parses a birthday into the proper format for sending to the server
	 * 
	 * @param array
	 * @return
	 */
	protected String parseBday(ArrayList<String> array) {
		
		String bday;
		String month = "";
		
		switch(array.get(2)) {
		case "January":
			month = "01";
			break;
		case "February":
			month = "02";
			break;
		case "March":
			month = "03";
			break;
		case "April":
			month = "04";
			break;
		case "May":
			month = "05";
			break;
		case "June":
			month = "06";
			break;
		case "July":
			month = "07";
			break;
		case "August":
			month = "08";
			break;
		case "September":
			month = "09";
			break;
		case "October":
			month = "10";
			break;
		case "November":
			month = "11";
			break;
		case "December":
			month = "12";
			break;
		}
		
		//Format will be YYYY/MM/DD
		bday = array.get(4) + "/" + month + "/" + array.get(3);
		
		return bday;
	}
	
	/**
	 * Gets the screen ID
	 * 
	 * @return THe screen ID
	 */
	protected int getID() {
		return ID;
	}
}