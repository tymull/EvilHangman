package hangman;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.SortedSet;

public class EvilHangmanGame implements IEvilHangmanGame {

    //JUST NEED TO FINISH WITH OUTPUTS TO USER I.E. IF THEY GUESSED CORRECTLY OR NOT
    //make key word, guess see if in map
    //build key word in StringBuilder
    //compare set sizes, then look at keys
    //use previous key to start with when building keys
    private HashSet<String> myDictionary = new HashSet<String>();//hash
    private int wordLength = 0;
    private int remainingGuesses = 0; //this will be set in main;
    private TreeSet<Character> guessedLetters = new TreeSet<Character>();
    private StringBuilder currentWord = new StringBuilder();

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        clear(); // have to clear vars from previous game every time this is called
        if (wordLength <= 1)
            throw new EmptyDictionaryException("Nothing in dictionary with less than 2 letters");
        this.wordLength = wordLength;
        for (int i = 0; i < wordLength; i++) {
            currentWord.append('-'); //creates all blank word of wordLength
        }
        try (Scanner scan = new Scanner(dictionary)) {
            //Scanner scan = new Scanner(dictionary);
            while (scan.hasNext()) {
                String check = scan.next();
                if ((check.length() == wordLength) && (check.matches("[a-zA-z]+"))) {
                    check.toLowerCase();
                    myDictionary.add(check); //said don't need to check for bogus characters in dictionary
                }
            }
            if (myDictionary.isEmpty())
                throw new EmptyDictionaryException("Dictionary is empty or has nothing with this many letters");
            //scan.close();
        }
        //catch (Exception e) {
        //    System.out.println("Error: " + e.getMessage());
        //    e.printStackTrace();
        //}
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if (Character.isLetter(guess)) { //if it is char
            guess = Character.toLowerCase(guess);
            //System.out.println(guess);
            //System.out.println(guessedLetters.toString());
            if (guessedLetters.contains(guess))
                throw new GuessAlreadyMadeException("You already used that letter");
            HashMap<String, HashSet<String>> partDict = new HashMap<String, HashSet<String>>();
            //partitioned dictionary ^
            //this will keep track of which set is largest in partition of dictionary
            guessedLetters.add(guess);
            HashSet<String> maxSet = new HashSet<String>();
            //remembers previous key in case find equal set size
            StringBuilder prevKey = new StringBuilder();
            for (String word:myDictionary)
            {
                //set to currentWord (so it keeps previous letters)
                StringBuilder key = new StringBuilder(currentWord.toString());
                for (int i = 0; i < wordLength; i++) //loops through each letter of each word to build key
                {
                    if (word.charAt(i) == guess)
                        key.setCharAt(i, guess); //will keep '-' if not same letter
                }
                if (partDict.containsKey(key.toString()))
                    partDict.get(key.toString()).add(word); //adds this word to the set of this partition key
                else //does not yet have this key
                {
                    partDict.put(key.toString(), new HashSet<String>()); //create new partition
                    partDict.get(key.toString()).add(word); //add word to new partition
                }
                //rinse and repeat for all words in myDictionary
            }

            for (HashMap.Entry<String, HashSet<String>> pair:partDict.entrySet()) //find best partition
            {
                HashSet<String> s = new HashSet<String>(pair.getValue()); //used to iterate through sets
                if (s.size() > maxSet.size())
                {
                    maxSet = new HashSet<String>(s); //this is new max Set
                    prevKey = new StringBuilder(pair.getKey()); //new key relating to current max Set
                }
                else if (s.size() == maxSet.size()) //if both same size, remember key
                {
                    //will compare this current key with prevKey to find precedence
                    StringBuilder currKey = new StringBuilder(pair.getKey());
                    //first must see if one has less letters
                    int prevCntr = 0;
                    int currCntr = 0;
                    for (int i = 0; i < wordLength; i++)
                    {
                        if (prevKey.charAt(i) == '-')
                            prevCntr++;
                        if (currKey.charAt(i) == '-')
                            currCntr++;
                    }
                    if (prevCntr < currCntr)
                    {
                        prevKey.replace(0, prevKey.length(), currKey.toString());
                        maxSet = new HashSet<String>(s); //then need to change max set and key
                    }
                    else if (prevCntr == currCntr)
                    {
                        //now need to find which has rightmost letter
                        for (int i = wordLength - 1; i >= 0; i--) //iterate backwards through words
                        {
                            if ((prevKey.charAt(i) != '-') && (currKey.charAt(i) == '-'))
                                break; //then break loop and do nothing since prev is preferred
                            else if ((prevKey.charAt(i) == '-') && (currKey.charAt(i) != '-'))
                            {
                                prevKey.replace(0, prevKey.length(), currKey.toString());
                                maxSet = new HashSet<String>(s); //then need to change max set and key
                            }
                            //if they both == '-' or both != '-', loop to next char to find precedence
                        }
                    }
                    //if prevCntr > currCntr, do nothing
                }
            }
            //now we have best partition and will use it to update myDictionary and determine if guessed well
            myDictionary = new HashSet<String>(maxSet);
            int currentDashes = 0;
            int newDashes = 0;
            for (int i = 0; i < wordLength; i++)
            {
                if (currentWord.charAt(i) == '-')
                    currentDashes++;
                if (prevKey.charAt(i) == '-')
                    newDashes++;
            }
            int numberOfGuessedLetter = currentDashes - newDashes;
            if (numberOfGuessedLetter > 0) {
                System.out.println("Yes, there is " + numberOfGuessedLetter + " " + guess + "\n");
                //prevKey always ends as winning key or pattern of partition
                currentWord.replace(0, currentWord.length(), prevKey.toString());
            }
            else {
                System.out.println("Sorry, there are no " + guess + "'s\n");
                remainingGuesses--;
            }
            //remainingGuesses--; //correctly made guess and can now decrement remainingGuesses
            //System.out.println(myDictionary.toString());
            return myDictionary; //returns current dictionary
        }
        else //if not char
        {
            System.out.println("Invalid input");
            return myDictionary; //returns current dictionary
        }
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public int getRemainingGuesses()
    {
        return remainingGuesses;
    }

    public void setRemainingGuesses(int remainingGuesses)
    {
        this.remainingGuesses = remainingGuesses;
    }

    public String printGuessedLetters()
    {
        StringBuilder output = new StringBuilder();
        for (Character c:guessedLetters)
        {
            output.append(c);
            output.append(" ");
        }
        return output.toString();
    }

    public void chooseWord()//to use at end after no guesses remain
    {
        Iterator<String> it = myDictionary.iterator();
        //will only be called when myDictionary has at least one element
        currentWord = new StringBuilder(it.next());
    }

    public StringBuilder getWord()
    {
        return currentWord; //Word pattern from map
    }

    public boolean fullWord()
    {
        for (int i = 0; i < wordLength; i++)
        {
            if (currentWord.charAt(i) == '-')
                return false; //if any dashes are left
        }
        return true; //if all dashes are filled
    }

    public void clear() //resets values in game to prepare for a new game
    {
        myDictionary.clear();
        wordLength = 0;
        remainingGuesses = 0;
        guessedLetters.clear();
        currentWord.setLength(0);
    }
}
