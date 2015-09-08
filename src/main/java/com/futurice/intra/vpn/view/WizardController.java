package com.futurice.intra.vpn.view;


import com.futurice.intra.vpn.Configurator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Handle flow of steps and show process progress to user.
 *
 * Created by konstantin.petrukhnov@futurice.com on 2015-09-04.
 */
@Slf4j
public class WizardController extends VBox{

    private int curStep = 0;

    public final static int STEP_1_INTRO = 0;
    public final static int STEP_2_OPTIONS = 1;
    public final static int STEP_3_SMS = 2;
    public final static int STEP_4_RESULT = 3;

    public final static String STEP_FXML_PATH = "/views/steps/" ;
    public final static String STEP_FXML_1_INTRO = STEP_FXML_PATH + "step-1-intro.fxml";
    public final static String STEP_FXML_2_OPTIONS = STEP_FXML_PATH + "step-2-options.fxml";
    public final static String STEP_FXML_3_SMS = STEP_FXML_PATH + "step-3-sms.fxml";
    public final static String STEP_FXML_4_RESULT = STEP_FXML_PATH + "step-4-result.fxml";

    //private Pane contentPane;

    private ArrayList<Pane> stepPanes = new ArrayList<>();
    private ArrayList<AbstractWizardStepController> stepControllers = new ArrayList<>();

    /*private Pane contentPaneStep1;
    private Pane contentPaneStep2;
    private Pane contentPaneStep3;
    private Pane contentPaneStep4;

    private AbstractWizardStepController contentPaneStep1Controller;
    private AbstractWizardStepController contentPaneStep2Controller;
    private AbstractWizardStepController contentPaneStep3Controller;
    private AbstractWizardStepController contentPaneStep4Controller;*/


    @FXML
    private BorderPane rootLayoutBorderPane;

    private Configurator config = new Configurator();

    public WizardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/wizard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            //loadStep(STEP_FXML_1_INTRO);
            fxmlLoader.load();

            //rootLayoutBorderPane.setCenter(contentPane);

        } catch (IOException e) {
            log.error("view failed", e);
            throw new RuntimeException(e);
        }

        init();
    }

    public void init() {
        //preload panes
        loadStep(STEP_FXML_1_INTRO);
        loadStep(STEP_FXML_2_OPTIONS);
        loadStep(STEP_FXML_3_SMS);
        loadStep(STEP_FXML_4_RESULT);

        //contentPane = contentPaneStep1;
        rootLayoutBorderPane.setCenter(stepPanes.get(STEP_1_INTRO));
    }

    /**
     * Load fxml into content pane
     * @param fxmlPath
     */
    /*private Pane loadStep(String fxmlPath) {
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane pane = load.load();
            ((AbstractWizardStepController)load.getController()).init(this, config);
            return pane;
        } catch (IOException e) {
            log.error("load step failed", e);
            throw new RuntimeException(e);
        }
    }*/

    /**
     * Load fxml into content pane and add pane and controllers to lists
     * @param fxmlPath
     */
    private void loadStep(String fxmlPath) {
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane pane = load.load();
            AbstractWizardStepController ctrl = (AbstractWizardStepController) load.getController();
            ctrl.init(this, config);
            stepPanes.add(pane);
            stepControllers.add(ctrl);
        } catch (IOException e) {
            log.error("load step failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Move to next step.
     */
    public void next()  {

        curStep++;
        //if new step is not last
        if (curStep < stepPanes.size()) {
            stepControllers.get(curStep).onShow();
            rootLayoutBorderPane.setCenter(stepPanes.get(curStep));
        } else {
            Platform.exit();
        }


        /*witch (curStep) {
            case STEP_1_INTRO: {
                rootLayoutBorderPane.setCenter(contentPaneStep2);
                break;
            }
            case STEP_2_OPTIONS: {
                rootLayoutBorderPane.setCenter(contentPaneStep3);
                break;
            }
            case STEP_3_SMS: {
                rootLayoutBorderPane.setCenter(contentPaneStep4);
                break;
            }
            case STEP_4_RESULT: {
                //exit app
                Platform.exit();
                break;
            }
        }
        curStep++;*/
    }

    /**
     * Move to previous step.
     */
    public void prev()  {

        curStep--;
        //if new step is not last
        if (curStep >= 0) {
            stepControllers.get(curStep).onShow();
            rootLayoutBorderPane.setCenter(stepPanes.get(curStep));
        } else {
            //todo: do wizard reset?
        }

        /*switch (curStep) {
            case STEP_2_OPTIONS: {
                rootLayoutBorderPane.setCenter(contentPaneStep1);
                break;
            }
            case STEP_3_SMS: {
                rootLayoutBorderPane.setCenter(contentPaneStep2);
                break;
            }
            case STEP_4_RESULT: {
                rootLayoutBorderPane.setCenter(contentPaneStep3);
                break;
            }
        }
        curStep--;*/
    }

}