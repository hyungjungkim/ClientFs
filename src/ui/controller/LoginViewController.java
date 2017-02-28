package ui.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fileprocessor.FileClient;
import fileprocessor.FileClientLogic;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import network.client.TCPReactor;

public class LoginViewController implements Initializable {

	@FXML
	private JFXTextField textFieldUserId;

	@FXML
	private JFXPasswordField textFieldUserPw;

	@FXML
	private JFXButton btnLogin;

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private JFXTextField textFieldSignUp;

	@FXML
	private JFXTextField textFieldEditProfile;

	@FXML
	private JFXTextField textFieldHelp;

	private boolean flagLogin = false;
	private FileClient fileClient;
	private SignUpViewController signUpViewController;
	private Socket soc;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//

	
		btnLogin.setTooltip(new Tooltip("Click to Login"));

		progressIndicator.setVisible(false);

		RequiredFieldValidator validator = new RequiredFieldValidator();

		textFieldUserId.getValidators().add(validator);
		validator.setMessage("Enter Id");

		textFieldUserId.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					textFieldUserId.validate();
				}
			}
		});

		RequiredFieldValidator validator2 = new RequiredFieldValidator();

		textFieldUserPw.getValidators().add(validator2);
		validator2.setMessage("Enter Password");

		textFieldUserPw.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!newValue) {
					textFieldUserPw.validate();
				}
			}
		});
	}
	
	

	public LoginViewController() {
		TCPReactor tcpReactor = new TCPReactor("10.250.67.75", 9900);
		// TCPReactor tcpReactor = new TCPReactor("127.0.0.1", 9900);

		soc = tcpReactor.getSocket();
		fileClient = new FileClientLogic(soc);
		
	}



	public void handleEditProfile(ActionEvent e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Preparing");
		alert.setHeaderText("Preparing");
		alert.setContentText("Preparing");
		alert.showAndWait();
	}

	public void handleHelp(ActionEvent e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Preparing");
		alert.setHeaderText("Preparing");
		alert.setContentText("Preparing");
		alert.showAndWait();
	}

	public void handleSignUp(MouseEvent e) {

		try {
			signUpViewController = new SignUpViewController(soc);
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/signupfx.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);

			SignUpViewController signUpViewController = loader.getController();

			Stage primaryStage = (Stage) btnLogin.getScene().getWindow();
			
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.setTitle("Project Ping-Pong");
			primaryStage.show();
		}

		catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void handleLoginBtn(ActionEvent e) {

		final ServiceExample serviceExample = new ServiceExample();

		progressIndicator.visibleProperty().bind(serviceExample.runningProperty());
		serviceExample.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				String result = serviceExample.getValue();
				try {

					String userId = textFieldUserId.getText();
					String userPw = textFieldUserPw.getText();

					boolean loginSuccess = fileClient.LoginUser(userId, userPw);

					if (loginSuccess && (flagLogin == false)) {
						flagLogin = true;
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/mainfx.fxml"));
						Parent root = loader.load();
						Scene scene = new Scene(root);

						MainViewController mainViewController = loader.getController();
						mainViewController.SetFileClient(fileClient);
						mainViewController.SetUserId(userId);

						Stage primaryStage = (Stage) btnLogin.getScene().getWindow();
						primaryStage.setScene(scene);
						primaryStage.centerOnScreen();
						primaryStage.setTitle("Project Ping-Pong");
						primaryStage.show();
					} else {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText("Look, an Error Dialog");
						alert.setContentText("No User Info!!");
						alert.showAndWait();
					}

				} catch (IOException ex) {
					ex.printStackTrace();
				}

			}
		});

		serviceExample.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				// DO stuff on failed
			}
		});
		serviceExample.restart(); // here you start your service
	}

	public void handleLogin(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			final ServiceExample serviceExample = new ServiceExample();
			progressIndicator.visibleProperty().bind(serviceExample.runningProperty());
			serviceExample.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent workerStateEvent) {
					String result = serviceExample.getValue(); // here you get
			
					try {

						String userId = textFieldUserId.getText();
						String userPw = textFieldUserPw.getText();
						boolean loginSuccess = fileClient.LoginUser(userId, userPw);

						if (!loginSuccess) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error Dialog");
							alert.setHeaderText("Look, an Error Dialog");
							alert.setContentText("No User Info!!");
							alert.showAndWait();
						} else {
							System.out.println("login 확인 : " + loginSuccess);
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/mainfx.fxml"));
							Parent root = loader.load();
							Scene scene = new Scene(root);

							MainViewController mainViewController = loader.getController();
							mainViewController.SetFileClient(fileClient);
							mainViewController.SetUserId(userId);

							Stage primaryStage = (Stage) btnLogin.getScene().getWindow();
							primaryStage.setScene(scene);
							primaryStage.centerOnScreen();
							primaryStage.setTitle("Project Ping-Pong");
							primaryStage.show();
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});
			serviceExample.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent workerStateEvent) {
					// DO stuff on failed
				}
			});
			serviceExample.restart(); // here you start your service
		}
	}
}
