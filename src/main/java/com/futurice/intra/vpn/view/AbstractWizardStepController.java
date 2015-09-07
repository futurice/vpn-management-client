package com.futurice.intra.vpn.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 * Step template
 *
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-07.
 */
abstract public class AbstractWizardStepController extends Pane {

    private WizardController parent;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    public void setParent(WizardController parent) {
        this.parent = parent;
    }

    @FXML
    private void buttonNextOnAction(ActionEvent event) {
        parent.next();
    }

    @FXML
    private void buttonPrevOnAction(ActionEvent event) {
        parent.prev();
    }


}
