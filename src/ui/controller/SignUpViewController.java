package ui.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import fileprocessor.FileClient;
import fileprocessor.FileClientLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SignUpViewController implements Initializable {

	@FXML
	private JFXTextField textFieldUserId;

	@FXML
	private JFXButton btnConfirm;

	@FXML
	private JFXTextField textFieldUserName;

	@FXML
	private JFXPasswordField textFieldUserPw;

	@FXML
	private JFXButton btnSignUp;

	@FXML
	private JFXButton btnCancel;

	private FileClient fileClient;
	private Socket soc = new Socket();
	
	public SignUpViewController(Socket soc) {
		// TODO Auto-generated constructor stub
		this.soc = soc;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fileClient = new FileClientLogic(soc);
	}

	public void handleCancel(ActionEvent event) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/loginfx.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);

			LoginViewController logInController = loader.getController();

			Stage primaryStage = (Stage) btnSignUp.getScene().getWindow();
		
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.setTitle("Project Ping-Pong");
			primaryStage.show();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		
	}

	
	
	public void handleConfirm(ActionEvent event) {
		String UserId = textFieldUserId.getText();
		
		boolean resultUserIdCheck = false;
		try {
			resultUserIdCheck = fileClient.IsExistId(UserId);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(resultUserIdCheck) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Look, an Error Dialog");
			alert.setContentText("Already UserID Exists!!!");
			alert.showAndWait();
		}
		else if(UserId.length() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Look, an Error Dialog");
			alert.setContentText("Write something!!");
			alert.showAndWait();
		}
		else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Look, an Information Dialog");
			alert.setContentText("You can use this UserId!!!");
			alert.showAndWait();
		}
	}

	public void handleSignUp(MouseEvent event) {
    	String UserId = textFieldUserId.getText();
		String UserPw = textFieldUserPw.getText();
		String UserName = textFieldUserName.getText();
		
		boolean resultUserIdCheck = false;
		try {
			resultUserIdCheck = fileClient.IsExistId(UserId);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(resultUserIdCheck) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Look, an Error Dialog");
			alert.setContentText("Already UserID Exists!!!");
			alert.showAndWait();
		
		}
		else if(UserId.length() == 0 || UserPw.length()==0 || UserName.length()==0 ) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Look, an Error Dialog");
			alert.setContentText("Write something!!");
			alert.showAndWait();
		}
		
		else {
		
			boolean resultSignUP = false;
			try {
				resultSignUP = fileClient.RegistUser(UserId, UserPw, UserName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(resultSignUP) {
				
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/loginfx.fxml"));
					Parent root = loader.load();
					Scene scene = new Scene(root);
			
					LoginViewController logInViewController = loader.getController();
			
					Stage primaryStage = (Stage) btnSignUp.getScene().getWindow();
				
					primaryStage.setScene(scene);
					primaryStage.centerOnScreen();
					primaryStage.setTitle("Project Ping-Pong");
					primaryStage.show();
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
				
			}
			else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText("Look, an Information Dialog");
				alert.setContentText("SignUp Complete. Go To Login View");
				alert.showAndWait();
			}
		
		}
    }

}
