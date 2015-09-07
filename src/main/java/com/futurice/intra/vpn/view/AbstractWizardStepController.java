package com.futurice.intra.vpn.view;

import com.futurice.intra.vpn.Configurator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.Properties;

/**
 * Step template
 *
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-07.
 */
abstract public class AbstractWizardStepController extends Pane {

    protected WizardController wizard;
    protected Configurator config;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    public void init(WizardController wizard, Configurator config) {
        this.wizard = wizard;
        this.config = config;
        configureView();
    }

    @FXML
    private void buttonNextOnAction(ActionEvent event) {
        if (verifyInput()) {
            wizard.next();
        } else {
            //show error
        }
    }

    @FXML
    private void buttonPrevOnAction(ActionEvent event) {
        wizard.prev();
    }

    protected void configureView() {

    }


    protected boolean verifyInput() {
        return true;
    }


}
