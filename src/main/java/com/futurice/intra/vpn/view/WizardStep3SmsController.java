package com.futurice.intra.vpn.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.FileNotFoundException;

/**
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-04.
 */
@Slf4j
public class WizardStep3SmsController extends AbstractWizardStepController {

    @FXML
    private PasswordField smsField;

    @FXML
    private Label serverResponse;

    @FXML
    private Label progressBarLabel;

    @FXML
    private ProgressBar progressBar;


    private final static int STATE_USER_INPUT = 0;
    private final static int STATE_SENDING_PASSWORD = 1;
    private int curState = 0;

    @Override
    protected boolean verifyInput() {
        //hide initially
        serverResponse.setVisible(false);
        curState = STATE_SENDING_PASSWORD;
        // New Thread to keep gui responsive
        Thread send = new Thread() {
            public void run() {
                String response = config.enterPassword(smsField.getText());
                curState = STATE_USER_INPUT;
                if (response != null) {
                    Platform.runLater(() -> {
                        //show error to user
                        serverResponse.setText("There was an error when sending the information:\n" + response);
                        serverResponse.setVisible(true);
                    });
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
                while(curState == STATE_SENDING_PASSWORD){
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

        return false;
    }


}


