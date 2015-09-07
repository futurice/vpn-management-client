package com.futurice.intra.vpn.view;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Handle flow of steps and show process progress to user.
 *
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-04.
 */
@Slf4j
public class WizardController extends VBox{

    private int curStep = 1;

    public final static int STEP_1_INTRO = 1;
    public final static int STEP_2_OPTIONS = 2;
    public final static int STEP_3_SMS = 3;
    public final static int STEP_4_RESULT = 4;

    public final static String STEP_FXML_PATH = "/views/steps/" ;
    public final static String STEP_FXML_1_INTRO = STEP_FXML_PATH + "step-1-intro.fxml";
    public final static String STEP_FXML_2_OPTIONS = STEP_FXML_PATH + "step-2-options.fxml";
    public final static String STEP_FXML_3_SMS = STEP_FXML_PATH + "step-3-sms.fxml";
    public final static String STEP_FXML_4_RESULT = STEP_FXML_PATH + "step-4-result.fxml";

    //@FXML
    private Pane contentPane;

    @FXML
    private BorderPane topLayoutBorderPane;

    public WizardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/wizard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try {
            loadStep(STEP_FXML_1_INTRO);
            fxmlLoader.load();

            topLayoutBorderPane.setCenter(contentPane);

        } catch (IOException e) {
            log.error("view failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Load fxml into content pane
     * @param fxmlPath
     */
    private void loadStep(String fxmlPath) {
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource(fxmlPath));
            contentPane = load.load();
            ((AbstractWizardStepController)load.getController()).setParent(this);
        } catch (IOException e) {
            log.error("load step failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Move to next step.
     */
    public void next()  {
        switch (curStep) {
            case STEP_1_INTRO: {
                loadStep(STEP_FXML_2_OPTIONS);
                break;
            }
            case STEP_2_OPTIONS: {
                loadStep(STEP_FXML_3_SMS);
                break;
            }
            case STEP_3_SMS: {
                loadStep(STEP_FXML_4_RESULT);
                break;
            }
        }
        topLayoutBorderPane.setCenter(contentPane);
        curStep++;
    }

    /**
     * Move to previous step.
     */
    public void prev()  {
        switch (curStep) {
            case STEP_2_OPTIONS: {
                loadStep(STEP_FXML_1_INTRO);
                break;
            }
            case STEP_3_SMS: {
                loadStep(STEP_FXML_2_OPTIONS);
                break;
            }
            case STEP_4_RESULT: {
                loadStep(STEP_FXML_3_SMS);
                break;
            }
        }
        topLayoutBorderPane.setCenter(contentPane);
        curStep--;
    }

}