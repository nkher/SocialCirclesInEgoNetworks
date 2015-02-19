package org.clustering.nkher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*  Class: Starter
 * 
 *  Description: This class contains the main function from where you can start or run the program. It sequentially calls the 
 *               functions from the different Classes in order to create adjacency matrices, sort ArrayLists etc. and finally
 *               writes the data into files in the Output folder of the project.
 */

public class Starter {
	
	public static void main(String[] args) throws IOException {
				
		// Creating objects for different classes 
		ReadFiles readFiles = new ReadFiles();
		UtilityFunctions utilityFunctions = new UtilityFunctions();
		Cluster cluster = new Cluster();
		Scanner scanner  =  new Scanner(System.in);
		
		// Taking input from the user
		System.out.println("Please enter any one Ego node network number from the following list and say enter");
		System.out.println("Your choice is ");
		System.out.println("1. 0\n2. 107\n3. 1684\n4. 1912\n5. 3437\n6. 348\n7. 3980\n8. 414\n9. 686\n10. 698");
		int egoNetworkNodeNumber = scanner.nextInt();
		scanner.close(); // Closing the scanner object
		
		final long startTime = System.currentTimeMillis(); // Declaring a long variable to note the Start time
		
		ArrayList<FacebookNode> allNodeSimilarityList = new ArrayList<FacebookNode>(); // Creating a ArrayList of FacebookNodes		
		
		int adjacencyMatrix[][] = readFiles.getAdjacencyNodeFeatureMatrix(egoNetworkNodeNumber); // Reading the '.feat' file and storing it in the double dimensional array		
		allNodeSimilarityList = utilityFunctions.calculateJacardCoefficient(adjacencyMatrix); // Calculating the Jaccard Coefficient for each node in the ego network
		utilityFunctions.writeFaceBookNodesToFile(allNodeSimilarityList); // Write all the FacebookNodes Numbers and respective top ten similarities to File
		System.out.println("Total number of nodes is Ego Network " + egoNetworkNodeNumber + " are " + allNodeSimilarityList.size());
		
		allNodeSimilarityList = readFiles.getEdges(allNodeSimilarityList, egoNetworkNodeNumber); // Get the connections of each node and store it in the edge list of a FacebookNode object
		allNodeSimilarityList = utilityFunctions.sortArrayListByEdgeCount(allNodeSimilarityList); // Sort the array list of facebooknodes by their Edge list count  
		
		ArrayList<Circle> allCircles = new ArrayList<Circle>();
		allCircles = cluster.clusterData(allNodeSimilarityList);
		utilityFunctions.writeCirclesToFile(allCircles);
	
		final long endTime = System.currentTimeMillis(); // Declaring a long variable to note the Start time
		System.out.println("Total execution time - " + (endTime - startTime) + " Milliseconds"); // Printing the total execution time
	}	
}
