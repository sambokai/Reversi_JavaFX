/*******************************************************************************
 * Copyright (c) 2016. Sam Bokai (sam.bokai@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Dieses Programm ist Freie Software: Sie können es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Dieses Programm wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package reversi;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {

    /** JavaFX Referenzen **/
    @FXML
    private ToggleGroup difficultyToggleGroup;
    @FXML
    private ToggleGroup gridsizeToggleGroup;
    @FXML
    private ToggleGroup showHelpToggleGroup;
    @FXML
    private Pane gamePane;
    @FXML
    private JFXDialog gridChangeDialog;
    @FXML
    private JFXDialog gameOverDialog;
    @FXML
    private Label gameoverDialogTitleLabel;
    @FXML
    private Label gameoverDialogBodyLabel;
    @FXML
    private StackPane root;
    @FXML
    private JFXButton acceptButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXButton newGameButton;
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private JFXToggleButton showHelpButton;
    @FXML
    private Label leftStatusBarLabel;
    @FXML
    private Label rightStatusBarLabel;
    @FXML
    private ImageView rightStatusBarImageView;
    @FXML
    private ImageView leftStatusBarImageView;

    private Group tileGroup = new Group();

    /**
     * 0 = empty, 1 = white, 2 = black
     */
    static int[][] internal_board;

    /**
     * 1= turn; 0 = leave as is
     */
    static private int[][] tiles_to_turn;

    /** Spieler der aktuell am Zug ist */
    int current_player = 1;
    /** Spieler der aktuell NICHT am Zug ist */
    int opposing_player = 2;
    /** aktuelle Schwierigkeitsgrad */
    int difficulty;
    /** Anzahl aller weißen Steine auf dem Spielbretts */
    int white_tiles;
    /** Anzahl aller schwarzen Steine auf dem Spielbretts */
    int black_tiles;
    /** Ob die Hilfemarkierungen angezeigt werden sollen */
    boolean showHelp;

    /** Größe eines einzelnen Feldes in pixeln */
    static double tile_size;

    /** Größe des Spielbretts (Breite)*/
    static int width;
    /** Größe des Spielbretts (Höhe)*/
    static int height;

    //Transitions
    final double NON_TRANSPARENT_STATUS = 0.9;
    final double TRANSPARENT_STATUS = 0.3;
    final int STATUSBAR_FADE_TIME = 250; //milliseconds

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showHelp = true;
        showHelpButton.setSelected(showHelp);
        gamePane.getChildren().setAll(tileGroup);
        updateUserGridsize();
        updateUserDifficulty();
        resetBoard(false);
        updateScore(true);

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
                cancelButton.setOnMouseClicked((e) -> {
                    oldValue.setSelected(true);
                    gridChangeDialog.close();
                });

                acceptButton.setOnMouseClicked((e) -> {
                    updateUserGridsize();
                    gridChangeDialog.close();
                    tabPane.getSelectionModel().select(0);
                });
            }
        });


        showHelpToggleGroup.selectedToggleProperty().addListener((ov, toggle, new_toggle) -> {
            updateUserShowHelpPop(new_toggle);

        });

    }

    /**
     * Versucht einen Zug auf dem, mit der Maus, angeklickten Feld zu spielen.
     * @param mouse Zu behandelndes MouseEvent.
     */
    private void mouseSetTile(MouseEvent mouse) {
        int x = mouseXtoTileX(mouse.getX()), y = mouseYtoTileY(mouse.getY());
        placePiece(x, y);

    }

    /**
     * Wandelt Fenster-Pixel-X-Koordinate in Spielfeld X-Koordinate um.
     * @param x Umzuwandelnde Fenster-Pixel-X-Koordinate
     * @return Umgewandelte X Position im Spielfeld
     */
    private int mouseXtoTileX(double x) {
        return (int) (x / tile_size);
    }

    /**
     * Wandelt Fenster-Pixel-Y-Koordinate in Spielfeld Y-Koordinate um.
     * @param y Umzuwandelnde Fenster-Pixel-Y-Koordinate
     * @return Umgewandelte Y Position im Spielfeld
     */
    private int mouseYtoTileY(double y) {
        return (int) (y / tile_size);
    }

    /**
     * Versucht einen Zug zu spielen. Falls angeklicktes Feld bereits belegt ist verlasse die Methode.
     * Falls Zug gespielt wurde und der nächste Spieler keinen möglichen Zug hat, überspringe den Spieler.
     * @param x X Koordinate des zu spielenden Zuges
     * @param y Y Koordinate des zu spielenden Zuges
     */
    private void placePiece(int x, int y) {
        updateOpposingPlayer();

        //if spot is full leave the placepiece method
        if (internal_board[x][y] != 0 && internal_board[x][y] != 99) {
            return;
        }

        //Check if any moves on X,Y are possible - If yes update tiles_to_turn accordingly, set the clicked tile, alternate current_player, and reverse all legally marked tiles
        if (checkAllDirections(x, y, true)) {
            //nur setTile() FALLS der zug legal war
            setTile(x, y, current_player);
            reverseAllMarkedTiles();
            alternatePlayers();
            // TODO: spieler überspringt falls er keinen move machen kann
            if (!anyMovePossible()) {
                System.out.println("Player " + current_player + " has NO LEGAL MOVE.");
                alternatePlayers();
                checkForGameOver();
            }
            updateScore(true);
        } else {
            System.out.println("Can't play this move.");
        }


        updateHelpPops();
        updateRender();
    }

    /**
     * Prüft auf alle möglichen Züge auf einer bestimmten Position (x,y). Setzt, wenn gewünscht, auch die entsprechenden Tiles
     * @param x X Koordinate des zu überprüfenden tiles
     * @param y Y Koordinate des zu überprüfenden tiles
     * @param setTiles legt fest ob nur geprüft werden soll, ob ein zug möglich ist ODER ob die steine im möglichen zug auch gesetzt werden sollen (tilesToTurn method wird belegt)
     * @return Ob mindestens ein Zug auf (x, y) möglich ist oder nicht
     */
    private boolean checkAllDirections(int x, int y, boolean setTiles) {
        //falls der geklickte tile bereits mit einem stein belegt ist, brich die methode ab
        if (internal_board[x][y] != 0 && internal_board[x][y] != 99) {
            return false;
        }
        //hat mindestens einen gültigen pfad
        boolean hasAValidPath = false;
        reversi.Direction[] compass = new reversi.Direction[8];
        compass[0] = new reversi.Direction(0, 1);
        compass[1] = new reversi.Direction(1, 1);
        compass[2] = new reversi.Direction(1, 0);
        compass[3] = new reversi.Direction(1, -1);
        compass[4] = new reversi.Direction(0, -1);
        compass[5] = new reversi.Direction(-1, -1);
        compass[6] = new reversi.Direction(-1, 0);
        compass[7] = new reversi.Direction(-1, 1);

        //loope durch alle 8 richtungen SE, E, NE, usw.
        for (int i = 0; i < compass.length; i++) {
            //gehe einen schritt in die aktuelle richtung weiter
            int x_pos = x;
            int y_pos = y;
            x_pos += compass[i].getDx();
            y_pos += compass[i].getDy();
            int[][] temp_reverse = new int[width][height];

            //Falls EIN schritt in die gesuchte richtung noch spielfeld ist
            //und falls EIN schritt in die gesuchte richtung der gegner steht ...
            if (isOnBoard(x_pos, y_pos)
                    && internal_board[x_pos][y_pos] == opposing_player) {
                //speicher die coordinate des schrittes vorübergehend
                temp_reverse[x_pos][y_pos] = 1;
                x_pos += compass[i].getDx();
                y_pos += compass[i].getDy();

                while (isOnBoard(x_pos, y_pos) && internal_board[x_pos][y_pos] == opposing_player) {
                    temp_reverse[x_pos][y_pos] = 1;
                    x_pos += compass[i].getDx();
                    y_pos += compass[i].getDy();
                }

                if (isOnBoard(x_pos, y_pos) && internal_board[x_pos][y_pos] == current_player) {
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
                }


            }
        }

        return hasAValidPath;
    }

    /**
     * Prüft ob der aktuelle Spieler mindestens einen möglichen Zug hat
     * @return Mindestens 1 Zug möglich
     */
    private boolean anyMovePossible() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (checkAllDirections(x, y, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Überprüft ob das Spiel zuende ist (Keine Züge mehr möglich)
     * zeigt den Gewinner + Endscore in einem Dialog an und erlaubt den Neustart des Spiels (newGameButton)
     * @return Gibt zurück ob das Spiel zuende ist oder nicht.
     */
    private boolean checkForGameOver() {
        if (!anyMovePossible()) {
            System.out.println("\nGAME OVER\n");
            updateScore(false);

            String winner = "";
            //announce winner
            if (white_tiles > black_tiles) {
                winner = "White";
            } else if (black_tiles > white_tiles) {
                winner = "Black";
            } else if (black_tiles == white_tiles) {
                winner = "TIE";
            }
            System.out.println("Winner: " + winner);

            if (black_tiles != white_tiles) {
                gameoverDialogTitleLabel.setText("Game Over - " + winner + " wins!");
            } else {
                gameoverDialogTitleLabel.setText("Game Over - TIE!");
            }
            gameoverDialogBodyLabel.setText("End Score\n\nWhite: " + white_tiles + "\nBlack: " + black_tiles);
            gameoverDialogBodyLabel.setAlignment(Pos.CENTER_LEFT);
            gameOverDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
            gameOverDialog.setOverlayClose(false);
            gameOverDialog.show(root);
            newGameButton.setOnMouseClicked((e) -> {
                resetBoard(false);
                gameOverDialog.close();
            });


            return true;
        } else {
            return false;
        }
    }

    /**
     * Überprüft ob die angegebene Spielfeldkoordinate sich auf dem Spielfeld befindet
     * @param x
     * @param y
     * @return (X, Y) ist auf dem Spielfeld oder nicht.
     */
    private boolean isOnBoard(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Setzt alle möglichen Spielfelder im internen Spielfeld-Array auf 99 (Code für HelpPop).
     * Der renderer zeigt bei jeder 99 eine Markierung an die dem Spieler anzeigt, dass auf dem Feld ein Zug möglich ist.
     */
    private void updateHelpPops() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (internal_board[x][y] == 99) {
                    internal_board[x][y] = 0;
                }
                if (showHelp && checkAllDirections(x, y, false)) {
                    internal_board[x][y] = 99;
                }
            }
        }
    }

    /**
     * Dreht alle Tiles im tiles_to_turn array um und setzt das array wieder auf 0
     */
    private void reverseAllMarkedTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles_to_turn[x][y] == 1) {
                    reverseTile(x, y);
                }
            }
        }
        tiles_to_turn = new int[width][height];
    }

    /**
     * Setzt einen spezifizierten Spieler auf das gewünschte Tile
     *
     * @param x
     * @param y
     * @param player Spieler der das angegebene Teil besetzen soll; 1 = white, 2 = black;
     */
    public void setTile(int x, int y, int player) {
        internal_board[x][y] = player;
    }

    /**
     * Drehe den Spielstein auf dem angegebenen Spielfeld um
     * @param x
     * @param y
     */
    public void reverseTile(int x, int y) {

        if (internal_board[x][y] == 1) {
            setTile(x, y, 2);
        } else if (internal_board[x][y] == 2) {
            setTile(x, y, 1);
        } else return;
    }

    /**
     * Aktualisiere die grafische Darstellung des internen Spielfeld-Arrays
     */
    public void updateRender() {
        tileGroup.getChildren().clear(); //WICHTIG! um memoryleaks zu verhindern.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Tile tile = new Tile(x, y, internal_board[x][y]);
                tileGroup.getChildren().add(tile);

            }
        }


    }

    /**
     * Aktualisiert den Punktestand indem es alle Steine zählt und die jeweiligen Spielerscores erneuert
     * @param printToConsole
     */
    private void updateScore(boolean printToConsole) {
        //TODO: implementiere GUI-punkteanzeige
        white_tiles = 0;
        black_tiles = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (internal_board[x][y] == 1) {
                    white_tiles++;
                } else if (internal_board[x][y] == 2) {
                    black_tiles++;
                }
            }
        }
        if (printToConsole) {
            System.out.println("WHITE: " + white_tiles);
            rightStatusBarLabel.setText(Integer.toString(white_tiles));
            System.out.println("BLACK: " + black_tiles);
            leftStatusBarLabel.setText(Integer.toString(black_tiles));
        }
    }

    //TODO: überspringe den zug falls current_player keine zugmöglichkeit hat
    /**
     * Wechsel den Spieler ab. (z.b. Weiß -> Schwarz)
     */
    private void alternatePlayers() {
        if (current_player == 1) {
            current_player = 2;
        } else if (current_player == 2) {
            current_player = 1;
        }
        updateOpposingPlayer();
        printCurrentPlayer();
    }

    /**
     * Aktualisiere die Transparenz der unteren Statusbar je nach aktuellem Spieler.
     */
    private void updateStatusBarTransparency() {
        //Transitions
        FadeTransition rightStatusImageFade = new FadeTransition(Duration.millis(STATUSBAR_FADE_TIME), rightStatusBarImageView);
        FadeTransition leftStatusImageFade = new FadeTransition(Duration.millis(STATUSBAR_FADE_TIME), leftStatusBarImageView);
        FadeTransition rightStatusLabelFade = new FadeTransition(Duration.millis(STATUSBAR_FADE_TIME), rightStatusBarLabel);
        FadeTransition leftStatusLabelFade = new FadeTransition(Duration.millis(STATUSBAR_FADE_TIME), leftStatusBarLabel);

        if (current_player == 1) {
            rightStatusImageFade.setFromValue(TRANSPARENT_STATUS);
            rightStatusImageFade.setToValue(NON_TRANSPARENT_STATUS);
            rightStatusImageFade.play();
            rightStatusLabelFade.setFromValue(TRANSPARENT_STATUS);
            rightStatusLabelFade.setToValue(NON_TRANSPARENT_STATUS);
            rightStatusLabelFade.play();


            leftStatusImageFade.setFromValue(NON_TRANSPARENT_STATUS);
            leftStatusImageFade.setToValue(TRANSPARENT_STATUS);
            leftStatusImageFade.play();
            leftStatusLabelFade.setFromValue(NON_TRANSPARENT_STATUS);
            leftStatusLabelFade.setToValue(TRANSPARENT_STATUS);
            leftStatusLabelFade.play();

        } else if (current_player == 2) {

            rightStatusImageFade.setFromValue(NON_TRANSPARENT_STATUS);
            rightStatusImageFade.setToValue(TRANSPARENT_STATUS);
            rightStatusImageFade.play();
            rightStatusLabelFade.setFromValue(NON_TRANSPARENT_STATUS);
            rightStatusLabelFade.setToValue(TRANSPARENT_STATUS);
            rightStatusLabelFade.play();

            leftStatusImageFade.setFromValue(TRANSPARENT_STATUS);
            leftStatusImageFade.setToValue(NON_TRANSPARENT_STATUS);
            leftStatusImageFade.play();
            leftStatusLabelFade.setFromValue(TRANSPARENT_STATUS);
            leftStatusLabelFade.setToValue(NON_TRANSPARENT_STATUS);
            leftStatusLabelFade.play();
        }
    }

    //TODO: implementiere "spieler am zug" GUI-anzeige
    /**
     * Gib den aktuellen Spieler in der Konsole aus.
     */
    private void printCurrentPlayer() {
        //DEBUG
        System.out.println("\n\n");
        if (current_player == 1) System.out.println("It's WHITE's turn!");
        else if (current_player == 2) System.out.println("It's BLACK's turn!");
        updateStatusBarTransparency();
    }

    /**
     * Aktualisiere den gegenerischen Spieler anhand des aktuellen Spielers.
     */
    public void updateOpposingPlayer() {
        if (current_player == 1) {
            opposing_player = 2;
        } else if (current_player == 2) {
            opposing_player = 1;
        }
    }

    /**
     * Setze das Spielfeld/Score/currentplayer zurück.
     * @param debug Legt fest ob Debugcode ausgeführt werden soll oder nicht
     */
    private void resetBoard(boolean debug) {
        internal_board = new int[width][height];
        tiles_to_turn = new int[width][height];

        internal_board[(int) (width / (float) 2 - 0.5)][(int) (height / (float) 2 - 0.5)] = 1;
        internal_board[(int) (width / (float) 2 + 0.5)][(int) (height / (float) 2 - 0.5)] = 2;
        internal_board[(int) (width / (float) 2 - 0.5)][(int) (height / (float) 2 + 0.5)] = 2;
        internal_board[(int) (width / (float) 2 + 0.5)][(int) (height / (float) 2 + 0.5)] = 1;

        /* Debug */
        if (debug) {
            internal_board[(int) (width / (float) 2 + 1.5)][(int) (height / (float) 2 - 0.5)] = 2;
            internal_board[(int) (width / (float) 2 - 1.5)][(int) (height / (float) 2 - 1.5)] = 2;
            internal_board[(int) (width / (float) 2 - 2.5)][(int) (height / (float) 2 - 2.5)] = 2;

        }
        current_player = 1;
        opposing_player = 2;

        //TODO: make beginning player random


        rightStatusBarLabel.setText(Integer.toString(0));
        leftStatusBarLabel.setText(Integer.toString(0));

        printCurrentPlayer();
        updateHelpPops();
        updateRender();
    }

    /**
     * Prüfe nach ob der Schalter für die HelpPops in den "Settings" an- oder ausgestellt ist.
     * @param new_toggle Der zu überprüfende Schalter
     */
    private void updateUserShowHelpPop(Toggle new_toggle) {
        if (new_toggle == null) {
            showHelp = false;
            updateHelpPops();
            updateRender();
        } else {
            showHelp = true;
            updateHelpPops();
            updateRender();

        }
    }

    /**
     * Prüfe nach welcher Schwierigkeitsgrad in den "Settings" eingestellt ist.
     * @return
     */
    private int updateUserDifficulty() {
        RadioButton selectedDifficulty = (RadioButton) difficultyToggleGroup.getSelectedToggle();
        switch (selectedDifficulty.getText()) {
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

    /**
     * Prüfe nach welche Spielfeldgräße in den "Settings" eingestellt ist.
     */
    private void updateUserGridsize() {
        RadioButton selectedGridsize = (RadioButton) gridsizeToggleGroup.getSelectedToggle();
        switch (selectedGridsize.getText()) {
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
        printCurrentPlayer();
        resetBoard(false);
    }

    /* GETTER für zukünftige AI-Gegner-Klasse */
    public static int[][] getInternal_board() {
        return internal_board;
    }

    public int getCurrent_player() {
        return current_player;
    }

    public int getOpposing_player() {
        return opposing_player;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getWhite_tiles() {
        return white_tiles;
    }

    public int getBlack_tiles() {
        return black_tiles;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

}
