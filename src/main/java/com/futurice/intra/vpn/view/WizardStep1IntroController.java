package com.futurice.intra.vpn.view;

import com.futurice.intra.vpn.Generator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-04.
 */
@Slf4j
public class WizardStep1IntroController extends AbstractWizardStepController {

    @FXML
    private Label introTextLabel;

    @Override
    protected void configureView() {
        introTextLabel.setText(this.config.getIntroText());
    }


}


