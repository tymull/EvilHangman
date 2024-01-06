package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class EvilHangmanGame implements IEvilHangmanGame {


    private HashSet<String> myDictionary = new HashSet<>();
    private TreeSet<Character> guessedLetters = new TreeSet<>();
    private int wordLength = 0;
    private int remainingGuesses = 0;
    private StringBuilder currWord = new StringBuilder();
    private int numGuessed = 0;

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        clear();
        Scanner scan = new Scanner(dictionary);
        setWordLength(wordLength);

        while (scan.hasNext()) {
            String word = scan.next();
            if (word.matches("[a-zA-Z]+") && word.length() == wordLength) {
                word = word.toLowerCase();
                myDictionary.add(word);
            }
        }
        if (myDictionary.isEmpty())
            throw new EmptyDictionaryException("Either dictionary is empty or there are no words with this length");
        initCurrWord(wordLength);

    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        //Will set guesses and check guess is valid in main
        guess = Character.toLowerCase(guess);
        setNumGuessed(0);
        if (guessedLetters.contains(guess))
            throw new GuessAlreadyMadeException("You already used that letter");
        HashMap<String, HashSet<String>> partDict = new HashMap<>(); //partitioned dictionary
        HashSet<String> maxSet = new HashSet<>();
        String origWord = getCurrWordString();
        String prevKey = getCurrWordString();

        for (String word : myDictionary) { //for each word in the dictionary, partition it based on this guess
            for (int i = 0; i < wordLength; i++) { //make key
                if (word.charAt(i) == guess) {
                    currWord.setCharAt(i,guess);
                }
            }
            if (partDict.containsKey(currWord.toString())){
                partDict.get(currWord.toString()).add(word);
                setCurrWord(origWord);
            }
            else {//partDict doesn't have this key yet
                partDict.put(currWord.toString(), new HashSet<String>());
                partDict.get(currWord.toString()).add(word);
                setCurrWord(origWord);
            }
        }

        Set<String> keys = partDict.keySet();
        for (String key : keys) {
            if (maxSet.size() < partDict.get(key).size()) { //if maxSet is bigger or equal it will take alphabetical precedence
                maxSet = partDict.get(key);
                prevKey = key;
            }
            else if (maxSet.size() == partDict.get(key).size()) { //then have to find precedence
                boolean prevKeyHasLetter = false;
                boolean keyHasLetter = false;
                for (int i = 0; i < wordLength; i++) {
                    if (prevKey.charAt(i) == guess)
                        prevKeyHasLetter = true;
                    if (key.charAt(i) == guess)
                        keyHasLetter = true;
                }
                if (prevKeyHasLetter && !keyHasLetter) { //then key set is preferred
                    maxSet = partDict.get(key);
                    prevKey = key;
                }
                else if (prevKeyHasLetter == keyHasLetter) { //either they both have or don't have
                    if (getNumLetters(key) < getNumLetters(prevKey)) {
                        maxSet = partDict.get(key);
                        prevKey = key;
                    }
                    else if (getNumLetters(key) == getNumLetters(prevKey)) { //if prevKey > key then just keep
                        for (int i = wordLength - 1; i >= 0; i--) { //iterate backwards to find rightmost letter
                            if (prevKey.charAt(i) == guess && key.charAt(i) != guess)
                                break; //break and do nothing
                            else if (prevKey.charAt(i) != guess && key.charAt(i) == guess) {
                                maxSet = partDict.get(key);
                                prevKey = key;
                            }
                        }
                    }
                }
            }
        }

        numGuessed = getNumDashes(origWord) - getNumDashes(prevKey);
        setCurrWord(prevKey);

        if (numGuessed > 0) { //then made a correct guess
            guessedLetters.add(guess);
        }
        else {
            guessedLetters.add(guess);
            remainingGuesses--;
        }

        myDictionary = maxSet;
        return maxSet;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public int getNumDashes(String word) {
        int numDashes = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == '-')
                numDashes++;
        }
        return numDashes;
    }

    public int getNumLetters(String word) {
        int numLetters = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) != '-')
                numLetters++;
        }
        return numLetters;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public int getRemainingGuesses() {
        return remainingGuesses;
    }

    public void setRemainingGuesses(int remainingGuesses) {
        this.remainingGuesses = remainingGuesses;
    }

    public void initCurrWord(int wordLength) {
        for (int i = 0; i < wordLength; i++) {
            currWord.append('-');
        }
    }

    public StringBuilder getCurrWord() {
        return currWord;
    }

    public String getCurrWordString() {
        return currWord.toString();
    }

    public void setCurrWord(String word) {
        currWord.setLength(0);
        currWord.append(word);
    }

    public int getNumGuessed() {
        return numGuessed;
    }

    public void setNumGuessed(int numGuessed) {
        this.numGuessed = numGuessed;
    }

    public void clear() {
        myDictionary.clear();
        guessedLetters.clear();
        wordLength = 0;
        remainingGuesses = 0;
        currWord.setLength(0);
    }
}
