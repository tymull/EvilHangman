package hangman;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args) throws IOException {
        File dictFilePath = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int remainingGuesses = Integer.parseInt(args[2]);
        Scanner scan = new Scanner(System.in);
        EvilHangmanGame game = new EvilHangmanGame();
        if (wordLength >= 2 && remainingGuesses >= 1) {
            try {
                game.startGame(dictFilePath, wordLength);
                game.setRemainingGuesses(remainingGuesses);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            throw new IOException("Invalid args");
        }

        while (game.getRemainingGuesses() > 0) {
            System.out.println("You have " + game.getRemainingGuesses() + " guesses left");
            System.out.println("Used letters: " + game.getGuessedLetters().toString());
            System.out.println("Word: " + game.getCurrWordString());
            System.out.print("Enter guess: ");


            try {
                String guess = scan.next();
                if (guess.matches("[a-zA-Z]+")) {
                    char guessChar = Character.toLowerCase(guess.charAt(0));
                    Set<String> maxSet = game.makeGuess(guessChar);

                    if (game.getNumGuessed() == 0) { //then guessed incorrectly
                        System.out.println("Sorry, there are no " + guessChar + " ’s");
                        if (game.getRemainingGuesses() == 0) {
                            String finalWord = new String();
                            for (String word : maxSet) {
                                finalWord = word;
                                break;
                            }
                            System.out.println("Sorry, you lost! The word was: " + finalWord);
                        }
                    }
                    else { //then guessed correctly
                        System.out.println("Yes, there is " + game.getNumGuessed() + " " + guessChar);
                        String currentWord = game.getCurrWordString();
                        if (game.getNumDashes(currentWord) == 0) { //then they won
                            System.out.println("You win! You guessed the word: " + currentWord);
                            break; //break out of while loop
                        }
                    }

                }
                else
                    System.out.println("Invalid input!");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
