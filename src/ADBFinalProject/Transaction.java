package ADBFinalProject;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * A transaction in the simulation.
 */
public class Transaction {
  /**
   * The number of the transaction. 
   */	  	
  public int transactionNumber;
  
  /**
   * The type of the transaction.
   */	
  public String transactionType;
  
  /**
   * The arrival time of the transaction.
   */
  public int arrivalTime;
  
  /**
   * It indicates whether the commit was successful or not.
   */	  
  public Boolean commitSuccess;
  
  /**
   * The list of the times that the sites have been accessed.
   */	  
  public ArrayList<Integer> listOfTimeAccessed;
  
  /**
   * The list of the sites that have been accessed.
   */	 
  public ArrayList<Integer> listOfSitesAccessed;
  
  /**
   * The reason why the transaction has been aborted.
   */	 
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
    listOfTimeAccessed = new ArrayList<Integer>();
    listOfSitesAccessed = new ArrayList<Integer>();
    abortReason = null;
  }
  
  /**
   * Get a list of the sites accessed and the respective access times.
   * @return a list of the sites accessed and the respective access times.
   */
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
  
  /**
   * A pair containing a site and the time the site was accessed.
   */
  public class AccessPair {
    /**
     * The site accessed.
     */
    public int site;
    
    /**
     * The time the site was accessed.
     */
    public int accessTime;
    
    /**
     * Construct a new access pair with the specified site and the specified access time.
     * @param site the site accessed.
     * @param accessTime the time the site was accesed.
     */
    public AccessPair(int site, int accessTime) {
      this.site = site;
      this.accessTime = accessTime;
    }
  }
}
