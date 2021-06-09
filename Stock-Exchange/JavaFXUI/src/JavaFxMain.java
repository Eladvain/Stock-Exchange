import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent load = FXMLLoader.load(getClass().getResource("FXMLFiles/firstMenu.fxml"));
        Scene scene = new Scene(load, 700, 600);
        primaryStage.setScene(scene);

        primaryStage.show();

    }
}
