package org.clustering.nkher;
import java.util.ArrayList;

/*  Class: Cluster
 *  Description: This class contains functions to create clusters. It also makes use of some functions of other classes.
 *               There are wo main functions which include the clustering logic which are mentioned below. 
 *  
 *  Data Members:
 *  1. UtilityFunctions object 
 *  
 *  Functions:
 *  1. clusterData(ArrayList<FacebookNode> allNodeSimilarityList). Returns an ArrayList of Circles
 *  2. calculateConnectionStrength(Circle circle, FacebookNode currentNode, ArrayList<FacebookNode> allNodeSimilarityList). Returns the connection strength.
 */

public class Cluster {
	
	UtilityFunctions utilityFunctions = new UtilityFunctions();
	
	/*  Function Name: clusterData()
	 *  Parameters:  1. List of Facebook Nodes (DataType: ArrayList<FacebookNode>) 
	 *  			 
	 *  Description: Iterates through the List of Facebook Node in the ArrayList and creates clusters in the form of circles 
	 *               by taking into account factors like top-n similarity, connections strength and connection value.
	 *               
	 *  Return: Returns an ArrayList of Circles. 			 
	 */
	
	public ArrayList<Circle> clusterData(ArrayList<FacebookNode> allNodeSimilarityList){
		
		int i, topNCount=0, topNCountThreshold;
		int noOfRows = allNodeSimilarityList.size();
		ArrayList<Circle> allCircles = new ArrayList<Circle>();
		int threshold = allNodeSimilarityList.get(0).edgeList.size();	
		threshold = threshold/2;
		
		// Determining the topNCount value in order to consider the similarity between nodes
		if(noOfRows <= 100)
			topNCountThreshold = 3;
		else if(noOfRows > 100 && noOfRows <= 300)
			topNCountThreshold = 6;
		else if(noOfRows > 300 && noOfRows < 500)
			topNCountThreshold = 10;		
		else
			topNCountThreshold = 15;
	
		for(int s=0; s<allNodeSimilarityList.size()-1; s++){ // Iterating over all nodes (For loop 1)
			
			if(allNodeSimilarityList.get(s).edgeList.size() >= threshold){ // Checking the edgelist size against the threshold (Connection Value)
				
				    /* Creating a new circle and giving its number and also adding the (sth)node to the list */
					Circle circle = new Circle();
					circle.circleNumber = "Circle"+(s+1);
					circle.list.add(allNodeSimilarityList.get(s).nodeNumber);
					
					for(i=s+1; i<allNodeSimilarityList.size()-1; i++){ // Iterating over all nodes	below the sth node (For loop 2)	 	
						
						FacebookNode currentNode = allNodeSimilarityList.get(i);					
						double connectionStrength = calculateConnectionStrength(circle, currentNode, allNodeSimilarityList); // calculating connection strength for the current node with the existing circle
						
						if(connectionStrength >= 0.5){ // If connection strength is at least 50 percent
							
							/* Code to check similarity between current Node and nodes in Circle - Start */
							for(int k=0; k<circle.list.size(); k++){	
								FacebookNode compareNode = utilityFunctions.getFBNode(allNodeSimilarityList, circle.list.get(k));					
								for(String nodeNumber : currentNode.topNMap.keySet()){
									if(compareNode.topNMap.containsKey(nodeNumber))
										topNCount++;
								}
								
								// Break the 'k' loop if threshold is not reached 
								if(topNCount < topNCountThreshold) 
									break; 
									
								// If last iteration then add that node and break the loop
								if((k == circle.list.size()-1)){
									circle.list.add(currentNode.nodeNumber);
									break;
								}
								
								topNCount = 0; // Set the count to zero
							
							}
							/* Code to check similarity between current Node and nodes in Circle - Finish */
						} //End of connection strength if loop
					}// End of For loop 2
					
					allCircles.add(circle); // Adding the circle to the array list of circles
			}// End of connection value if loop
			
		}// End of For loop 1
		return allCircles;
	}
	
	
	/*  Function Name: calculateConnectionStrength()
	 *  Parameters:  1. A circle object (DataType: Circle)
	 *               2. A FacebookNode object (DataType: FacebookNode) 
	 *               3. List of Facebook Nodes (DataType: ArrayList<FacebookNode>)
	 *  			 
	 *  Description: Calculates the connection strength of a particular node say 'currentNode' to a circle with the help of the following formulae
	 *               Connection Strength  = (k/n) where
	 *               k --> Number of nodes in the circle to which the currentNode has an edge
	 *               n --> Total number of nodes in the circle
	 *               
	 *  Return: Returns the Connection Strength as a double value. 			 
	 */
	
	public double calculateConnectionStrength(Circle circle, FacebookNode currentNode, ArrayList<FacebookNode> allNodeSimilarityList){
		
		double connectionStrength = 0;
		int n = circle.list.size();
		int k=0;
		for(int m=0; m<n; m++){
			FacebookNode node = utilityFunctions.getFBNode(allNodeSimilarityList, circle.list.get(m));
			if(node.edgeList.contains(currentNode.nodeNumber))
				k++;
		}
		
		connectionStrength = (double)k/n;
		return connectionStrength;
	}
}
