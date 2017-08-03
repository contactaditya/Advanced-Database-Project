package ADBFinalProject;

import ADBFinalProject.Status;
import ADBFinalProject.Data;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * A site in the simulation.
 * @author Shikuan Huang
 */
public class Site {
  /**
   * The current status of this site.
   */
  public Status status;
  
  /**
   * A list containing the data at this site.
   */
  public ArrayList<Data> listOfData;
  
  /**
   * The table containing the names of the data and whether they are locked or not.
   */
  public HashMap<String, Integer> lockTable;
  
  /**
   * The time when this site last recovered.
   */
  public Integer lastRecoverTime;

  /***
   * Create a new site with default values.
   */
  public Site() {
    status = Status.activeAndConsistent;
    listOfData = new ArrayList<Data>();
    lockTable = new HashMap<String, Integer>();
    lastRecoverTime = null;
  }
  
  /**
   * Adds a new data item to the site.
   * @param dataToAdd the data to add to the site.
   */
  public void addData(Data dataToAdd) {
     listOfData.add(dataToAdd);
  }
  
  /**
   * Lock the data item specified.
   * @param dataName the name of the data item to lock.
   * @param lockType what type of lock to put on the data.
   * @return whether the data was locked successfully.
   * @throws Exception when an invalid lock type is specified.
   */
  public boolean lockData(String dataName, String lockType) throws Exception {
      if (isDataLocked(dataName) == 1) {
        return false;
      }
      if (lockType == "read") {
        if (lockTable.get(dataName) == 2) {
	  return true;
        }
         lockTable.put(dataName, 1);	
      } else if (lockType == "write") {
	 lockTable.put(dataName, 2);
      } else {
	 throw new Exception("Invalid lock type!");
      }
	
     return true;
  }
  
  /**
   * Determine whether the specified data item is locked or not.
   * @param dataName the name of the data item to lock.
   * @return whether the data item is locked and what type of lock is on it.
   * @throws Exception when the data is in an invalid lock state.
   */
  public int isDataLocked(String dataName) throws Exception {
	if (lockTable.get(dataName) != null && lockTable.get(dataName) == 0) {
	  return 0;
	} else if (lockTable.get(dataName) != null && lockTable.get(dataName) == 1) {
	  return 1;
	} else if (lockTable.get(dataName) != null && lockTable.get(dataName) == 2) {
	  return 2;
	} else if (lockTable.get(dataName) == null) {
	  return -1;
	} else {
	  throw new Exception("Site.isDataLocked(String dataName) is in an invalid state!");
	}
  }
  
  /**
   * Get a reference to the data item with the data name specified.
   * @param dataName the name of the data item to get.
   * @return a reference to the data item specified.
   */
  public Data getData(String dataName) {
    for (int data = 0; data < listOfData.size(); data++) {
      if (listOfData.get(data).name.equals(dataName)) {
        return listOfData.get(data);
      }
    }
    return null;
  }
  
  /**
   * Write the specified value to the specified data item.
   * @param time the time the operation was issued.
   * @param dataName the name of the data item to write to.
   * @param value the value to write to the data item.
   * @return whether the write is successful or not.
   * @throws Exception when there is a an issue with the lock on the data item.
   */
  public boolean writeToData(int time, String dataName, int value) throws Exception {
      if (isDataLocked(dataName) == 1) {
         return false;
      }
      for (int data = 0; data < listOfData.size(); data++) {
        if (listOfData.get(data).name.equals(dataName)) {
          listOfData.get(data).write(time, value);
          break;
        }
      }
    return true;
  }
  
  /**
   * Determine whether the specified transaction has a lock on the data item specified.
   * @param transactionNumber the number of the transaction to check.
   * @param dataName the name of the data item to check.
   * @return whether the specified transaction has a lock on the data item specified.
   */
  public boolean haveLock(int transactionNumber, String dataName) {
    // Go through the list of data and see if the transaction that has the lock on the specified item is the transaction specified.
    for (Data data : listOfData)  {
      if (data.name.equals(dataName)) {
	if (data.waitingQueue.isEmpty()) {
	   return false;
	}
	if (data.waitingQueue.get(0) == transactionNumber) {
	   return true;
	}
      }		
    }
    return false;
  }
  
  /**
   * Determine whether the specified transaction is already waiting for the lock on the specified
   * data item.
   * @param transactionNumber the number of the transaction to check.
   * @param dataName the name of the data item to check.
   * @return whether the specified transaction is already waiting for the lock the the specified
   * data item.
   */
  public boolean isAlreadyWaitingForData(int transactionNumber, String dataName) {
     for (int data = 0; data < listOfData.size(); data++) {
       if (listOfData.get(data).name.equals(dataName)) {
	 for (int transactionOnLine = 0; transactionOnLine < listOfData.get(data).waitingQueue.size(); transactionOnLine++) {
	    if (listOfData.get(data).waitingQueue.get(transactionOnLine) == transactionNumber) {
	      return true;
	    }
	 }
       }
     }
    return false;
  }
}
