
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.Action;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ProjectDB extends Application {
	private ObservableList<ObservableList> data;
	private TableView tableview;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("JavaFX Welcome");
		stage.show();

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		Text scenetitle = new Text("Welcome");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label ID = new Label("ID:");
		grid.add(ID, 0, 1);

		TextField idTxt = new TextField();
		grid.add(idTxt, 1, 1);

		Label name = new Label("Name:");
		grid.add(name, 2, 1);

		TextField nametxt = new TextField();
		grid.add(nametxt, 3, 1);

		Label dept = new Label("Department:");
		grid.add(dept, 0, 2);

		ComboBox combo = new ComboBox();
		grid.add(combo, 1, 2);

		Label credit = new Label("Credit hour:");

		grid.add(credit, 2, 2);
		TextField crTxt = new TextField();
		grid.add(crTxt, 3, 2);

		Button btnIns = new Button("Insert");
		Button btnSch = new Button("Search");

		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().addAll(btnIns, btnSch);
		grid.add(hbBtn, 0, 3);

		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/lab8", "root", "");
		String SQL = "SELECT dept_name from department";
		ResultSet rs = c.createStatement().executeQuery(SQL);
		while (rs.next()) {
			combo.getItems().addAll(rs.getString(1));
		}
		tableview = new TableView();
		btnSch.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				data = FXCollections.observableArrayList();
				String SQL = "SELECT *" + " from student";
				// name
				if (!nametxt.getText().isEmpty())
					SQL += " where  name= '" + nametxt.getText() + "'";
				else if (!(nametxt.getText().isEmpty() && idTxt.getText().isEmpty()))
					SQL += " where  name= '" + nametxt.getText() + "' and ID = '" + idTxt.getText() + "'";
				else if (!(nametxt.getText().isEmpty() && idTxt.getText().isEmpty() && crTxt.getText().isEmpty()))
					SQL += " where  name= '" + nametxt.getText() + "' and ID = '" + idTxt.getText()
							+ "' and tot_cred = " + crTxt.getText();
				else if (!(nametxt.getText().isEmpty() && idTxt.getText().isEmpty() && crTxt.getText().isEmpty()
						&& combo.getItems().isEmpty()))
					SQL += " where  name= '" + nametxt.getText() + "' and ID = '" + idTxt.getText()
							+ "' and tot_cred = " + crTxt.getText() + " and dept_name = '" + combo.getValue() + "'";
				// id
				else if (!(idTxt.getText().isEmpty()))
					SQL += " where  ID= '" + idTxt.getText() + "'";
				else if (!(idTxt.getText().isEmpty() && combo.getItems().isEmpty()))
					SQL += " where  ID= '" + idTxt.getText() + "' and dept_name = '" + combo.getValue() + "'";
				else if (!(idTxt.getText().isEmpty() && crTxt.getText().isEmpty()))
					SQL += " where  ID= '" + idTxt.getText() + "' and tot_cred = " + crTxt.getText();
				else if (!(idTxt.getText().isEmpty() && combo.getItems().isEmpty() && crTxt.getText().isEmpty()))
					SQL += " where  ID= '" + idTxt.getText() + "' and dept_name = '" + combo.getValue()
							+ "' and tot_cred = " + crTxt.getText();
				// deptartment
				else if (!(combo.getItems().isEmpty()))
					SQL += " where dept_name = '" + combo.getValue() + "'";
				else if (!(combo.getItems().isEmpty() && crTxt.getText().isEmpty()))
					SQL += " where dept_name = '" + combo.getValue() + "' and tot_cred = " + crTxt.getText();
				else if (!(combo.getItems().isEmpty() && nametxt.getText().isEmpty()))
					SQL += " where dept_name = '" + combo.getValue() + "' and name = '" + nametxt.getText() + "'";
				else if (!(combo.getItems().isEmpty() && crTxt.getText().isEmpty() && nametxt.getText().isEmpty()))
					SQL += " where dept_name = '" + combo.getValue() + "' and tot_cred = " + crTxt.getText()
							+ " and name = '" + nametxt.getText() + "'";
				// credits
				else if (!(crTxt.getText().isEmpty()))
					SQL += " where tot_cred = " + crTxt.getText();
				else if (!(crTxt.getText().isEmpty() && nametxt.getText().isEmpty()))
					SQL += " where tot_cred = " + crTxt.getText() + " and name = '" + nametxt.getText() + "'";
				// ResultSet
				ResultSet rs;
				tableview.getColumns().clear();
				try {
					rs = c.createStatement().executeQuery(SQL);
					for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
						// We are using non property style for making dynamic table
						final int j = i;
						TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
						col.setCellValueFactory(
								new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
									public ObservableValue<String> call(
											CellDataFeatures<ObservableList, String> param) {
										return new SimpleStringProperty(param.getValue().get(j).toString());
									}
								});

						tableview.getColumns().addAll(col);
						System.out.println("Column [" + i + "] ");

					}
					/********************************
					 * Data added to ObservableList *
					 ********************************/
					while (rs.next()) {
						// Iterate Row
						ObservableList<String> row = FXCollections.observableArrayList();
						for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
							// Iterate Column
							row.add(rs.getString(i));
						}
						System.out.println("Row [1] added " + row);
						data.add(row);

					}

					// FINALLY ADDED TO TableView
					tableview.setItems(data);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		btnIns.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				try {
					Statement stmt = (Statement) c.createStatement();
					String query = "INSERT INTO student (ID,name,dept_name,tot_cred) VALUES" + "('" + idTxt.getText()
							+ "','" + nametxt.getText() + "','" + combo.getSelectionModel().getSelectedItem() + "','"
							+ crTxt.getText() + "')";
					System.out.println(query);
					stmt.executeUpdate(query);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		grid.add(tableview, 2, 5);

		stage.setWidth(600);
		HBox d = new HBox(10);
		String show = "show tables";
		rs = c.createStatement().executeQuery(show);

		Tab advisor = new Tab("advisor");
		advisor.setClosable(false);
		Tab classroom = new Tab("classroom");
		classroom.setClosable(false);
		Tab course = new Tab("course");
		course.setClosable(false);
		Tab department = new Tab("department");
		department.setClosable(false);
		Tab instructor = new Tab("instructor");
		instructor.setClosable(false);
		Tab prereq = new Tab("prereq");
		prereq.setClosable(false);
		Tab section = new Tab("section");
		section.setClosable(false);
		Tab student = new Tab("student");
		student.setClosable(false);
		Tab takes = new Tab("takes");
		takes.setClosable(false);
		Tab teaches = new Tab("teaches");
		teaches.setClosable(false);
		Tab time_slot = new Tab("time slot");
		time_slot.setClosable(false);

		TabPane pane = new TabPane();
		pane.getTabs().addAll(advisor, classroom, course, department, instructor, prereq, section, student, takes,
				teaches, time_slot);

		student.setContent(grid);

		Scene scene = new Scene(pane, 300, 275);

		stage.setScene(scene);

	}

}
