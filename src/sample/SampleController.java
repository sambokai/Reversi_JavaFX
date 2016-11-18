package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class SampleController implements Initializable {
    public Tab gameTab;
    @FXML public Canvas board_canvas;
    private GraphicsContext gc;

    private double field_width;
    private double field_height;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("INITIALIZED BOI");
        field_width = board_canvas.getWidth() / 8;
        field_height = board_canvas.getHeight() / 8;
        gc = board_canvas.getGraphicsContext2D();
        drawBoard(gc);
    }

    private void drawBoard(GraphicsContext gc){
        gc.setFill(Color.DARKGREEN);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 == col % 2) {
                    gc.setFill(Color.DARKGREEN);
                } else {
                    gc.setFill(Color.GREEN);
                }
                gc.fillRect(row*field_width, col*field_height, field_width, field_height);
            }
        }

    }
}
