package ADBFinalProject;

import java.util.ArrayList;
import java.util.HashMap;

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
  
  public ArrayList<AccessPair> getAccessInformation() {
    HashMap<Integer, Integer> accessTable = new HashMap<Integer, Integer>();
    for (int record = 0; record < listOfSitesAccessed.size(); record++) {
      int siteAccessed = listOfSitesAccessed.get(record);
      int timeAccessed = listOfTimeAccessed.get(record);
      if (accessTable.get(siteAccessed) == null) {
    	accessTable.put(siteAccessed, timeAccessed);
      } else {
    	if (timeAccessed > accessTable.get(siteAccessed)) {
    	  accessTable.put(siteAccessed, timeAccessed);
    	}
      }
    }
    
    ArrayList<AccessPair> accessInformation = new ArrayList<AccessPair>();
    for (HashMap.Entry<Integer, Integer> entry : accessTable.entrySet()) {
      int site = entry.getKey();
      int time = entry.getValue();
      AccessPair newAccessPair = new AccessPair(site, time);
      accessInformation.add(newAccessPair);
    }
    return accessInformation;
  }
  
  public class AccessPair {
    public int site;
    public int accessTime;
    
    public AccessPair(int site, int accessTime) {
      this.site = site;
      this.accessTime = accessTime;
    }
  }
}