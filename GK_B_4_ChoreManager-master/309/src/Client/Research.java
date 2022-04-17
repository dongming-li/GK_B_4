package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import java.sql.Time;
import java.util.ArrayList;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for displaying a screen to select criteria for doing research on a chore and displaying the results.
 * Class contacts the database via client/server code to obtain list of chores and to process research request.
 * Research is done on allowance and time giving min, avg, median, and max values along with the number of families, users, and
 * assigned chores the data was taken from.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class Research 
{
	static Image logo;

	public static final int SCREEN = 999;	// Reference for server to make correct database call
	
	private int minAge = 15;
	private int maxAge = 15;
	private String familyUsername;
	private String location;
	private String gender;
	private String userType;
	private String country;
	private String state;
	private String city;
    private ArrayList<String> chores = new ArrayList<String>(); // Holds list of chores to do research on
	ArrayList<String> otherLocation = new ArrayList<String>();	// Holds location selected by user other then their current location
	Button submitButton;
	Button cancelButton;
	
	/**
	 * Constructor for creating the research window.
	 * 
	 * @param familyName
	 */
	protected Research(String familyName) 
	{
		familyUsername = familyName;
		
		Display display = Display.getDefault();
		
		logo = display.getSystemImage(SWT.ICON_QUESTION);
		Shell shell = createShell(display);
		shell.open();
		
		while (!shell.isDisposed()) 
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	/**
	 * Creates the shell within the display window. This shell displays choices for the users along with the results.
	 * 
	 * @param display
	 * @return
	 */
	private Shell createShell(Display display) 
	{
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(610, 580);
		shell.setText("Research");
		shell.setImage(logo);
		
		// Create the different areas within the shell
		Group locationGroup = defineLocation(shell);
		Group choreGroup = defineChore(shell, display);
		Group genderGroup = defineGender(shell);
		Group ageGroup = defineAge(shell);
		Group typeGroup = defineType(shell);
		Group resultsGroup = defineResult(shell, display); 
        
		// Layout the shell area
		setLayout(locationGroup, choreGroup, genderGroup, ageGroup, typeGroup, resultsGroup);

		return shell;
	}
	
	/**
	 * Defines the area where the user selects a location. Buttons and listeners to record the button selection are created.
	 * 
	 * @param shell
	 * @return
	 */
	private Group defineLocation(Shell shell)
	{
		// Creates a group to contain all location options/buttons
		Group locationGroup = new Group(shell, SWT.NONE);
		locationGroup.setLayout(new RowLayout(SWT.VERTICAL));
        locationGroup.setText("Location");

        // Buttons for different options
        Button btnWorld = new Button(locationGroup, SWT.RADIO);
        btnWorld.setText("The World");
        
        Button btnCountry = new Button(locationGroup, SWT.RADIO);
        btnCountry.setText("My Country");
 
        Button btnState = new Button(locationGroup, SWT.RADIO);
        btnState.setText("My State");
 
        Button btnCity = new Button(locationGroup, SWT.RADIO);
        btnCity.setText("My City");
        
        Button btnOther = new Button(locationGroup, SWT.RADIO);
        btnOther.setText("Other");
        
        // Listeners for each button to perform a action if selected
        btnCountry.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	location = "country";
                }
            }             
        });
        
        btnState.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	location = "state";
                }
            }             
        });
        
        btnCity.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	location = "city";
                }
            }            
        });
        
        btnWorld.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	location = "world";
                }
            }            
        });
        
        btnOther.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	location = "other";
                	OtherLocation o = new OtherLocation();
                	otherLocation = o.getData();
                }
            }           
        });
		return locationGroup;
	}
	
	/**
	 * Defines the area where a user selects chores for researching. Creates a listener to record all chores selected.
	 * Database is contacted via client/server code to get a list of chores.
	 * 
	 * @param shell
	 * @param display
	 * @return
	 */
	private Group defineChore(Shell shell, Display display)
	{
		// Creates a group to contain all chore information
		Group choreGroup = new Group(shell, SWT.NONE);
		choreGroup.setLayout(new FillLayout());
        choreGroup.setText("Chores");
	    
        // Create a scrollable table of all defined chores
	    Table table = new Table(choreGroup, SWT.CHECK | SWT.V_SCROLL);
        ArrayList<Object> send2 = new ArrayList<Object>();
		send2.add(9903);
		ArrayList<Object> response2 = client.clientConnection(send2);
		
	    for (int i = 0; i < response2.size(); i++) 
	    {
	      TableItem item = new TableItem(table, SWT.NONE);
	      item.setText((String)response2.get(i));
	    }
	    
	    table.setSize(SWT.DEFAULT, SWT.DEFAULT);
	    table.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	    
	    // Listener for tracking chores selected
        table.addListener(SWT.Selection, new Listener() 
        {
            public void handleEvent(Event event) 
            {
              String selection;
              
              if( event.detail == SWT.CHECK) 
              {
                selection = ((TableItem)event.item).getText();
                
                if( chores.contains(selection))
                {
            	  chores.remove(selection);
                }
                else
                {
            	  chores.add(selection);
                }
              }
            }
          });
        
	    return choreGroup;
	}
	
	/**
	 * Defines the area where a user selects the gender for research. Create listeners to record selection.
	 * 
	 * @param shell
	 * @return
	 */
	private Group defineGender(Shell shell)
	{
		// Creates a group for all gender information/buttons
		Group genderGroup = new Group(shell, SWT.NONE);
		genderGroup.setLayout(new RowLayout(SWT.VERTICAL));
        genderGroup.setText("Gender");
        
        // Buttons for different options
        Button btnMale = new Button(genderGroup, SWT.RADIO);
        btnMale.setText("Male");
        
        Button btnFemale = new Button(genderGroup, SWT.RADIO);
        btnFemale.setText("Female");
        
        Button btnBoth = new Button(genderGroup, SWT.RADIO);
        btnBoth.setText("Both");
        
        // Listeners for each button to record user selected choice
        btnMale.addSelectionListener(new SelectionAdapter() 
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	gender = "male";
                }
            }            
        });
        
        btnFemale.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	gender = "female";
                }
            }             
        });    
        
        btnBoth.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	gender = "both";
                }
            }             
        });
        
		return genderGroup;
	}
	
	/**
	 * Defines the area where a user selects a age range for child research. Creates listeners to record selection.
	 * Research on parents and others are not limited by this age range.
	 * 
	 * @param shell
	 * @return
	 */
	private Group defineAge(Shell shell)
	{
		// Creates a group for all age information
		Group ageGroup = new Group(shell, SWT.NONE);
		ageGroup.setLayout(new RowLayout(SWT.VERTICAL));
        ageGroup.setText("Childs Age");
        
        // Spinners for selecting the age range
		Label minLabel = new Label(ageGroup, SWT.NONE);
		minLabel.setText("Minimum Age:");
        
        Spinner minSpinner = new Spinner(ageGroup, SWT.BORDER);
        minSpinner.setMinimum(5);
        minSpinner.setMaximum(25);
        minSpinner.setSelection(15);
        minSpinner.setIncrement(1);

		Label maxLabel = new Label(ageGroup, SWT.NONE);
		maxLabel.setText("Maximum Age:");
		
        Spinner maxSpinner = new Spinner(ageGroup, SWT.BORDER);
        maxSpinner.setMinimum(5);
        maxSpinner.setMaximum(25);
        maxSpinner.setSelection(15);
        maxSpinner.setIncrement(1);
        
        // Listeners for to record the age range selected
        minSpinner.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	minAge = minSpinner.getSelection();
            }            
        });       
        
        maxSpinner.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	maxAge = maxSpinner.getSelection();
            }             
        });
        
		return ageGroup;
	}
	
	/**
	 * Defines the area where a user selects the user type. Creates listeners for recording selection.
	 * 
	 * @param shell
	 * @return
	 */
	private Group defineType(Shell shell)
	{
		// Creates a group for user type information/buttons
		Group typeGroup = new Group(shell, SWT.NONE);
		typeGroup.setLayout(new RowLayout(SWT.VERTICAL));
        typeGroup.setText("User Type");
        
        // Buttons for selecting user type
        Button btnParent = new Button(typeGroup, SWT.RADIO);
        btnParent.setText("Parent");
        
        Button btnChild = new Button(typeGroup, SWT.RADIO);
        btnChild.setText("Child");
        
        Button btnOthers = new Button(typeGroup, SWT.RADIO);
        btnOthers.setText("Other");
        
        // Listeners to record user type when button selected
        btnParent.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
              
                if(source.getSelection())  
                {
                	userType = "parent";
                }
            }             
        });        
        
        btnChild.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	userType = "child";
                }
            }             
        });   
        
        btnOthers.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	Button source=  (Button) e.getSource();
                
                if(source.getSelection())  
                {
                	userType = "other";
                }
            }             
        }); 
        
		return typeGroup;
	}
	 
	/**
	 * Defines the area for displaying research results. Also defines the function of the submit and cancel button when clicked.
	 * Submit sends criteria to database via client/server code. The server makes multiple calls to the database having the database
	 * process the data to obtain min, avg, median, max, and number of families, users, and task to return for display.
	 * 
	 * @param shell
	 * @param display
	 * @return
	 */
	private Group defineResult(Shell shell, Display display)
	{
		// Creates a group displaying all result information
		Group resultsGroup = new Group(shell, SWT.NONE);
        resultsGroup.setText("Results");
        
        // Text box for displaying results
        Text text = new Text(resultsGroup, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text.append("Your research results will display here");
		text.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		text.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		text.setLocation(8, 18);		
		text.setSize(510, 290);
		
		// Submit and cancel research buttons
		submitButton = new Button(shell, SWT.PUSH);
		submitButton.setText("SUBMIT");
		
		cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("CANCEL");
		
		// Submit listener gathers user search parameters and calls server, also displays results
		submitButton.addSelectionListener(new SelectionAdapter()  
		{      	 
            @SuppressWarnings("unchecked")
			@Override
            public void widgetSelected(SelectionEvent e) 
            {
            	boolean warning = false;
            	String msg1 = "";
            	String msg2 = "";
            	String msg3 = "";
            	String msg4 = "";
            	String msg5 = "";
            	ArrayList<Object> data = new ArrayList<Object>();
            	
            	// Get information selected else display warning if nothing selected
            	if(location.equalsIgnoreCase("other"))
            	{
	            	country = null;
	            	state = null;
	            	city = null;
	            	
            		if(otherLocation.size() != 0)
	            	{
	                	warning = false;

	            		if(otherLocation.size() == 1)
	                	{
	                		country = otherLocation.get(0);
	                	}
	                	else if(otherLocation.size() == 2)
	                	{
	                		country = otherLocation.get(0);
	                		state = otherLocation.get(1);
	                	}
	                	else
	                	{
	                		country = otherLocation.get(0);
	                		state = otherLocation.get(1);
	                		city = otherLocation.get(2);
	                	}
	            	}
	            	else
	            	{
	            		warning = true;
	            		msg1 = "When choosing location other, you need to select a country at minimum. "
	            				+ "Select other again to select a location.\n\n";
	            	}
            	}

            	if(chores.size() == 0)
            	{
            		warning = true;
            		msg2 = "At least one chore needs to be selected.\n\n";
            	}
            	
            	if(gender == null)
            	{
            		warning = true;
            		msg3 = "Select a gender.\n\n";
            	}
            	          	
            	if(userType == null)
            	{
            		warning = true;
            		msg5 = "Select a user type.\n\n";
            	}
            	
            	if(userType != null && userType.equalsIgnoreCase("Child") && maxAge < minAge)
            	{
            		warning = true;
            		msg4 = "Maximum age is lower then minimum age.";
            	}
            	
            	// Display warning if something wasn't selected
            	// else add information to data to be sent to server, call server and display results
            	if(warning == true)
            	{
            		MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
            		dialog.setText("Warning");
            		dialog.setMessage(msg1 + msg2 + msg3 + msg4 + msg5);
            		dialog.open();
            	}
            	else
            	{
                	// Add data
            		data.add(SCREEN);
                	data.add(familyUsername);
                	data.add(location);
                	data.add(chores);
                	data.add(gender);
                	
                	if(userType.equalsIgnoreCase("Child"))
                	{
                		data.add(minAge);
                		data.add(maxAge);
                	}
                	else
                	{
                		data.add(0);
                		data.add(100);
                	}
                	
                	data.add(userType);
            		data.add(country);
            		data.add(state);
            		data.add(city);
                	
            		// Call server
                	text.setText("Processing your research request");
	            	ArrayList<Object> response = client.clientConnection(data);
	            	text.setText("YOUR SEARCH RESULTS\n\n");
	            	
	            	// Display results
	            	for(int i = 0; i < response.size(); i++)
	            	{
	            		ArrayList<Object> info = (ArrayList<Object>)response.get(i);
	            		System.out.println("here");
	            		
	            		for(int j = 0; j < info.size(); j++)
	            		{
	            			if(j == 0)
	            				text.append("For chore " + (String)info.get(0) + "\n");
	            				if(info.size() == 1)
	            					text.append("	There are no results\n\n");
	            			else if(j == 1)
	            				text.append("The results are based on  " + (int)info.get(1) + " families, ");
	            			else if(j == 2)
	            				text.append((int)info.get(2) + " individuals, ");
	            			else if(j == 3)
	            				text.append("and being completed " + (int)info.get(3) + " times.\n");
	            			else if(j == 4)
	            				text.append("\tTime Results\n\t\tMinimum time taken = " + (Time)info.get(4) + "\n");
	            			else if(j == 5)
	            				text.append("\t\tAverage time taken = " + (Time)info.get(5) + "\n");
	            			else if(j == 6)
	            				text.append("\t\tMaximum time taken = " + (Time)info.get(6) + "\n");
	            			else if(j == 7)
	            				text.append("\tAllowance Results\n\t\tMinimum allowance = $" + ((String)info.get(7) + "\n"));
	            			else if(j == 8)
	            				text.append("\t\tAverage allowance = $" + (String)info.get(8) + "\n");
	            			else if(j == 9)
	            				text.append("\t\tMaximum allowance = $" + (String)info.get(9) + "\n");
	            			else if(j == 10)
	            				text.append("\tMedian Values\n\t\tMedian time = " + (Time)info.get(10) + "\n");
	            			else if(j == 11)
	            				text.append("\t\tMedian allowance = $" + (String)info.get(11) + "\n\n");
	            		}
	            	}
            	}
            }             
        });
        
		// Close screen 
        cancelButton.addSelectionListener(new SelectionAdapter()  
        {      	 
            @Override
            public void widgetSelected(SelectionEvent e) 
            {
            	shell.dispose();
            }             
        });
        
		return resultsGroup;        
	}
	
	/**
	 * Sets the layout of the window positioning each group for the final display.
	 * 
	 * @param locationGroup
	 * @param choreGroup
	 * @param genderGroup
	 * @param ageGroup
	 * @param typeGroup
	 * @param resultsGroup
	 */
	private void setLayout(Group locationGroup, Group choreGroup, Group genderGroup, Group ageGroup, Group typeGroup, Group resultsGroup)
	{
		FormData data = new FormData(SWT.DEFAULT, 100);
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		locationGroup.setLayoutData(data);
		
		data = new FormData(150, 100);
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(locationGroup, 10);
		choreGroup.setLayoutData(data);

		data = new FormData(SWT.DEFAULT, 100);
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(choreGroup, 10);
		genderGroup.setLayoutData(data);
		
		data = new FormData(SWT.DEFAULT, 100);
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(genderGroup, 10);
		typeGroup.setLayoutData(data);
		
		data = new FormData(SWT.DEFAULT, 100);
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(typeGroup, 10);
		ageGroup.setLayoutData(data);
		
		data = new FormData(520, 300);
		data.top = new FormAttachment(choreGroup, 10);
		data.left = new FormAttachment(5, 0);
		resultsGroup.setLayoutData(data);
		
		data = new FormData(SWT.DEFAULT, SWT.DEFAULT);
		data.top = new FormAttachment(resultsGroup, 10);
		data.left = new FormAttachment(5, 0);
		submitButton.setLayoutData(data);
		
		data = new FormData(SWT.DEFAULT, SWT.DEFAULT);
		data.top = new FormAttachment(resultsGroup, 10);
		data.left = new FormAttachment(submitButton, 10);
		cancelButton.setLayoutData(data);
	}
}