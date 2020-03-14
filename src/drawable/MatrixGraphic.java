/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawable;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Cristian
 */
public class MatrixGraphic {

    //private int map[][];

    private final int WATER = 0;
    private final int GROUND = 1;
    private final int TOWER = 2;
    private final int ATTACK = 3;
    private final int SHIP = 4;
    private final int FAIL = 5;

    private final int WIDTH = 15;
    private final int HEIGHT = 15;
    private int posX = 0;
    private int posY = 0;

    public MatrixGraphic() {
        //this.map = matrixMap;
    }

    public void drawMap(Graphics g,int [][] mapDraw) {
        posX = 0;
        posY = 0;
    
        for (int i = 0; i < mapDraw.length; i++) {
            for (int j = 0; j < mapDraw[0].length; j++) {
                
                switch (mapDraw[i][j]) {
                    
                    case WATER:
                        g.setColor(Color.BLUE);
                        g.fillRect(posX, posY, HEIGHT, WIDTH);
                        break;
                    case GROUND:
                        g.setColor(Color.ORANGE);
                        g.fillRect(posX, posY, HEIGHT, WIDTH);
                        break;
                    case TOWER:
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect(posX, posY, HEIGHT, WIDTH);
                        break;
                    case ATTACK:
                        g.setColor(Color.RED);
                        g.fillRect(posX, posY, HEIGHT, WIDTH);
                        break;
                    case SHIP:
                        g.setColor(Color.BLACK);
                        g.fillRect(posX, posY, HEIGHT, WIDTH);
                        break;
                    case FAIL:
                    g.setColor(Color.YELLOW);
                    g.fillRect(posX, posY, HEIGHT, WIDTH);
                    break;
                }
                g.setColor(Color.BLACK);
                g.drawRect(posX, posY, HEIGHT, WIDTH);
                posX += HEIGHT;
            }
            
            posX = 0;
            posY += WIDTH;
        }
    }
    
}
