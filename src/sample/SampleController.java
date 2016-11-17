package sample;

import com.jfoenix.controls.JFXTabPane;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class SampleController {
    public Label helloWorld;

    public void sayHelloWorld(ActionEvent actionEvent) {
        helloWorld.setText("Hello World!");
    }


}
