package com.futurice.intra.vpn.view;

import com.futurice.intra.vpn.vendor.SegmentableButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
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
    private Label progressBarLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label serverResponse;

    private final static int STATE_USER_INPUT = 0;
    private final static int STATE_SENDING_DATA = 1;
    private int curState = 0;


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

            Thread send = new Thread() {
                public void run() {
                    String response = config.askForSettings(
                            usernameField.getText(),
                            passwordField.getText(),
                            vpnPasswordField1.getText(),
                            (String) computerTypeField.getToggleGroup().getSelectedToggle().getUserData(),
                            emailField.getText(),
                            (String) computerOwnerField.getToggleGroup().getSelectedToggle().getUserData(),
                            (String) employmentStatusField.getToggleGroup().getSelectedToggle().getUserData()
                    );

                    curState = STATE_USER_INPUT;
                    if (response != null) {
                        //show error to user
                        serverResponse.setVisible(true);
                        serverResponse.setText("There was an error when sending the information:\n" + response);
                    } else {
                        Platform.runLater(() -> {
                            wizard.next();
                        });
                        curState = STATE_USER_INPUT;
                    }
                }
            };
            send.start();

            Thread statusUpdates = new Thread(){
                public void run(){
                    progressBarLabel.setVisible(true);
                    progressBar.setVisible(true);
                    while(curState == STATE_SENDING_DATA){
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            log.error("thread interrupted", e);
                        }
                    }
                    //hide
                    progressBarLabel.setVisible(false);
                    progressBar.setVisible(false);
                }
            };
            statusUpdates.start();

        } else {
            serverResponse.setVisible(true);
            serverResponse.setText("Some fields are invalid.");
        }

        return false;

    }


}
