
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	ComboBox<String> combo;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Alert a = new Alert(AlertType.NONE);
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/lab8", "root", "");
		tableview = new TableView();

		Tab advisor = new Tab("Advisor");
		advisor.setClosable(false);
		Tab classroom = new Tab("Classroom");
		classroom.setClosable(false);
		Tab course = new Tab("Course");
		course.setClosable(false);
		Tab department = new Tab("Department");
		department.setClosable(false);
		Tab instructor = new Tab("Instructor");
		instructor.setClosable(false);
		Tab prereq = new Tab("Prereq");
		prereq.setClosable(false);
		Tab section = new Tab("Section");
		section.setClosable(false);
		Tab student = new Tab("Student");
		student.setClosable(false);
		Tab takes = new Tab("Takes");
		takes.setClosable(false);
		Tab teaches = new Tab("Teaches");
		teaches.setClosable(false);
		Tab time_slot = new Tab("Time slot");
		time_slot.setClosable(false);
		Tab aboutme = new Tab("About me");
		aboutme.setClosable(false);

		TabPane pane = new TabPane();
		pane.getTabs().addAll(aboutme, advisor, classroom, course, department, instructor, prereq, section, student,
				takes, teaches, time_slot);

		combo = new ComboBox<>();
		String SQL = "SELECT dept_name from department";
		ResultSet rs = c.createStatement().executeQuery(SQL);
		while (rs.next()) {
			combo.getItems().addAll(rs.getString(1));
		}

		// student tab
		GridPane gridStudent = new GridPane();
		gridStudent.setAlignment(Pos.CENTER);
		gridStudent.setHgap(10);
		gridStudent.setVgap(10);
		gridStudent.setPadding(new Insets(25, 25, 25, 25));
		Text scenetitle = new Text("Student table");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridStudent.add(scenetitle, 0, 0, 2, 1);

		Label ID = new Label("ID:");
		gridStudent.add(ID, 0, 1);

		TextField idTxt = new TextField();
		gridStudent.add(idTxt, 1, 1);

		Label name = new Label("Name:");
		gridStudent.add(name, 2, 1);

		TextField nametxt = new TextField();
		gridStudent.add(nametxt, 3, 1);

		Label dept = new Label("Department:");
		gridStudent.add(dept, 0, 2);

		gridStudent.add(combo, 1, 2);

		Label credit = new Label("Credit hour:");
		gridStudent.add(credit, 2, 2);

		TextField crTxt = new TextField();
		gridStudent.add(crTxt, 3, 2);

		Button btnInsSt = new Button("Insert");
		Button btnSchSt = new Button("Search");

		HBox hbBtnSt = new HBox(10);
		hbBtnSt.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnSt.getChildren().addAll(btnInsSt, btnSchSt);
		gridStudent.add(hbBtnSt, 0, 3);

		btnSchSt.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String querySt = "SELECT * from student";
			ArrayList<String> conditions = new ArrayList<>();

			if (!nametxt.getText().isEmpty()) {
				conditions.add(" name= '" + nametxt.getText() + "'");
			}

			if (!idTxt.getText().isEmpty()) {
				conditions.add(" ID = '" + idTxt.getText() + "'");
			}

			if (!crTxt.getText().isEmpty()) {
				conditions.add(" tot_cred = " + crTxt.getText());
			}

			if (!combo.getItems().isEmpty()) {
				if (!(combo.getValue() == null))
					conditions.add(" dept_name = '" + combo.getValue() + "'");
			}

			if (!conditions.isEmpty()) {
				querySt += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(querySt);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(querySt);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsSt.disableProperty().bind(idTxt.textProperty().isEmpty().or(nametxt.textProperty().isEmpty()
				.or(combo.getSelectionModel().selectedItemProperty().isNull().or(crTxt.textProperty().isEmpty()))));

		btnInsSt.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				// Check if the ID being inserted already exists in the table
				String checkQuery = "SELECT * FROM student WHERE ID='" + idTxt.getText() + "'";
				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (!checkResult.next()) {
					// If no rows are returned, it means the ID does not already exist in the table
					// Insert the values into the table
					String query = "INSERT INTO student (ID,name,dept_name,tot_cred) VALUES" + "('" + idTxt.getText()
							+ "','" + nametxt.getText() + "','" + combo.getSelectionModel().getSelectedItem() + "','"
							+ crTxt.getText() + "')";
					System.out.println(query);
					stmt.executeUpdate(query);
					a.setAlertType(AlertType.CONFIRMATION);
					a.setContentText("added successfuly!");
					a.show();
				} else {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		student.setContent(gridStudent);

		// advisor tab
		GridPane gridAdvisor = new GridPane();
		gridAdvisor.setAlignment(Pos.CENTER);
		gridAdvisor.setHgap(10);
		gridAdvisor.setVgap(10);
		gridAdvisor.setPadding(new Insets(25, 25, 25, 25));
		Text advisortabletxt = new Text("Advisor Table");
		advisortabletxt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridAdvisor.add(advisortabletxt, 0, 0, 2, 1);

		Label s_ID = new Label("Student ID:");
		gridAdvisor.add(s_ID, 0, 1);

		TextField s_idTxt = new TextField();
		gridAdvisor.add(s_idTxt, 1, 1);

		Label i_id = new Label("Instructor ID:");
		gridAdvisor.add(i_id, 2, 1);

		TextField i_idTxt = new TextField();
		gridAdvisor.add(i_idTxt, 3, 1);

		Button btnInsAdvisor = new Button("Insert");
		Button btnSchAdvisor = new Button("Search");

		HBox hbBtnad = new HBox(10);
		hbBtnad.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnad.getChildren().addAll(btnInsAdvisor, btnSchAdvisor);
		gridAdvisor.add(hbBtnad, 0, 2);
		advisor.setContent(gridAdvisor);

		btnSchAdvisor.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryAd = "SELECT * from advisor";
			ArrayList<String> conditions = new ArrayList<>();

			if (!i_idTxt.getText().isEmpty()) {
				conditions.add(" i_ID = '" + i_idTxt.getText() + "'");

			}
			if (!s_idTxt.getText().isEmpty()) {
				conditions.add(" s_ID = '" + s_idTxt.getText() + "'");
			}

			if (!conditions.isEmpty()) {
				queryAd += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryAd);

			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryAd);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsAdvisor.disableProperty().bind(s_idTxt.textProperty().isEmpty().or(i_idTxt.textProperty().isEmpty()));

		btnInsAdvisor.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				// Check if the ID being inserted already exists in the advisor table
				String checkQuery = "SELECT * FROM advisor WHERE s_ID ='" + s_idTxt.getText() + "' AND i_ID ='"
						+ i_idTxt.getText() + "'";
				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Advisor with this ID and instructor ID already exists in the table!");
					a.show();
					return;
				}

				// Check if the student ID being inserted exists in the student table
				String checkStudentQuery = "SELECT * FROM student WHERE ID ='" + s_idTxt.getText() + "'";
				ResultSet checkStudentResult = stmt.executeQuery(checkStudentQuery);
				if (!checkStudentResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Student ID does not exist in the student table!");
					a.show();
					return;
				}

				// Check if the instructor ID being inserted exists in the instructor table
				String checkInstructorQuery = "SELECT * FROM instructor WHERE ID ='" + i_idTxt.getText() + "'";
				ResultSet checkInstructorResult = stmt.executeQuery(checkInstructorQuery);
				if (!checkInstructorResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Instructor ID does not exist in the instructor table!");
					a.show();
					return;
				}
				String query = "INSERT INTO advisor (s_ID,i_ID) VALUES " + "('" + s_idTxt.getText() + "','"
						+ i_idTxt.getText() + "')";
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("added successfuly!");
				a.show();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		// classroom tab
		GridPane gridClassroom = new GridPane();
		gridClassroom.setAlignment(Pos.CENTER);
		gridClassroom.setHgap(10);
		gridClassroom.setVgap(10);
		gridClassroom.setPadding(new Insets(25, 25, 25, 25));
		Text classroomtabletxt = new Text("Classroom Table");
		classroomtabletxt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridClassroom.add(classroomtabletxt, 0, 0, 2, 1);

		Label building = new Label("Building:");
		gridClassroom.add(building, 0, 1);

		TextField building_txt = new TextField();
		gridClassroom.add(building_txt, 1, 1);

		Label capacity = new Label("Capacity:");
		gridClassroom.add(capacity, 2, 1);

		TextField capacity_txt = new TextField();
		gridClassroom.add(capacity_txt, 3, 1);

		Label room_number = new Label("Room number:");
		gridClassroom.add(room_number, 0, 2);

		TextField room_numberTxt = new TextField();
		gridClassroom.add(room_numberTxt, 1, 2);

		Button btnInsClassroom = new Button("Insert");
		Button btnSchClassroom = new Button("Search");

		HBox hbBtnCl = new HBox(10);
		hbBtnCl.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnCl.getChildren().addAll(btnInsClassroom, btnSchClassroom);
		gridClassroom.add(hbBtnCl, 0, 3);
		classroom.setContent(gridClassroom);

		btnSchClassroom.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from classroom";
			ArrayList<String> conditions = new ArrayList<>();

			if (!building_txt.getText().isEmpty()) {
				conditions.add(" building = '" + building_txt.getText() + "'");
			}
			if (!capacity_txt.getText().isEmpty()) {
				conditions.add(" capacity = " + capacity_txt.getText());
			}
			if (!room_numberTxt.getText().isEmpty()) {
				conditions.add(" room_number = '" + room_numberTxt.getText() + "'");
			}
			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsClassroom.disableProperty().bind(building_txt.textProperty().isEmpty()
				.or(capacity_txt.textProperty().isEmpty().or(room_numberTxt.textProperty().isEmpty())));

		btnInsClassroom.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM classroom WHERE building = '" + building_txt.getText()
						+ "' AND room_number = '" + room_numberTxt.getText() + "'";
				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}
				String query = "INSERT INTO classroom (building,room_number,capacity) VALUES ('"
						+ building_txt.getText() + "','" + room_numberTxt.getText() + "'," + capacity_txt.getText()
						+ ")";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("Added successfuly!");
				a.show();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		// course
		GridPane gridCourse = new GridPane();
		gridCourse.setAlignment(Pos.CENTER);
		gridCourse.setHgap(10);
		gridCourse.setVgap(10);
		gridCourse.setPadding(new Insets(25, 25, 25, 25));
		Text courseTableText = new Text("Course Table");
		courseTableText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridCourse.add(courseTableText, 0, 0, 2, 1);

		Label course_idCourse = new Label("Course ID:");
		gridCourse.add(course_idCourse, 0, 1);

		TextField course_idtxt = new TextField();
		gridCourse.add(course_idtxt, 1, 1);

		Label title = new Label("Title:");
		gridCourse.add(title, 2, 1);

		TextField titleTxt = new TextField();
		gridCourse.add(titleTxt, 3, 1);

		Label dept_nameC = new Label("Department name:");
		gridCourse.add(dept_nameC, 0, 2);

		ComboBox<String> combo2 = new ComboBox<>();
		combo2.setItems(combo.getItems());
		gridCourse.add(combo2, 1, 2);

		Label credits_Course = new Label("Credits:");
		gridCourse.add(credits_Course, 2, 2);

		TextField credits_Coursetxt = new TextField();
		gridCourse.add(credits_Coursetxt, 3, 2);

		Button btnInsCourse = new Button("Insert");
		Button btnSchCourse = new Button("Search");

		HBox hbBtnCo = new HBox(10);
		hbBtnCo.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnCo.getChildren().addAll(btnInsCourse, btnSchCourse);
		gridCourse.add(hbBtnCo, 0, 3);
		course.setContent(gridCourse);
		btnSchCourse.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from course";
			ArrayList<String> conditions = new ArrayList<>();

			if (!course_idtxt.getText().isEmpty()) {
				conditions.add(" course_id = '" + course_idtxt.getText() + "'");
			}
			if (!titleTxt.getText().isEmpty()) {
				conditions.add(" title = '" + titleTxt.getText() + "'");
			}
			if (!combo2.getItems().isEmpty()) {
				if (combo2.getValue() != null) {
					conditions.add(" dept_name = '" + combo2.getValue() + "'");
				}
			}
			if (!credits_Coursetxt.getText().isEmpty()) {
				conditions.add(" credits = '" + credits_Coursetxt.getText() + "'");
			}
			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsCourse.disableProperty()
				.bind(course_idtxt.textProperty().isEmpty().or(titleTxt.textProperty().isEmpty().or(credits_Coursetxt
						.textProperty().isEmpty().or(combo2.getSelectionModel().selectedItemProperty().isNull()))));

		btnInsCourse.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM course WHERE course_id = '" + course_idtxt.getText() + "'";

				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}
				String query = "INSERT INTO course (course_id,title,dept_name,credits) VALUES ('"
						+ course_idtxt.getText() + "','" + titleTxt.getText() + "','" + combo2.getValue() + "',"
						+ credits_Coursetxt.getText() + ")";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("added successfuly!");
				a.show();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		// department tab
		GridPane gridDepartment = new GridPane();
		gridDepartment.setAlignment(Pos.CENTER);
		gridDepartment.setHgap(10);
		gridDepartment.setVgap(10);
		gridDepartment.setPadding(new Insets(25, 25, 25, 25));
		Text deptTableText = new Text("Department Table");
		deptTableText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridDepartment.add(deptTableText, 0, 0, 2, 1);

		Label dept_name = new Label("Department:");
		gridDepartment.add(dept_name, 0, 1);

		TextField dept_nameTxt = new TextField();
		gridDepartment.add(dept_nameTxt, 1, 1);

		Label building_dept = new Label("Building:");
		gridDepartment.add(building_dept, 2, 1);

		TextField bulding_deptTxt = new TextField();
		gridDepartment.add(bulding_deptTxt, 3, 1);

		Label budget = new Label("Budget:");
		gridDepartment.add(budget, 0, 2);

		TextField budgetTxt = new TextField();
		gridDepartment.add(budgetTxt, 1, 2);

		Button btnInsDept = new Button("Insert");
		Button btnSchDept = new Button("Search");

		HBox hbBtnDp = new HBox(10);
		hbBtnDp.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnDp.getChildren().addAll(btnInsDept, btnSchDept);
		gridDepartment.add(hbBtnDp, 0, 3);
		department.setContent(gridDepartment);
		btnSchDept.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from department";
			ArrayList<String> conditions = new ArrayList<>();

			if (!dept_nameTxt.getText().isEmpty()) {
				conditions.add(" dept_name = '" + dept_nameTxt.getText() + "'");
			}
			if (!bulding_deptTxt.getText().isEmpty()) {
				conditions.add(" building = '" + bulding_deptTxt.getText() + "'");
			}
			if (!budgetTxt.getText().isEmpty()) {
				conditions.add(" budget = " + budgetTxt.getText());
			}
			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsDept.disableProperty().bind(bulding_deptTxt.textProperty().isEmpty()
				.or(budgetTxt.textProperty().isEmpty().or(dept_nameTxt.textProperty().isEmpty())));

		btnInsDept.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM department WHERE dept_name = '" + dept_nameTxt.getText() + "'";

				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}
				String query = "INSERT INTO department (dept_name,building,budget) VALUES ('" + dept_nameTxt.getText()
						+ "','" + bulding_deptTxt.getText() + "'," + budgetTxt.getText() + ")";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("added successfuly!");
				a.show();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		// instructor tab
		GridPane gridInst = new GridPane();
		gridInst.setAlignment(Pos.CENTER);
		gridInst.setHgap(10);
		gridInst.setVgap(10);
		gridInst.setPadding(new Insets(25, 25, 25, 25));
		Text instTableText = new Text("Instructor Table");
		instTableText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridInst.add(instTableText, 0, 0, 2, 1);

		Label instID = new Label("ID:");
		gridInst.add(instID, 0, 1);

		TextField id_instTxt = new TextField();
		gridInst.add(id_instTxt, 1, 1);

		Label name_Inst = new Label("Name:");
		gridInst.add(name_Inst, 2, 1);

		TextField nameInstTxt = new TextField();
		gridInst.add(nameInstTxt, 3, 1);

		Label dept_nameInst = new Label("Department:");
		gridInst.add(dept_nameInst, 0, 2);

		ComboBox<String> combo3Inst = new ComboBox<>();
		combo3Inst.setItems(combo.getItems());
		gridInst.add(combo3Inst, 1, 2);

		Label salaryInst = new Label("Salary:");
		gridInst.add(salaryInst, 2, 2);

		TextField salaryInstTxt = new TextField();
		gridInst.add(salaryInstTxt, 3, 2);

		Button btnInsInst = new Button("Insert");
		Button btnSchInst = new Button("Search");

		HBox hbBtnInst = new HBox(10);
		hbBtnInst.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnInst.getChildren().addAll(btnInsInst, btnSchInst);
		gridInst.add(hbBtnInst, 0, 3);
		instructor.setContent(gridInst);
		btnSchInst.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from instructor";
			ArrayList<String> conditions = new ArrayList<>();

			if (!id_instTxt.getText().isEmpty()) {
				conditions.add(" ID = '" + id_instTxt.getText() + "'");
			}
			if (!nameInstTxt.getText().isEmpty()) {
				conditions.add(" name = '" + nameInstTxt.getText() + "'");
			}
			if (!combo3Inst.getItems().isEmpty()) {
				if (combo3Inst.getValue() != null)
					conditions.add(" dept_name = '" + combo3Inst.getValue() + "'");
			}
			if (!salaryInstTxt.getText().isEmpty()) {
				conditions.add(" salary = " + salaryInstTxt.getText());
			}
			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsInst.disableProperty()
				.bind(combo3Inst.getSelectionModel().selectedItemProperty().isNull().or(salaryInstTxt.textProperty()
						.isEmpty().or(nameInstTxt.textProperty().isEmpty().or(id_instTxt.textProperty().isEmpty()))));

		btnInsInst.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM instructor WHERE ID = '" + id_instTxt.getText() + "'";

				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}
				String query = "INSERT INTO instructor (ID,name,dept_name,salary) VALUES ('" + id_instTxt.getText()
						+ "','" + nameInstTxt.getText() + "','" + combo3Inst.getValue() + "'," + salaryInstTxt.getText()
						+ ")";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("added successfuly!");
				a.show();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		// prereq tab
		GridPane gridPre = new GridPane();
		gridPre.setAlignment(Pos.CENTER);
		gridPre.setHgap(10);
		gridPre.setVgap(10);
		gridPre.setPadding(new Insets(25, 25, 25, 25));
		Text preTableText = new Text("Pre-requisit Table");
		preTableText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridPre.add(preTableText, 0, 0, 2, 1);

		Label course_idPre = new Label("ID:");
		gridPre.add(course_idPre, 0, 1);

		TextField course_idTxtPre = new TextField();
		gridPre.add(course_idTxtPre, 1, 1);

		Label prereq_id = new Label("Name:");
		gridPre.add(prereq_id, 2, 1);

		TextField prereq_idTxt = new TextField();
		gridPre.add(prereq_idTxt, 3, 1);

		Button btnInsPre = new Button("Insert");
		Button btnSchPre = new Button("Search");

		HBox hbBtnPre = new HBox(10);
		hbBtnPre.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnPre.getChildren().addAll(btnInsPre, btnSchPre);
		gridPre.add(hbBtnPre, 0, 3);
		prereq.setContent(gridPre);
		btnSchPre.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from prereq";
			ArrayList<String> conditions = new ArrayList<>();

			if (!course_idTxtPre.getText().isEmpty()) {
				conditions.add(" course_id = '" + course_idTxtPre.getText() + "'");
			}
			if (!prereq_idTxt.getText().isEmpty()) {
				conditions.add(" prereq_id = '" + prereq_idTxt.getText() + "'");
			}
			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsPre.disableProperty()
				.bind(course_idTxtPre.textProperty().isEmpty().or(prereq_idTxt.textProperty().isEmpty()));

		btnInsPre.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM prereq WHERE course_id = '" + course_idTxtPre.getText() + "'";

				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}

				String checkcourseq = "SELECT * FROM course WHERE course_id ='" + course_idTxtPre.getText() + "'";
				ResultSet resultCourse1 = stmt.executeQuery(checkcourseq);
				if (!resultCourse1.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("course ID does not exist in the course table!");
					a.show();
					return;
				}

				checkcourseq = "SELECT * FROM course WHERE course_id ='" + prereq_idTxt.getText() + "'";
				ResultSet resultCourse2 = stmt.executeQuery(checkcourseq);
				if (!resultCourse2.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("prereq ID does not exist in the course table!");
					a.show();
					return;
				}

				String query = "INSERT INTO prereq (course_id,prereq_id) VALUES ('" + course_idTxtPre.getText() + "','"
						+ prereq_idTxt.getText() + "')";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("added successfuly!");
				a.show();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		// section tab
		GridPane gridSec = new GridPane();
		gridSec.setAlignment(Pos.CENTER);
		gridSec.setHgap(10);
		gridSec.setVgap(10);
		gridSec.setPadding(new Insets(25, 25, 25, 25));
		Text secTableText = new Text("Section Table");
		secTableText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridSec.add(secTableText, 0, 0, 2, 1);

		Label course_idSec = new Label("Course id:");
		gridSec.add(course_idSec, 0, 1);

		TextField course_idSecTxt = new TextField();
		gridSec.add(course_idSecTxt, 1, 1);

		Label sec_idSec = new Label("Section id:");
		gridSec.add(sec_idSec, 2, 1);

		TextField sec_idSecTxt = new TextField();
		gridSec.add(sec_idSecTxt, 3, 1);

		Label semesterSec = new Label("Semester:");
		gridSec.add(semesterSec, 0, 2);

		TextField semesterSecTxt = new TextField();
		gridSec.add(semesterSecTxt, 1, 2);

		Label yearSec = new Label("Year:");
		gridSec.add(yearSec, 2, 2);

		TextField yearSectxt = new TextField();
		gridSec.add(yearSectxt, 3, 2);

		Label buildingSec = new Label("Building:");
		gridSec.add(buildingSec, 0, 3);

		TextField buildingSecTxt = new TextField();
		gridSec.add(buildingSecTxt, 1, 3);

		Label roomNumSec = new Label("Room Number:");
		gridSec.add(roomNumSec, 2, 3);

		TextField roomNumSecTxt = new TextField();
		gridSec.add(roomNumSecTxt, 3, 3);

		Label time_slotIdSec = new Label("Time slot id:");
		gridSec.add(time_slotIdSec, 0, 4);

		TextField time_slotIdSecTxt = new TextField();
		gridSec.add(time_slotIdSecTxt, 1, 4);

		Button btnInsSec = new Button("Insert");
		Button btnSchSec = new Button("Search");

		HBox hbBtnSec = new HBox(10);
		hbBtnSec.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnSec.getChildren().addAll(btnInsSec, btnSchSec);
		gridSec.add(hbBtnSec, 0, 5);
		section.setContent(gridSec);
		btnSchSec.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from section";
			ArrayList<String> conditions = new ArrayList<>();

			if (!course_idSecTxt.getText().isEmpty()) {
				conditions.add(" course_id = '" + course_idSecTxt.getText() + "'");
			}
			if (!sec_idSecTxt.getText().isEmpty()) {
				conditions.add(" sec_id = '" + sec_idSecTxt.getText() + "'");
			}
			if (!semesterSecTxt.getText().isEmpty()) {
				conditions.add(" semester = '" + semesterSecTxt.getText() + "'");
			}
			if (!yearSectxt.getText().isEmpty()) {
				conditions.add(" year = " + yearSectxt.getText());
			}
			if (!buildingSecTxt.getText().isEmpty()) {
				conditions.add(" building = '" + buildingSecTxt.getText() + "'");
			}
			if (!roomNumSecTxt.getText().isEmpty()) {
				conditions.add(" room_number = '" + roomNumSecTxt.getText() + "'");
			}
			if (!time_slotIdSecTxt.getText().isEmpty()) {
				conditions.add(" time_slot_id = '" + time_slotIdSecTxt.getText() + "'");
			}
			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsSec.disableProperty().bind(course_idSecTxt.textProperty().isEmpty().or(sec_idSecTxt.textProperty()
				.isEmpty()
				.or(semesterSecTxt.textProperty().isEmpty().or(yearSectxt.textProperty().isEmpty().or(buildingSecTxt
						.textProperty().isEmpty()
						.or(roomNumSecTxt.textProperty().isEmpty().or(time_slotIdSecTxt.textProperty().isEmpty())))))));

		btnInsSec.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM section WHERE course_id = '" + course_idSecTxt.getText()
						+ "' AND sec_id = '" + sec_idSecTxt.getText() + "' AND semester ='" + semesterSecTxt.getText()
						+ "' AND year = " + yearSectxt.getText();

				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}

				String checkcourseq = "SELECT * FROM course WHERE course_id ='" + course_idSecTxt.getText() + "'";
				ResultSet resultCourse = stmt.executeQuery(checkcourseq);
				if (!resultCourse.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("course ID does not exist in the course table!");
					a.show();
					return;
				}

				String checkbuildseq = "SELECT * FROM classroom WHERE building ='" + buildingSecTxt.getText() + "'";
				ResultSet resultBuild1 = stmt.executeQuery(checkbuildseq);
				if (!resultBuild1.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("building does not exist in the classroom table!");
					a.show();
					return;
				}

				checkbuildseq = "SELECT * FROM classroom WHERE room_number ='" + roomNumSecTxt.getText() + "'";
				ResultSet resultBuild2 = stmt.executeQuery(checkbuildseq);
				if (!resultBuild2.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("room number does not exist in the classroom table!");
					a.show();
					return;
				}

				String checkTimeslotseq = "SELECT * FROM time_slot WHERE time_slot_id ='" + time_slotIdSecTxt.getText()
						+ "'";
				ResultSet resultTimeslot = stmt.executeQuery(checkTimeslotseq);
				if (!resultTimeslot.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("building does not exist in the classroom table!");
					a.show();
					return;
				}

				String query = "INSERT INTO section (course_id,sec_id,semester,year,building,room_number,time_slot_id) VALUES ('"
						+ course_idSecTxt.getText() + "','" + sec_idSecTxt.getText() + "','" + semesterSecTxt.getText()
						+ "'," + yearSectxt.getText() + ",'" + buildingSecTxt.getText() + "','"
						+ roomNumSecTxt.getText() + "','" + time_slotIdSecTxt.getText() + "')";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("Added successfuly!");
				a.show();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		// takes tab
		GridPane gridTakes = new GridPane();
		gridTakes.setAlignment(Pos.CENTER);
		gridTakes.setHgap(10);
		gridTakes.setVgap(10);
		gridTakes.setPadding(new Insets(25, 25, 25, 25));
		Text takesTableText = new Text("Section Table");
		takesTableText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridTakes.add(takesTableText, 0, 0, 2, 1);

		Label st_idTakes = new Label("Student id:");
		gridTakes.add(st_idTakes, 0, 1);

		TextField st_idTakesTxt = new TextField();
		gridTakes.add(st_idTakesTxt, 1, 1);

		Label course_idTakes = new Label("Course id:");
		gridTakes.add(course_idTakes, 2, 1);

		TextField course_idTakestxt = new TextField();
		gridTakes.add(course_idTakestxt, 3, 1);

		Label sec_idTakes = new Label("Section id:");
		gridTakes.add(sec_idTakes, 0, 2);

		TextField sec_idTakestxt = new TextField();
		gridTakes.add(sec_idTakestxt, 1, 2);

		Label semesterTakes = new Label("Semester:");
		gridTakes.add(semesterTakes, 2, 2);

		TextField semester_Takestxt = new TextField();
		gridTakes.add(semester_Takestxt, 3, 2);

		Label yearTakes = new Label("Year:");
		gridTakes.add(yearTakes, 0, 3);

		TextField yearTakestxt = new TextField();
		gridTakes.add(yearTakestxt, 1, 3);

		Label gradeTakes = new Label("Grade:");
		gridTakes.add(gradeTakes, 2, 3);

		TextField gradeTakestxt = new TextField();
		gridTakes.add(gradeTakestxt, 3, 3);

		Button btnInsTakes = new Button("Insert");
		Button btnSchTakes = new Button("Search");

		HBox hbBtnTakes = new HBox(10);
		hbBtnTakes.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnTakes.getChildren().addAll(btnInsTakes, btnSchTakes);
		gridTakes.add(hbBtnTakes, 0, 5);
		takes.setContent(gridTakes);
		btnSchTakes.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from takes";
			ArrayList<String> conditions = new ArrayList<>();

			if (!st_idTakesTxt.getText().isEmpty()) {
				conditions.add(" ID = '" + st_idTakesTxt.getText() + "'");
			}
			if (!course_idTakestxt.getText().isEmpty()) {
				conditions.add(" course_id = '" + course_idTakestxt.getText() + "'");
			}
			if (!sec_idTakestxt.getText().isEmpty()) {
				conditions.add(" sec_id = '" + sec_idTakestxt.getText() + "'");
			}
			if (!semester_Takestxt.getText().isEmpty()) {
				conditions.add(" semester = '" + semester_Takestxt.getText() + "'");
			}
			if (!yearTakestxt.getText().isEmpty()) {
				conditions.add(" year = " + yearTakestxt.getText());
			}
			if (!gradeTakestxt.getText().isEmpty()) {
				conditions.add(" grade = '" + gradeTakestxt.getText() + "'");
			}

			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsTakes.disableProperty().bind(st_idTakesTxt.textProperty().isEmpty().or(course_idTakestxt.textProperty()
				.isEmpty().or(sec_idTakestxt.textProperty().isEmpty().or(semester_Takestxt.textProperty().isEmpty()
						.or(yearTakestxt.textProperty().isEmpty().or(gradeTakestxt.textProperty().isEmpty()))))));

		btnInsTakes.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM takes WHERE ID = '" + st_idTakesTxt.getText() + "' AND course_id = '"
						+ course_idTakestxt.getText() + "' AND sec_id = '" + sec_idTakestxt.getText()
						+ "' AND semester ='" + semester_Takestxt.getText() + "' AND year = " + yearTakestxt.getText();

				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}

				String checkcourseq = "SELECT * FROM section WHERE course_id = '" + course_idTakestxt.getText()
						+ "' AND sec_id = '" + sec_idTakestxt.getText() + "' AND semester ='"
						+ semester_Takestxt.getText() + "' AND year = " + yearTakestxt.getText();

				ResultSet resultCourse = stmt.executeQuery(checkcourseq);
				if (!resultCourse.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("course does not exist in the section table!");
					a.show();
					return;
				}

				String checkStseq = "SELECT * FROM student WHERE ID ='" + st_idTakesTxt.getText() + "'";
				ResultSet resultStudent = stmt.executeQuery(checkStseq);
				if (!resultStudent.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Student id does not exist in the student table!");
					a.show();
					return;
				}

				String query = "INSERT INTO takes (ID,course_id,sec_id,semester,year,grade) VALUES ('"
						+ st_idTakesTxt.getText() + "','" + course_idTakestxt.getText() + "','"
						+ sec_idTakestxt.getText() + "','" + semester_Takestxt.getText() + "'," + yearTakestxt.getText()
						+ ",'" + gradeTakestxt.getText() + "')";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("Added successfuly!");
				a.show();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		// teaches tab
		GridPane gridTeaches = new GridPane();
		gridTeaches.setAlignment(Pos.CENTER);
		gridTeaches.setHgap(10);
		gridTeaches.setVgap(10);
		gridTeaches.setPadding(new Insets(25, 25, 25, 25));
		Text teachesTableText = new Text("Teaches Table");
		teachesTableText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridTeaches.add(teachesTableText, 0, 0, 2, 1);

		Label inst_idTeaches = new Label("Instructor id:");
		gridTeaches.add(inst_idTeaches, 0, 1);

		TextField inst_idTeachesTxt = new TextField();
		gridTeaches.add(inst_idTeachesTxt, 1, 1);

		Label course_idTeaches = new Label("Course id:");
		gridTeaches.add(course_idTeaches, 2, 1);

		TextField course_idTeachestxt = new TextField();
		gridTeaches.add(course_idTeachestxt, 3, 1);

		Label sec_idTeaches = new Label("Section id:");
		gridTeaches.add(sec_idTeaches, 0, 2);

		TextField sec_idTeachestxt = new TextField();
		gridTeaches.add(sec_idTeachestxt, 1, 2);

		Label semesterTeaches = new Label("Semester:");
		gridTeaches.add(semesterTeaches, 2, 2);

		TextField semester_Teachestxt = new TextField();
		gridTeaches.add(semester_Teachestxt, 3, 2);

		Label yearTeaches = new Label("Year:");
		gridTeaches.add(yearTeaches, 0, 3);

		TextField yearTeachestxt = new TextField();
		gridTeaches.add(yearTeachestxt, 1, 3);

		Button btnInsTeaches = new Button("Insert");
		Button btnSchTeaches = new Button("Search");

		HBox hbBtnTeaches = new HBox(10);
		hbBtnTeaches.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnTeaches.getChildren().addAll(btnInsTeaches, btnSchTeaches);
		gridTeaches.add(hbBtnTeaches, 0, 5);
		teaches.setContent(gridTeaches);
		btnSchTeaches.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from teaches";
			ArrayList<String> conditions = new ArrayList<>();

			if (!inst_idTeachesTxt.getText().isEmpty()) {
				conditions.add(" ID = '" + inst_idTeachesTxt.getText() + "'");
			}
			if (!course_idTeachestxt.getText().isEmpty()) {
				conditions.add(" course_id = '" + course_idTeachestxt.getText() + "'");
			}
			if (!sec_idTeachestxt.getText().isEmpty()) {
				conditions.add(" sec_id = '" + sec_idTeachestxt.getText() + "'");
			}
			if (!semester_Teachestxt.getText().isEmpty()) {
				conditions.add(" semester = '" + semester_Teachestxt.getText() + "'");
			}
			if (!yearTeachestxt.getText().isEmpty()) {
				conditions.add(" year = " + yearTeachestxt.getText());
			}
			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsTeaches.disableProperty().bind(inst_idTeachesTxt.textProperty().isEmpty()
				.or(course_idTeachestxt.textProperty().isEmpty().or(sec_idTeachestxt.textProperty().isEmpty().or(
						semester_Teachestxt.textProperty().isEmpty().or(yearTeachestxt.textProperty().isEmpty())))));

		btnInsTeaches.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM teaches WHERE ID = '" + inst_idTeachesTxt.getText()
						+ "' AND course_id = '" + course_idTeachestxt.getText() + "' AND sec_id = '"
						+ sec_idTeachestxt.getText() + "' AND semester ='" + semester_Teachestxt.getText()
						+ "' AND year = " + yearTeachestxt.getText();

				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}

				String checkcourseq = "SELECT * FROM section WHERE course_id = '" + course_idTeachestxt.getText()
						+ "' AND sec_id = '" + sec_idTeachestxt.getText() + "' AND semester ='"
						+ semester_Teachestxt.getText() + "' AND year = " + yearTeachestxt.getText();

				ResultSet resultCourse = stmt.executeQuery(checkcourseq);
				if (!resultCourse.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("course does not exist in the section table!");
					a.show();
					return;
				}

				String checkStseq = "SELECT * FROM instructor WHERE ID ='" + inst_idTeachesTxt.getText() + "'";
				ResultSet resultInst = stmt.executeQuery(checkStseq);
				if (!resultInst.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Instructor id does not exist in the instructor table!");
					a.show();
					return;
				}

				String query = "INSERT INTO teaches (ID,course_id,sec_id,semester,year) VALUES ('"
						+ inst_idTeachesTxt.getText() + "','" + course_idTeachestxt.getText() + "','"
						+ sec_idTeachestxt.getText() + "','" + semester_Teachestxt.getText() + "',"
						+ yearTeachestxt.getText() + ")";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("Added successfuly!");
				a.show();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		// time slot tab
		GridPane gridTimeSlot = new GridPane();
		gridTimeSlot.setAlignment(Pos.CENTER);
		gridTimeSlot.setHgap(10);
		gridTimeSlot.setVgap(10);
		gridTimeSlot.setPadding(new Insets(25, 25, 25, 25));
		Text timeSlotTableText = new Text("Time slot Table");
		timeSlotTableText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridTimeSlot.add(timeSlotTableText, 0, 0, 2, 1);

		Label dayTimeSlot = new Label("Day:");
		gridTimeSlot.add(dayTimeSlot, 0, 1);

		TextField dayTimeSlottxt = new TextField();
		gridTimeSlot.add(dayTimeSlottxt, 1, 1);

		Label timeSlotId = new Label("Time slot id:");
		gridTimeSlot.add(timeSlotId, 2, 1);

		TextField timeSlotIdTxt = new TextField();
		gridTimeSlot.add(timeSlotIdTxt, 3, 1);

		Label startHr = new Label("Start hour:");
		gridTimeSlot.add(startHr, 0, 2);

		TextField startHrtxt = new TextField();
		gridTimeSlot.add(startHrtxt, 1, 2);

		Label endHr = new Label("End hour:");
		gridTimeSlot.add(endHr, 2, 2);

		TextField endHrtxt = new TextField();
		gridTimeSlot.add(endHrtxt, 3, 2);

		Label startMin = new Label("Start Min:");
		gridTimeSlot.add(startMin, 0, 3);

		TextField startMintxt = new TextField();
		gridTimeSlot.add(startMintxt, 1, 3);

		Label endMin = new Label("End Minute:");
		gridTimeSlot.add(endMin, 2, 3);

		TextField endMintxt = new TextField();
		gridTimeSlot.add(endMintxt, 3, 3);

		Button btnInsTimeSlot = new Button("Insert");
		Button btnSchTimeSlot = new Button("Search");

		HBox hbBtnTimeSlot = new HBox(10);
		hbBtnTimeSlot.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtnTimeSlot.getChildren().addAll(btnInsTimeSlot, btnSchTimeSlot);
		gridTimeSlot.add(hbBtnTimeSlot, 0, 5);
		time_slot.setContent(gridTimeSlot);
		btnSchTimeSlot.setOnAction(e -> {
			data = FXCollections.observableArrayList();
			String queryCl = "SELECT * from time_slot";
			ArrayList<String> conditions = new ArrayList<>();

			if (!dayTimeSlottxt.getText().isEmpty()) {
				conditions.add(" day = '" + dayTimeSlottxt.getText() + "'");
			}
			if (!endHrtxt.getText().isEmpty()) {
				conditions.add(" end_hr = " + endHrtxt.getText());
			}
			if (!endMintxt.getText().isEmpty()) {
				conditions.add(" end_min = " + endMintxt.getText());
			}
			if (!startHrtxt.getText().isEmpty()) {
				conditions.add(" start_hr = " + startHrtxt.getText());
			}
			if (!startMintxt.getText().isEmpty()) {
				conditions.add(" start_min = " + startMintxt.getText());
			}
			if (!timeSlotIdTxt.getText().isEmpty()) {
				conditions.add(" time_slot_id = '" + timeSlotIdTxt.getText() + "'");
			}
			if (!conditions.isEmpty()) {
				queryCl += " WHERE " + String.join(" AND ", conditions);
			}
			System.out.println(queryCl);
			// ResultSet
			ResultSet rS;
			tableview.getColumns().clear();
			try {
				rS = c.createStatement().executeQuery(queryCl);
				for (int i = 0; i < rS.getMetaData().getColumnCount(); i++) {
					// We are using non property style for making dynamic table
					final int j = i;
					TableColumn col = new TableColumn(rS.getMetaData().getColumnName(i + 1));
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});

					tableview.getColumns().addAll(col);
					System.out.println("Column [" + i + "] ");

				}
				/********************************
				 * Data added to ObservableList *
				 ********************************/
				while (rS.next()) {
					// Iterate Row
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= rS.getMetaData().getColumnCount(); i++) {
						// Iterate Column
						row.add(rS.getString(i));
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
		});

		btnInsTimeSlot.disableProperty().bind(dayTimeSlottxt.textProperty().isEmpty().or(endHrtxt.textProperty()
				.isEmpty().or(endMintxt.textProperty().isEmpty().or(startHrtxt.textProperty().isEmpty()
						.or(timeSlotIdTxt.textProperty().isEmpty().or(startMintxt.textProperty().isEmpty()))))));

		btnInsTimeSlot.setOnAction(e -> {
			try {
				Statement stmt = c.createStatement();

				String checkQuery = "SELECT * FROM time_slot WHERE day = '" + dayTimeSlottxt.getText()
						+ "' AND end_hr = " + endHrtxt.getText() + " AND end_min = " + endMintxt.getText()
						+ " AND start_hr = " + startHrtxt.getText() + " AND time_slot_id = '" + timeSlotIdTxt.getText()
						+ "' AND start_min = " + startMintxt.getText();

				ResultSet checkResult = stmt.executeQuery(checkQuery);

				if (checkResult.next()) {
					a.setAlertType(AlertType.WARNING);
					a.setContentText("Already exists!");
					a.show();
					return;
				}

				String query = "INSERT INTO time_slot (time_slot_id,day,start_hr,start_min,end_hr,end_min) VALUES ('"
						+ timeSlotIdTxt.getText() + "','" + dayTimeSlottxt.getText() + "'," + startHrtxt.getText() + ","
						+ startMintxt.getText() + "," + endHrtxt.getText() + "," + endMintxt.getText() + ")";
				System.out.println(query);
				stmt.executeUpdate(query);
				a.setAlertType(AlertType.CONFIRMATION);
				a.setContentText("Added successfuly!");
				a.show();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		GridPane gridAboutme = new GridPane();
		gridAboutme.setAlignment(Pos.TOP_LEFT);
		gridAboutme.setHgap(10);
		gridAboutme.setVgap(10);
		gridAboutme.setPadding(new Insets(25, 25, 25, 25));
		Text Aboutme = new Text("Personal Info:");
		Aboutme.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		gridAboutme.add(Aboutme, 0, 0, 2, 1);

		Label Name = new Label("Full name : Andrea Sami George Tafish\r\n" + " Major: Software Engineering\r\n"
				+ "Home Phone: 022747913\r\n" + "Mobile Phone: 0598667311\r\n" + "Date of birth: 2/5/2002\r\n"
				+ "Location: Bethlehem, Palestine");
		gridAboutme.add(Name, 0, 1);
		Label age = new Label(
				"Hobbies\r\n" + "I like gaming, scouting, watching TV, listening to music, \njogging, card games.");
		gridAboutme.add(age, 1, 1);
		Label res = new Label(
				"University life\r\n" + "I chose BU, because from a future perspective Bethlehem university satisfied\n"
						+ " my needs where I want to be and what I want to be in my future career,\n"
						+ " and from a personal perspective I'am comfortable with the general\n atmosphere "
						+ "it provides and the religious/political parties it's involved in\n, and finally"
						+ " I chose this university to be close to my home and life which\n makes travelling "
						+ "and things easier to me making it the perfect university to me.");
		gridAboutme.add(res, 0, 2);
		Label swer = new Label("Software Engineering\r\n" + "Software engineering is a systematic engineering\n "
				+ "approach to software development. A software engineer\n"
				+ " is a person who applies the principles of software \nengineering to"
				+ " design, develop, maintain, test, and \nevaluate computer software.");
		gridAboutme.add(swer, 1, 2);

		Image image = new Image(new FileInputStream("C:\\Users\\Administrator\\Desktop\\IMG_1690.jpg"));
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(250);
		imageView.setFitWidth(250);
		gridAboutme.add(imageView, 0, 5);
		aboutme.setContent(gridAboutme);

		Scene scene = new Scene(pane);
		stage.show();
		stage.setTitle("University DB");

		stage.setWidth(800);
		stage.setScene(scene);
		stage.setResizable(false);
		Stage tableviewStage = new Stage();
		// Create a new scene and set it as the scene for the stage
		Scene tableviewScene = new Scene(tableview, 400, 400);
		tableviewStage.setScene(tableviewScene);

		// Set the stage title and show the stage
		tableviewStage.setTitle("Table View");
		tableviewStage.show();

	}

}
