/*
 * trieStruct.java
 * Course: Data Structure and Algorithm Analysis
 * Created on : Feb 21, 2018
 *
 * The Program has two classes: Node and Trie classes.
 * The Node class has an array of 26 references to Node along with boolean to indicate an end of word,
 * and outDegree to indicate there exists more character connection.
 * The Trie class implements a Trie data structure, which is a tree of degree 26 implemented as an array in Node class.
 * The Trie can efficiently store and check words like a spell-checker
 * It can insert, delete, list and count the words stored in its' data structure.
 * This is used for setting the maximum size of words in listAll function.
 *
 * In the main driver, the program will read a text file from System.in that contains text commands, and output to System.out.
 * The response are followed by the following rules:
 * A is followed by a string, where the string will be inserted to the Trie. Print messages depend on the success of insertion.
 * D is followed by a string, where the string will be deleted to the Trie. Print messages depend on the success of deletion.
 * S is followed by a string, where the string will be searched. Print messages depend on the success of search.
 * M prints number of words in the Trie
 * C is followed by a string, where the string will be searched to indicate the correct spelling of the string.
 * L prints the list of elements of the Trie in alphabetical order, one word per line
 * E terminates the text reading.
 *
 */

import java.io.*;
import java.util.Scanner;


public class trieStruct {
	//main driver
	public static void main(String[] args) throws IOException {

		Scanner scan = new Scanner(System.in);
		Trie newTest = new Trie();
		String line; //holds each command.

		while (scan.hasNext()){
			line = scan.next();

			//Insert a word to Trie.
			if (line.equals("A")){
				line = scan.next();
				if (newTest.insert(line))
					System.out.println("Word inserted"); //if the word does not exist
				else
					System.out.println("Word already exists"); //if the word already exists
			}

			//delete a word in Trie
			else if (line.equals("D")){
				line = scan.next();
				if (newTest.delete(line))
					System.out.println("Word deleted"); //if the word exists, successfully delete
				else
					System.out.println("Word not found"); //the word does not exist.
			}

			//search a word in Trie
			else if (line.equals("S")){
				line = scan.next();
				if (newTest.isPresent(line))
					System.out.println("Word found"); //if the word exists
				else
					System.out.println("Word not found"); //if the word does not exist
			}

			//print the total number of words
			else if (line.equals("M")){
				System.out.println("Membership is    " + newTest.membership());
			}

			//search a word in Trie
			else if (line.equals("C")){
				line = scan.next();
				if (newTest.isPresent(line))
					System.out.println("Spelling OK " + line); //if the word exists
				else
					System.out.println("Spelling mistake " + line); //if the word does not exist
			}

			//print all the words in the Trie in alphabetical order.
			else if (line.equals("L")){
				newTest.listAll();
			}

			//The end of the input file; terminate the program.
			else if (line.equals("E")){
				break;
			}
		}
		scan.close();
	}

	//the element part of the Trie data structure
	static class Node {
		//set to true if it is the end of a word; otherwise, false
		boolean terminal;
		//indicates the number of existing character in an array children
		int outDegree;
		//holds 26 reference, alphabets, to Node
		Node children[];

		//constructor
		public Node(){
			this.terminal = false;
			this.outDegree = 0;
			this.children = new Node[26];
		}
	}

	//Trie class to implement Trie data structure
	static class Trie {
		//The root of all other nodes
		private Node root;

		//constructor
		public Trie(){
			root = new Node();
		}

		//inserting a word in String to the Trie
		//@param:	s	a word to be inserted
		//@return:		false if is already present, true otherwise
		public boolean insert(String s){
			Node traverse = root;
			//go down the Trie structure as much as the string length
			for (int i = 0; i<s.length(); ++i){
				//add a new node for missing character
				if (traverse.children[s.charAt(i)-'a'] == null){
					Node newTrieNode = new Node();
					traverse.children[s.charAt(i)-'a'] = newTrieNode;
					traverse.outDegree++;
				}
				//if the character already exists in the Trie, just move on to the next child node.
				traverse = traverse.children[s.charAt(i)-'a']; //traverse down to the next child node.
			}
			//if it is already exists denoted by terminal == true, return false; otherwise return true at the end.
			if (traverse.terminal){
				return false;
			}
			else{
				traverse.terminal = true;
			}
			return traverse.terminal;
		}


		//Check if the word string already exists in the Trie
		//@param:	s	a word to be found
		//@return:		true if s is present, false otherwise
		public boolean isPresent(String s){
			Node traverse = root;
			for (int i = 0; i<s.length(); ++i){
				//no matching character of the string in the children array.
				if (traverse.children[s.charAt(i)-'a'] == null){
					return false;
				}
				//traverse down to the next child node.
				else{
					traverse = traverse.children[s.charAt(i)-'a'];
				}
			}
			return traverse.terminal;
		}

		//delete an existing word string in the Trie
		//@param:	s	a word to be deleted
		//@return:		false if s is not present, ture otherwise
		public boolean delete(String s){
			if (!isPresent(s)) //case: s is not present
				return false;
			else{
				delete(s, root);
				return true;
			}
		}

		//boolean datatype false indicates whether there are still characters to be deleted.
		//recursively delete characters until hit a character which is shared by other words (outDegree > 1)
		//or an end of other words (traverse.terminal == true)
		//otherwise true, and done with deletion.
		//@param:	s			a word to be deleted
		//@param:	traverse	Node traverse the Trie
		//@return:				false if there are still characters to be deleted, otherwise true
		private boolean delete(String s, Node traverse){
			//at the end of the string; base case
			if (s.isEmpty()){
				traverse.terminal = false;
				//Check if the deleted word is in between other words or not
				if (traverse.outDegree == 0){
					traverse = null;
					return false; //case: the deleted word end is a leaf node, possible adjustment is needed.
				}
				else{
					return true; //case: the deleted word has children nodes, so no more action is needed.
				}
			}

			//cutting down one character at a time from the front of string s to traverse down the Trie
			if (!delete(s.substring(1), traverse.children[s.charAt(0)-'a'])){

				//adjustment after the deletion.
				if (traverse.outDegree > 1){
					traverse.outDegree--;
					return true; //case: the character is shared by other word, no more action is needed.
				}
				else if (traverse.terminal == true){
					traverse.outDegree--;
					return true; //case: there exists another word on the way back to root, no more action is needed.
				}
				else{
					traverse = null;
					return false; 	//case: the character is still the only one used by the word in the sequence, possible further adjustment is needed.
				}
			}
			return true;
		}

		//calls the private recursive version of membership for the number of words
		//@return:		the number of words in data structure
		public int membership(){

			return membership(root);
		}

		//recursively traverse the Trie to get the counting of number of words
		//@param:	traverse	traverse through the Trie recursively
		//@return:				number of words in data structure
		private int membership(Node traverse){

			int numOfMember = 0; //number count for words
			//hit the end of the word, count one up
			if (traverse.terminal){
				numOfMember++;
			}
			//leaf node
			if (traverse.outDegree == 0){
				return numOfMember;
			}
			//searching any existing characters among 26 alphabet characters,
			//if so, recursively go down the Trie
			for (int i = 0; i < 26; ++i){
				if (traverse.children[i] != null){
					numOfMember += membership(traverse.children[i]);
				}
			}
			return numOfMember;
		}

		//list all members (words) of the Trie in alphabetical order
		//print one word per line.
		public void listAll(){
			String wordTrav = ""; //string to hold characters that traverse in recursive function
			listAll(root, wordTrav);
		}

		//private recursive function for listAll
		//recursively traverse the Trie
		//@param:	traverse	Node traversing Trie
		//@param:	wordTrav	temporarily holds word characters while traversing
		private void listAll(Node traverse, String wordTrav){
			String word = wordTrav;
			//base case: arrived at the end of word; print it.
			if (traverse.terminal){
				System.out.println(word);
			}
			//leaf node: no more to search down the trie
			if (traverse.outDegree == 0){
				return;
			}
			//searching any existing characters among 26 alphabet characters
			for (int i = 0; i < 26; ++i){
				if (traverse.children[i] != null){
					word = wordTrav + (char)(i+'a');
					listAll(traverse.children[i], word);
				}
			}
		}

	}

}
