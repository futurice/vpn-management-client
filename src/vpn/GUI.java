package vpn;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class GUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;
	private JPanel navigationButtons;
	private Box buttonsBox;
	private JPanel contentPanel;
	private CardLayout contentLayout;

	private String introText = "This wizard will guide you through"
			+ " the process of creating the configurations needed to access"
			+ " the Futurice VPN service.";

	private int state;

	public GUI() {
		this.setTitle("Futurice VPN Configuration Wizard");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				reallyQuit((JFrame) e.getSource());
			}
		});
		this.setLayout(new BorderLayout());

		this.backButton = new JButton("Back");
		this.backButton.addActionListener(this);
		this.nextButton = new JButton("Next");
		this.nextButton.addActionListener(this);
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this);

		this.navigationButtons = new JPanel();
		this.navigationButtons.setLayout(new BorderLayout());
		this.navigationButtons.add(new JSeparator(), BorderLayout.NORTH);

		this.buttonsBox = new Box(BoxLayout.X_AXIS);
		this.buttonsBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		this.buttonsBox.add(this.backButton);
		this.buttonsBox.add(Box.createHorizontalStrut(5));
		this.buttonsBox.add(this.nextButton);
		this.buttonsBox.add(Box.createHorizontalStrut(30));
		this.buttonsBox.add(this.cancelButton);

		this.navigationButtons.add(this.buttonsBox, java.awt.BorderLayout.EAST);

		this.contentPanel = new JPanel();
		this.contentPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
		this.contentLayout = new CardLayout();
		this.contentPanel.setLayout(this.contentLayout);

		this.add(this.contentPanel, BorderLayout.NORTH);
		this.add(this.navigationButtons, BorderLayout.SOUTH);

		this.setIntro();
		this.setForm();

		this.state = 0;

		this.setMinimumSize(new Dimension(500, 500));
		this.setLocationByPlatform(true);
		this.setVisible(true);
	}

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

	public void setForm() {
		JPanel formPanel = new JPanel();

		// Employment dropdown
		JLabel employment = new JLabel("Employment status:");
		String[] employmentOptions = { "Choose one.", "Employee", "External" };
		JComboBox employmentBox = new JComboBox(employmentOptions);
		employment.setLabelFor(employmentBox);
		formPanel.add(employmentBox);

		this.contentPanel.add(formPanel, "form");
	}

	public void next() {
		switch (this.state) {
		case 0:
			this.contentLayout.show(this.contentPanel, "form");
			this.nextButton.setEnabled(false);
			this.backButton.setEnabled(true);
			this.state = 1;
			break;

		case 1:
			this.contentLayout.show(this.contentPanel, "loading");
		}
		this.repaint();
	}

	public void back() {
		switch (this.state) {
		case 1:
			this.contentLayout.show(this.contentPanel, "intro");
			this.nextButton.setEnabled(true);
			this.state = 0;
			break;

		}
	}

	public static void reallyQuit(JFrame frame) {
		int n = JOptionPane.showConfirmDialog(frame,
				"Are you sure you want to exit the wizard?", "Exit?",
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.cancelButton) {
			reallyQuit(this);
		} else if (e.getSource() == this.nextButton) {
			this.next();
		} else if (e.getSource() == this.backButton) {
			this.back();
		}

	}

}
