package reversi;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {


    @FXML private ToggleGroup difficultyToggleGroup;
    @FXML private ToggleGroup gridsizeToggleGroup;
    @FXML private Pane gamePane;
    @FXML private JFXDialog gridChangeDialog;
    @FXML private StackPane root;
    @FXML private JFXButton acceptButton;
    @FXML private JFXButton cancelButton;
    @FXML private JFXTabPane tabPane;

    private Group tileGroup = new Group();

    /** 0 = empty, 1 = white, 2 = black */
    static int[][] internal_board;

    /** 1= turn; 0 = leave as is */
    static private int[][] tiles_to_turn;


    int current_player = 1;
    int opposing_player = 2;
    int difficulty;
    int white_tiles;
    int black_tiles;


    static double tile_size;
    static int width;
    static int height;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gamePane.getChildren().setAll(tileGroup);
        updateUserGridsize();
        updateUserDifficulty();
        resetBoard(false);

        printCurrentPlayer();
        updateScore();

        gamePane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouse) {
                mouseSetTile(mouse);
            }
        });

        gridsizeToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                gridChangeDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
                gridChangeDialog.show(root);
                gridChangeDialog.setOverlayClose(false);
                cancelButton.setOnMouseClicked((e)->{
                    oldValue.setSelected(true);
                    gridChangeDialog.close();
                });

                acceptButton.setOnMouseClicked((e)->{
                    updateUserGridsize();
                    gridChangeDialog.close();
                    tabPane.getSelectionModel().select(0);
                });
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

    /**
     *
     * @param x
     * @param y
     * @return
     */
    private int placePiece(int x , int y) {
        updateOpposingPlayer();

        //if spot is full leave the placepiece method
        if (internal_board[x][y] != 0 && internal_board[x][y] !=99) {
            return -1;
        }

        //Check if any moves are possible - If yes update tiles_to_turn accordingly, set the clicked tile, alternate current_player, and reverse all legally marked tiles
        if (checkAllDirections(x,y, true)) {
            //nur setTile() FALLS der zug legal war
            setTile(x,y,current_player);
            reverseAllMarkedTiles();
            alternatePlayers();
// TODO: spieler überspringt falls er keinen move machen kann
//            if (!anyMovesPossible(false)){
//                alternatePlayers();
//            }
            updateScore();
        }


        updateHelpPops();
        updateRender();
        return 0;
    }

    /**
     *
     * @param x
     * @param y
     * @param setTiles legt fest ob nur geprüft werden soll, ob ein zug möglich ist ODER ob die steine im möglichen zug auch gesetzt werden sollen (tilesToTurn method wird belegt)
     * @return
     */
    private boolean checkAllDirections(int x, int y, boolean setTiles){
        //falls der geklickte tile bereits mit einem stein belegt ist, brich die methode ab
        if (internal_board[x][y] != 0 && internal_board[x][y] != 99) {
            return false;
        }
        //hat mindestens einen gültigen pfad
        boolean hasAValidPath = false;
        reversi.Direction[] compass = new reversi.Direction[8];
        compass[0] = new reversi.Direction(0,1);
        compass[1] = new reversi.Direction(1,1);
        compass[2] = new reversi.Direction(1,0);
        compass[3] = new reversi.Direction(1,-1);
        compass[4] = new reversi.Direction(0,-1);
        compass[5] = new reversi.Direction(-1,-1);
        compass[6] = new reversi.Direction(-1,0);
        compass[7] = new reversi.Direction(-1,1);

        //loope durch alle 8 richtungen SE, E, NE, usw.
        for (int i = 0; i < compass.length; i++){
            //gehe einen schritt in die aktuelle richtung weiter
            int x_pos = x;
            int y_pos = y;
            x_pos += compass[i].getDx();
            y_pos += compass[i].getDy();
            int[][] temp_reverse = new int[width][height];

                //Falls EIN schritt in die gesuchte richtung noch spielfeld ist
                //und falls EIN schritt in die gesuchte richtung der gegner steht ...
                if (isOnBoard(x_pos, y_pos)
                && internal_board[x_pos][y_pos] == opposing_player){
                    //speicher die coordinate des schrittes vorübergehend
                        temp_reverse[x_pos][y_pos] = 1;
                    x_pos += compass[i].getDx();
                    y_pos += compass[i].getDy();

                    while (isOnBoard(x_pos,y_pos) && internal_board[x_pos][y_pos] != 0) {
                        if (internal_board[x_pos][y_pos] == opposing_player) {
                            temp_reverse[x_pos][y_pos] = 1;
                            x_pos += compass[i].getDx();
                            y_pos += compass[i].getDy();
                            break;
                        } else if (internal_board[x_pos][y_pos] == current_player){
                            hasAValidPath = true;

                            //falls steine auch gesetzt werden sollen
                            if (setTiles) {
                                // ... füge die temporär getrackten steine dem finalen tiles_to_turn hinzu ...
                                for (int print_x = 0; print_x < width; print_x++) {
                                    for (int print_y = 0; print_y < height; print_y++) {
                                        if (temp_reverse[print_x][print_y] == 1) {
                                            tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                                        }
                                    }
                                }
                            }


                            // und beende die suche in die aktuelle richtung
                            break;
                        }
                        break;
                    }


                }
        }

        return hasAValidPath;
    }

    private boolean isOnBoard(int x, int y){
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private void updateHelpPops(){
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++) {
                if (internal_board[x][y] == 99) {
                    internal_board[x][y] = 0;
                }
            }
        }

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++) {
                if (checkAllDirections(x,y,false)){
                    internal_board[x][y] = 99;
                }
            }
        }
    }

    /**
     * Dreht alle Tiles im tiles_to_turn array um und setzt das array wieder auf 0
     */
    private void reverseAllMarkedTiles(){
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                if (tiles_to_turn[x][y] == 1) {
                    reverseTile(x,y);
                }
            }
        }
        tiles_to_turn = new int[width][height];
    }

    /**
     * Setzt einen spezifizierten Spieler auf das gewünschte Tile
     * @param x
     * @param y
     * @param player Spieler der das angegebene Teil besetzen soll; 1 = white, 2 = black;
     */
    public void setTile(int x, int y, int player){
        internal_board[x][y] = player;
    }

    public void reverseTile(int x, int y) {

        if (internal_board[x][y] == 1) {
            setTile(x,y, 2);
        } else if (internal_board[x][y] == 2) {
            setTile(x,y, 1);
        } else return;
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


    /**
     * Aktualisiert den Punktestand indem es alle Steine zählt und die jeweiligen Spielerscores erneuert
     */
    private void updateScore(){
        //TODO: implementiere GUI-punkteanzeige
        white_tiles = 0;
        black_tiles = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (internal_board[x][y] == 1) {
                    white_tiles++;
                } else if (internal_board[x][y] == 2){
                    black_tiles++;
                }
            }
        }
        System.out.println("WHITE: " + white_tiles);
        System.out.println("BLACK: " + black_tiles);
    }


    //TODO: überspringe den zug falls current_player keine zugmöglichkeit hat
    private void alternatePlayers(){
        if (current_player == 1){
            current_player = 2;
        }
        else if (current_player == 2) {
            current_player = 1;
        }
        updateOpposingPlayer();
        printCurrentPlayer();
    }

    //TODO: implementiere "spieler am zug" GUI-anzeige
    private void printCurrentPlayer(){
        //DEBUG
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("3x3 = " + internal_board[3][3]);
        if (current_player == 1) System.out.println("It's WHITE's turn!");
        else if (current_player == 2) System.out.println("It's BLACK's turn!");
    }

    public void updateOpposingPlayer(){
        if (current_player == 1) {
            opposing_player = 2;
        } else if (current_player == 2) {
            opposing_player = 1;
        }
    }

    /**
     *
     * @param debug Legt fest ob Debugcode ausgeführt werden soll oder nicht
     */
    private void resetBoard(boolean debug) {
        internal_board = new int[width][height];
//      TODO: reset board soll nur das board zurücksetzen, die spieler zurücksetzen, und updaterender() enthalten. die 4 zeilen, welche das startFeldMuster erstellen, sollen in die initialize methode delegiert werden mithilfe von setTile().
        internal_board[(int) (width/(float)2-0.5)][(int) (height/(float)2-0.5)] = 1;  internal_board[(int) (width/(float)2+0.5)][(int) (height/(float)2-0.5)] = 2;
        internal_board[(int) (width/(float)2-0.5)][(int) (height/(float)2+0.5)] = 2;
        internal_board[(int) (width/(float)2+0.5)][(int) (height/(float)2+0.5)] = 1;

        /* Debug */
        if (debug) {
            internal_board[(int) (width/(float)2+1.5)][(int) (height/(float)2-0.5)] = 2;
            internal_board[(int) (width/(float)2-1.5)][(int) (height/(float)2-1.5)] = 2;
            internal_board[(int) (width/(float)2-2.5)][(int) (height/(float)2-2.5)] = 2;

        }
        current_player = 1;
        opposing_player = 2;

        updateHelpPops();
        updateRender();
    }

    private int updateUserDifficulty(){
        RadioButton selectedDifficulty = (RadioButton) difficultyToggleGroup.getSelectedToggle();
        switch(selectedDifficulty.getText()) {
            case "Easy":
                difficulty = 1;
                break;
            case "Medium":
                difficulty = 2;
                break;
            case "Hard":
                difficulty = 3;
                break;
            default:
                break;
        }
        return difficulty;
    }

    private void updateUserGridsize(){
        RadioButton selectedGridsize = (RadioButton) gridsizeToggleGroup.getSelectedToggle();
        switch (selectedGridsize.getText()){
            case "6x6":
                width = 6;
                height = 6;
                break;
            case "8x8":
                width = 8;
                height = 8;
                break;
            case "10x10":
                width = 10;
                height = 10;
                break;
            default:
                break;
        }
        System.out.println("Changed Gridsize to " + width + "x" + height);
        System.out.println("GAME WAS RESET");
        tile_size = (double) 600 / (double) width; //TODO: dynamic window size
        internal_board = new int[width][height];
        tiles_to_turn = new int[width][height];
        resetBoard(false);
    }


}
