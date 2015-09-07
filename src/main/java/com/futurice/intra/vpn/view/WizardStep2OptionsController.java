package com.futurice.intra.vpn.view;

import com.futurice.intra.vpn.vendor.SegmentableButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.SegmentedButton;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-04.
 */
@Slf4j
public class WizardStep2OptionsController extends AbstractWizardStepController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private SegmentedButton employmentStatusField;

    @FXML
    private SegmentedButton computerOwnerField;

    @FXML
    private SegmentedButton computerTypeField;

    @FXML
    private PasswordField vpnPasswordField1;

    @FXML
    private PasswordField vpnPasswordField2;

    @FXML
    private Label emailField;

    @FXML
    private Label serverResponse;


    // The options to choose from
    private String[] employmentOptions;
    private String[] computerOptions;
    private String[] ownerOptions;

    @Override
    protected void configureView() {
        //  dynamic toggle buttons
        //initialize options
        this.employmentOptions = config.getSettings("EMPLOYMENT_OPTIONS").split(",");
        this.computerOptions = config.getSettings("COMPUTER_OPTIONS").split(",");
        this.ownerOptions = config.getSettings("OWNER_OPTIONS").split(",");
        //create buttons
        addSegmentedButtons(this.employmentOptions, employmentStatusField);
        addSegmentedButtons(this.computerOptions, computerTypeField);
        addSegmentedButtons(this.ownerOptions, computerOwnerField);

        usernameField.setText(config.getUser());
        setEmailText(config.getUser());

        //set fucus so user could start typing pass
        passwordField.requestFocus();

        //validation
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateUser();
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePass();
        });

        vpnPasswordField1.textProperty().addListener((observable, oldValue, newValue) -> {
            validateVpnPass();
        });

        vpnPasswordField2.textProperty().addListener((observable, oldValue, newValue) -> {
            validateVpnPass();
        });
    }

    /**
     * Set email
     * @param name
     */
    private void setEmailText(String name) {
        emailField.setText(name + "@" + config.getSettings("DOMAIN"));
    }

    /*
        Validation methods
     */

    private boolean validateUser() {
        setEmailText(usernameField.getText());
        markAsWarning(usernameField, usernameField.getText().length() == 0);
        return usernameField.getText().length() != 0;
    }

    private boolean validatePass() {
        markAsWarning(passwordField, passwordField.getText().length() == 0);
        return passwordField.getText().length() != 0;
    }

    private boolean validateVpnPass() {
        //if unequal or empty
        boolean invalid = (!vpnPasswordField1.getText().equals(vpnPasswordField2.getText())) || (vpnPasswordField1.getText().length() == 0);
        markAsWarning(vpnPasswordField1, invalid);
        markAsWarning(vpnPasswordField2, invalid);
        return !invalid;
    }

    /**
     * Set filed marked as warning
     * @param node
     * @param warning
     */
    private void markAsWarning(Node node, boolean warning) {
        if (warning) {
            node.getStyleClass().addAll("warningField");
        } else {
            node.getStyleClass().removeAll("warningField");
        }
    }

    /**
     * add toggle buttons
     * @param values
     * @param destination
     */
    private void addSegmentedButtons( String[] values, SegmentedButton destination) {
        for (String val : values) {
            SegmentableButton b = new SegmentableButton(val);
            b.setUserData(val);
            destination.getButtons().add(b);
        }
        destination.getButtons().get(0).setSelected(true);
    }

    @Override
    protected boolean verifyInput() {
        //hide text, as if no error yet
        serverResponse.setVisible(false);
        //validate all
        if (validateUser() & validatePass() & validateVpnPass()) {
            String result = config.askForSettings(
                    usernameField.getText(),
                    passwordField.getText(),
                    vpnPasswordField1.getText(),
                    (String)computerTypeField.getToggleGroup().getSelectedToggle().getUserData(),
                    emailField.getText(),
                    (String)computerOwnerField.getToggleGroup().getSelectedToggle().getUserData(),
                    (String)employmentStatusField.getToggleGroup().getSelectedToggle().getUserData()
            );
            if(result == null) {
                return true;
            } else {
                serverResponse.setVisible(true);
                serverResponse.setText("There was an error when sending the information:\n" + result);
                return false;
            }
        } else {
            serverResponse.setVisible(true);
            serverResponse.setText("Some fields are invalid.");
            return false;
        }





    }

    /**
     * Check that the values in the form are not empty
     */
    /*public void checkForm() {
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
    }*/
}
