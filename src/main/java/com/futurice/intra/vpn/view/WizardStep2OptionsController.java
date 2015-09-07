package com.futurice.intra.vpn.view;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.SegmentedButton;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-04.
 */
@Slf4j
public class WizardStep2OptionsController  extends AbstractWizardStepController {

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
    private TextField emailField;


    // The options to choose from
    private String[] employmentOptions;
    private String[] computerOptions;
    private String[] ownerOptions;

    private Properties settings = new Properties();


    public WizardStep2OptionsController() {

        //load settings
        try {
            settings.load(this.getClass().getResourceAsStream("settings.cfg"));
        } catch (IOException e) {
            log.error("load settings failed", e);
        }

        //dynamic toggle buttons
        // Initialize options

        this.employmentOptions = settings.getProperty("EMPLOYMENT_OPTIONS").split(",");
        this.computerOptions = settings.getProperty("COMPUTER_OPTIONS").split(",");
        this.ownerOptions = settings.getProperty("OWNER_OPTIONS").split(",");

        addSegmentedButtons(this.employmentOptions, employmentStatusField);
        addSegmentedButtons(this.computerOptions, computerTypeField);
        addSegmentedButtons(this.ownerOptions, computerOwnerField);


    }

    private void addSegmentedButtons( String[] values, SegmentedButton destination) {
        for (String val : values) {
            ToggleButton b = new ToggleButton(val);
            destination.getButtons().add(b);
        }
    }

}
