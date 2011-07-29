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
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class GUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	//GUI elements
	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;
	private JPanel navigationButtons;
	private Box buttonsBox;
	private JPanel contentPanel;
	private CardLayout contentLayout;
	private JComboBox employmentBox;
	private JTextField ownerField;
	private JComboBox computerBox;
	private JTextArea doneStatus;

	//Text shown on the first page of the wizard
	private String introText = "This wizard will guide you through"
			+ " the process of creating the configurations needed to access"
			+ " the Futurice VPN service.";

	//To keep track of which page we are on
	private int state;

	//The information we collect
	private String employmentStatus;
	private String ownerName;
	private String computerType;

	//The options to choose from
	private String[] employmentOptions = { "Choose one.", "Employee",
			"External" };
	private String[] computerOptions = { "Choose one.", "Laptop", "Desktop",
			"Mobile phone" };
	
	//Main program
	private Configurator config;
	
	/**
	 * The GUI for the configuration wizard.
	 * @param configurator
	 */

	public GUI(Configurator configurator) {
		this.config = configurator;
		
		//Set some options for this frame
		this.setTitle("Futurice VPN Configuration Wizard");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				reallyQuit((JFrame) e.getSource());
			}
		});
		this.setLayout(new BorderLayout());

		this.employmentStatus = null;
		this.ownerName = null;
		this.computerType = null;

		this.backButton = new JButton("Back");
		this.backButton.addActionListener(this);
		this.nextButton = new JButton("Next");
		this.nextButton.addActionListener(this);
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this);

		//Panel to keep the buttons
		this.navigationButtons = new JPanel();
		this.navigationButtons.setLayout(new BorderLayout());
		this.navigationButtons.add(new JSeparator(), BorderLayout.NORTH);

		//Box to organize the buttons
		this.buttonsBox = new Box(BoxLayout.X_AXIS);
		this.buttonsBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		this.buttonsBox.add(this.backButton);
		this.buttonsBox.add(Box.createHorizontalStrut(5));
		this.buttonsBox.add(this.nextButton);
		this.buttonsBox.add(Box.createHorizontalStrut(30));
		this.buttonsBox.add(this.cancelButton);

		this.navigationButtons.add(this.buttonsBox, java.awt.BorderLayout.EAST);

		//Panel for actual content
		this.contentPanel = new JPanel();
		this.contentPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		this.contentLayout = new CardLayout();
		this.contentPanel.setLayout(this.contentLayout);

		this.add(this.contentPanel, BorderLayout.NORTH);
		this.add(this.navigationButtons, BorderLayout.SOUTH);

		//Initialize the different stages
		this.setIntro();
		this.setForm();
		this.setLoading();
		this.setDone();

		this.state = 0;

		this.setMinimumSize(new Dimension(500, 500));
		this.setLocationByPlatform(true);
		this.setVisible(true);
	}

	/**
	 * Set up the first page of the wizard
	 */
	public void setIntro() {
		JPanel introPanel = new JPanel();
		JTextArea introPane = new JTextArea();
		introPane.setText(this.introText);
		introPane.setLineWrap(true);
		introPane.setWrapStyleWord(true);
		introPane.setSize(400, 400);
		introPanel.add(introPane);

		this.backButton.setEnabled(false);

		this.contentPanel.add(introPanel, "intro");
		this.contentLayout.show(this.contentPanel, "intro");
	}

	/**
	 * Set up the page where we ask for the information
	 */
	public void setForm() {
		JPanel formPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		// Header
		c.gridy = 0;
		c.gridx = 0;
		JLabel header = new JLabel("Please fill out the form below.");
		formPanel.add(header, c);
		c.gridy++;

		// Employment dropdown
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel employment = new JLabel("Employment status: ");
		formPanel.add(employment, c);
		employmentBox = new JComboBox(this.employmentOptions);
		employmentBox.addActionListener(this);
		employment.setLabelFor(employmentBox);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(employmentBox, c);

		// Owner
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel owner = new JLabel("Owner: ");
		formPanel.add(owner, c);
		ownerField = new JTextField(20);
		
		//We check the value after each key release
		ownerField.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent keyEvent) {}
			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				ownerName = ownerField.getText();
				checkForm();
			}
		});
		owner.setLabelFor(ownerField);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_START;
		formPanel.add(ownerField, c);

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

		this.contentPanel.add(formPanel, "form");
	}
	
	/**
	 * Set up a "Loading" page to show while we wait for the configuration
	 */
	public void setLoading(){
		JPanel loadingPanel = new JPanel();
		JLabel loadingLabel = new JLabel("Please wait.");
		loadingPanel.add(loadingLabel);
		this.contentPanel.add(loadingPanel, "loading");
	}

	/**
	 * Set up the last page of the wizard
	 */
	public void setDone(){
		JPanel donePanel = new JPanel();
		JLabel doneLabel = new JLabel("You are done! \\o/");
		doneStatus = new JTextArea();
		donePanel.add(doneLabel);
		this.contentPanel.add(donePanel, "done");
	}
	public void checkForm() {
		if (this.ownerName != null && this.ownerName.length() > 0
				&& this.employmentStatus != null
				&& this.employmentStatus.length() > 0
				&& this.computerType != null && this.computerType.length() > 0) {
			this.nextButton.setEnabled(true);
		} else {
			this.nextButton.setEnabled(false);
		}
	}

	/**
	 * With this we move forward in the wizard
	 */
	public void next() {
		switch (this.state) {
		case 0:
			this.contentLayout.show(this.contentPanel, "form");
			this.nextButton.setEnabled(false);
			this.backButton.setEnabled(true);
			this.state++;
			this.checkForm();
			break;

		case 1:
			this.contentLayout.show(this.contentPanel, "loading");
			this.nextButton.setEnabled(false);
			this.state++;
			break;
		}
		this.repaint();
	}

	/**
	 * With this we move back to the previous stage of the wizard
	 */
	public void back() {
		switch (this.state) {
		case 1:
			this.contentLayout.show(this.contentPanel, "intro");
			this.nextButton.setEnabled(true);
			this.state--;
			break;
			
		case 2:
			this.contentLayout.show(this.contentPanel, "form");
			this.state--;
			this.checkForm();
			break;
		}
	}

	/**
	 * Show confirmation alert when trying to exit
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

			// Check the owner
		} else if (source == this.ownerField) {
			if (this.ownerField.getText().length() > 0) {
				this.ownerName = this.ownerField.getText();
			} else {
				this.ownerName = null;
			}
			this.checkForm();

			// Check computer type
		} else if (source == this.computerBox) {
			if (this.computerBox.getSelectedIndex() != 0) {
				this.computerType = this.computerOptions[this.computerBox
						.getSelectedIndex()];
			} else {
				this.computerType = null;
			}
			this.checkForm();

		}

	}

}
