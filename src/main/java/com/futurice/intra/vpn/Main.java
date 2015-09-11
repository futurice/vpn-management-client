/**
 * 
 */
package com.futurice.intra.vpn;

import com.futurice.intra.vpn.view.WizardController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

/**
 * @author Oskar Ehnström (oskar.ehnstrom@futurice.com)
 *
 */
public class Main extends Application {

	private Stage primaryStage;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//Configurator conf = new Configurator();
		//conf.gui();

		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Futurice VPN wizard");

		// Set the application icon.
		//this.primaryStage.getIcons().add(new Image("file:resources/images/address_book_32.png"));

		WizardController wizardController = new WizardController();
		primaryStage.setScene(new Scene(wizardController));
		primaryStage.setWidth(540);
		primaryStage.setHeight(600);
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(540);

		//close confirmation
		Platform.setImplicitExit(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit confirmation");
                alert.setHeaderText("Exit and lose all changes?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    Platform.exit();
                } else {
                    //nothing
                }
			}
		});

        //show application
		primaryStage.show();

	}

}
