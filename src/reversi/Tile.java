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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;



/**
 * Created by sambokai on 20.11.16.
 */
public class Tile extends ImageView {

    private Image emptyTile_img = new Image(getClass().getResource("res/empty100.png").toExternalForm());
    private Image blackTile_img = new Image(getClass().getResource("res/black100_shadow.png").toExternalForm());
    private Image whiteTile_img = new Image(getClass().getResource("res/white100_shadow.png").toExternalForm());
    private Image emptyPop_img = new Image(getClass().getResource("res/help_pop100.png").toExternalForm());
    private int tile_type;

    /**
     *
     * @param x Position in board coordinates. (ex. 8x8)
     * @param y see parameter "x"
     * @param tile_type 0 = empty, 1 = white, 2 = black, 99 = help-pop
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
            case 99:
                setImage(emptyPop_img);
                this.tile_type = tile_type;
                break;
            default:
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
