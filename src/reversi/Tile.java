package reversi;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;



/**
 * Created by sambokai on 20.11.16.
 */
public class Tile extends ImageView {

    private ReversiPiece piece;
    private Image emptyTile_img = new Image(getClass().getResource("res/empty2.png").toExternalForm());
    private Image blackTile_img = new Image(getClass().getResource("res/black.png").toExternalForm());
    private Image whiteTile_img = new Image(getClass().getResource("res/white.png").toExternalForm());




    public Tile(int x, int y) {
        super();
        setImage(emptyTile_img);
        setFitHeight(GameBoardController.tile_size);
        System.out.println(GameBoardController.tile_size);
        setPreserveRatio(true);
        setSmooth(true);
        setCache(true);
        this.setX(x * GameBoardController.tile_size);
        this.setY(y * GameBoardController.tile_size);

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
