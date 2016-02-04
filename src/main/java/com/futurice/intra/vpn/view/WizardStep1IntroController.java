package com.futurice.intra.vpn.view;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-04.
 */
@Slf4j
public class WizardStep1IntroController extends AbstractWizardStepController {

    @FXML
    private Label introTextLabel;

    @FXML
    private Label introNoteTextLabel;

    @FXML
    private Hyperlink introTextUrl;

    @Override
    protected void configureView() {
        String url = config.getIntroUrl();
        introTextLabel.setText(config.getIntroText());
        introNoteTextLabel.setText(config.getIntroNote());
        introTextUrl.setText(url);

        introTextUrl.setOnAction(event -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(url));
            } catch (IOException e) {
                //do nothing
            } catch (URISyntaxException e) {
                //do nothing
            }
        });
    }


}


