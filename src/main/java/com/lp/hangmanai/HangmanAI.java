package com.lp.hangmanai;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

/*
This AI works by using the information it gains to narrow down a list of
potential solutions. Then, it counts all occurences of each letter from A-Z in
each word, and then guesses the letter that appears most often in the list. 

A noticable flaw exists, in that it counts all occurences of each letter in each
word, instead of simply counting up by one for each word with a certain letter,
but implementing that would be a pain, and this works fine.

Never underestimate the laziness of your average programmer.
*/

public class HangmanAI {
    public static void main(String[] args) {
        ArrayList<String> words = new ArrayList();
        
        try {
            File file = new File("src/main/java/com/lp/hangmanai/english_words.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                words.add(reader.nextLine().toUpperCase());
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Words file not found.\n");
            e.printStackTrace();
        }
        
        Scanner input = new Scanner(System.in);
        
        while (true) {
            System.out.println("Please enter a word. The AI can guess wrong 6 times before losing.\n");
            
            String guessWord = input.nextLine().strip().split(" ")[0].toUpperCase();
            
            int wordLength = guessWord.length();
            
            if (wordLength == 0) {
                System.out.println("ERROR: Word length is zero.\n");
                continue;
            }
            
            ArrayList<String> viableWords = new ArrayList();
            for (String word : words) if (word.length() == wordLength) viableWords.add(word);
            
            // Letter counts for all words of the same length as the guess word.
            // The purpose of this declaration is explained farther down.
            int backupLetterCounts[] = new int[26];
            for (String word : viableWords) {
                for (char c : word.toCharArray()) {
                    int index = (int)c - 65; // Converts ASCII value of each letter to a number in the interval [0, 26)
                    backupLetterCounts[index]++;
                }
            }
            
            ArrayList<Boolean> covered = new ArrayList();
            for (int i = 0; i < wordLength; i++) covered.add(true);
            
            String guessedLetters = "";
            String wrongLetters = "";
            
            boolean aiWin = false;
            
            for (int wrongGuesses = wrongGuesses = 6; wrongGuesses > 0;) {
                // Finding letter frequency among viable words.
                int letterCounts[] = new int[26];
                for (String word : viableWords) {
                    for (char c : word.toCharArray()) {
                        int index = (int)c - 65;
                        letterCounts[index]++;
                    }
                }
                
                // This is where the backup letter counts come in.
                // If the user inputs a word which is not contained in english_words.txt, eventually the list of viable words will be exhausted.
                // In this case, the AI needs something to fall back on, so it uses the backup letter counts as a new data set for the rest of the round.
                if (viableWords.isEmpty()) {
                    letterCounts = backupLetterCounts.clone();
                }
                
                // Erasing already guessed letters
                for (int i = 0; i < guessedLetters.length(); i++) {
                    int index = (int)guessedLetters.charAt(i) - 65;
                    letterCounts[index] = -1;
                }
                
                // Finding most common unguessed letter among viable words
                int maxIndex = 0;
                for (int i = 1; i < 26; i++) {
                    if (letterCounts[i] > letterCounts[maxIndex]) maxIndex = i;
                }
                
                // Guessing letter
                char guessedLetter = (char)(maxIndex + 65); // Converts the integer representation back to an ASCII character
                guessedLetters += guessedLetter;
                
                System.out.println("The AI guesses " + String.valueOf(guessedLetter) + ".");
                
                int countInWord = 0;
                for (int i = 0; i < wordLength; i++) {
                    if (guessWord.charAt(i) == guessedLetter) {
                        countInWord++;
                        covered.set(i, false);
                    }
                }
                
                System.out.println("There are " + countInWord + " " + guessedLetter + "s in the word.");
                if (countInWord == 0) {
                    wrongGuesses--;
                    wrongLetters += guessedLetter;
                    System.out.println("Incorrect guess! The AI has " + wrongGuesses + " more incorrect guesses.");
                }
                System.out.println();
                
                for (int i = 0; i < wordLength; i++) {
                    if (covered.get(i)) System.out.print("_ ");
                    else System.out.print(guessWord.charAt(i) + " ");
                }
                System.out.println("\n");
                
                // Checking win
                aiWin = true;
                for (int i = 0; i < wordLength; i++) {
                    if (covered.get(i)) {
                        aiWin = false;
                        break;
                    }
                }
                if (aiWin) break;
                
                // Narrowing down viable words
                for (int i = 0; i < viableWords.size(); i++) {
                    boolean viable = true;
                    for (int j = 0; j < wordLength; j++) {
                        if ((!wrongLetters.isEmpty() && viableWords.get(i).matches("[" + wrongLetters + "]"))
                                || (guessWord.charAt(j) == guessedLetter && viableWords.get(i).charAt(j) != guessedLetter)
                                || (guessWord.charAt(j) != guessedLetter && viableWords.get(i).charAt(j) == guessedLetter)) {
                            viable = false;
                            break;
                        }
                    }
                    
                    if (!viable) {
                        viableWords.remove(i);
                        i--;
                    }
                }
                
                System.out.print("Press enter to continue: ");
                input.nextLine();
                System.out.println();
            }
            
            System.out.println("The AI " + (aiWin ? "wins" : "loses") + "!\n");
            
            System.out.println("Would you like to play again? (y/n)\n");
            
            if (!input.nextLine().toLowerCase().startsWith("y")) break;
            
            System.out.println();
        }
    }
}
