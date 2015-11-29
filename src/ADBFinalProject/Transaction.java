package ADBFinalProject;

import java.util.LinkedList;

/***
 * A transaction in the simulation.
 * @author Shikuan Huang
 */
public class Transaction {
  public int transactionNumber;
  public String transactionType;
  public int arrivalTime;
  public Boolean commitSuccess;
  public LinkedList<Integer> listOfData;
  public LinkedList<Integer> listOfTimeAccessed;
  public LinkedList<Integer> listOfSites;
  
  /***
   * Create a new transaction with a transaction number and the arrival time.
   * @param transactionNum the transaction number. 
   * @param type the type of transaction.
   * @param arrival the arrival time of the transaction.
   */
  public Transaction(int transactionNum, String type, int arrival) {
	transactionNumber = transactionNum;
	transactionType = type;
	arrivalTime = arrival;
	commitSuccess = null;
	listOfData = new LinkedList<Integer>();
	listOfTimeAccessed = new LinkedList<Integer>();
	listOfSites = new LinkedList<Integer>();
  }
}
