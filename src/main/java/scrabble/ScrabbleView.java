package scrabble;
/**
 *  an example GUI view for an NxM game
 */

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import boardgame.ui.PositionAwareButton;
import boardgame.GameStorage;
import game.GameUI;
import boardgame.ui.BoardGameView;

/**
* This class provides the UI for playing Number Scrabble
* Extends BoardGameView
*/
public class ScrabbleView extends BoardGameView{
    private ScrabbleGame myGame;
    public ScrabbleView(int wide, int tall, GameUI gameFrame){
        // call the superclass constructor
        super(wide, tall, gameFrame);
        myGame = (ScrabbleGame)getGame();
        setGameNameLabel("Number Scrabble");
    }

    /**
     * Sets the game controller to be a new ScrabbleGame
     */
    @Override
    protected void instantiateController(){
        setGameController(new ScrabbleGame());
    }

    public void setGameController(ScrabbleGame controller){
        setGame(controller);
    }

    /**
     * Returns the string to be displayed when a winner occurs or tie game occurs
     * @return game over string
     */
    @Override
    protected String gameOverMessage(){
        String playAgainMsg = " Play Again?";
        if (myGame.getWinner() == 0){
            return "Tie Game!" + playAgainMsg;
        } else if (myGame.getWinner() == 1){
            return "Odds win!" + playAgainMsg;
        } else{
            return "Evens win!" + playAgainMsg;
        }
    }

    /**
     * If grid is populated, prompt user for file to save game to
     */
    @Override
    protected void saveGame(){
        if (myGame.isBoardEmpty()){
            JOptionPane.showMessageDialog(null, "Can't save empty board", "Save error", JOptionPane.ERROR_MESSAGE);
        } else{
            File scrabbleFolder = new File("./assets/scrabble");
            JFileChooser fc = new JFileChooser(scrabbleFolder);
            fc.setDialogTitle("Specify a file to save");
            int userSelect = fc.showSaveDialog(this);
            
            if (userSelect == JFileChooser.APPROVE_OPTION){
                try{
                    Path path = fc.getSelectedFile().toPath();
                    GameStorage.save(myGame, path);
                } catch (IOException ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Invalid Filename", JOptionPane.ERROR_MESSAGE);
                    saveGame();
                }
            }
        }
    }

    /**
     * Prompt user to select file to load game from, load game into controller if file is valid.
     * If file is not valid, prompt user to select a valid one.
     */
    @Override
    protected void loadGame(){
        File scrabbleFolder = new File("./assets/scrabble");
        JFileChooser fc = new JFileChooser(scrabbleFolder);
        Path scrabbleGamePath;
        
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            scrabbleGamePath = fc.getSelectedFile().toPath();
            try{
                GameStorage.load(myGame, scrabbleGamePath);
                updatePlayerTurnLabel();
                updateView();
            } catch (IOException ex){
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                                        "Invalid Scrabble File", JOptionPane.ERROR_MESSAGE);
                loadGame();
            } catch (RuntimeException rex){
                JOptionPane.showMessageDialog(null, rex.getMessage(),
                                        "Invalid Scrabble File", JOptionPane.ERROR_MESSAGE);
                loadGame();
            }
        }
    }

    /**
     * Prompt user to input a number from 0-9 in the grid button selected in e
     * @param e grid location clicked for input
     */
    @Override
    protected void enterInput(ActionEvent e){
        //get input from user
        String num = JOptionPane.showInputDialog(enterNumberMessage());
        
        //send input to game and update view if user enters value
        if (num != null){
            PositionAwareButton clicked = ((PositionAwareButton)(e.getSource()));
            try{
                myGame.takeTurn(clicked.getAcross(), clicked.getDown(),num);
                clicked.setText(myGame.getCell(clicked.getAcross(),clicked.getDown()));
                updatePlayerTurnLabel();
            } catch (RuntimeException ex){
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private String enterNumberMessage(){
        if (myGame.getCurrPlayer() == 1){
            return "Input an odd number";
        }
        return "Input an even number";
    }

    /**
     * Update player data according to result of game
     */
    @Override
    protected void incrementPlayerData(){
        if (myGame.getWinner() == 1){
            incrementPlayerWins(1);
            incrementPlayerLosses(2);
        } else if (myGame.getWinner() == 2){
            incrementPlayerWins(2);
            incrementPlayerLosses(1);            
        } else if (myGame.getWinner() == 0){
            incrementPlayerTie();
        }
    }

    private void updatePlayerTurnLabel(){
        setPlayerTurnLabel(myGame.getCurrPlayer());
    }

}
