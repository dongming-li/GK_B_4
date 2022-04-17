package Client;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import java.util.ArrayList;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Class for handling a popup window if user selects other location in research.
 * Contacts database to get a list of locations via client/server code.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class OtherLocation 
{
	static Image logo;

	public static final int SCREEN = 999; // Reference for server
    private ArrayList<String> location = new ArrayList<String>();   // Holds location selected
	private Button submit;
	private Button cancel;
	private Combo country;
	private Combo state;
	private Combo city;
	
	/**
	 * Constructor for creating and displaying the window
	 */
	protected OtherLocation() 
	{
		Display popup = Display.getDefault();
		
		logo = popup.getSystemImage(SWT.ICON_QUESTION);
		Shell shell = createShell(popup);
		shell.open();
		
		while (!shell.isDisposed()) 
		{
			if (!popup.readAndDispatch())
				popup.sleep();
		}
	}
	
	/**
	 * Creates the shell within the display window. This shell displays choices for the users and records their selection.
	 * Contacts the database via client/server coder to get list of locations.
	 * @param popup
	 * @return
	 */
	private Shell createShell(Display popup)
	{
		Shell shell = new Shell(popup);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(300, 250);
		shell.setText("Other Location");
		shell.setImage(logo);
		
		// Displays location options to choose from and a submit or cancel button
		Label countryLabel = new Label(shell, SWT.NONE);
		countryLabel.setText("Country");
		
		Label stateLabel = new Label(shell, SWT.NONE);
		stateLabel.setText("State");
		
		Label cityLabel = new Label(shell, SWT.NONE);
		cityLabel.setText("City");
		
		submit = new Button(shell, SWT.PUSH);
		submit.setText("Submit");
		
		cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");

		country = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
		state = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
		city = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);

		country.setText("Select a country first");
		state.setText("Select a country then state if desired  ");
		city.setText("Select a state then a city if desired      ");
		
		// Gets a list of countries from the database
		ArrayList<Object> countryReq = new ArrayList<Object>();
		countryReq.add(100);
		ArrayList<Object> response = client.clientConnection(countryReq);

		for (int i = 0; i < response.size(); i++) 
		{
			country.add((String)response.get(i));
		}
		
		// Layout of the screen for country
		FormData data = new FormData();
		data.top = new FormAttachment(1, 0);
		data.left = new FormAttachment(11,0);
		countryLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(countryLabel, 5);
		data.left = new FormAttachment(11,0);
		country.setLayoutData(data);
		
		// Listener to record country if selected and get list os states from database
		country.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				//Eventually link this to a "Selected Country" method
				System.out.println("Country: " + country.getText());
				ArrayList<Object> stateReq = new ArrayList<Object>();
				stateReq.add(101);
				stateReq.add(country.getText());
				ArrayList<Object> response = client.clientConnection(stateReq);
				state.removeAll();
				
				for (int i = 0; i < response.size(); i++)
				{
					state.add((String)response.get(i));
				}
			}
		});
		
		// Layout of the screen for state
		data = new FormData();
		data.top = new FormAttachment(country, 5);
		data.left = new FormAttachment(11, 0);
		stateLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(stateLabel, 5);
		data.left = new FormAttachment(11, 0);
		state.setLayoutData(data);
		
		// Listener to record state selected and get a list of cities from database
		state.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				//Eventually link this to a "Selected State" method
				System.out.println("State: " + state.getText());
				ArrayList<Object> cityReq = new ArrayList<Object>();
				cityReq.add(102);
				cityReq.add(state.getText());
				ArrayList<Object> response = client.clientConnection(cityReq);
				city.removeAll();
				
				for (int i = 0; i < response.size(); i++) 
				{
					city.add((String)response.get(i));
				}
			}
		});
		
		// Layout city information
		data = new FormData();
		data.top = new FormAttachment(state, 5);
		data.left = new FormAttachment(11, 0);
		cityLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(cityLabel, 5);
		data.left = new FormAttachment(11, 0);
		city.setLayoutData(data);
		
		// Listener to record city selected
		city.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				//Eventually link this to a "Selected City" method
				System.out.println("City: " + city.getText());
			}
		});

		// Layout submit button
		data = new FormData();
		data.top = new FormAttachment(city, 20);
		data.left = new FormAttachment(11,0);
		submit.setLayoutData(data);
		
		// Listener to record selected location and close window
		submit.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{

				if(!country.getText().equalsIgnoreCase("Select a country first"))
				{
					location.add(country.getText());
				}
				
				if(!state.getText().equalsIgnoreCase("Select a country then state if desired  ") && !state.getText().equals(""))
				{
					location.add(state.getText());
				}
				
				if(!city.getText().equalsIgnoreCase("Select a state then a city if desired      ") && !city.getText().equals(""))
				{
					location.add(city.getText());
				}
				
				shell.close();
			}
		});
		
		// Layout cancel button
		data = new FormData();
		data.top = new FormAttachment(city, 20);
		data.left = new FormAttachment(submit,10);
		cancel.setLayoutData(data);
		
		// Listener to close window
		cancel.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{

				shell.close();
			}
		});
		
		return shell;
	}
	
	/**
	 * Returns the selected location by the user
	 * 
	 * @return
	 */
	protected ArrayList<String> getData() 
	{	
		return location;
	}

}