package reversi;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;



/**
 * Created by sambokai on 20.11.16.
 */
public class Tile extends Rectangle {

    private ReversiPiece piece;



    public Tile(int x, int y ) {


        setWidth(GameBoardController.tile_size);
        setHeight(GameBoardController.tile_size);

        relocate(x * GameBoardController.tile_size, y * GameBoardController.tile_size);
        setFill(Color.web("#4CAF50"));
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public ReversiPiece getPiece() {
        return this.piece;
    }

    public void setPiece(ReversiPiece piece) {
        this.piece = piece;
    }
}
