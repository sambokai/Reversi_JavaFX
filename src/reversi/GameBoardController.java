package reversi;

import com.jfoenix.controls.JFXTabPane;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {
    @FXML public Tab gameTab;
    @FXML public JFXTabPane tabPane;
    @FXML public Group boardGroup;
    @FXML public ToggleGroup difficultyToggleGroup;

    public int difficulty;

    private Group tileGroup = new Group();
    @FXML public Pane gamePane;

    int[][] internal_board;
    public static double tile_size;
    public static int width;
    public static int height;


    public void setTile(int x, int y, int type){ //TODO: morgen hier weitermachen: implementieren, dass setTile() nur klappt falls *REGELN VON REVERSI ES ZULASSEN*
        internal_board[x][y] = type;
        updateRender();
    }

    public Pane getGamePane() {
        return gamePane;
    }

    public void updateRender() {
        tileGroup.getChildren().clear();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Tile tile = new Tile(x,y,internal_board[x][y]);
                tileGroup.getChildren().add(tile);
            }
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        internal_board = new int[width][height];
        gamePane.getChildren().setAll(tileGroup);

        width = 8;
        height = 8;
        tile_size = 600 / width;

        readUserDifficulty();
        resetBoard();

        gamePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse) {
                mouseSetTile(mouse);
            }
        });

    }

    private void mouseSetTile(MouseEvent mouse) {
        int x = mouseXtoTileX(mouse.getX()), y = mouseYtoTileY(mouse.getY());
        setTile(x,y,2);

    }

    private int mouseXtoTileX(double x) {
        return (int) (x / tile_size);
    }

    private int mouseYtoTileY(double y){
        return (int) (y / tile_size);
    }

    private void resetBoard() {
        internal_board = new int[width][height];
        internal_board[(int) (width/2-0.5)][(int) (height/2-0.5)] = 1;
        internal_board[(int) (width/2+0.5)][(int) (height/2-0.5)] = 2;
        internal_board[(int) (width/2-0.5)][(int) (height/2+0.5)] = 2;
        internal_board[(int) (width/2+0.5)][(int) (height/2+0.5)] = 1;
        updateRender();
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
