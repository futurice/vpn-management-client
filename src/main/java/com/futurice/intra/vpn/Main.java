/**
 * 
 */
package com.futurice.intra.vpn;

import com.futurice.intra.vpn.view.WizardController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
		primaryStage.setHeight(540);
		primaryStage.setMinHeight(540);
		primaryStage.setMinWidth(540);

		primaryStage.show();

	}

}
