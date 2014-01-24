/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class Shapefile extends Application {

    private final ObservableList ops = FXCollections.observableArrayList("==", "!=", ">", "<");
    private final ObservableList joints = FXCollections.observableArrayList("AND", "OR");
    private ObservableList proprties;

    private int gridRowCount = 0;
    private GridPane queryGrid = new GridPane();
    private ConnectionJDBC jdbc = new ConnectionJDBC();

    private void init(Stage primaryStage) {
        jdbc.openConnection();
        proprties = FXCollections.observableArrayList(jdbc.getAttributes().getItems());
        jdbc.closeConnection();
        //jdbc.testDatabase();

        Group root = new Group();
        VBox vbox = new VBox();

        primaryStage.setScene(new Scene(root));

        queryGrid.setPadding(new Insets(18, 18, 18, 18));

        RowConstraints rowinfo = new RowConstraints(40, 40, 40);
        ColumnConstraints colinfo = new ColumnConstraints(90, 90, 90);

        for (int i = 0; i <= 10; i++) {
            queryGrid.getRowConstraints().add(rowinfo);
        }

        for (int j = 0; j <= 5; j++) {
            queryGrid.getColumnConstraints().add(colinfo);
        }

        AddRowToGrid();
        //AddRowToGrid();

        vbox.getChildren().addAll(queryGrid, new Separator());

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-base: green;");

        submitBtn.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                jdbc.createShapefile(getQuery());
                Platform.exit();
            }
        });

        root.getChildren().add(vbox);
        root.getChildren().add(submitBtn);
    }

    //obtain value from the grid and form a query 
    private String getQuery() {
        String query = "";
        ObservableList<Node> children = queryGrid.getChildren();

        //loop through grid
        for (int i = 0; i < gridRowCount + 1; i++) {
            if (i != 0) {
                query += " " + ((ComboBox) children.get(i * 5)).getValue().toString() + " ";
            }

            query += ((ComboBox) children.get(i * 5 + 1)).getValue().toString() + " ";
            query += ((ComboBox) children.get(i * 5 + 2)).getValue().toString() + " ";
            query += ((TextField) children.get(i * 5 + 3)).getText();
        }
        return query;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public class xyz implements EventHandler {

        @Override
        public void handle(Event event) {
            AddRowToGrid();
        }
    }

    private void AddRowToGrid() {
        boolean isFirstRow = false;

        if (queryGrid.getChildren().isEmpty()) {
            gridRowCount = 0;
            isFirstRow = true;
        } else {
            gridRowCount++;
        }

        if (isFirstRow) {
            Button addBtn = new Button("Add");
            addBtn.setStyle("-fx-base: green;");
            //xyz obj = new xyz();
            //addBtn.setOnMouseClicked(obj);

            addBtn.setOnMouseClicked(new EventHandler<Event>() {

                @Override
                public void handle(Event event) {
                    AddRowToGrid();
                }
            });

            GridPane.setHalignment(addBtn, HPos.CENTER);
            GridPane.setConstraints(addBtn, 0, gridRowCount);
            queryGrid.getChildren().add(addBtn);
        } else {
            ComboBox joint = ComboBoxBuilder.create()
                    .id("uneditable-combobox")
                    .promptText("Make a choice...")
                    .items(FXCollections.observableArrayList(joints)).build();

            GridPane.setHalignment(joint, HPos.CENTER);
            GridPane.setConstraints(joint, 0, gridRowCount);
            queryGrid.getChildren().add(joint);
        }

        ComboBox attributes = ComboBoxBuilder.create()
                .id("uneditable-combobox")
                .promptText("Make a choice...")
                .items(FXCollections.observableArrayList(proprties)).build();

        ComboBox operations = ComboBoxBuilder.create()
                .id("uneditable-combobox")
                .promptText("Make a choice...")
                .items(FXCollections.observableArrayList(ops)).build();

        /*ComboBox values = ComboBoxBuilder.create()
         .id("uneditable-combobox")
         .promptText("Make a choice...")
         .items(FXCollections.observableArrayList(proprties)).build();*/
        TextField values = new TextField("");

        GridPane.setHalignment(attributes, HPos.CENTER);
        GridPane.setHalignment(operations, HPos.CENTER);
        GridPane.setHalignment(values, HPos.CENTER);

        //Place content
        GridPane.setConstraints(attributes, 1, gridRowCount);
        GridPane.setConstraints(operations, 2, gridRowCount);
        GridPane.setConstraints(values, 3, gridRowCount);

        queryGrid.getChildren().add(attributes);
        queryGrid.getChildren().add(operations);
        queryGrid.getChildren().add(values);

        if (!isFirstRow) {
            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-base: red;");
            deleteBtn.setOnMouseClicked(new EventHandler<Event>() {

                @Override
                public void handle(Event event) {
                    // delete the current row
                    gridRowCount--;
                    Button deleteBtn = (Button) event.getSource();
                    DeleteRowFromGrid(queryGrid.getRowIndex(deleteBtn) * 5);
                }
            });

            GridPane.setHalignment(deleteBtn, HPos.CENTER);
            GridPane.setConstraints(deleteBtn, 4, gridRowCount);
            queryGrid.getChildren().add(deleteBtn);
        } else {
            //dummy button
            Button dummyBtn = new Button("");
            dummyBtn.setVisible(false);
            GridPane.setConstraints(dummyBtn, 4, gridRowCount);
            queryGrid.getChildren().add(dummyBtn);
        }

    }

    private void DeleteRowFromGrid(int startIndex) {
        int endIndex = startIndex + 5;
        ObservableList<Node> children = queryGrid.getChildren();

        int size = children.size();
        ArrayList<Node> sublist = new ArrayList<Node>();
        sublist.addAll(children.subList(endIndex, size));

        children.remove(startIndex, size);

        size = sublist.size();

        for (int i = 0; i < size; i++) {
            Node child = sublist.get(i);
            int rowIndex = GridPane.getRowIndex(child);
            int colIndex = GridPane.getColumnIndex(child);
            GridPane.setConstraints(child, colIndex, rowIndex - 1);

            children.add(child);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
