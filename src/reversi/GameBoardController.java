package reversi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import sun.plugin2.applet.Applet2ManagerCache;

import java.net.URL;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {
    @FXML public Tab gameTab;

    @FXML public Group boardGroup;
    @FXML public ToggleGroup difficultyToggleGroup;

    public int difficulty;

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    @FXML public Pane gamePane;

    public static double tile_size;
    public static int width;
    public static int height;



    public void createContent() {
        gamePane.getChildren().setAll(tileGroup, pieceGroup);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = new Tile(x,y);
                tileGroup.getChildren().add(tile);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        width = 8;
        height = 8;

        tile_size = 600 / width;
        readUserDifficulty();
        createContent();
    }

    public int readUserDifficulty(){
        RadioButton selectedDifficulty = (RadioButton) difficultyToggleGroup.getSelectedToggle();
        switch(selectedDifficulty.getText()) {
            case "Easy":
                difficulty = 1;
            case "Medium":
                difficulty = 2;
            case "Hard":
                difficulty = 3;
        }
        return difficulty;
    }
}
