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
    private Image blackTile_img = new Image(getClass().getResource("res/black2.png").toExternalForm());
    private Image whiteTile_img = new Image(getClass().getResource("res/white2.png").toExternalForm());
    private int tile_type;

    /**
     *
     * @param x Position in board coordinates. (ex. 8x8)
     * @param y see parameter "x"
     * @param tile_type 0 = empty, 1 = white, 2 = black
     */
    public Tile(int x, int y, int tile_type) {
        super();
        switch(tile_type) {
            case 0:
                setImage(emptyTile_img);
                this.tile_type = tile_type;
                break;
            case 1:
                setImage(whiteTile_img);
                this.tile_type = tile_type;
                break;
            case 2:
                setImage(blackTile_img);
                this.tile_type = tile_type;
                break;
        }

        setFitHeight(GameBoardController.tile_size);
        setPreserveRatio(true);
        setSmooth(true);
        setCache(true);
        this.setX(x * GameBoardController.tile_size);
        this.setY(y * GameBoardController.tile_size);

    }



    public int getTile_type() {
        return tile_type;
    }



    public void setTileType(int type) {
        this.tile_type = type;
    }
}
