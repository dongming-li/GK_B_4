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
 * Class for displaying the Define chore screen.
 * Class contacts the database via client/server code to pull non-defined chores and to define selected chores.
 * 
 * @author GK_B_4_ChoreManager
 *
 */
public class DefineChore {

	static Image logo;
	Combo  chore;
	
	private String familyUsername;
	private String parentUser;
	
	/**
	 * Constructor for the screen, takes in a family name and parent who is defining the chore.
	 * 
	 * @param familyName
	 * @param parentName
	 */
	protected DefineChore(String familyName, String parentName) {
		
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
	 * Creates the main DefineChore UI shell
	 * 
	 * @param display
	 * 
	 * @return The created shell
	 */
	private Shell createShell(Display display) {
		
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(750, 750);
		shell.setText("Define a Chore");
		shell.setImage(logo);
		
		Label choreNameLabel = new Label(shell, SWT.NONE);
		choreNameLabel.setText("New Chore Name:");
		
		Label prereqLabel = new Label(shell, SWT.NONE);
		prereqLabel.setText("Prerequisites (Max 5):");
		Text reqText1 = new Text(shell, SWT.SINGLE | SWT.BORDER);
		Text reqText2 = new Text(shell, SWT.SINGLE | SWT.BORDER);
		Text reqText3 = new Text(shell, SWT.SINGLE | SWT.BORDER);
		Text reqText4 = new Text(shell, SWT.SINGLE | SWT.BORDER);
		Text reqText5 = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label allowanceLabel = new Label(shell, SWT.NONE);
		allowanceLabel.setText("Allowance (Format: ##.##): $");
		Text allowanceText = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		Label descriptionLabel = new Label(shell, SWT.NONE);
		descriptionLabel.setText("Chore Description:");
		Text descText = new Text(shell, SWT.SINGLE | SWT.BORDER);
		
		
		
		Button submitButton = new Button(shell, SWT.NONE);
		submitButton.setText("Submit");
		
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		
		//Retireve the list of chores that can be defined
		chore = new Combo(shell, SWT.DROP_DOWN | SWT.BORDER);
		ArrayList<Object> jobs = new ArrayList<Object>();
		ArrayList<Object> defined = new ArrayList<Object>();
		jobs.add(9903);
		ArrayList<Object> response = client.clientConnection(jobs);
		defined.add(9913);
		defined.add(familyUsername);
		ArrayList<Object> response2 = client.clientConnection(defined);
		for(int i = 0; i < response.size(); i++)
		{
			if(!response2.contains(response.get(i)))
			{
				chore.add((String)response.get(i));
			}
		}
		
		/*
		 * Layouts
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		choreNameLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(choreNameLabel, 5);
		data.left = new FormAttachment(5, 0);
		chore.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(10, 20);
		data.left = new FormAttachment(5, 0);
		prereqLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(prereqLabel, 5);
		data.left = new FormAttachment(5, 0);
		reqText1.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqText1, 5);
		data.left = new FormAttachment(5, 0);
		reqText2.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqText2, 5);
		data.left = new FormAttachment(5, 0);
		reqText3.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqText3, 5);
		data.left = new FormAttachment(5, 0);
		reqText4.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqText4, 5);
		data.left = new FormAttachment(5, 0);
		reqText5.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqText5, 10);
		data.left = new FormAttachment(5, 0);
		allowanceLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqText5, 10);
		data.left = new FormAttachment(allowanceLabel, 5);
		allowanceText.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(allowanceLabel, 10);
		data.left = new FormAttachment(5, 0);
		descriptionLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(descriptionLabel, 5);
		data.left = new FormAttachment(5, 0);
		descText.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(descText, 20);
		data.left = new FormAttachment(5, 0);
		submitButton.setLayoutData(data);
	
		submitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
				ArrayList<Object> reqs = new ArrayList<Object>();
				
				String req1 = reqText1.getText();
				if (!req1.equals("")) {
					reqs.add(req1);
				}
				String req2 = reqText2.getText();
				if (!req2.equals("")) {
					reqs.add(req2);
				}
				String req3 = reqText3.getText();
				if (!req3.equals("")) {
					reqs.add(req3);
				}
				String req4 = reqText4.getText();
				if (!req4.equals("")) {
					reqs.add(req4);
				}
				String req5 = reqText5.getText();
				if (!req5.equals("")) {
					reqs.add(req5);
				}
				
				
				double allowance = Double.parseDouble(allowanceText.getText());
				String description = descText.getText();
				
				Shell cShell = confirmShell(display, chore.getText(), reqs, allowance, description);
				cShell.open();
				cShell.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent event) {
						cShell.dispose();
						shell.dispose();
					}
				});
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(descText, 20);
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
	 * Creates the confirmation UI shell using the given parameters.
	 * 
	 * @param display, name, reqs, allowance, description
	 * 
	 * @return The created shell
	 */
	private Shell confirmShell(Display display, String name, ArrayList<Object> reqs, Double allowance, String description) {
		Shell cshell = new Shell(display);
		FormLayout layout = new FormLayout();
		cshell.setLayout(layout);
		cshell.setSize(750, 750);
		cshell.setText("Confirm your definition");
		cshell.setImage(logo);
		
		/*
		 * Initialize the labels for confirmation
		 */
		
		Label confirmLabel = new Label(cshell, SWT.NONE);
		confirmLabel.setText("Are you sure you want to add the following chore?:");
		
		Label choreNameLabel = new Label(cshell, SWT.NONE);
		choreNameLabel.setText(name);

		Label reqLabel = new Label(cshell, SWT.NONE);
		reqLabel.setText("Prerequisites:");
		
		Label reqLabel1 = new Label(cshell, SWT.NONE);	
		reqLabel1.setVisible(false);
		
		Label reqLabel2 = new Label(cshell, SWT.NONE);	
		reqLabel2.setVisible(false);
		
		Label reqLabel3 = new Label(cshell, SWT.NONE);
		reqLabel3.setVisible(false);
		
		Label reqLabel4 = new Label(cshell, SWT.NONE);
		reqLabel4.setVisible(false);
		
		Label reqLabel5 = new Label(cshell, SWT.NONE);
		reqLabel5.setVisible(false);
		
		/*
		 * Create the requirement labels, depending on how many requirements the chore has
		 */
		
		switch (reqs.size()) {
		
		case 1:
			reqLabel1.setText("1.) " + (String)reqs.get(0));
			reqLabel1.setVisible(true);
			break;
		case 2:
			reqLabel1.setText("1.) " + (String)reqs.get(0));
			reqLabel2.setText("2.) " + (String)reqs.get(1));
			reqLabel1.setVisible(true);
			reqLabel2.setVisible(true);
			break;
		case 3:
			reqLabel1.setText("1.) " + (String)reqs.get(0));
			reqLabel2.setText("2.) " + (String)reqs.get(1));
			reqLabel3.setText("3.) " + (String)reqs.get(2));
			reqLabel1.setVisible(true);
			reqLabel2.setVisible(true);
			reqLabel3.setVisible(true);
			break;
		case 4:
			reqLabel1.setText("1.) " + (String)reqs.get(0));
			reqLabel2.setText("2.) " + (String)reqs.get(1));
			reqLabel3.setText("3.) " + (String)reqs.get(2));
			reqLabel4.setText("4.) " + (String)reqs.get(3));
			reqLabel1.setVisible(true);
			reqLabel2.setVisible(true);
			reqLabel3.setVisible(true);
			reqLabel4.setVisible(true);
			break;
		case 5:
			reqLabel1.setText("1.) " + (String)reqs.get(0));
			reqLabel2.setText("2.) " + (String)reqs.get(1));
			reqLabel3.setText("3.) " + (String)reqs.get(2));
			reqLabel4.setText("4.) " + (String)reqs.get(3));
			reqLabel5.setText("5.) " + (String)reqs.get(4));
			reqLabel1.setVisible(true);
			reqLabel2.setVisible(true);
			reqLabel3.setVisible(true);
			reqLabel4.setVisible(true);
			reqLabel5.setVisible(true);
			break;
		default:
			
		}
		
		Label allowanceLabel = new Label(cshell, SWT.NONE);
		allowanceLabel.setText("Allowance: $" + allowance);
		
		Label ErrorMessage = new Label(cshell, SWT.NONE);
		
		ErrorMessage.setText("Oops, something went wrong.");
		Color Red = display.getSystemColor(SWT.COLOR_RED);
		ErrorMessage.setForeground(Red);
		ErrorMessage.setVisible(false);
		Label descLabel = new Label(cshell, SWT.NONE);
		descLabel.setText("Description: " + description);
		
		Button confirmButton = new Button(cshell, SWT.NONE);
		confirmButton.setText("Confirm");
		
		Button cancelButton = new Button(cshell, SWT.NONE);
		cancelButton.setText("Cancel");
		
		/*
		 * Layouts
		 */
		
		FormData data = new FormData();
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		confirmLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(confirmLabel, 10);
		data.left = new FormAttachment(5, 0);
		choreNameLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(choreNameLabel, 10);
		data.left = new FormAttachment(5, 0);
		reqLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqLabel, 5);
		data.left = new FormAttachment(5, 0);
		reqLabel1.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqLabel1, 5);
		data.left = new FormAttachment(5, 0);
		reqLabel2.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqLabel2, 5);
		data.left = new FormAttachment(5, 0);
		reqLabel3.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqLabel3, 5);
		data.left = new FormAttachment(5, 0);
		reqLabel4.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqLabel4, 5);
		data.left = new FormAttachment(5, 0);
		reqLabel5.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(reqLabel5, 10);
		data.left = new FormAttachment(5, 0);
		allowanceLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(allowanceLabel, 10);
		data.left = new FormAttachment(5, 0);
		descLabel.setLayoutData(data);
		
		data = new FormData();
		data.top = new FormAttachment(descLabel, 20);
		data.left = new FormAttachment(5, 0);
		confirmButton.setLayoutData(data);
		data = new FormData();
		data.top = new FormAttachment(confirmButton, 10);
		data.left = new FormAttachment(5, 0);
		ErrorMessage.setLayoutData(data);
		confirmButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ArrayList<Object> send = new ArrayList<Object>();
				send.add(9900);
				send.add(familyUsername);
				send.add(chore.getText());
				send.add(reqs);
				send.add(allowance);
				send.add(description);
				ArrayList<Object> response = client.clientConnection(send);
				
				if ((Boolean)response.get(0)) {
					cshell.dispose();
				} else {
					ErrorMessage.setVisible(true);
				}
			}
		});
		
		data = new FormData();
		data.top = new FormAttachment(descLabel, 20);
		data.left = new FormAttachment(confirmButton, 10);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				cshell.dispose();
			}
		});
		
		return cshell;
	}
	
}