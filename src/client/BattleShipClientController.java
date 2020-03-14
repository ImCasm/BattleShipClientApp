package client;

import gui.panelWindow;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JOptionPane;
import server.SocketController;

/**
 *
 * @author Cristian
 */
public class BattleShipClientController extends SocketController implements Runnable {

    private final int WATER = 0;

    private final int GROUND = 1;
    private final int TOWER = 2;
    private final int ATTACK = 3;
    private final int SHIP = 4;
    private final int FAIL = 5;
    private panelWindow panelMyMap;
    private panelWindow panelEnemyMap;

    private final String ATTACK_CODE = "5000";
    private final String ATTACK_RES_CODE = "6000";
    private boolean myTurn;
    private int numShipsCreated = 0;
    private int numTowersCreated = 0;
    private int attackCounter = 27; //10 torres y el resto de casillas de barco

    private int[][] myMap;
    private int[][] enemyMap;

    public BattleShipClientController(String newHostname, int newPort) throws IOException {
        super(newHostname, newPort);
        myTurn = false;
    }

    public BattleShipClientController(Socket newSocket) throws IOException {
        super(newSocket);
    }

    public boolean isReadyToStart() {
        return numShipsCreated == 5;
    }

    public boolean createTower(int row, int column) {
        if (numTowersCreated < 10 && myMap[row][column] == GROUND) {
            myMap[row][column] = TOWER;
            numTowersCreated++;
            return true;
        }

        return false;
    }

    public boolean createShip(int row, int column) {
        if (isReadyToStart() || row >= myMap.length - 1 || column >= myMap[0].length - 1) {
            return false;
        }

        if (myMap[row][column] == GROUND) {
            if (createTower(row, column)) {
                panelMyMap.repaint();
            }

        }

        int large;
        switch (numShipsCreated) {
            case 0:
                large = 5;
                break;
            case 1:
                large = 4;
                break;
            case 2:
            case 3:
                large = 3;
                break;
            case 4:
                large = 2;
                break;
            default:
                large = 0;
                break;
        }

        for (int i = column; i < column + large; i++) {
            if (myMap[row][i] != WATER) {
                return false;
            }
        }

        for (int i = column; i < column + large; i++) {
            myMap[row][i] = SHIP;
        }

        numShipsCreated++;
        panelMyMap.repaint();
        return true;
    }

    public String getCodeMessage(String message) {
        return message.split(";")[0];
    }

    public String getMessage(String message) {
        String parameters[] = message.split(";");

        return parameters[parameters.length - 1];
    }

    public String[] getParameters(String response) {
        return response.split(";");
    }

    public void register(String username) {
        writeText("REGISTER;" + username);
    }

    public void exitGame() {
        String code = "QUIT";
        writeText(code);
    }

    public boolean attack(int row, int column) {
        if (myTurn) {
            String code = "ATTACK;" + row + ";" + column;
            writeText(code);
            return true;
        }
        return false;
    }
    
    

    private boolean attackVerification(int row, int column) {
        if (myMap[row][column] == SHIP || myMap[row][column] == TOWER) {
            myMap[row][column] = ATTACK;
            attackCounter--;
            if (attackCounter == 0) { //perdi
                writeText("LOSER;Perdi");
                JOptionPane.showMessageDialog(panelMyMap, "Perdiste :(");
            }
            
            myTurn = false;

            return true;
        } else {
            myMap[row][column] = FAIL;
            myTurn = true;

            return false;
        }
    }

    public void filter(String response) {

        if (response == null) {
            return;
        }

        String code = getCodeMessage(response);
        String parameters[] = getParameters(response);
        String messageResponse = getMessage(response);

        switch (code) {

            case "5000":// Me atacan
                System.out.println("Me atacaron");

                if (numShipsCreated != 5 || numTowersCreated != 10) {
                    //no estoy listo
                    writeText("NOT_READY;No estoy listo aun");
                    break;
                }

                int row = Integer.parseInt(parameters[1]);
                int column = Integer.parseInt(parameters[2]);
                String repsonse;
                if (attackVerification(row, column)) {
                    repsonse = "ATTACK_RES;" + row + ";" + column + ";" + 1;
                    myTurn = false;
                } else {
                    repsonse = "ATTACK_RES;" + row + ";" + column + ";" + 0;
                    myTurn = true;
                }

                writeText(repsonse);
                break;

            case "6000":// Respuesta de mi ataque

                int rowR = Integer.parseInt(parameters[1]);
                int columnR = Integer.parseInt(parameters[2]);
                int valueR = Integer.parseInt(parameters[3]);

                if (valueR == 1) {//Si le di
                    enemyMap[rowR][columnR] = ATTACK;
                    myTurn = true;
                    System.out.println("Le di");
                } else {
                    enemyMap[rowR][columnR] = FAIL;
                    myTurn = false;
                    System.out.println("No le di");
                }

                break;

            case "1000":// Primer jugador registrado, se lleva el primer turno
                myTurn = true;
                break;

            case "2000":// Segundo jugador registrado, segundo turno
                myTurn = false;
                break;

            case "9999":// Mensaje cualquiera que debes ser mostrado por pantalla
                JOptionPane.showMessageDialog(panelMyMap, messageResponse);
                break;

            case "-1":// Termina la partida
                JOptionPane.showMessageDialog(panelMyMap, messageResponse);
                System.exit(0);
                break;
        }
    }

    @Override
    public void run() {
        while (true) {
            panelMyMap.repaint();
            panelEnemyMap.repaint();
            String msg = readText();
            if (msg != null) {
                filter(msg);
            }
        }
    }

    public int[][] getMyMap() {
        return myMap;
    }

    public int[][] getEnemyMap() {
        return enemyMap;
    }

    public void setMyMap(int[][] myMap) {
        this.myMap = myMap;
    }

    public void setEnemyMap(int[][] enemyMap) {
        this.enemyMap = enemyMap;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public panelWindow getPanel() {
        return panelMyMap;
    }

    public void setPanelMyMap(panelWindow panelMyMap) {
        this.panelMyMap = panelMyMap;
    }

    public void setPanelEnemyMap(panelWindow panelEnemyMap) {
        this.panelEnemyMap = panelEnemyMap;
    }
}
