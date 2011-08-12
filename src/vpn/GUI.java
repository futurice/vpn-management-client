package vpn;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class GUI extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	// GUI elements
	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;
	private JPanel navigationButtons;
	private Box buttonsBox;
	private JPanel contentPanel;
	private CardLayout contentLayout;
	private JComboBox employmentBox;
	private JTextField ldapUserField, emailField, smsPasswordField;
	private JComboBox computerBox, ownerBox;
	private JTextArea doneStatus;
	private JPasswordField ldapPassField, vpnPassField, vpnPassField2;
	private JLabel hint, statusLabel;

	private GUI gui;

	// To keep track of which page we are on
	private int state;

	// The information we collect
	private String employmentStatus;
	private String owner;
	private String computerType;
	private String ldapUser;
	private String ldapPassword;
	private String vpnPassword;
	private String email;
	private String smsPassword;

	// The options to choose from
	private String[] employmentOptions = { "Choose one.", "Employee",
			"External" };
	private String[] computerOptions = { "Choose one.", "Laptop", "Desktop",
			"Mobile" };
	private String[] ownerOptions = { "Choose one.", "Futurice", "Home" };

	private static int INTRO = 0;
	private static int FORM = 1;
	private static int LOADING = 2;
	private static int PASSWORD = 3;
	private static int LOADING2 = 4;
	private static int DONE = 5;

	// Main program
	private Configurator config;

	/**
	 * The GUI for the configuration wizard.
	 * 
	 * @param configurator
	 */

	public GUI(Configurator configurator) {

		// Cofigurator
		this.config = configurator;

		// Set some options for this frame
		this.setTitle("Futurice VPN Configuration Wizard");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				reallyQuit((JFrame) e.getSource());
			}
		});
		this.setLayout(new BorderLayout());

		// Initializing
		this.employmentStatus = null;
		this.owner = null;
		this.computerType = null;
		this.ldapPassField = null;
		this.ldapUserField = null;
		this.vpnPassword = null;
		this.email = null;
		this.gui = this;

		// Buttons
		this.backButton = new JButton("Back");
		this.backButton.addActionListener(this);
		this.nextButton = new JButton("Next");
		this.nextButton.addActionListener(this);
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this);

		// Panel to keep the buttons
		this.navigationButtons = new JPanel();
		this.navigationButtons.setLayout(new BorderLayout());
		this.navigationButtons.add(new JSeparator(), BorderLayout.NORTH);

		// Box to organize the buttons
		this.buttonsBox = new Box(BoxLayout.X_AXIS);
		this.buttonsBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		this.buttonsBox.add(this.backButton);
		this.buttonsBox.add(Box.createHorizontalStrut(5));
		this.buttonsBox.add(this.nextButton);
		this.buttonsBox.add(Box.createHorizontalStrut(30));
		this.buttonsBox.add(this.cancelButton);

		this.navigationButtons.add(this.buttonsBox, java.awt.BorderLayout.EAST);

		// Panel for actual content
		this.contentPanel = new JPanel();
		this.contentPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		this.contentLayout = new CardLayout();
		this.contentPanel.setLayout(this.contentLayout);

		this.add(this.contentPanel, BorderLayout.NORTH);
		this.add(this.navigationButtons, BorderLayout.SOUTH);

		// Initialize the different stages
		this.setIntroView();
		this.setFormView();
		this.setLoadingView();
		this.setPasswordView();
		this.setDoneView();

		this.state = 0;

		this.setMinimumSize(new Dimension(500, 500));
		this.pack();
		this.setLocationByPlatform(true);
		this.setVisible(true);
	}

	/**
	 * Set up the first page of the wizard
	 */
	public void setIntroView() {
		JPanel introPanel = new JPanel();
		JTextArea introPane = new JTextArea();
		introPane.setLineWrap(true);
		introPane.setWrapStyleWord(true);
		introPane.setText(this.config.getIntroText());
		introPane.setOpaque(false);
		introPane.setSize(400, 500);
		introPanel.add(introPane);

		this.backButton.setEnabled(false);
		
		if (Generator.openSSLPath()==null)
			this.nextButton.setEnabled(false);

		this.contentPanel.add(introPanel, "intro");
		this.contentLayout.show(this.contentPanel, "intro");
	}

	/**
	 * Set up the page where we ask for the information
	 */
	public void setFormView() {
		JPanel formPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		// Header
		c.gridy = 0;
		c.gridx = 0;
		JLabel header = new JLabel("Please fill out the form below.");
		formPanel.add(header, c);

		// LDAP username
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel ldapU = new JLabel("Futurice username: ");
		formPanel.add(ldapU, c);
		this.ldapUserField = new JTextField(20);
		this.ldapUserField.addKeyListener(this);
		
		if (this.config.getUser().length()== 4){
			this.ldapUserField.setText(this.config.getUser());
		}
		
		
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(this.ldapUserField, c);

		// LDAP password
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel ldapP = new JLabel("Futurice password: ");
		formPanel.add(ldapP, c);
		this.ldapPassField = new JPasswordField(20);
		this.ldapPassField.addKeyListener(this);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(this.ldapPassField, c);

		// Employment dropdown
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel employment = new JLabel("Employment status: ");
		formPanel.add(employment, c);
		employmentBox = new JComboBox(this.employmentOptions);
		employmentBox.addActionListener(this);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(employmentBox, c);

		// Owner
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel owner = new JLabel("Computer owner: ");
		formPanel.add(owner, c);
		ownerBox = new JComboBox(this.ownerOptions);
		ownerBox.addActionListener(this);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(ownerBox, c);

		// Computer type
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel computer = new JLabel("Computer type: ");
		formPanel.add(computer, c);
		computerBox = new JComboBox(computerOptions);
		computerBox.addActionListener(this);
		computer.setLabelFor(computerBox);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(computerBox, c);

		// VPN password
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel vpnP = new JLabel("Choose VPN password: ");
		formPanel.add(vpnP, c);
		this.vpnPassField = new JPasswordField(20);
		this.vpnPassField.addKeyListener(this);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(this.vpnPassField, c);

		// VPN password confirmation
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel vpnP2 = new JLabel("Confirm VPN password: ");
		formPanel.add(vpnP2, c);
		this.vpnPassField2 = new JPasswordField(20);
		this.vpnPassField2.addKeyListener(this);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(this.vpnPassField2, c);

		// email
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel emailL = new JLabel("Email: ");
		formPanel.add(emailL, c);
		this.emailField = new JTextField(20);
		this.emailField.addKeyListener(this);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(this.emailField, c);

		// hint
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth=2;
		this.hint = new JLabel("");
		formPanel.add(hint, c);
		
		// hint
		c.gridx = 0;
		c.gridy+=2;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth=2;
		JLabel n = new JLabel("When you click next, a password will be sent to your phone.");
		formPanel.add(n, c);

		this.contentPanel.add(formPanel, "form");
	}

	/**
	 * Set up a "Loading" page to show while we wait
	 */
	public void setLoadingView() {
		JPanel loadingPanel = new JPanel();
		JLabel loadingLabel = new JLabel("Please wait.");
		loadingPanel.add(loadingLabel);
		this.statusLabel = new JLabel("Status");
		loadingPanel.add(this.statusLabel);
		this.contentPanel.add(loadingPanel, "loading");
	}

	public void setPasswordView() {
		JPanel passwordPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		// Header (receive)
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 2;
		JLabel header = new JLabel(
				"Please enter the password you received by sms.");
		passwordPanel.add(header, c);
		c.gridwidth = 1;

		// LDAP username
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel label = new JLabel("Password: ");
		passwordPanel.add(label, c);
		this.smsPasswordField = new JTextField(20);
		this.smsPasswordField.addKeyListener(this);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		passwordPanel.add(this.smsPasswordField, c);

		this.contentPanel.add(passwordPanel, "password");
	}

	/**
	 * Set up the last page of the wizard
	 */
	public void setDoneView() {
		JPanel donePanel = new JPanel();
		doneStatus = new JTextArea();
		doneStatus.setOpaque(false);
		doneStatus.setLineWrap(true);
		doneStatus.setWrapStyleWord(true);
		doneStatus.setSize(400, 500);
		doneStatus.setText(this.config.getFinishingText());
		donePanel.add(doneStatus);
		this.contentPanel.add(donePanel, "done");
	}

	/**
	 * With this we move forward in the wizard
	 */
	public void next() {
		switch (this.state) {
	
		// If we're on the first page
		case 0:
			this.contentLayout.show(this.contentPanel, "form");
			this.nextButton.setEnabled(false);
			this.backButton.setEnabled(true);
			this.state = FORM;
			this.checkForm();
			break;
	
		// On the second page, filling the form
		case 1:
			this.contentLayout.show(this.contentPanel, "loading");
			this.nextButton.setEnabled(false);
			this.backButton.setEnabled(false);
			this.state = LOADING;
			this.sendForm();
			break;
	
		// On the Please wait -page
		case 2:
			this.contentLayout.show(this.contentPanel, "password");
			this.backButton.setEnabled(true);
			this.state = PASSWORD;
			break;
	
		// On the password page
		case 3:
			this.contentLayout.show(this.contentPanel, "loading");
			this.sendPassword();
			this.backButton.setEnabled(false);
			this.nextButton.setEnabled(false);
			this.state = LOADING2;
			break;
	
		// Loading page again
		case 4:
			this.contentLayout.show(this.contentPanel, "done");
			this.doneStatus.setText(this.config.getFinishingText());
			this.backButton.setEnabled(false);
			this.nextButton.setEnabled(false);
			this.cancelButton.setText("Finish");
			this.state = DONE;
			break;
		}
		this.repaint();
	}

	/**
	 * With this we move back to the previous stage of the wizard
	 */
	public void back() {
		switch (this.state) {
		// While filling the form
		case 1:
			this.contentLayout.show(this.contentPanel, "intro");
			this.nextButton.setEnabled(true);
			this.state = INTRO;
			break;
	
		// Loading screen
		case 2:
			this.contentLayout.show(this.contentPanel, "form");
			this.state = FORM;
			this.checkForm();
			break;
	
		// Password screen
		case 3:
			this.contentLayout.show(this.contentPanel, "form");
			this.state = FORM;
			this.checkForm();
			break;
			
		case 4:
			this.contentLayout.show(this.contentPanel, "password");
			this.state = PASSWORD;
			break;
	
		// Done screen
		case 5:
			this.contentLayout.show(this.contentPanel, "password");
			this.state = PASSWORD;
			break;
	
		}
		this.repaint();
	}

	/**
	 * Check that the values in the form are not empty
	 */
	public void checkForm() {
		this.updateValues();
		if (this.owner != null
				&& !this.owner.isEmpty()
				&& this.employmentStatus != null
				&& !this.employmentStatus.isEmpty()
				&& this.computerType != null
				&& !this.computerType.isEmpty()
				&& this.ldapUser != null
				&& !this.ldapUser.isEmpty()
				&& this.ldapPassword != null
				&& !this.ldapPassword.isEmpty()
				&& this.vpnPassword != null
				&& !this.vpnPassword.isEmpty()
				&& this.email != null
				&& !this.email.isEmpty()
				&& this.vpnPassword.equals(String
						.copyValueOf(this.vpnPassField2.getPassword()))) {
			this.nextButton.setEnabled(true);
			this.hint.setText("");
		} else {
			if (!this.vpnPassword.equals(String
						.copyValueOf(this.vpnPassField2.getPassword()))){
				this.hint.setText("Make sure the VPN passwords are identical.");
			} else {
				this.hint.setText("");
			}
			this.nextButton.setEnabled(false);
		}
	}

	private void updateValues() {
		this.ldapUser = this.ldapUserField.getText();
		if (this.ldapUser != null)
			this.emailField.setText(this.ldapUser+"@futurice.com");
		this.ldapPassword = String
				.copyValueOf(this.ldapPassField.getPassword());
		this.vpnPassword = String.copyValueOf(this.vpnPassField.getPassword());
		this.email = this.emailField.getText();
	}

	/**
	 * This method sends the information to the configurator, which sends it to
	 * the server.
	 */
	public void sendForm() {

		// Send the form in a new thread so the GUI won't stall
		Thread send = new Thread() {
			public void run() {
				gui.returnMessage(config.askForSettings(ldapUser, ldapPassword,
						vpnPassword, computerType, email, owner,
						employmentStatus));
			}
		};
		send.start();
		
		Thread statusUpdates = new Thread(){
			public void run(){
				while(gui.state == LOADING){
					gui.statusLabel.setText(gui.config.getStatus());
					gui.repaint();
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		statusUpdates.start();

	}

	/**
	 * This method sends the password to the back end
	 */
	public void sendPassword() {

		// New Thread to keep gui responsive
		Thread send = new Thread() {
			public void run() {
				gui.returnMessage(config.enterPassword(smsPassword));
			}
		};
		send.start();
		
		Thread statusUpdates = new Thread(){
			public void run(){
				while(gui.state == LOADING2){
					gui.statusLabel.setText(gui.config.getStatus());
					gui.repaint();
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		statusUpdates.start();

	}

	/**
	 * This method is called after the interaction with the back end is complete
	 * 
	 * @param message
	 */
	public void returnMessage(String message) {
		if (message != null) {
			JOptionPane.showConfirmDialog(this,
					"There was an error when sending the information:\n"
							+ message, "Error while sending information.",
					JOptionPane.DEFAULT_OPTION);
			this.back();
		} else {
			this.next();
		}
	}

	/**
	 * Show confirmation alert when trying to exit
	 * 
	 * @param frame
	 */
	public static void reallyQuit(JFrame frame) {
		int n = JOptionPane.showConfirmDialog(frame,
				"Are you sure you want to exit the wizard?", "Exit?",
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	/**
	 * Listen for events
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == this.cancelButton) {
			if (this.state >= 4) {
				System.exit(0);
			}
			reallyQuit(this);
			
		} else if (source == this.nextButton) {
			this.next();
			
		} else if (source == this.backButton) {
			this.back();

			// Check the employment status
		} else if (source == this.employmentBox) {
			if (this.employmentBox.getSelectedIndex() != 0) {
				this.employmentStatus = this.employmentOptions[this.employmentBox
						.getSelectedIndex()];
			} else {
				this.employmentStatus = null;
			}
			this.checkForm();

			// Check the computer
		} else if (source == this.computerBox) {
			if (this.computerBox.getSelectedIndex() != 0) {
				this.computerType = this.computerOptions[this.computerBox
						.getSelectedIndex()];
			} else {
				this.computerType = null;
			}
			this.checkForm();

			//Check the owner
		} else if (source == this.ownerBox) {
			if (this.ownerBox.getSelectedIndex() != 0) {
				this.owner = this.ownerOptions[this.ownerBox.getSelectedIndex()];
				
			} else {
				this.owner = null;
			}
			this.checkForm();

		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (arg0.getSource() == this.smsPasswordField) {
			this.smsPassword = this.smsPasswordField.getText();
			if (!this.smsPassword.isEmpty()) {
				this.nextButton.setEnabled(true);
			}
		} else {
			this.checkForm();
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

}
