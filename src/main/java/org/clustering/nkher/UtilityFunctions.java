package org.clustering.nkher;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/*  Class: UtilityFunctions
 * 
 *  Description: This class stores functions like calculating the Jaccard coefficient similarity values between 
 *  nodes in the Ego Network, writing the similarity hash map values of each facebook node (in an Ego Network) to a file,
 *  getting a complete facebook Node by the node number.
 *  
 *  Functions:
 *  1. calculateJacardCoefficient(int adjacencyMatrix[][]). Returns an ArrayList of FacebookNodes.
 *  2. writeFaceBookNodesToFile(ArrayList<FacebookNode> allFBNodes). Returns nothing.
 *  3. writeCirclesToFile(ArrayList<Circle> allCircles). Returns nothing.
 *  4. sortArrayListByEdgeCount(ArrayList<FacebookNode> allNodeSimilarityList). Returns ArrayList of FacebookNodes.
 *  5. getFBNode(ArrayList<FacebookNode> allNodeSimilarityList, String nodeNumber). Returns a FacebookNode type of object.
 */

public class UtilityFunctions {
	
	/*  Function Name: calculateJacardCoefficient()
	 *  Parameters:  1. Adjacency Feature Matrix (DataType: int[][]) 
	 *  			 
	 *  Description: Takes the double dimensional integer array representing the feature matrix. Calculates the JC similarity 
	 *               between each pair of nodes in the ego network (except from the ego node itself) and stores it in the 'similarityMap'
	 *               HashMap for each FaceBookNode object. It then sorts each Nodes 'similarityMap' by the values(JC value) and then stores 
	 *               the top ten in the FaceBookNode's 'topTenMap' LinkedHashMap.  
	 *               
	 *  Return: Returns an ArrayList of FacebookNodes. 			 
	 */
	
	public ArrayList<FacebookNode> calculateJacardCoefficient(int adjacencyMatrix[][]){
		
		ArrayList<FacebookNode> allNodeSimilarityList = new ArrayList<FacebookNode>();
		int noOfRows = adjacencyMatrix.length; // This determines the number of nodes in the array
		double jaccardCoeff=0; // Local variable to store the J-C similarity value
		int topN = 0; // This determines how many top nodes to consider for similarity matching
		
		// Setting the topN value depending on the number of nodes in the Ego Network
		if(noOfRows <= 100)
			topN = 8;
		else if(noOfRows > 100 && noOfRows <= 300)
			topN = 15;
		else if(noOfRows > 300 && noOfRows < 500)
			topN = 25;		
		else
			topN = 25;
		
		for(int k=0;k<noOfRows;k++){ // Iterating over all the nodes starting from 0th Node (For loop 1)
			
			FacebookNode newNode = new FacebookNode(); // Creating a new Node
			newNode.nodeNumber = "N"+adjacencyMatrix[k][0]; // Giving the new node a name
			
			for(int i=0;i<noOfRows;i++){ // Iterating over all the nodes once again (For loop 2)
				
				/* Calculating the Jaccard - Coefficient - Start */
				
				HashMap<String, Integer> tempHash = new HashMap<String, Integer>(); // Hash Map to help calculate the J-C similarity value 
				
				if(i != k) // Condition to avoid calculating a nodes similarity value with itself 
				{
					for(int j=0;j<adjacencyMatrix[i].length;j++){ // Iterating over the columns of the node with which the J-C value is to be calculated
						
							if(j==0){ // Initially adding zero for all values in the hash map
								tempHash.put("00", 0);
								tempHash.put("10", 0);
								tempHash.put("01", 0);
								tempHash.put("11", 0);
							}
							else{ // Logic to icrement the keys of the hashmap based on the features in both the nodes
								if(adjacencyMatrix[k][j] == 0 && adjacencyMatrix[i][j] == 0)
									tempHash.put("00", tempHash.get("00") + 1);
								else if(adjacencyMatrix[k][j] == 1 && adjacencyMatrix[i][j] == 0)
									tempHash.put("10", tempHash.get("10") + 1);
								else if(adjacencyMatrix[k][j] == 0 && adjacencyMatrix[i][j] == 1)
									tempHash.put("01", tempHash.get("01") + 1);
								else
									tempHash.put("11", tempHash.get("11") + 1);
							}
					}
				}
				else
					continue;
				
				jaccardCoeff = (double)tempHash.get("11")/(tempHash.get("11") + tempHash.get("10") + tempHash.get("01"));
				jaccardCoeff = (double)Math.round(jaccardCoeff * 10000)/10000;
				
				/* Adding the node number and its similarity value to the 
				 * similarity map of the node for which the FacebookNode object was made
				 */
				
				newNode.similarityMap.put("N"+adjacencyMatrix[i][0], jaccardCoeff);	
				
				/* Calculating the Jaccard - Coefficient - End */
				
			} // End of for loop 2
			
			/* Now sorting the similarity Map of each FacebookNode based on the J-C value in descending order.
			 * Then re-entering topN nodes from the similarityMap to the topN Linked HashMap 
			 */
			/* Sorting Start */
			Set<Entry<String, Double>> set = newNode.similarityMap.entrySet();
			ArrayList<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
			
			Collections.sort(list, new Comparator<Map.Entry<String, Double>>() 
			{
				public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
				{
					return (o2.getValue()).compareTo(o1.getValue());
				}
			});
			/* Sorting End */
			
			/* Adding to the Linked HashMap - Start */
			for (Map.Entry<String, Double> entry : list){
				newNode.topNMap.put(entry.getKey(), entry.getValue());
				if(newNode.topNMap.size() == topN)
					break;
			}
			/* Adding to the Linked HashMap - End */
			
			/* Adding the newNode which filled similarityMap and topN Map to the list of Facebook Nodes*/
			allNodeSimilarityList.add(newNode); 
			
		} // End of for loop 1
		
		return allNodeSimilarityList;
	}
	
	/*  Function Name: writeFaceBookNodesToFile()
	 *  Parameters:  1. List of FacebookNodes (DataType: ArrayList<FacebookNode>) 
	 *  			 
	 *  Description: Iterates through the List of FacebookNodes in the ArrayList and writes the topTenMap LinkedHashMap keys and 
	 *               value to the file.  
	 *               
	 *  Return: void. 			 
	 */
	
	public void writeFaceBookNodesToFile(ArrayList<FacebookNode> allFBNodes) throws IOException {
		
		File file = new File("Output/FaceBookNode_List.txt"); // Taking the filename as a string parameter into a file type object
		FileWriter fw = new FileWriter(file); // Passing the file object to a FileWriter instance
		BufferedWriter bw = new BufferedWriter(fw); // Passing the FileWriter object to a BufferedWriter instance
		
		/* Iterating over the list of FacebookNodes and printing them onto a file */	
		for(FacebookNode fbNode: allFBNodes){
			bw.write("Node number : " + fbNode.nodeNumber + " ");
			bw.newLine();
			for(Entry<String, Double> entry : fbNode.topNMap.entrySet())
				bw.write(entry.getKey() + " --> " + entry.getValue() + ", ");
			bw.newLine();
			bw.newLine();
			bw.write("**------------------------------------------------------------**");
			bw.newLine();
		}
		bw.close();
	}
	
	/*  Function Name: writeCirclesToFile()
	 *  Parameters:  1. List of Circles (DataType: ArrayList<Circle>) 
	 *  			 
	 *  Description: Iterates through the List of Circles in the ArrayList and writes the Circle Number
	 *               and the Node Numbers inside it to a file.  
	 *               
	 *  Return: void. 			 
	 */
	
	public void writeCirclesToFile(ArrayList<Circle> allCircles) throws IOException {
		
		System.out.println("Number of Circles : " + allCircles.size());
		File file = new File("Output/Circles.txt"); // Taking the filename as a string parameter into a file type object
		FileWriter fw = new FileWriter(file); // Passing the file object to a FileWriter instance
		BufferedWriter bw = new BufferedWriter(fw); // Passing the FileWriter object to a BufferedWriter instance
		int j;
		
		/* Iterating over the list of circles and printing them onto a file */
		for(Circle circle: allCircles){
				bw.write(circle.circleNumber + " ");
				bw.newLine();
				bw.write("Nodes in the circle are : ");
				for(j=0;j<circle.list.size();j++)
					bw.write(circle.list.get(j) + " ");
				bw.newLine();
				bw.newLine();
				bw.write("**------------------------------------------------------------**");
				bw.newLine();
		}
		bw.close();
	}
	
	/*  Function Name: getFBNode()
	 *  Parameters:  1. List of Facebook Nodes (DataType: ArrayList<FacebookNode>) 
	 *               2. Node Number (DataType: String)
	 *  			 
	 *  Description: Iterates through the List of Facebook Node in the ArrayList and searches for the FacebookNode object
	 *               with the same Node Number and returns the same.  
	 *               
	 *  Return: A FacebookNode Type object. 			 
	 */
	
	public FacebookNode getFBNode(ArrayList<FacebookNode> allNodeSimilarityList, String nodeNumber){
		FacebookNode fbNode = new FacebookNode(); 
		for(FacebookNode facebookNode : allNodeSimilarityList){
			if(facebookNode.nodeNumber.equalsIgnoreCase(nodeNumber)){
				fbNode.nodeNumber = facebookNode.nodeNumber;
				fbNode.similarityMap = facebookNode.similarityMap;
				fbNode.topNMap = facebookNode.topNMap;
				fbNode.edgeList = facebookNode.edgeList;
				break;
			}
		}
		return fbNode;
	}
	
	/*  Function Name: sortArrayListByEdgeCount()
	 *  Parameters:  1. List of Facebook Nodes (DataType: ArrayList<FacebookNode>) 
	 *  			 
	 *  Description: Iterates through the List of Facebook Node in the ArrayList and applied a in place bubble sort on the Facebook Nodes.
	 *  		     The sort criteria here is to sort the Node objects with descending value of their 'edgeList' size.
	 *               
	 *  Return: Returns an ArrayList of FacebookNodes. 				 
	 */
	
	public ArrayList<FacebookNode> sortArrayListByEdgeCount(ArrayList<FacebookNode> allNodeSimilarityList){
		
		int i, j;
		int listSize = allNodeSimilarityList.size();
		
		/* Applying a Bubble sort in Descending order */
		
		for(i=0;i<=listSize-1;i++){
			for(j=0;j<listSize-i-1;j++){
				
				/* Swapping the Facebook Nodes if the jth node's edgelist size is less than (j+1)st node's edgelist size */
				if(allNodeSimilarityList.get(j).edgeList.size() < allNodeSimilarityList.get(j+1).edgeList.size()){
					FacebookNode tempFacebookNode = new FacebookNode();
					tempFacebookNode = allNodeSimilarityList.get(j);
					allNodeSimilarityList.set(j, allNodeSimilarityList.get(j+1));
					allNodeSimilarityList.set(j+1, tempFacebookNode);
				}
			}
		}
		return allNodeSimilarityList;	
	}
}
