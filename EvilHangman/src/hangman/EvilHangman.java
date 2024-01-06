package hangman;

import java.io.File;
import java.util.Scanner;

public class EvilHangman {

    public static void main(String[] args) {
        EvilHangmanGame game = new EvilHangmanGame();
        Scanner scan = new Scanner(System.in);
        try {
            File dictionary = new File(args[0]);
            int wordLength = Integer.parseInt(args[1]);
            int remainingGuesses = Integer.parseInt(args[2]);
            game.startGame(dictionary, wordLength);
            game.setRemainingGuesses(remainingGuesses); //will keep track of remaining guesses within game

            while (remainingGuesses > 0) {
                try {
                    System.out.println("You have " + remainingGuesses + " left");
                    System.out.println("Used letters: " + game.printGuessedLetters());
                    System.out.println("Word: " + game.getWord());
                    System.out.print("Enter guess: ");

                    String input = scan.next();

                    char guess;
                    if (input.length() == 1) { //if it is a char
                        input = input.toLowerCase(); //DOES THIS cause problem if $?
                        guess = input.charAt(0);
                        game.makeGuess(guess);
                        remainingGuesses = game.getRemainingGuesses();//////////
                    }
                    else //if not char
                        System.out.println("Invalid input");
                    if (game.fullWord()) {
                        System.out.println("You Win!\nThe word was: " + game.getWord());
                        break;
                    }

                    //scan.close();
                    if (remainingGuesses == 0) {
                        game.chooseWord();
                        System.out.println("You lose!\nThe word was: " + game.getWord());
                    }
                }
                catch (GuessAlreadyMadeException e) {
                    System.out.println(e.getMessage());
                    //e.printStackTrace();
                    //return guessedLetters;
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                    //System.out.println("Usage: java Main dictionary wordLength guesses");
                    //e.printStackTrace();
                }
            }

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            //System.out.println("Usage: java Main dictionary wordLength guesses");
            //e.printStackTrace();
        }
        finally
        {
            scan.close();
        }
    }

}
