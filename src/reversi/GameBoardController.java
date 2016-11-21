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

import java.net.URL;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {

    @FXML public Tab gameTab;
    @FXML public JFXTabPane tabPane;
    @FXML public ToggleGroup difficultyToggleGroup;
    @FXML public Pane gamePane;

    private Group tileGroup = new Group();

    /** 0 = empty, 1 = white, 2 = black */
    int[][] internal_board;

    /** 1= turn; 0 = leave as is */
    int[][] tiles_to_turn;

    int[][] surrounding = new int[3][3];

    int current_player = 1;
    int opposing_player = 2;
    public int difficulty;


    public static double tile_size;
    public static int width;
    public static int height;


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
        placePiece(x,y);
    }

    private int mouseXtoTileX(double x) {
        return (int) (x / tile_size);
    }

    private int mouseYtoTileY(double y){
        return (int) (y / tile_size);
    }

    private void placePiece(int x , int y) {
        updateOpposingPlayer();
        determineSurrounding(x,y);
    }



    private boolean updateTurnables(int x, int y){

    }

    private void determineSurrounding(int x, int y) {
        try {
            surrounding[0][0] = internal_board[x-1][y-1];
        } catch (Exception e) {
            surrounding[0][0] = -1;
        }
        try {
            surrounding[0][1] = internal_board[x][y-1];
        } catch (Exception e) {
            surrounding[0][1] = -1;
        }
        try {
            surrounding[0][2] = internal_board[x+1][y-1];
        } catch (Exception e) {
            surrounding[0][2] = -1;
        }
        try {
            surrounding[1][0] = internal_board[x-1][y];
        } catch (Exception e) {
            surrounding[1][0] = -1;
        }
        try {
            surrounding[1][1] = -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            surrounding[1][2] = internal_board[x+1][y];
        } catch (Exception e) {
            surrounding[1][2] = -1;
        }
        try {
            surrounding[2][0] = internal_board[x-1][y+1];
        } catch (Exception e) {
            surrounding[2][0] = -1;
        }
        try {
            surrounding[2][1] = internal_board[x][y+1];
        } catch (Exception e) {
            surrounding[2][1] = -1;
        }
        try {
            surrounding[2][2] = internal_board[x+1][y+1];
        } catch (Exception e) {
            surrounding[2][2] = -1;
        }
    }

    /**
     * Überprüft ob der Zug gespielt werden darf
     * @param x
     * @param y
     * @return
     */
    public boolean isLegalMove(int x, int y) {
        return true;
    }


    public void setTile(int x, int y, int type){
        internal_board[x][y] = type;
        updateRender();
    }

    public void reverseTile(int x, int y) {

        if (internal_board[x][y] == 1) {
            internal_board[x][y] = 2;
        } else if (internal_board[x][y] == 2) {
            internal_board[x][y] = 1;
        } else return;

        updateRender();
    }

    public void updateOpposingPlayer(){
        if (current_player == 1) {
            opposing_player = 2;
        } else if (current_player == 2) {
            opposing_player = 1;
        }
    }

    public void updateRender() {
        tileGroup.getChildren().clear(); //WICHTIG! um memoryleaks zu verhindern.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Tile tile = new Tile(x,y,internal_board[x][y]);
                tileGroup.getChildren().add(tile);
            }
        }
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
