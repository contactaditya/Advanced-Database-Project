package ADBFinalProject;

import java.util.LinkedList;

/***
 * A transaction in the simulation.
 * @author Shikuan Huang
 */
public class Transaction {
  public int transactionNumber;
  public int arrivalTime;
  public Boolean commitSuccess;
  public LinkedList<Integer> listOfData;
  public LinkedList<Integer> listOfTimeAccessed;
  public LinkedList<Integer> listOfSites;
  
  /***
   * Create a new transaction with a transaction number and the arrival time.
   * @param transactionNum 
   * @param arrival
   */
  public Transaction(int transactionNum, int arrival) {
	transactionNumber = transactionNum;
	arrivalTime = arrival;
	commitSuccess = null;
	listOfData = new LinkedList<Integer>();
	listOfTimeAccessed = new LinkedList<Integer>();
	listOfSites = new LinkedList<Integer>();
  }
}
