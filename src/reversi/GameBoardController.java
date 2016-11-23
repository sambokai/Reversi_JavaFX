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

    @FXML private Tab gameTab;
    @FXML private JFXTabPane tabPane;
    @FXML private ToggleGroup difficultyToggleGroup;
    @FXML private Pane gamePane;

    private Group tileGroup = new Group();

    /** 0 = empty, 1 = white, 2 = black */
    static int[][] internal_board;

    /** 1= turn; 0 = leave as is */
    static private int[][] tiles_to_turn;

    private int[][] surrounding = new int[3][3];

    int current_player = 1;
    int opposing_player = 2;
    int difficulty;
    int white_tiles;
    int black_tiles;


    static double tile_size;
    static int width;
    static int height;

    private static void updateUserGridsize(){
        width = 8;
        height = 8;
        tile_size = (double) 600 / (double) width;
        internal_board = new int[width][height];
        tiles_to_turn = new int[width][height];
    }

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

        //Check if spot is full
        if (internal_board[x][y] != 0) {
            return -1;
        }

        //Check if any moves are possible - If yes update tiles_to_turn accordingly, set the clicked tile, alternate current_player, and reverse all legally marked tiles
        if (checkEastToWest(x,y) || checkWestToEast(x,y) || checkNorthToSouth(x,y) || checkSouthToNorth(x,y)
            || checkNorthWestToSouthEast(x,y) || checkSouthEastToNorthWest(x,y) || checkNorthEastToSouthWest(x,y) || checkSouthWestToNorthEast(x,y)){
            /* Check if move is valid and if valid add all affected tiles to the global tiles_to_turn array */
            checkEastToWest(x,y);
            checkWestToEast(x,y);
            checkNorthToSouth(x,y);
            checkSouthToNorth(x,y);
            checkNorthWestToSouthEast(x,y);
            checkSouthEastToNorthWest(x,y);
            checkNorthEastToSouthWest(x,y);
            checkSouthWestToNorthEast(x,y);

            //nur setTile() FALLS der zug legal war
            setTile(x,y,current_player);
            alternatePlayers();
            reverseAllMarkedTiles();
            updateScore();
        }

        return 0;
    }

    @SuppressWarnings("Duplicates")
    private boolean checkEastToWest(int x, int y) {
        //  Check EAST TO WEST

        // falls am linken rand geklickt wurde, beende die methode (da links vom click kein tile ist sondern das programm fenster endet)
        if (x == 0) return false;
        // if 1 tile LEFT to the cliked tile is owned by opponent, THEN continue:
        if (x < width && internal_board[x-1][y] == opposing_player) {

            //array for tracking potential reverse-candidates
            int[][] temp_reverse = new int[width][height];
            //temporarily track the, already checked, 1 tile LEFT to clicked tile
            temp_reverse[x-1][y] = 1;
            //go through all tiles from EAST to WEST beginning at 2 tiles left to the clicked tile, since 1 tile left was already checked
            for (int x_pos= x - 2; x_pos >= 0; x_pos--){

                //wenn auf einen gegnerischen Stein gestoßen wird
                // füge ihn dem temporären tracking array hinzu
                if (internal_board[x_pos][y] == opposing_player){
                    temp_reverse[x_pos][y] = 1;
                }

                //wenn auf einen eigenen stein gestoßen wird ...
                else if (internal_board[x_pos][y] == current_player) {

                    // ... füge die temporär getrackten steine dem finalen tiles_to_turn hinzu ...
                    for (int print_x = 0; print_x < width; print_x++) {
                        for (int print_y = 0; print_y < height; print_y++) {
                            if (temp_reverse[print_x][print_y] == 1) {
                                tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                            }
                        }
                    }

                    // und beende die EAST to WEST suche
                    return true;
                }

                else { //falls auf einen leeren tile gestoßen wird
                    return false;
                }
            }
        }
        return false; //falls ein tile links vom geklickten tile NICHT leer ist
    }

    @SuppressWarnings("Duplicates")
    private boolean checkWestToEast(int x, int y) {
        // FOR DETAILED COMMENTS SEE checkEastToWest()
        // falls am rechten rand geklickt wurde, beende die methode (da rechts vom click kein tile ist sondern das programm fenster endet)
        if (x == width-1) return false;
        if (x < width && internal_board[x+1][y] == opposing_player) {
            int[][] temp_reverse = new int[width][height];
            temp_reverse[x+1][y] = 1;
            for (int x_pos= x + 2; x_pos < width; x_pos++){
                if (internal_board[x_pos][y] == opposing_player){
                    temp_reverse[x_pos][y] = 1;
                }
                else if (internal_board[x_pos][y] == current_player) {
                    for (int print_x = 0; print_x < width; print_x++) {
                        for (int print_y = 0; print_y < height; print_y++) {
                            if (temp_reverse[print_x][print_y] == 1) {
                                tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                            }
                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    private boolean checkNorthToSouth(int x, int y) {
        //  Check NORTH to SOUTH

        // falls am unteren rand geklickt wurde, beende die methode (da unterhalb vom click kein tile ist sondern das programmfenster endet)
        if (y == height - 1) return false;

        // if 1 tile BELOW the cliked tile is owned by opponent, THEN continue:
        if (y < height && internal_board[x][y+1] == opposing_player) {

            //array for tracking potential reverse-candidates
            int[][] temp_reverse = new int[width][height];
            //temporarily track the, already checked, 1 tile LEFT to clicked tile
            temp_reverse[x][y+1] = 1;
            //go through all tiles from NORTH to SOUTH beginning at 2 tiles below the clicked tile, since 1 tile below was already checked
            for (int y_pos = y + 2; y_pos < height; y_pos++){

                //wenn auf einen gegnerischen Stein gestoßen wird
                // füge ihn dem temporären tracking array hinzu
                if (internal_board[x][y_pos] == opposing_player){
                    temp_reverse[x][y_pos] = 1;
                }

                //wenn auf einen eigenen stein gestoßen wird ...
                else if (internal_board[x][y_pos] == current_player) {

                    // ... füge die temporär getrackten steine dem finalen tiles_to_turn hinzu ...
                    for (int print_x = 0; print_x < width; print_x++) {
                        for (int print_y = 0; print_y < height; print_y++) {
                            if (temp_reverse[print_x][print_y] == 1) {
                                tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                            }
                        }
                    }

                    // und beende die NORTH to SOUTH suche
                    return true;
                }

                else { //falls auf einen leeren tile gestoßen wird
                    return false;
                }
            }
        }
        return false; //falls ein tile links vom geklickten tile NICHT leer ist
    }

    @SuppressWarnings("Duplicates")
    private boolean checkSouthToNorth(int x, int y) {
        // FOR DETAILED COMMENTS SEE checkSouthToNorth()
        if (y == 0) return false;
        if (x < width && internal_board[x][y-1] == opposing_player) {
            int[][] temp_reverse = new int[width][height];
            temp_reverse[x][y-1] = 1;
            for (int y_pos= y - 2; y_pos >= 0; y_pos--){
                if (internal_board[x][y_pos] == opposing_player){
                    temp_reverse[x][y_pos] = 1;
                }
                else if (internal_board[x][y_pos] == current_player) {
                    for (int print_x = 0; print_x < width; print_x++) {
                        for (int print_y = 0; print_y < height; print_y++) {
                            if (temp_reverse[print_x][print_y] == 1) {
                                tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                            }
                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    private boolean checkNorthWestToSouthEast(int x, int y) {
        //  Check NORTHEAST to SOUTHWEST

        // falls am unteren rand ODER am rechten rand geklickt wurde, beende die methode (da rechts vom/ unter dem click kein tile ist sondern das programm fenster endet)
        if (x == width-1 || y == height-1) return false; //possible bug concerning -1

        // if the tile that is 1 below and 1 to the right of the clicked tile is owned by opponent, THEN continue:
        if (x < width && y < height && internal_board[x+1][y+1] == opposing_player) {
            //array for tracking potential reverse-candidates
            int[][] temp_reverse = new int[width][height];
            //temporarily track the, already checked, tile 1 below and 1 to the right to clicked tile
            temp_reverse[x+1][y+1] = 1;
            //go through all tiles from NORTHEAST to SOUTHEAST beginning at 2 tiles below/right of the clicked tile, since 1 tile left was already checked
            for (int x_pos= x + 2, y_pos = y +2; x_pos < width && y_pos < height; x_pos++, y_pos++){
                    //wenn auf einen gegnerischen Stein gestoßen wird
                    // füge ihn dem temporären tracking array hinzu
                    if (internal_board[x_pos][y_pos] == opposing_player){
                        temp_reverse[x_pos][y_pos] = 1;
                    }

                    //wenn auf einen eigenen stein gestoßen wird ...
                    else if (internal_board[x_pos][y_pos] == current_player) {

                        // ... füge die temporär getrackten steine dem finalen tiles_to_turn hinzu ...
                        for (int print_x = 0; print_x < width; print_x++) {
                            for (int print_y = 0; print_y < height; print_y++) {
                                if (temp_reverse[print_x][print_y] == 1) {
                                    tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                                }
                            }
                        }

                        // und beende diese suche
                        return true;
                    }

                    else { //falls auf einen leeren tile gestoßen wird
                        return false;
                    }
            }
        }
        return false; //falls ein tile links vom geklickten tile NICHT leer ist
    }

    @SuppressWarnings("Duplicates")
    private boolean checkSouthEastToNorthWest(int x, int y) {
        // FOR DETAILED COMMENTS see checkNortheastToSouthwest()
        if (x == 0 || y == 0) return false;
        if (x < width && y < height && internal_board[x-1][y-1] == opposing_player) {
            int[][] temp_reverse = new int[width][height];
            temp_reverse[x-1][y-1] = 1;
            for (int x_pos= x - 2, y_pos = y - 2; x_pos >= 0 && y_pos >= 0; x_pos--, y_pos--){
                if (internal_board[x_pos][y_pos] == opposing_player){
                    temp_reverse[x_pos][y_pos] = 1;
                }
                else if (internal_board[x_pos][y_pos] == current_player) {
                    for (int print_x = 0; print_x < width; print_x++) {
                        for (int print_y = 0; print_y < height; print_y++) {
                            if (temp_reverse[print_x][print_y] == 1) {
                                tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                            }
                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    private boolean checkNorthEastToSouthWest(int x, int y) {
        // FOR DETAILED COMMENTS see checkNortheastToSouthwest()
        if (x == width-1 || y == 0) return false;
        if (x < width && y < height && internal_board[x+1][y-1] == opposing_player) {
            int[][] temp_reverse = new int[width][height];
            temp_reverse[x+1][y-1] = 1;
            for (int x_pos= x + 2, y_pos = y - 2; x_pos < width && y_pos >= 0; x_pos++, y_pos--){
                if (internal_board[x_pos][y_pos] == opposing_player){
                    temp_reverse[x_pos][y_pos] = 1;
                }
                else if (internal_board[x_pos][y_pos] == current_player) {
                    for (int print_x = 0; print_x < width; print_x++) {
                        for (int print_y = 0; print_y < height; print_y++) {
                            if (temp_reverse[print_x][print_y] == 1) {
                                tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                            }
                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    private boolean checkSouthWestToNorthEast(int x, int y) {
        // FOR DETAILED COMMENTS see checkNortheastToSouthwest()
        if (x == 0 || y == height-1) return false;
        if (x < width && y < height && internal_board[x-1][y+1] == opposing_player) {
            int[][] temp_reverse = new int[width][height];
            temp_reverse[x-1][y+1] = 1;
            for (int x_pos= x - 2, y_pos = y + 2; x_pos >= 0 && y_pos < height; x_pos--, y_pos++){
                if (internal_board[x_pos][y_pos] == opposing_player){
                    temp_reverse[x_pos][y_pos] = 1;
                }
                else if (internal_board[x_pos][y_pos] == current_player) {
                    for (int print_x = 0; print_x < width; print_x++) {
                        for (int print_y = 0; print_y < height; print_y++) {
                            if (temp_reverse[print_x][print_y] == 1) {
                                tiles_to_turn[print_x][print_y] = temp_reverse[print_x][print_y];
                            }
                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
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
        internal_board[(int) (width/(float)2-0.5)][(int) (height/(float)2-0.5)] = 1;
        internal_board[(int) (width/(float)2+0.5)][(int) (height/(float)2-0.5)] = 2;
        internal_board[(int) (width/(float)2-0.5)][(int) (height/(float)2+0.5)] = 2;
        internal_board[(int) (width/(float)2+0.5)][(int) (height/(float)2+0.5)] = 1;

        /* Debug */
        if (debug) {
            internal_board[(int) (width/(float)2+1.5)][(int) (height/(float)2-0.5)] = 2;
            internal_board[(int) (width/(float)2-1.5)][(int) (height/(float)2-1.5)] = 2;
            internal_board[(int) (width/(float)2-2.5)][(int) (height/(float)2-2.5)] = 2;

        }

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
}
