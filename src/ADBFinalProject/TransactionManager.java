package ADBFinalProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import ADBFinalProject.Data;
import ADBFinalProject.Transaction;

//Author : Aditya Gupta (ag4479)

public class TransactionManager {
  public LinkedList<Transaction> transactions;
  public int numberOfSites;
  public Site[] sites;
  public ArrayList<Operation> bufferOfOperations;

  public TransactionManager(int numberOfSites) {
	transactions = new LinkedList<Transaction>();
	this.numberOfSites = numberOfSites;
    sites = new Site[numberOfSites];
    bufferOfOperations = new ArrayList<Operation>();
	for (int site = 0; site < numberOfSites; site++) {
	  sites[site] = new Site();
	}
  }
  
  public void initializeDataAtSite(int siteNumber, String dataName, int dataValue, boolean isReplicated) {
	Data dataToAdd = new Data(dataName, dataValue, 0, isReplicated);
	sites[siteNumber].addData(dataToAdd);
	sites[siteNumber].lockTable.put(dataName, 0);
  }

  public void tryBufferedOperations(int time) throws Exception {
	  /**
	   * Go Through the arraylist called buffer of operations and for each 
	   * operation call appropriate method depending on the type of operation.
      **/
	  
	  for(Operation operation : bufferOfOperations) {
		 if(operation.operationType == "read") {
			int transactionNumber = operation.transactionNumber;
			String dataName = operation.dataName;
		    read(time,transactionNumber,dataName,false);
		    bufferOfOperations.remove(operation);
		 }
		 else if(operation.operationType == "write") {
			int transactionNumber = operation.transactionNumber;
			String dataName = operation.dataName; 
			int value = operation.valueToWrite;
			write(time,transactionNumber,dataName,value); 
		    bufferOfOperations.remove(operation);
		 }
	  }   
  }
  
  public void begin(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " begins at time " + time + ".");
	String transactionType = "readwrite";
    Transaction newTransaction = new Transaction(transactionNumber, transactionType, time);
    transactions.add(newTransaction);
  }
  
  public void beginRO(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " (read only) begins at time " + time + ".");
	String transactionType = "readonly";
	Transaction newTransaction = new Transaction(transactionNumber, transactionType, time);
	transactions.add(newTransaction);
  }
  
  public void read(int time, int transactionNumber, String dataName) throws Exception {
	read(time, transactionNumber, dataName, false);
  }
	
  public void read(int time, int transactionNumber, String dataName, boolean wait) throws Exception {
	/**
	    INCLUDE AS LAST PARAMETER A FLAG TO WAIT OR NOT
		KEEP RECORD WHETHER A SITE CONTAINED D DURING THE SEARCH OR NOT
		for all sites not failed
		  if site contains D
		    if READONLY
		      if (state is consistent || (state not consistent && D is not replicated))
		        find correct copy for D to read
		          read D
		    else READWRITE
		      if D is not locked && (state is consistent || D is not replicated)
		        lock D
		        read D
		        return
		      else
		        if not wait && D is replicated
		          try another site
		        else if wait || D is non-replicated
		          if T is older than R that locked D
		            if T is older than Y the last transaction waiting for D
		              get on waiting line for D
		          if D is non-replicated
		            break
		     
		// HERE ONLY IF NOTHING IS READ
		if no site checked contains D
		  buffer operation
		else if not wait
		  call READ again with wait set to true
		else
		  abort
	 */
	Transaction transactionCopy = getTransaction(transactionNumber);
	Site currentSite = null;
	Data dataToReadCopy = null;
	int valueToReadIndex = 0;
	
	boolean foundDataInSearch = false;
	
	// FIND A SITE THAT CONTAINS DATA TO READ
    for (int site = 0; site < sites.length; site++) {
      if (sites[site].status.equals(Status.failed)) {
        continue;
      }
    	
      // DETERMINE IF CURRENT SITE CONTAINS DATA TO READ
	  try {
		boolean siteContainsData = false;
	    Site siteToSearch = sites[site];
		for(Data data : siteToSearch.listOfData) {
		  if(data.name.equals(dataName)) {
			currentSite = siteToSearch;
		    dataToReadCopy = data;
		    siteContainsData = true;
		    foundDataInSearch = true;
		  }
	    }
		if (!siteContainsData) {
		  continue;
		}
	  } catch(Exception e) {
	    System.out.println(e);
	  }
	  
	  // THE OPERATION COMES FROM A READONLY TRANSACTION
	  // FIND THE CORRECT COPY OF VALUE TO READ
      if (transactionCopy.transactionType.equals("readonly")) {
        if (currentSite.status.equals(Status.activeAndConsistent) ||
        	(currentSite.status.equals(Status.activeNotConsistent) && !dataToReadCopy.isReplicated)) {
          for (int times = dataToReadCopy.modifiedTimes.size() - 1 ; times >= 0 ; times--) {
      	    if (dataToReadCopy.modifiedTimes.get(times) < transactionCopy.arrivalTime) {
      		  if (!(times == dataToReadCopy.modifiedTimes.size() - 1 && dataToReadCopy.isTemporary)) {
      		    valueToReadIndex = times;
      		    System.out.println("  > Transaction " + transactionNumber + " read " + dataName
      		    	+ " = " + dataToReadCopy.values.get(valueToReadIndex));
      		    updateTransactionAccessInformation(transactionNumber, site, time);
      		    break;
      		  }
      		}
      	  }
        }
	    return;
      } else {
    	// THE OPERATION COMES FROM A READWRITE TRANSACTION
    	if (sites[site].isDataLocked(dataName) == 0
    	  && (currentSite.status.equals(Status.activeAndConsistent) || !dataToReadCopy.isReplicated)) {
    	  // DATA IS IN SITE AND NOT LOCKED, AND EITHER SITE CONSISTENT OR DATA REPLICATED
    	  sites[site].lockData(dataName);
          sites[site].getData(dataName).waitingQueue.add(transactionNumber);
          // ASSUMES THAT IF THERES NO LOCK THEN THERE IS NO TEMPORARY WRITE
          System.out.println("  > Transaction + " + transactionNumber + " read " + dataName
        	  + " = " + dataToReadCopy.values.get(dataToReadCopy.values.size() - 1));
          updateTransactionAccessInformation(transactionNumber, site, time);
          return;
    	} else if (sites[site].isDataLocked(dataName) == 1) {
    	  // DATA IS IN SITE AND LOCKED
    	  if (!wait && dataToReadCopy.isReplicated) {
    		break;
    	  } else if (wait || !dataToReadCopy.isReplicated) {
    		if (transactionCopy.arrivalTime <
    	        getTransaction(dataToReadCopy.waitingQueue.get(0)).arrivalTime) {
    		  if (dataToReadCopy.waitingQueue.isEmpty()) {
    	    	sites[site].getData(dataName).waitingQueue.add(transactionNumber);
    	    	
    	    	Operation operationToBuffer = new Operation(transactionNumber,
    	    	    transactionCopy.transactionType, "read", dataName);
    	    	bufferOfOperations.add(operationToBuffer);
    	      } else {
    	    	int lastTransactionInQueue =
    	    	    dataToReadCopy.waitingQueue.get(dataToReadCopy.waitingQueue.size() - 1);
    	    	if (transactionCopy.arrivalTime <
    	    	    getTransaction(lastTransactionInQueue).arrivalTime) {
    	    	  sites[site].getData(dataName).waitingQueue.add(transactionNumber);
    	    	  
    	    	  Operation operationToBuffer = new Operation(transactionNumber,
    	    	      transactionCopy.transactionType, "read", dataName);
    	    	  bufferOfOperations.add(operationToBuffer);
    	    	} else {
    	    	  String abortReason = "transaction "
    	    	      + transactionNumber + " cannot get lock on " + dataName
    	    	      + " since transaction " + lastTransactionInQueue + " had a lock on "
    	    	      + dataName + " and is older.";
    	    	  abortTransaction(transactionNumber, abortReason);
    	    	}
    	      }
    		}
    	  }
    	  
    	  if (!dataToReadCopy.isReplicated) {
    		break;
    	  }
    	} else {
    	  // DATA IS NOT IN SITE. SHOULD NOT HAPPEN. DO NOTHING.
    	}   			 
	  }
    }
    
    // HERE ONLY IF NOTHING IS READ AFTER FOR LOOP-ING
    if (!foundDataInSearch) {
      Operation operationToBuffer = new Operation(transactionNumber,
          transactionCopy.transactionType, "read", dataName);
      bufferOfOperations.add(operationToBuffer);
    } else if (!wait) {
      read(time, transactionNumber, dataName, true);
    } else {
      // CANNOT GET IN QUEUE FOR DATA
      String abortReason = " transaction " + transactionNumber + " cannot get a lock on "
          + dataName + " since " + dataName 
          + " at all sites is held or is waiting on by older transactions.";
	  abortTransaction(transactionNumber, abortReason);
    }
  }
	
  public void write(int time, int transactionNumber, String dataName, int value) throws Exception {	
	System.out.println("Transaction " + transactionNumber + " wants to write " + value + " to " + dataName + " at time " + time + ".");
	/**
	 * for all sites S not failed
	 *   if get lock on D successful
	 *     get lock on data
	 *     write value to data
	 *   else if D is locked
	 *     if T.arrivalTime is earlier than transaction S that holds lock on D's arrival time
	 *       if (T.arrivalTime is earlier than last item in D's queue
	 *         get on D's queue
	 *       else
	 *         abort
	 *     else
	 *       abort
	 */
	for (int site = 0; site < numberOfSites; site++) {
	  // FOR EACH SITE THAT HAS NOT FAILED
	  if (!sites[site].status.equals(Status.failed)) {
		if (sites[site].isDataLocked(dataName) == 0) {
	      // DATA IS IN TABLE AND NOT LOCKED.
		  sites[site].lockData(dataName);
		  sites[site].getData(dataName).waitingQueue.add(transactionNumber);
		  sites[site].writeToData(time, dataName, value);
		  for (int data = 0; data < sites[site].listOfData.size(); data++) {
			if (sites[site].listOfData.get(data).name.equals(dataName)) {
			  sites[site].listOfData.get(data).hasBeenWrittenToAfterRecovery = true;
			}
			break;
		  }
		  updateTransactionAccessInformation(transactionNumber, site, time);
		  break;
		} else if (sites[site].isDataLocked(dataName) == 1) {
		  // DATA IS IN TABLE AND LOCKED.
		  Data dataToWriteToCopy = sites[site].getData(dataName);
		  Transaction currentTransaction = getTransaction(transactionNumber);
		  if (currentTransaction.arrivalTime < 
		      getTransaction(dataToWriteToCopy.waitingQueue.get(0)).arrivalTime) {
			if (dataToWriteToCopy.waitingQueue.isEmpty()) {
			  sites[site].getData(dataName).waitingQueue.add(transactionNumber);
			  
			  Operation operationToBuffer = new Operation(transactionNumber,
	    	      currentTransaction.transactionType, "write", dataName);
	    	  bufferOfOperations.add(operationToBuffer);
	    	  return;
			} else {
			  int lastTransactionInQueue =
			      dataToWriteToCopy.waitingQueue.get(dataToWriteToCopy.waitingQueue.size() - 1);
			  if (getTransaction(transactionNumber).arrivalTime
			      < getTransaction(lastTransactionInQueue).arrivalTime) {
                sites[site].getData(dataName).waitingQueue.add(transactionNumber);
                
                Operation operationToBuffer = new Operation(transactionNumber,
      	    	    currentTransaction.transactionType, "write", dataName);
      	    	bufferOfOperations.add(operationToBuffer);
      	    	return;
			  } else {
				String abortReason = "transaction "
				    + transactionNumber + " cannot get lock on " + dataName
				    + " since transaction " + lastTransactionInQueue + " had a lock on "
				    + dataName + " and is older.";
				abortTransaction(transactionNumber, abortReason);
				return;
			  }
			}
		  }
		  else {
			String abortReason = "transaction "
				+ transactionNumber + " cannot get a lock on " + dataName
				+ " since transaction " + dataToWriteToCopy.waitingQueue.get(0) + " had a lock on "
				+ dataName + " and is older.";
		    abortTransaction(transactionNumber, abortReason);
		    return;
		  }
		}
	  }
	}
  }
	
  public void dump() {
	//System.out.println("DUMP called");
	System.out.println("\nValue of Data at Sites");
	for (int site = 0; site < numberOfSites; site++) {
	  dump(site, null);
	}
  }
  
  public void dump(int siteNumber) {
	//System.out.println("DUMP called for site " + siteNumber);
	System.out.println("\nValue of Data at Site " + siteNumber);
	dump(siteNumber, null);
  }
  
  public void dump(String dataName) {
	//System.out.println("DUMP called for data " + dataName);
	System.out.println("\nValue of " + dataName + " at All Sites");
	for (int site = 0; site < numberOfSites; site++) {
	  dump(site, dataName); 
	}
  }
	
  public void dump(int siteNumber, String key) {
	System.out.println("<Site " + siteNumber + ">");
	for (int data = sites[siteNumber].listOfData.size() - 1; data > 0 ; data--) {
	  String currentDataName = sites[siteNumber].listOfData.get(data).name;
	  if (key == null || currentDataName.equals(key)) {
		System.out.println("  " + currentDataName);
		for (int value = 0; value < sites[siteNumber].listOfData.get(data).values.size(); value++) {
		  System.out.println("    " + sites[siteNumber].listOfData.get(data).values.get(value)
		      + ", modified at time " + sites[siteNumber].listOfData.get(data).modifiedTimes.get(value));
		}
	  }
	}
	System.out.println();
  }
  
  public void end(int time, int transactionNumber) {
	System.out.println("Transaction " + (transactionNumber + 1) + " wants to end at time " + time + ".");
	if (getTransaction(transactionNumber).commitSuccess != null
	    && getTransaction(transactionNumber).commitSuccess == false) {
      System.out.println("  > Transaction " + transactionNumber + " failed to commit.");
      return;
	}
	System.out.println("Transaction " + transactionNumber + " ends at time " + time);
	// REPORT WHETHER TRANSACTION CAN COMMIT
	/* for all sites accessed
	 *   if the site is down || siteLastRecovery == null || lastAccessTime < siteLastRecoveryTime
	 *     set transaction.commitStatus to fail
	 *     set transaction.abortReason to site x has failed since last access
	 *     break 
	 */
	ArrayList<Transaction.AccessPair> accessInformation =
	    getTransaction(transactionNumber).getAccessInformation();
	
	for (int record = 0; record < accessInformation.size(); record++) {
	  int siteAccessed = accessInformation.get(record).site;
	  int timeAccessed = accessInformation.get(record).accessTime;
	  if (sites[siteAccessed].status.equals(Status.failed)
		  || ((sites[siteAccessed].lastRecoverTime != null)
		  && timeAccessed < sites[siteAccessed].lastRecoverTime)) {
		for (Transaction transaction : transactions) {
	      if (transaction.transactionNumber == transactionNumber) {
	    	transaction.commitSuccess = false;
	    	transaction.abortReason = " one or more of the sites accessed has failed since last access.";
	    	System.out.println("  > Transaction " + transactionNumber + " failed to commit.");
	    	return;
	      }
		}
	  }
	}
	for (Transaction transaction : transactions) {
	  if (transaction.transactionNumber == transactionNumber) {
		transaction.commitSuccess = true;
		System.out.println("  > Transaction " + transactionNumber + " comitted successfully.");
		return;
	  }
	}
  }
	
  public void fail(int time, int siteNumber) {

	/* set site's status to fail
	 * set all values in lockTable to 0 (empty)
	 */
    sites[siteNumber - 1].status = Status.failed;
    Iterator i = sites[siteNumber - 1].lockTable.entrySet().iterator();
    HashMap.Entry value;
    while (i.hasNext()) {
      value = (HashMap.Entry)i.next();
      value.setValue(0);
  	System.out.println("Site " + siteNumber + " failed at time " + time);
    }
  }

  public void recover(int time, int siteNumber) {

	/* set site's status to activeNotConsistent
	 * set site's last recoverTime to time
	*/
	
	Site currentSite = null;  
	currentSite = sites[siteNumber - 1];
	currentSite.status = Status.activeNotConsistent;
	currentSite.lastRecoverTime = time;
	System.out.println("Site " + siteNumber + " recovered at time " + time);
	
  }
  
  public void printSummary() throws Exception {
	// Output whether each transaction committed successfully or failed to commit.
	System.out.println("Commit Summary");
	for (Transaction transaction : transactions) {
	  if (transaction.commitSuccess == null) {
		//throw new Exception("A transaction has not committed successfully or failed!");
	  } else if (transaction.commitSuccess) {
	    System.out.println("Transaction " + transaction.transactionNumber + " committed successfully.");
	  } else {
		System.out.println("Transaction " + transaction.transactionNumber + " aborted because " +
	        transaction.abortReason);
	  }
	}
	
	System.out.println("\nOperations in operations buffer: " + bufferOfOperations.size());
  }
  
  public Transaction getTransaction(int transactionNumber) {
    for (Transaction transaction : transactions) {
      if (transaction.transactionNumber == transactionNumber) {
        return transaction;
      }
    }
    return null;
  }
  
  public boolean abortTransaction(int transactionNumber, String reason) {
    for (Transaction transaction : transactions) {
      if (transaction.transactionNumber == transactionNumber) {
    	transaction.commitSuccess = false;
    	transaction.abortReason = reason;
    	return true;
      }
    }
    return false;
  }
  
  public void updateTransactionAccessInformation(int transactionNumber,
      int siteNumber, int accessTime) {
    for (Transaction transaction : transactions) {
      if (transaction.transactionNumber == transactionNumber) {
    	transaction.listOfSitesAccessed.add(siteNumber);
    	transaction.listOfTimeAccessed.add(accessTime);
    	return;
      }
    }
  }
}
	
	
	

