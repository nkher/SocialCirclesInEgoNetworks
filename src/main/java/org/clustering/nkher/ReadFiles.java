package org.clustering.nkher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Scanner;


/*  Class: ReadFiles
 * 
 *  Description: This class stores functions for reading feature and edges files and creating adjacency matrices for the same.
 *                It reads files from the Facebook_Data folder in the project. 
 *  Functions:
 *  
 *  1. getAdjacencyNodeFeatureMatrix(int egoNetworkNodeNumber). Returns a Double - D integer array.
 *  2. getEdges(ArrayList<FacebookNode> allNodeSimilarityList, int egoNetworkNodeNumber). Returns ArrayList of FacebookNodes.
 */

public class ReadFiles {
	
	/*  Function Name: getAdjacencyNodeFeatureMatrix()
	 *  Parameters: 1. Ego Network Node Number (DataType: Integer)
	 *  
	 *  Description: Reads the nodes in the ego network and also its features from
	 *  			 the ".feat" file and stores it in an double dimensional array
	 *  
	 *  Returns: Returns an Double-D Integer Array representing the feature Adjacency Matrix for the Ego Network
	 */
	
	public int[][] getAdjacencyNodeFeatureMatrix(int egoNetworkNodeNumber) throws IOException{
		
		try{
			
			String line = "";
			int lineCount = 0, rowCount = 0;
			String[] numbersArray = null;
			
			/* Code to read the file */ 
			File file = new File("Facebook_Data/"+egoNetworkNodeNumber+".feat");
		    FileReader fr = new FileReader(file);
		    LineNumberReader lnr = new LineNumberReader(fr); 
		    line = lnr.readLine();
		    numbersArray = line.split(" "); // To get the number of rows
		 
		    /* Code to count number of rows/nodes in the network */
		    
            while (lnr.readLine() != null)
            	rowCount++;
	        lnr.close();
		
			Scanner scan = new Scanner(new FileReader(file));
					
			int featMatrix[][] = new int[rowCount+1][numbersArray.length]; // Initializing the array that will store the nodes adjacency
			
			// Creating the Adjacency matrix
			while(scan.hasNextLine()){
				line = scan.nextLine();
				String[] numbers = line.split(" ");
				for(int i=0;i<numbers.length;i++)
					featMatrix[lineCount][i] = Integer.parseInt(numbers[i]);
				lineCount++;
			}
			scan.close();
			lnr.close();
			fr.close();
			
			return featMatrix;
		}
		catch(IOException e){
			System.out.println("There is no such file ! Please give the correct file name.");
			System.exit(0);
		}
		return null;	
	}
	
	/*  Function Name: getEdges()
	 *  Parameters:  1. List of FacebookNodes(DataType: ArrayList<FacebookNode>) 
	 *  			 2. Ego Network Number (DataType: Integer)
	 *  
	 *  Description: Reads the edges between nodes in the ego network from the ".edges" file and stores it in the ArrayList "edgeList"
	 *               of the FacebookNode object 
	 *               
	 *  Return: Returns an ArrayList of FacebookNodes, where each object of Type FacebookNode in the array list has its connections(edges)
	 *          stored in the 'edgeList' of the FacebookNode object 			 
	 */
	
	public ArrayList<FacebookNode> getEdges(ArrayList<FacebookNode> allNodeSimilarityList, int egoNetworkNodeNumber) throws FileNotFoundException{
		
		UtilityFunctions utilityFunctions = new UtilityFunctions();
		File file = new File("Facebook_Data/"+egoNetworkNodeNumber+".edges");
		String line = "";
		FacebookNode fbNode = null;
		Scanner scan = new Scanner(new FileReader(file));
		
		// Read the file line by line and store the connections in the edgeList of a FacebookNode
		while(scan.hasNextLine()){
			line = scan.nextLine();
			String[] numbers = line.split(" ");
			fbNode = utilityFunctions.getFBNode(allNodeSimilarityList, ("N"+numbers[0]));
			fbNode.edgeList.add("N"+numbers[1]);	
		}
		scan.close();
		return allNodeSimilarityList;
	}
}
