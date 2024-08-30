import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
//delete line below later
import java.util.*;
//delete line above later
import java.io.*;

public class WordleLogic{
  
  //Toggle DEBUG MODE On/Off
  public static final boolean DEBUG_MODE = true;  
  //Toggle WARM_UP On/Off
  public static final boolean WARM_UP = false;
  
  
  private static final String FILENAME = "englishWords5.txt";
  //Number of words in the words.txt file
  private static final int WORDS_IN_FILE = 5758; // Review BJP 6.1 for  
  
  //Use for generating random numbers!
  private static final Random rand = new Random();
  
    
	public static final int MAX_ATTEMPTS = 6; //max number of attempts
	public static final int WORD_LENGTH = 5; //WORD_LENTGH-letter word 
	                                // as is 5 like wordle, could be changed
	
	private static final char EMPTY_CHAR = WordleView.EMPTY_CHAR;  //use to delete char
	
 
  //************       Color Values       ************
  
  //Green (right letter in the right place)
  private static final Color CORRECT_COLOR = new Color(53, 209, 42); 
  //Yellow (right letter in the wrong place)
  private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52); 
  //Dark Gray (letter doesn't exist in the word)
  private static final Color WRONG_COLOR = Color.DARK_GRAY; 
  //Light Gray (default keyboard key color, letter hasn't been checked yet)
  private static final Color NOT_CHECKED_COLOR = new Color(160, 163, 168); 
  
  private static final Color DEFAULT_BGCOLOR = Color.BLACK;

  //***************************************************
  
  //************      Class variables     ************

  //Add them as necessary (I have some but less than 5)
private static String[] wordBankArr = new String[WORDS_IN_FILE];
private static int currentRow = 0;
private static int currentCol = 0;
private static char[] inputArr = new char[5];
private static String input = "";
private static char[] secretWordArr = new char[5];

  //***************************************************

  
  
  //************      Class methods     ************

  // There are 6 already defined below, with 5 of them to be completed.
  // Add class helper methods as necessary. Our solution has 12 of them total.
  
  
  // Complete for 3.1.1
  public static void warmUp() {
  	WordleView.setCellLetter(5,4,'c');
  	WordleView.setCellColor(5,4,WRONG_PLACE_COLOR);
  	WordleView.getKeyboardColor('c');
  	WordleView.setKeyboardColor('c',WRONG_PLACE_COLOR);
  	
  	WordleView.setCellLetter(0,0,'c');
  	WordleView.setCellColor(0,0,CORRECT_COLOR);
  	WordleView.getKeyboardColor('c');
  	WordleView.setKeyboardColor('c',CORRECT_COLOR);
  	
  	WordleView.setCellLetter(1,2,'o');
  	WordleView.setCellColor(1,2,NOT_CHECKED_COLOR);
  	WordleView.getKeyboardColor('o');
  	WordleView.setKeyboardColor('o',NOT_CHECKED_COLOR);
  	
  	WordleView.setCellLetter(3,3,'s');
  	WordleView.setCellColor(3,3,WRONG_COLOR);
  	WordleView.getKeyboardColor('s');
  	WordleView.setKeyboardColor('s',WRONG_COLOR);

  }
  
  
  
  //This function gets called ONCE when the game is very first launched
  //before the player has the opportunity to do anything.
  //
  //Returns the chosen mystery word the user needs to guess
  public static String init() throws FileNotFoundException {
  	  File wordsFile = new File(FILENAME);
  	  Scanner scan = new Scanner(wordsFile);
  	  for (int i=0;i<wordBankArr.length;i++){
  	  	  	  wordBankArr[i] = scan.next();
  	  }
  	  String secretWord = new String(wordBankArr[rand.nextInt(WORDS_IN_FILE)]);
  	
  	  for (int a = 0; a < secretWordArr.length; a++){
  	  	  	secretWordArr[a] = Character.toUpperCase(secretWord.charAt(a));
  	  }
  	return secretWord.toUpperCase();
  }
  
  
  
  //This function gets called everytime the user inputs 'Backspace'
  //pressing the physical or virtual keyboard.
  // call on Backspace input
  public static void deleteLetter(){
  	if (DEBUG_MODE) {
  		System.out.println("in deleteLetter()");
  	}
  	if(currentCol <=5 && currentCol > 0){
  		currentCol--;
  		WordleView.setCellLetter(currentRow,currentCol,EMPTY_CHAR);
  		inputArr[currentCol] = EMPTY_CHAR;
  	}
 
  }
  
  //helper function:
  
  public static void addInputtedLettersToCell(char[] secretWordArr, char[] inputArr){
  	Color color;
  	
  	for(int i=0;i<secretWordArr.length;i++){
		if(secretWordArr[i] == inputArr[i]){
			color = CORRECT_COLOR;
		}
		else if(secretWordArr[i] != inputArr[i] && IsInputtedCharInSecretWord_ARR(secretWordArr, i) == true){
			color = WRONG_PLACE_COLOR;
		}else{
			color = WRONG_COLOR;
		}
		WordleView.setCellColor(currentRow,i,color);
		prioritizeKeyboardColors(color, inputArr[i]);
	 }
	
	 //checks if it's a duplicate below:
	 
		int count = 0;
		for(int x=secretWordArr.length-1; x>0; x--){
			if(secretWordArr[x] == inputArr[x]){
				color = CORRECT_COLOR;
			}
			else if(secretWordArr[x] != inputArr[x] && IsInputtedCharInSecretWord_ARR(secretWordArr, x) == true){
				color = WRONG_PLACE_COLOR;
			}
			else{
				color = WRONG_COLOR;
			}

			if(letterAppears(inputArr, inputArr[x]) > letterAppears(secretWordArr, inputArr[x])){
				int target = letterAppears(inputArr, inputArr[x]) - letterAppears(secretWordArr, inputArr[x]);
					if(color == WRONG_PLACE_COLOR && LetterAppearsMoreThanOnce(inputArr[x], inputArr)== true){
						if(target - count > 0){
								WordleView.setCellColor(currentRow,x,WRONG_COLOR);
								prioritizeKeyboardColors(color, inputArr[x]);
								count++;
						}
						else{
							if(count == 1){
								WordleView.setCellColor(currentRow,x,color);
								prioritizeKeyboardColors(color, inputArr[x]);
								count = 0;
							}
							WordleView.setCellColor(currentRow,x,color);
							prioritizeKeyboardColors(color, inputArr[x]);
						}
					}
					else{
						WordleView.setCellColor(currentRow,x,color);
						prioritizeKeyboardColors(color, inputArr[x]);
					}
			}
			else{
				WordleView.setCellColor(currentRow,x,color);
				prioritizeKeyboardColors(color, inputArr[x]);
			}
		}
	}
				
   
  
 //helper function: converts a char array to string;  
  public static String arrToString(char[] wordArr){
  	  String input = "";
  	  for(int i=0; i < wordArr.length; i++){
  	  	  input+= wordArr[i];
  	  }
  	  //System.out.println(input);
  	  return input.toLowerCase();
  }
  
  
  //helper function: takes a char array and returns a boolean; 
  //it returns true if the letter at the entered index (i) is anywhere in the entered array 
  //and false if it's not.
  public static boolean IsInputtedCharInSecretWord_ARR(char[] wordArr, int i){
  	  for (int b=0; b<wordArr.length; b++){
  	  	  if (wordArr[b]== inputArr[i]){
  	  	  	  return true;
  	  	  }
  	  }
  	  return false;
  }
  
	
 //another helper function: takes two char arrays and returns true 
 //if each letter in one array is the same as each letter in a second array
 //and returns false if that's not the case
   public static boolean wasSecretWordGuessed(char[] wordArr1, char[] wordArr2){
 	 for(int z = 0; z < wordArr1.length; z++){
 	 	 if(wordArr2[z] != wordArr1[z]){
 	 	 	 return false;
 	 	 }
 	 }
 	 return true;
   }
   
   //another helper function: sees if the inputted string is in the wordbank array
   //and returns the boolean true if it is and false if it's not
   public static boolean isInputAWord(String input, String[] wordBank){
   	   for(int i=0; i < wordBank.length; i++){
   	   	   if(input.equals(wordBank[i])){
   	   	   		   return true;
   	   	   	}
   	   }
   	   return false;
   }
   
   //another helper function: checks if there is an empty char anywhere in the inputted array
   //b/c if there is there won't be five letters; returns false if there is 
   //and true if after going through entire array there isnt
   
   public static boolean isInputFiveLetters(char[] inputArr){
   	   System.out.println("Checking this function:" + Arrays.toString(inputArr));
   	   input = "";
   	   for(int b=0; b < inputArr.length; b++){
   	   	   input += inputArr[b];
   	   	   //if(inputArr[b] == EMPTY_CHAR){
   	   	   	   //return false;
   	
   	   }
   	   if (input.length() < 5){
   	   	   return false;
   	   }
   	   return true;
   }
   
   //helper function: takes a char, which will be a letter,
   //then counts how many times that letter appears in the array;
   //if the count is more than 1 it returns true.
   
   public static boolean LetterAppearsMoreThanOnce(char letter, char[] secretWordArr){
   	   int timesLetterAppears = 0;
   	   
   	   for(int k=0; k<secretWordArr.length; k++){
   	   	   if(letter == secretWordArr[k]){
   	   	   	   timesLetterAppears++;
   	   	   }
   	   }
   	   
   	   if(timesLetterAppears > 1){
   	   	   return true;
   	   }
   	   else{
   	   	   return false;
   	   }
   	}
   
   	//helper function: takes a char, which will be a letter in this case, 
   	//and counts how many times the letter appears in the enter array; it then returns the count.
   	
   	public static int letterAppears(char[] array, char letter){
   		int count = 0;
   		for(int l=0; l<array.length; l++){
   			if(letter == array[l]){
   				count++;
   			}
   		}
   		return count;
   	}
  
  //made the prioritize keyboard color part a helper function to aviod repetition:
   public static void prioritizeKeyboardColors(Color color, char inputtedLetter){
   	   if(color.equals(CORRECT_COLOR)){
			WordleView.setKeyboardColor(inputtedLetter,color);
		}
		else if(color.equals(WRONG_PLACE_COLOR)){
			if(WordleView.getKeyboardColor(inputtedLetter).equals(CORRECT_COLOR)){
				WordleView.setKeyboardColor(inputtedLetter,CORRECT_COLOR);
			}
			else{
				WordleView.setKeyboardColor(inputtedLetter,color);
			}
		}
		else{
			WordleView.setKeyboardColor(inputtedLetter,color);
		}
	}
   
	
  
  //This function gets called everytime the player inputs 'Enter'
  //pressing the physical or virtual keyboard. 
  public static void checkLetters() {
  	if (DEBUG_MODE) {
  		System.out.println("in checkLetters()");
  	}
  	
  	if(isInputFiveLetters(inputArr) == false || isInputAWord(arrToString(inputArr), wordBankArr) == false){
  		WordleView.wiggleRow(currentRow);
  	}
  	else if(currentRow + 1 < MAX_ATTEMPTS && wasSecretWordGuessed(secretWordArr,inputArr) == false){
  		addInputtedLettersToCell(secretWordArr, inputArr);
  		currentRow++;
  		currentCol = 0;
  	}
  	else if(currentRow + 1 >= MAX_ATTEMPTS){ 
  		if (wasSecretWordGuessed(secretWordArr,inputArr) == false){
  			addInputtedLettersToCell(secretWordArr, inputArr);
  			WordleView.gameOver(false);
  		}
  		else{
  			addInputtedLettersToCell(secretWordArr, inputArr);
  			WordleView.gameOver(true);
  		}
  	}
  	else{
  		addInputtedLettersToCell(secretWordArr, inputArr);
  		WordleView.gameOver(true);
  	}
  }
  	
  	
  	
 
  
  
  
  //This function gets called everytime the player types a valid letter
  //on the keyboard or clicks one of the letter keys on the 
  //graphical keyboard interface.  
  //The key pressed is passed in as a char uppercase for letter
  public static void inputLetter(char key){
  	//if (key == 'w' || key == 'W'){
  		//WordleView.wiggleRow(3);
  	//}
  	//Some placeholder debugging code...
  	if (currentCol <= 4 && currentCol >= 0){
  		WordleView.setCellLetter(currentRow,currentCol,key);
  		inputArr[currentCol] = key;	
  		
  		currentCol++;
  	}
 
  	
  	
  	
  	System.out.println("Letter pressed!: " + key);
  	

  	
  	if (WARM_UP) {
  		
  		System.out.println("A row should wiggle");
  		
  		
  		
  		
  		
  	} 
  
  	
  	
  	
  	
  }  
  
  
  
  //Initializes and launches the game logic and its GUI window
	public static void main(String[] args) throws FileNotFoundException {              
		String secret = null;
		
		if (!WARM_UP) {
			//Calls to intialize the game logic and pick the secret word
			secret = WordleLogic.init();
		}
		
		
		
		//Creates the game window
		WordleView.create(secret);
		
		if (WARM_UP) {
			warmUp();
		} 
	}       
}