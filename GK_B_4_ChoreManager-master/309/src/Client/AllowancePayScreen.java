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
 * Class for Displaying the total owed allowances for Parents
 * Class contacts the database via client/server code to pull the allowance values.
 * 
 * @author GK_B_4ChoreManager
 *
 */
public class AllowancePayScreen {

	static Image logo;
	
	private String familyUsername;
	
	private Double toPay = 0.0;
	private Double paying = 0.0;
	private Boolean improperInput = true;
	private Boolean improperAmount = true;
	
	/**
	 * 
	 * @param famName
	 */
	protected AllowancePayScreen(String famName) {
		familyUsername = famName;
		
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
	 * Creates the UI shell for displaying the allowances
	 * 
	 * @param display
	 * @return The created shell object
	 */
	@SuppressWarnings("unchecked")
	private Shell createShell(Display display) {
		Shell shell = new Shell(display);
		FormLayout layout = new FormLayout();
		shell.setLayout(layout);
		shell.setSize(500, 500);
		shell.setText("Pay Allowances");
		shell.setImage(logo);
		
		ArrayList<Object> send = new ArrayList<Object>();
		send.add(9919);
		send.add(familyUsername);
		ArrayList<Object> response = client.clientConnection(send);
		
		Label userLabel = new Label(shell, SWT.NONE);
		userLabel.setText("Select a user to pay:");
		
		Combo userCombo = new Combo(shell, SWT.NONE);
		for (int i = 0; i < response.size(); i++) {
			userCombo.add((String)((ArrayList<Object>)response.get(i)).get(0));
		}
		
		Label owedLabel = new Label(shell, SWT.NONE);
		owedLabel.setText("Amount owed to selected user: " + toPay);
		
		Label ErrorMessage = new Label(shell, SWT.NONE);
		ErrorMessage.setText("Oops, something went wrong.");
		Color Red = display.getSystemColor(SWT.COLOR_RED);
		ErrorMessage.setForeground(Red);
		ErrorMessage.setVisible(false);
		
		Label payLabel = new Label(shell, SWT.NONE);
		payLabel.setText("Enter amount to pay: ");
		
		Text payText = new Text(shell, SWT.SINGLE | SWT.BORDER);
		payText.setEnabled(false);
		
		Button submitButton = new Button(shell, SWT.PUSH);
		submitButton.setText("Submit");
		
		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		
		/*
		 * 
		 */
		
		FormData data = new FormData();	
		data.top = new FormAttachment(5, 0);
		data.left = new FormAttachment(5, 0);
		userLabel.setLayoutData(data);
		
		data = new FormData();	
		data.top = new FormAttachment(userLabel, 5);
		data.left = new FormAttachment(5, 0);
		userCombo.setLayoutData(data);
		userCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				payText.setText("");
				for (int i = 0; i < response.size(); i++) {
					if (userCombo.getText().equals("")) {
						payText.setEnabled(false);
						toPay = 0.0;
						paying = 0.0;
					} else if (userCombo.getText().equals((String)((ArrayList<Object>)response.get(i)).get(0))) {
						toPay = 0.0;
						paying = 0.0;
						toPay = (Double)((ArrayList<Object>)response.get(i)).get(1);
						owedLabel.setText("Amount owed to selected user: $" + toPay);
						payText.setEnabled(true);
						shell.layout();
					}
				}
			}
		});
		
		data = new FormData();	
		data.top = new FormAttachment(userCombo, 5);
		data.left = new FormAttachment(5, 0);
		owedLabel.setLayoutData(data);
		
		data = new FormData();	
		data.top = new FormAttachment(owedLabel, 5);
		data.left = new FormAttachment(5, 0);
		payLabel.setLayoutData(data);
		
		data = new FormData();	
		data.top = new FormAttachment(payLabel, 5);
		data.left = new FormAttachment(5, 0);
		payText.setLayoutData(data);
		payText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					improperInput = false;
					paying = Double.parseDouble(payText.getText());
				} catch (NumberFormatException n) {
					improperInput = true;
					System.out.println("Improper input");
				}
				
				if (paying > toPay || paying < 0.0) {
					improperAmount = true;
				} else {
					improperAmount = false;
				}
			}
		});
		
		data = new FormData();	
		data.top = new FormAttachment(payText, 20);
		data.left = new FormAttachment(5, 0);
		submitButton.setLayoutData(data);
		data = new FormData();
		data.top = new FormAttachment(submitButton, 10);
		data.left = new FormAttachment(5, 0);
		ErrorMessage.setLayoutData(data);
		submitButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (improperInput || improperAmount) {
					ErrorMessage.setVisible(true);
				} else {
					ArrayList<Object> send = new ArrayList<Object>();
					send.add(9920);
					send.add(familyUsername);
					send.add(userCombo.getText());
					send.add(paying);
					ArrayList<Object> response = client.clientConnection(send);
					
					if ((Boolean)response.get(0)) {
						shell.dispose();
					} else {
						ErrorMessage.setVisible(true);
					}
				}
			}
		});
		
		data = new FormData();	
		data.top = new FormAttachment(payText, 20);
		data.left = new FormAttachment(submitButton, 10);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});
		
		return shell;
	}

	
}
