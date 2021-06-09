import Controllers.ControllerSecondMenu;
import GenratedCode.RizpaStockExchangeDescriptor;
import Tasks.TaskLoadXml;
import components.commerce.RitzpaStockManager;
import components.dataTransferObject.JaxbXmlToObject;
import components.dataTransferObject.ObjectSerialization;
import javafx.animation.FillTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.util.Duration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ControllerFirstPage {
RitzpaStockManager manager;
    @FXML
    private ScrollPane scrollPaneFirstMenu;

    @FXML
    private Pane PaneForTheFirstMenu;

    @FXML
    private Button ButtonLoafFromXml;
    @FXML
    private ProgressBar TaskProgressBarTask;

    @FXML
     public Label LableMessageTask;

    @FXML
     public Label LableTaskProgress;
    @FXML
    private Button ButtonLoadLastUpdate;
    @FXML
    void LoadFormXmlClicked(ActionEvent event)  {


        taskLoaderFromXmlAndSerialization(Boolean.TRUE);

    }

    @FXML
    void LoadLastUpdateClicked(ActionEvent event) throws IOException, ClassNotFoundException {

            taskLoaderFromXmlAndSerialization(Boolean.FALSE);
    }
    public static void errorPopUp(String header , String body)
    {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(body);
        errorAlert.showAndWait();
    }

    private void CreateNewMenu(String xmlPath) throws IOException {
        FXMLLoader root;
        try {
            root = new  FXMLLoader(getClass().getClassLoader().getResource(xmlPath));

            Parent parent = root.load();
            ControllerSecondMenu controller =  root.getController();
            controller.manager = this.manager;
            Stage stage = new Stage();
            stage.setMaxHeight(637);
            stage.setMaxWidth(1500);
            stage.setScene(new Scene(parent, 1200, 635));

            stage.setOnShowing((new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controller.setAllComponents();
                }
            }));
            stage.show();
          Stage stageFirst = (Stage) ButtonLoadLastUpdate.getScene().getWindow();
          stageFirst.close();

        }
        catch (IOException ex)
        {

        }
    }
    public void bindUiToTaskComponents(Task<Boolean> loadXmlTask)
    {
        TaskProgressBarTask.progressProperty().bind(loadXmlTask.progressProperty());
        LableTaskProgress.textProperty().bind(
                Bindings.concat(
                        Bindings.format(
                                "%.0f",
                                Bindings.multiply(
                                        loadXmlTask.progressProperty(),100)),"%"
                                )
                        );
        LableMessageTask.textProperty().bind(loadXmlTask.messageProperty());
        loadXmlTask.valueProperty().addListener(((observable, oldValue, newValue) ->
        {
            onTaskFinished(newValue,((TaskLoadXml)loadXmlTask).manager,((TaskLoadXml)loadXmlTask).errorDesc );
        }));
    }
    public void onTaskFinished(Boolean value, RitzpaStockManager rtz ,StringBuilder errorDesc)  {
        try {
            if (value) {
                manager = rtz;
                CreateNewMenu("FXMLFiles/SecondMenu.fxml");
            } else {
                errorPopUp("Error!!", errorDesc.toString());
            }
        }
        catch (IOException ex) {
            errorPopUp("Error!", "Faield in xml loading proccess");
        }
}
public void taskLoaderFromXmlAndSerialization(Boolean isXmlLoader)
{
    File xmlFile=null;
    if(isXmlLoader) {
        FileChooser xmLFIleChooser = new FileChooser();
        xmlFile = xmLFIleChooser.showOpenDialog(null);
        if(xmlFile==null)
            return;
    }
    Task<Boolean> taskLoadXml = new TaskLoadXml(xmlFile,isXmlLoader);
    bindUiToTaskComponents(taskLoadXml);
    new Thread(taskLoadXml).start();
}
    }



