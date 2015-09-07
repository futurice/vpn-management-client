package com.futurice.intra.vpn.view;


import com.futurice.intra.vpn.Configurator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

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

    private Pane contentPane;

    private Pane contentPaneStep1;
    private Pane contentPaneStep2;
    private Pane contentPaneStep3;
    private Pane contentPaneStep4;

    @FXML
    private BorderPane rootLayoutBorderPane;

    private Configurator config = new Configurator();

    public WizardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/wizard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            loadStep(STEP_FXML_1_INTRO);
            fxmlLoader.load();

            rootLayoutBorderPane.setCenter(contentPane);

        } catch (IOException e) {
            log.error("view failed", e);
            throw new RuntimeException(e);
        }

        init();
    }

    public void init() {
        //preload panes
        contentPaneStep1 = loadStep(STEP_FXML_1_INTRO);
        contentPaneStep2 = loadStep(STEP_FXML_2_OPTIONS);
        contentPaneStep3 = loadStep(STEP_FXML_3_SMS);
        contentPaneStep4 = loadStep(STEP_FXML_4_RESULT);

        contentPane = contentPaneStep1;
        rootLayoutBorderPane.setCenter(contentPane);
    }

    /**
     * Load fxml into content pane
     * @param fxmlPath
     */
    private Pane loadStep(String fxmlPath) {
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane pane = load.load();
            ((AbstractWizardStepController)load.getController()).init(this, config);
            return pane;
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
                //contentPane = contentPaneStep2;
                rootLayoutBorderPane.setCenter(contentPaneStep2);
                break;
            }
            case STEP_2_OPTIONS: {
                //contentPane = contentPaneStep3;
                rootLayoutBorderPane.setCenter(contentPaneStep3);
                break;
            }
            case STEP_3_SMS: {
                //contentPane = contentPaneStep4;
                rootLayoutBorderPane.setCenter(contentPaneStep4);
                break;
            }
            case STEP_4_RESULT: {
                //exit app
                Platform.exit();
                break;
            }
        }
        //rootLayoutBorderPane.setCenter(contentPane);
        curStep++;
    }

    /**
     * Move to previous step.
     */
    public void prev()  {
        switch (curStep) {
            case STEP_2_OPTIONS: {
                //contentPane = contentPaneStep1;
                rootLayoutBorderPane.setCenter(contentPaneStep1);
                break;
            }
            case STEP_3_SMS: {
                //contentPane = contentPaneStep2;
                rootLayoutBorderPane.setCenter(contentPaneStep2);
                break;
            }
            case STEP_4_RESULT: {
                //contentPane = contentPaneStep3;
                rootLayoutBorderPane.setCenter(contentPaneStep3);
                break;
            }
        }
        //rootLayoutBorderPane.setCenter(contentPane);
        curStep--;
    }

}