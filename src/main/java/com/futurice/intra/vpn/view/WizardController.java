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

    private ArrayList<Pane> stepPanes = new ArrayList<>();
    private ArrayList<AbstractWizardStepController> stepControllers = new ArrayList<>();


    @FXML
    private BorderPane rootLayoutBorderPane;

    private Configurator config = new Configurator();

    public WizardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/wizard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

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

        rootLayoutBorderPane.setCenter(stepPanes.get(STEP_1_INTRO));
    }

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
    }

}