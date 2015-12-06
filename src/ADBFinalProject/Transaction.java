package ADBFinalProject;

import java.util.ArrayList;

/***
 * A transaction in the simulation.
 * @author Shikuan Huang
 */
public class Transaction {
  public int transactionNumber;
  public String transactionType;
  public int arrivalTime;
  public Boolean commitSuccess;
  public ArrayList<Integer> listOfData;
  public ArrayList<Integer> listOfTimeAccessed;
  public ArrayList<Integer> listOfSitesAccessed;
  public String abortReason;
  
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
	listOfData = new ArrayList<Integer>();
	listOfTimeAccessed = new ArrayList<Integer>();
	listOfSitesAccessed = new ArrayList<Integer>();
	abortReason = null;
  }
}