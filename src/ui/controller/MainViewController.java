package ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.jfoenix.validation.RequiredFieldValidator;

import db.domain.DirFile;
import db.domain.FileObjectIdentifier;
import fileprocessor.FileClient;
import fileprocessor.FileClientLogic;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import network.client.TCPReactor;

/**
 * MainView 占싱븝옙트占쌘들러
 * 
 * @author Administrator
 *
 */
public class MainViewController implements Initializable {

	private FileClient fileClient;

	@FXML
	private Button btnLogOut;
	@FXML
	private Button btnDevInfo;
	@FXML
	private Label labelUserName;
	@FXML
	private TextField textFieldSearch;

	@FXML
	private Label labelCurrentPath;

	@FXML
	private ImageView ivCreateFolder;
	@FXML
	private ImageView ivFileUpload;
	@FXML
	private ImageView ivFileDownload;
	@FXML
	private ImageView ivDelete;
	@FXML
	private ImageView ivChangeName;
	@FXML
	private ImageView ivSearch;
	@FXML
	private ImageView ivSearchCancel;
	@FXML
	private ImageView ivCopy;
	@FXML
	private ImageView ivCut;
	@FXML
	private ImageView ivPaste;

	@FXML
	private Alert alertDevInfo;

	@FXML
	private Alert alertChangeName;

	private List<DirFile> dirFile;
	private String userId;
	private String searchName;
	private String currentPath;

	private DirFile selectedFileOrDir;

	private List<DirFile> dirFileBeforeSearch;

	private int flagSearch;

	private String beforePath;
	private String afterPath;
	private int flag = 0;

	final ContextMenu cm = new ContextMenu();

	@FXML
	private TableView<DirFile> tableList;

	@FXML
	private TableColumn<DirFile, Integer> tableColType;
	@FXML
	private TableColumn<DirFile, String> tableColName;
	@FXML
	private TableColumn<DirFile, Date> tableColDate;
	@FXML
	private TableColumn<DirFile, String> tableColPath;

	@FXML
	private AnchorPane anchorPaneLeft;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		alertDevInfo = new Alert(AlertType.INFORMATION);
		flagSearch = 0;

		SetTooltips();

		tableColType.setMinWidth(95);
		tableColName.setMinWidth(345);
		tableColDate.setMinWidth(380);
		tableColPath.setMinWidth(160);

	}

	public MainViewController() {
		RightClick();
	}

	public void handleLogOut(ActionEvent e) {
		//
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/loginfx.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);

			LoginViewController loginViewController = loader.getController();

			Stage primaryStage = (Stage) btnLogOut.getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Dev Info
	 * 
	 * @param e
	 */
	public void btnDevInfo(ActionEvent e) {
		//
		alertDevInfo.setContentText("Made By SKCC 4 Team");
		alertDevInfo.show();
	}

	public void handleSearch(MouseEvent e) {

		if (textFieldSearch.getLength() == 0) {
			AlertError(2);
		} else {
			flagSearch++;
			if (flagSearch == 1) {
				dirFileBeforeSearch = dirFile;
			}

			try {
				searchName = textFieldSearch.getText();
				dirFile = fileClient.FileSearch(userId, searchName);
				// null checking
				if (dirFile == null)
					dirFile = new ArrayList<DirFile>();

			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			DisplayList();
			labelCurrentPath.setText("");
		}
	}

	public void handleShowDevInfo(ActionEvent e) {
		//
		alertDevInfo.setContentText("SKCC Project #Team.4");
		alertDevInfo.show();
	}

	public void handleShowHelp(ActionEvent e) {
		//
		alertDevInfo.setContentText("Fake, Bye");
		alertDevInfo.show();
	}

	public void handleSearchCancel(MouseEvent e) {
		//

		if (dirFileBeforeSearch != null) {
			dirFile = dirFileBeforeSearch;

			// null checking
			if (dirFile == null)
				dirFile = new ArrayList<DirFile>();

			// TODO : is possible?
			dirFileBeforeSearch = null;
			flagSearch = 0;
			DisplayList();
			ShowPath();
			textFieldSearch.setText("");
		}
	}

	// Complete
	public void handleCreateFolder(MouseEvent e) {

		String entered = "none.";
		TextInputDialog dialog = new TextInputDialog("hi");
		dialog.setTitle("Create Folder");
		dialog.setHeaderText("Set a Folder Name");
		Optional<String> result = dialog.showAndWait();
		String folderNameWithPath = "";
		
		if (result.isPresent()) {

			folderNameWithPath = currentPath + "/" + result.get();
			if (IsExist(entered)) {
				System.out.println("Already Exist");
			} else {
				try {
					dirFile = fileClient.DirectoryCreate(userId, folderNameWithPath);

					// null checking
					if (dirFile == null) dirFile = new ArrayList<DirFile>();

					DisplayList();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void handleFileUpload(MouseEvent e) {
		//
		try {

			Node source = (Node) e.getSource();
			Window theStage = source.getScene().getWindow();

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
			File selectedFile = fileChooser.showOpenDialog(theStage);

			if (selectedFile != null) {
				String localFilePath = selectedFile.getPath();

				String entered = "none.";
				TextInputDialog dialog = new TextInputDialog(selectedFile.getName());
				dialog.setTitle("FileUpload");
				dialog.setHeaderText("Set Upload File Name :");

				Optional<String> result = dialog.showAndWait();
				String fileNameWithPath = "";
				if (result.isPresent() && !(IsExist(result.get()))) {

					fileNameWithPath = currentPath + "/" + result.get();

					dirFile = fileClient.FileUpload(userId, localFilePath, fileNameWithPath);

					// null checking
					if (dirFile == null) {
						dirFile = new ArrayList<DirFile>();
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Upload Completed");
						alert.setHeaderText(null);
						alert.setContentText("Upload Completed");
						alert.showAndWait();
					}

					DisplayList();
				}
				// duplicated name
				else {
					AlertError(3);
				}

			}

		} catch (IOException d) {
			d.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void handleFileDownload(MouseEvent e) {

		if (selectedFileOrDir == null) {
			AlertError(1);

		} else {

			if (selectedFileOrDir.getFlag() == 0) {
				AlertError(4);
			} else {
				// TODO : fileClinet.FileDownload parameter
				boolean isFileDownload = false;
				try {
					System.out.println(selectedFileOrDir);
					String localFilePath = selectedFileOrDir.getClientPath();
					isFileDownload = fileClient.FileDownload(userId, localFilePath);
					System.out.println(isFileDownload);

					if (isFileDownload) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Download Completed");
						alert.setHeaderText("aaaa");
						alert.setContentText("go to desktop\\fileServer");
						alert.showAndWait();
					} else {
						AlertError(5);
					}

				} catch (IOException f) {
					f.printStackTrace();
				}

			}
		}
	}

	public void handleDelete(MouseEvent e) {
		//
		if (selectedFileOrDir == null) {
			AlertError(6);
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText("Look, a Confirmation Dialog");
			alert.setContentText("Wanna Deletion?");

			// button text change possible
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {

				try {

					String fileNameWithPath = currentPath + "/" + selectedFileOrDir.getFileName();

					if (selectedFileOrDir.getFlag() == 0) {
						dirFile = fileClient.DirectoryRemove(userId, fileNameWithPath);
						// null checking
						if (dirFile == null)
							dirFile = new ArrayList<DirFile>();

					} else {
						dirFile = fileClient.FileRemove(userId, fileNameWithPath);
						// null checking
						if (dirFile == null)
							dirFile = new ArrayList<DirFile>();

					}

				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				DisplayList();

			} else {
				// ... user chose CANCEL or closed the dialog
			}
		}
	}

	public void handleChangeName(MouseEvent e) {

		if (selectedFileOrDir == null) {
			AlertError(6);
		} else {
			String entered = "none.";
			TextInputDialog dialog = new TextInputDialog("hi");
			dialog.setTitle("hi");
			dialog.setHeaderText("Change Name");

			Optional<String> result = dialog.showAndWait();

			if (result.isPresent()) {

				entered = result.get();

				if (IsExist(entered)) {

					AlertError(8);

				} else if (entered.length() == 0) {
					AlertError(9);
				}

				else {
					String beforePath = currentPath + "/" + selectedFileOrDir.getFileName();
					String afterPath = currentPath + "/" + entered;
					try {
						dirFile = fileClient.ChangeName(userId, beforePath, afterPath);
						// null checking
						if (dirFile == null)
							dirFile = new ArrayList<DirFile>();

					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					DisplayList();
				}

			}
		}
	}

	public void handleCopy(MouseEvent e) {
		if (selectedFileOrDir == null) {
			AlertError(10);
		} else {
			flag = 0;
			beforePath = currentPath + "/" + selectedFileOrDir.getFileName();
		}
	}

	public void handleCut(MouseEvent e) {
		if (selectedFileOrDir == null) {
			AlertError(10);
		} else {
			flag = 1;
			beforePath = currentPath + "/" + selectedFileOrDir.getFileName();
		}
	}

	public void handlePaste(MouseEvent e) {

		if (beforePath == null) {
			AlertError(11);
		} else {
			afterPath = currentPath;
			// dirFile = fileClient.BBB(flag, beforePath, afterPath);

			// null checking
			if (dirFile == null)
				dirFile = new ArrayList<DirFile>();

			DisplayList();
			beforePath = null;
		}

	}

	public void DisplayList() {
		//
		// always creating : root or non-root
		// TODO : fake object parameter check
		if(flagSearch == 0 ) dirFile.add(0, new DirFile(0, "...", "1111", 100, "2010-01-01", 2, ""));

		ObservableList<DirFile> observableList = FXCollections.observableArrayList(dirFile);

		tableList.setItems(observableList);

		// tableList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// TODO : size modify to path
		tableColType.setCellValueFactory(new PropertyValueFactory<>("flag"));
		tableColName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		tableColDate.setCellValueFactory(new PropertyValueFactory<>("modifiedDate"));
		tableColPath.setCellValueFactory(new PropertyValueFactory<>("clientPath"));

		tableColType.setStyle("-fx-alignment: CENTER;");
		tableColName.setStyle("-fx-alignment: CENTER;");
		tableColDate.setStyle("-fx-alignment: CENTER;");
		tableColPath.setStyle("-fx-alignment: CENTER;");

		tableColType.setCellFactory(new Callback<TableColumn<DirFile, Integer>, TableCell<DirFile, Integer>>() {
			@Override
			public TableCell<DirFile, Integer> call(TableColumn<DirFile, Integer> param) {
				TableCell<DirFile, Integer> cell = new TableCell<DirFile, Integer>() {
					ImageView imageview = new ImageView();

					@Override
					public void updateItem(Integer item, boolean empty) {
						if (item != null) {

							HBox box = new HBox();
							box.setSpacing(10);
							VBox vbox = new VBox();

							box.setStyle("-fx-alignment: CENTER;");

							imageview.setFitHeight(35);
							imageview.setFitWidth(35);

							if (item == 0) // folder
								imageview.setImage(new Image(
										MainViewController.class.getResource("img").toString() + "/folder.png"));
							else if (item == 1) // file
								imageview.setImage(new Image(
										MainViewController.class.getResource("img").toString() + "/file.png"));
							else // parent
								imageview.setImage(new Image(
										MainViewController.class.getResource("img").toString() + "/parent.png"));

							box.getChildren().addAll(imageview, vbox);

							setGraphic(box);
						}
					}
				};
				return cell;
			}
		});
		
		tableColPath.setCellFactory(new Callback<TableColumn<DirFile, String>, TableCell<DirFile, String>>() {
			@Override
			public TableCell<DirFile, String> call(TableColumn<DirFile, String> param) {
				TableCell<DirFile, String> cell = new TableCell<DirFile, String>() {
					

					@Override
					public void updateItem(String item, boolean empty) {
						if (item != null) {
							if(flagSearch !=0) setText(item);
						}
					}
				};
				return cell;
			}
		});

		tableList.setRowFactory(tv -> {
			TableRow<DirFile> row = new TableRow<DirFile>();
			row.setOnMouseClicked(event -> {

				try {
					TablePosition pos = tableList.getSelectionModel().getSelectedCells().get(0);
					int t_row = pos.getRow();
					DirFile item = tableList.getItems().get(t_row);
					TableColumn col = pos.getTableColumn();

					if (event.getClickCount() == 1 && (row.isEmpty())) {
						this.selectedFileOrDir = item;
						tableList.getSelectionModel().clearSelection();
					}

					if (event.getClickCount() == 1 && (!row.isEmpty())) {
						this.selectedFileOrDir = item;
					}

					if (event.getButton() == MouseButton.SECONDARY) {
						cm.show(row, event.getScreenX(), event.getScreenY());
					}

					if (event.getClickCount() == 2 && (!row.isEmpty())) {

						if (col.getId().equals("tableColName")) {
							String pathToGo = (String) col.getCellObservableValue(item).getValue();

							// folder
							if (item.getFlag() == 0) {
								Forward(pathToGo);
							}
							// parent
							if (item.getFlag() == 2) {

								if (currentPath.equals(userId)) {
									AlertError(7);
								} else {
									Back();
								}
							}

						}

					}
				}
				catch(IndexOutOfBoundsException iobe) {
					
				}

			});

			return row;
		});
	}

	public boolean IsExist(String name) {
		//
		for (int i = 0; i < dirFile.size(); i++) {
			if (dirFile.get(i).getFileName().equals(name))
				return true;
		}
		return false;
	}

	public void Back() {

		int lastIndex = currentPath.lastIndexOf("/");
		String renewPath = currentPath.substring(0, lastIndex);

		try {
			dirFile = fileClient.ShowList(userId, renewPath);
			// null checking
			if (dirFile == null)
				dirFile = new ArrayList<DirFile>();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.currentPath = renewPath;
		DisplayList();
		ShowPath();
	}

	public void Forward(String pathToGo) {

		String renewPath = currentPath + "/" + pathToGo;

		try {
			dirFile = fileClient.ShowList(userId, renewPath);
			// null checking
			if (dirFile == null)
				dirFile = new ArrayList<DirFile>();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.currentPath = renewPath;
		DisplayList();
		ShowPath();
	}

	public void SearchedFileDownload() {

		if (selectedFileOrDir == null) {
			AlertError(1);
		} else {

			if (selectedFileOrDir.getFlag() == 0) {
				AlertError(4);
			} else {
				// TODO : fileClinet.FileDownload parameter
				boolean isFileDownload = false;
				try {
					System.out.println(selectedFileOrDir);
					String localFilePath = selectedFileOrDir.getClientPath();
					isFileDownload = fileClient.FileDownload(userId, localFilePath);
					System.out.println(isFileDownload);

					if (isFileDownload) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Download Completed");
						alert.setHeaderText("aaaa");
						alert.setContentText("go to desktop\\fileServer");
						alert.showAndWait();
					} else {
						AlertError(5);
					}
				} catch (IOException f) {
					f.printStackTrace();
				}
			}
		}
	}
	
	public void SetFileClient(FileClient fileClinet) {
		this.fileClient = fileClient;
	}
	

	public void SetUserId(String userId) {

		this.userId = userId;
		this.currentPath = this.userId;
		ShowPath();
		System.out.println(userId);

		try {

			dirFile = fileClient.ShowList(this.userId, this.currentPath);

			if (dirFile == null)
				dirFile = new ArrayList<DirFile>();

			DisplayList();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (userId.equals("") || userId.equals(null))
			labelUserName.setText("Mr." + this.userId + " ");
		else {
			this.userId = userId;
			labelUserName.setText("Mr." + this.userId + " ");
		}
	}

	public void ShowPath() {
		labelCurrentPath.setText(currentPath + "/");
	}

	public void handleSearchEnter(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {

			if (textFieldSearch.getLength() == 0) {

				AlertError(2);
			} else {
				flagSearch++;
				if (flagSearch == 1) {
					dirFileBeforeSearch = dirFile;
				}

				try {
					searchName = textFieldSearch.getText();
					dirFile = fileClient.FileSearch(userId, searchName);
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				DisplayList();
				labelCurrentPath.setText("");
			}
		}
	}

	public void SetTooltips() {
		Tooltip.install(ivChangeName, new Tooltip("ChangeName"));
		Tooltip.install(ivCreateFolder, new Tooltip("CreateFolder"));
		Tooltip.install(ivDelete, new Tooltip("Delete"));
		Tooltip.install(ivFileDownload, new Tooltip("FileDownload"));
		Tooltip.install(ivFileUpload, new Tooltip("FileUpload"));
		Tooltip.install(ivSearch, new Tooltip("Search"));
		Tooltip.install(ivCopy, new Tooltip("Copy"));
		Tooltip.install(ivCut, new Tooltip("Cut"));
		Tooltip.install(ivPaste, new Tooltip("Paste"));
	}

	public void RightClick() {

		SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
		SeparatorMenuItem separatorMenuItem2 = new SeparatorMenuItem();
		SeparatorMenuItem separatorMenuItem3 = new SeparatorMenuItem();
		SeparatorMenuItem separatorMenuItem4 = new SeparatorMenuItem();
		SeparatorMenuItem separatorMenuItem5 = new SeparatorMenuItem();

		MenuItem item1 = new MenuItem("Copy");
		MenuItem item2 = new MenuItem("Cut");
		MenuItem item3 = new MenuItem("Paste");
		MenuItem item4 = new MenuItem("Delete");
		MenuItem item5 = new MenuItem("Download");
		MenuItem item6 = new MenuItem("Rename");

		cm.getItems().addAll(item1, separatorMenuItem1, item2, separatorMenuItem2, item3, separatorMenuItem3, item4,
				separatorMenuItem4, item5, separatorMenuItem5, item6);
	}

	public void AlertError(int n) {

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("Look, an Error Dialog");
		switch (n) {
		case 1:
			alert.setContentText("select file");
			break;
		case 2:
			alert.setContentText("write something!!!");
			break;
		case 3:
			alert.setContentText("Filename already exists");
			break;
		case 4:
			alert.setContentText("select only file, not folder");
			break;
		case 5:
			alert.setContentText("Download Fail");
			break;
		case 6:
			alert.setContentText("select file or folder");
			break;
		case 7:
			alert.setContentText("we are at root");
			break;
		case 8:
			alert.setContentText("Ooops, there was an error!");
			break;
		case 9:
			alert.setContentText("Write!!!");
			break;
		case 10:
			alert.setContentText("Select Something!!!");
			break;
		case 11:
			alert.setContentText("you should copy or cut!!!");
			break;
		}
		alert.showAndWait();
	}
}
