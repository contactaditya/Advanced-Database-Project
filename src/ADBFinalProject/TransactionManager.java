package ADBFinalProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import ADBFinalProject.Data;
import ADBFinalProject.Transaction;

/**
 * A transaction manager that handles input requests and translate them into operations for the
 *  simulation system.
 */
public class TransactionManager {
  /**
   * A list of all the transactions seen so far.
   */
  public LinkedList<Transaction> transactions;
  
  /**
   * The total number of sites in the simulation.
   */
  public int numberOfSites;
  
  /**
   * A list of all the sites in the simulation.
   */
  public Site[] sites;
  
  /**
   * The list of operations to be retried at every time cycle.
   */
  public ArrayList<Operation> bufferOfOperations;

  /**
   * Create a transaction manager with the specified number of sites to initialize.
   * @param numberOfSites the number of sites to use in the simulation.
   */
  public TransactionManager(int numberOfSites) {
	transactions = new LinkedList<Transaction>();
	this.numberOfSites = numberOfSites;
    sites = new Site[numberOfSites];
    bufferOfOperations = new ArrayList<Operation>();
	for (int site = 0; site < numberOfSites; site++) {
	  sites[site] = new Site();
	}
  }
  
  /**
   * Inserts the specified data with the specified value at the site specified and indicates
   * whether the data being inserted is replicated or not.
   * @param siteNumber the number of the site to initialize.
   * @param dataName the name of the data to insert.
   * @param dataValue the initial value of the data to insert.
   * @param isReplicated whether the data being inserted is replicated data or not.
   */
  public void initializeDataAtSite(int siteNumber, String dataName, int dataValue, boolean isReplicated) {
	Data dataToAdd = new Data(dataName, dataValue, 0, isReplicated);
	sites[siteNumber].addData(dataToAdd);
	sites[siteNumber].lockTable.put(dataName, 0);
  }

  /**
   * Retry all buffered operations in the buffer of operations.
   * @param time the current time when the operations are being retried.
   * @throws Exception when there is a problem while executing a read or write operation.
   */
  public void tryBufferedOperations(int time) throws Exception {
	if (bufferOfOperations.isEmpty()) {
	  return;
	}

	int totalOperations = bufferOfOperations.size();
	int operationsDone = 0;
	while (operationsDone < totalOperations) {
	  int currentTransactionNumber = bufferOfOperations.get(0).transactionNumber;
	  if (bufferOfOperations.get(0).operationType == "read") {
		int transactionNumber = bufferOfOperations.get(0).transactionNumber;
		String dataName = bufferOfOperations.get(0).dataName;
		read(time, transactionNumber, dataName);
		if (!bufferOfOperations.isEmpty()
		    && bufferOfOperations.get(0).transactionNumber == currentTransactionNumber)
		bufferOfOperations.remove(bufferOfOperations.get(0));
	  } else if (bufferOfOperations.get(0).operationType == "write") {
		int transactionNumber = bufferOfOperations.get(0).transactionNumber;
		String dataName = bufferOfOperations.get(0).dataName; 
		int value = bufferOfOperations.get(0).valueToWrite;
		write(time, transactionNumber, dataName, value); 
		if (!bufferOfOperations.isEmpty()
		    && bufferOfOperations.get(0).transactionNumber == currentTransactionNumber) {
		  bufferOfOperations.remove(bufferOfOperations.get(0));
		}
      } else {
		throw new Exception("Operation not handled!");
      }
      operationsDone++;
	}  
  }
  
  /**
   * Creates a new read/write transaction in the simulation.
   * @param time the time the transaction began.
   * @param transactionNumber the number of the operation.
   */
  public void begin(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " begins at time " + time + ".");
	String transactionType = "readwrite";
    Transaction newTransaction = new Transaction(transactionNumber, transactionType, time);
    transactions.add(newTransaction);
  }
  
  /**
   * Creates a new read only transaction.
   * @param time the time the transaction began.
   * @param transactionNumber the number of the transaction.
   */
  public void beginRO(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " (read only) begins at time " + time + ".");
	String transactionType = "readonly";
	Transaction newTransaction = new Transaction(transactionNumber, transactionType, time);
	transactions.add(newTransaction);
  }
  
  /**
   * Lock and read the value of the data item specified if the operation comes from a read/write
   * transaction or just read the value of the data if the operatin comes from a read only
   * transaction.
   * @param time the time the operation was issued.
   * @param transactionNumber the transaction that issued the operation
   * @param dataName the name of the data item to write to.
   * @throws Exception when there is a problem while reading data.
   */
  public void read(int time, int transactionNumber, String dataName) throws Exception {
	if (isTransactionCommittedOrAborted(transactionNumber)) {
	  return;
	}
	
	System.out.println("Transaction " + transactionNumber + " wants to read " + dataName + " at time " + time + ".");
	read(time, transactionNumber, dataName, false);
  }

  /**
   * Lock and read the value of the data item specified if the operation comes from a read/write
   * transaction or just read the value of the data if the operatin comes from a read only
   * transaction.
   * @param time the time the operation was issued.
   * @param transactionNumber the transaction that issued the operation
   * @param dataName the name of the data item to write to.
   * @param wait whether the transaction should wait in any available waiting queue.
   * @throws Exception when there is a problem while reading data.
   */
  public void read(int time, int transactionNumber, String dataName, boolean wait) throws Exception {
	Transaction transactionCopy = getTransaction(transactionNumber);
	Site currentSite = null;
	Data dataToReadCopy = null;
	int valueToReadIndex = 0;
	
	boolean foundDataInSearch = false;
	
	// Find a site that contains data to read.
    for (int site = 0; site < sites.length; site++) {
      if (sites[site].status.equals(Status.failed)) {
        continue;
      }
    	
      // Determine if the current site contains data to read.
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
	  
	  // The operation comes from a read only transaction.
      if (transactionCopy.transactionType.equals("readonly")) {
        if (currentSite.status.equals(Status.activeAndConsistent) ||
        	(currentSite.status.equals(Status.activeNotConsistent) && !dataToReadCopy.isReplicated)
        	|| dataToReadCopy.hasBeenWrittenToAfterRecovery) {
          // Find the correct copy of the value to read.
          for (int times = dataToReadCopy.modifiedTimes.size() - 1 ; times >= 0 ; times--) {
      	    if (dataToReadCopy.modifiedTimes.get(times) < transactionCopy.arrivalTime) {
      		  if (!(times == dataToReadCopy.modifiedTimes.size() - 1 && dataToReadCopy.isTemporary)) {
      		    valueToReadIndex = times;
      		    System.out.println("  > Transaction " + transactionNumber + " read " + dataName
      		    	+ " = " + dataToReadCopy.values.get(valueToReadIndex) + ".");
      		    updateTransactionAccessInformation(transactionNumber, site, time);
      		    return;
      		  }
      		}
      	  }
        }
	    return;
      } else {
    	// The operation comes from a read write transaction.
    	if (sites[site].haveLock(transactionNumber, dataName) || (sites[site].isDataLocked(dataName) < 2
    	  && (currentSite.status.equals(Status.activeAndConsistent) || !dataToReadCopy.isReplicated
    	  || dataToReadCopy.hasBeenWrittenToAfterRecovery))) {
    	  // Data is in site and is either not locked, transaction already hold a lock, or data
    	  // is read locked and the site is active and consistent or the data is replicated.
    	  sites[site].lockData(dataName, "read");
    	  if (sites[site].getData(dataName).waitingQueue.isEmpty()
    		  || (!sites[site].getData(dataName).waitingQueue.isEmpty()
    		  && sites[site].getData(dataName).waitingQueue.get(0) != transactionNumber)) {
            sites[site].getData(dataName).waitingQueue.add(transactionNumber);
    	  }
          System.out.println("  > Transaction " + transactionNumber + " read " + dataName
        	  + " = " + dataToReadCopy.values.get(dataToReadCopy.values.size() - 1) + ".");
          updateTransactionAccessInformation(transactionNumber, site, time);
          return;
    	} else if (sites[site].isDataLocked(dataName) > 1) {
    	  // Data is in the site and write locked.
    	  if (!wait && dataToReadCopy.isReplicated) {
    		break;
    	  } else if (wait || !dataToReadCopy.isReplicated) {
    		if (transactionCopy.arrivalTime <
    	        getTransaction(dataToReadCopy.waitingQueue.get(0)).arrivalTime) {
    			// If transaction has an earlier arrival time than the one holding the lock.
    		  if (dataToReadCopy.waitingQueue.isEmpty() &&
    		    (!dataToReadCopy.isReplicated || sites[site].status.equals(Status.activeAndConsistent) ||
    		    (sites[site].status.equals(Status.activeNotConsistent) && dataToReadCopy.hasBeenWrittenToAfterRecovery))) {
    			// If the waiting queue for the lock is empty, get on line for the lock.
    	    	sites[site].getData(dataName).waitingQueue.add(transactionNumber);
    	    	
    	    	Operation operationToBuffer = new Operation(transactionNumber,
    	    	    transactionCopy.transactionType, "read", dataName);
    	    	bufferOfOperations.add(operationToBuffer);
    	    	return;
    	      } else {
    	    	if (sites[site].isAlreadyWaitingForData(transactionNumber, dataName)) {
    	    	// The transaction is already on line for the lock, keep waiting.
    	    	  Operation operationToBuffer = new Operation(transactionNumber,
            	      transactionCopy.transactionType, "read", dataName);
                  bufferOfOperations.add(operationToBuffer);
    	    	  return;
    	    	}
    	    	  
    	    	int lastTransactionInQueue =
    	    	    dataToReadCopy.waitingQueue.get(dataToReadCopy.waitingQueue.size() - 1);
    	    	
    	    	if (transactionCopy.arrivalTime <
    	    	    getTransaction(lastTransactionInQueue).arrivalTime) {
    	          // If transaction has an earlier arrival time than the last one on line for the
    	    	  // lock, get on line for the lock.
    	    	  if (!dataToReadCopy.isReplicated || dataToReadCopy.hasBeenWrittenToAfterRecovery) {
    	    		sites[site].getData(dataName).waitingQueue.add(transactionNumber);
        	    	  
        	    	Operation operationToBuffer = new Operation(transactionNumber,
        	    	    transactionCopy.transactionType, "read", dataName);
        	    	bufferOfOperations.add(operationToBuffer);
        	    	return;
    	    	  } else if (dataToReadCopy.isReplicated && !dataToReadCopy.hasBeenWrittenToAfterRecovery) {
    	    		break;
    	    	  }
    	    	} else {
    	          // The transaction does not have an earlier arrival time than the last one on
        	      // line for the lock, abort.
    	    	  String abortReason = "transaction "
    	    	      + transactionNumber + " cannot get lock on " + dataName
    	    	      + " since transaction " + lastTransactionInQueue + " had a lock on "
    	    	      + dataName + " and is older.";
    	    	  abortTransaction(transactionNumber, abortReason);
    	    	  System.out.println("  > Transaction " + transactionNumber + " aborted!");
    	    	  return;
    	    	}
    	      }
    		}
    	  }
    	  
    	  // Since transaction cannot read data, if data to read is replicated, try next site.
    	  if (!dataToReadCopy.isReplicated) {
    		break;
    	  }
    	} else {
    	  // Data is not in any site. Should not happen.
    	  throw new Exception("Read is in an invalid state!");
    	}   			 
	  }
    }
    
    // If nothing is read and no lock is obtained.
    if (!foundDataInSearch) {
      // If data is found during search, but cannot read or get lock at this time.
      Operation operationToBuffer = new Operation(transactionNumber,
          transactionCopy.transactionType, "read", dataName);
      bufferOfOperations.add(operationToBuffer);
    } else if (!wait) {
      // If cannot read data and did not wait for a lock previously, now try to wait on any line
      // for a lock.
      read(time, transactionNumber, dataName, true);
    } else {
      // Cannot read from any site and no locks are available.
      String abortReason = "transaction " + transactionNumber + " cannot get a lock on "
          + dataName + " since " + dataName 
          + " at all sites is held or is waiting on by older transactions.";
	  abortTransaction(transactionNumber, abortReason);
	  System.out.println("  > Transaction " + transactionNumber + " aborted!");
    }
  }

  /**
   * Lock and writes the specified value to the specified data item.
   * @param time the item the operation was issued.
   * @param transactionNumber the transaction that issued the operation.
   * @param dataName the name of the data item to read.
   * @param value the name of the data item to write to.
   * @throws Exception when there is a problem writing to the data.
   */
  public void write(int time, int transactionNumber, String dataName, int value) throws Exception {
	if (isTransactionCommittedOrAborted(transactionNumber)) {
	  return;
	}
	System.out.println("Transaction " + transactionNumber + " wants to write " + value + " to " + dataName + " at time " + time + ".");

	for (int site = 0; site < numberOfSites; site++) {
	  // FOR EACH SITE THAT HAS NOT FAILED
	  if (!sites[site].status.equals(Status.failed)) {
		if (sites[site].haveLock(transactionNumber, dataName) || sites[site].isDataLocked(dataName) == 0) {
	      // DATA IS IN TABLE AND NOT LOCKED (OR ALREADY HAVE LOCK).
		  sites[site].lockData(dataName, "write");
		  sites[site].getData(dataName).waitingQueue.add(transactionNumber);
		  sites[site].writeToData(time, dataName, value);
		  for (int data = 0; data < sites[site].listOfData.size(); data++) {
			if (sites[site].listOfData.get(data).name.equals(dataName)) {
			  sites[site].listOfData.get(data).hasBeenWrittenToAfterRecovery = true;
			}
			break;
		  }
		  updateTransactionAccessInformation(transactionNumber, site, time);
		  continue;
		} else if (sites[site].isDataLocked(dataName) > 0) {
		  // DATA IS IN TABLE AND LOCKED.
		  Data dataToWriteToCopy = sites[site].getData(dataName);
		  Transaction currentTransaction = getTransaction(transactionNumber);
		  if (currentTransaction.arrivalTime < 
		      getTransaction(dataToWriteToCopy.waitingQueue.get(0)).arrivalTime) {
			if (dataToWriteToCopy.waitingQueue.isEmpty()) {
			  sites[site].getData(dataName).waitingQueue.add(transactionNumber);
			  
			  Operation operationToBuffer = new Operation(transactionNumber,
	    	      currentTransaction.transactionType, "write", dataName, value);
	    	  bufferOfOperations.add(operationToBuffer);
	    	  return;
			} else {
			  if (sites[site].isAlreadyWaitingForData(transactionNumber, dataName)) {
				// IF ALREADY WAITING IN QUEUE, HAVE TO REBUFFER OPERATION
				Operation operationToBuffer = new Operation(transactionNumber,
		      	    currentTransaction.transactionType, "write", dataName, value);
		      	bufferOfOperations.add(operationToBuffer);
		      	return;
			  }
				
			  int lastTransactionInQueue =
			      dataToWriteToCopy.waitingQueue.get(dataToWriteToCopy.waitingQueue.size() - 1);
			  if (getTransaction(transactionNumber).arrivalTime
			      < getTransaction(lastTransactionInQueue).arrivalTime) {
                sites[site].getData(dataName).waitingQueue.add(transactionNumber);
                
                Operation operationToBuffer = new Operation(transactionNumber,
      	    	    currentTransaction.transactionType, "write", dataName, value);
      	    	bufferOfOperations.add(operationToBuffer);
      	    	return;
			  } else {
				String abortReason = "transaction "
				    + transactionNumber + " cannot get lock on " + dataName
				    + " since transaction " + lastTransactionInQueue + " had a lock on "
				    + dataName + " and is older.";
				abortTransaction(transactionNumber, abortReason);
				System.out.println("  > Transaction " + transactionNumber + " aborted! 1");
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
		    System.out.println("  > Transaction " + transactionNumber + " aborted! 2");
		    return;
		  }
		}
	  }
	}
  }
	
  /**
   * Prints all copies of all committed values of all variables at all sites ordered by site.
   */
  public void dump() {
	System.out.println("\nValue of Data at Sites");
	for (int site = 0; site < numberOfSites; site++) {
	  dump(site, null);
	}
  }
  
  /**
   * Prints all copies of all committed values of all variables at the specified site.
   * @param siteNumber the site to output.
   */
  public void dump(int siteNumber) {
	System.out.println("\nValue of Data at Site " + siteNumber);
	dump(siteNumber, null);
  }
  
  /**
   * Prints all copies of all committed values of the specified variable at all sites.
   * @param dataName the name of the data item to output.
   */
  public void dump(String dataName) {
	//System.out.println("DUMP called for data " + dataName);
	System.out.println("\nValue of " + dataName + " at All Sites");
	for (int site = 0; site < numberOfSites; site++) {
	  dump(site, dataName); 
	}
  }

  /**
   * Prints all copies of all committed values of the specified variable at the specified site.
   * @param siteNumber the site to output.
   * @param key the name of the data item to output.
   */
  public void dump(int siteNumber, String key) {
	System.out.println("<Site " + (siteNumber + 1) + ">");
	for (int data = sites[siteNumber].listOfData.size() - 1; data >= 0 ; data--) {
	  String currentDataName = sites[siteNumber].listOfData.get(data).name;
	  if (key == null || currentDataName.equals(key)) {
		System.out.println("  " + currentDataName);
		for (int value = 0; value < sites[siteNumber].listOfData.get(data).values.size(); value++) {
		  System.out.print("    " + sites[siteNumber].listOfData.get(data).values.get(value)
		      + ", modified at time " + sites[siteNumber].listOfData.get(data).modifiedTimes.get(value));
		  if (value == sites[siteNumber].listOfData.get(data).values.size() - 1
			  && sites[siteNumber].listOfData.get(data).isTemporary) {
			System.out.print(" (uncommitted)\n");
		  } else {
			System.out.print("\n");
		  }
		}
	  }
	}
	System.out.println();
  }
  
  /**
   * Determine whether the specified transaction can commit or not and clears its resources held.
   * @param time the time the operation was issued.
   * @param transactionNumber the transaction that issued the command.
   */
  public void end(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " wants to end at time " + time + ".");
	if (getTransaction(transactionNumber).commitSuccess != null
	    && getTransaction(transactionNumber).commitSuccess == false) {
      System.out.println("  > Transaction " + transactionNumber + " failed to commit.");
      return;
	}
	System.out.println("Transaction " + transactionNumber + " ends at time " + time + ".");
	ArrayList<Transaction.AccessPair> accessInformation =
	    getTransaction(transactionNumber).getAccessInformation();
	
	// Determine whether any sites the transaction accessed has failed since last access.
	for (int record = 0; record < accessInformation.size(); record++) {
	  int siteAccessed = accessInformation.get(record).site;
	  int timeAccessed = accessInformation.get(record).accessTime;
	  if (sites[siteAccessed].status.equals(Status.failed)
		  || ((sites[siteAccessed].lastRecoverTime != null)
		  && timeAccessed < sites[siteAccessed].lastRecoverTime)) {
		for (Transaction transaction : transactions) {
	      if (transaction.transactionNumber == transactionNumber) {
	    	transaction.commitSuccess = false;
	    	transaction.abortReason = "one or more of the sites accessed have failed since last access.";
	    	cleanUpAbortedTransaction(transactionNumber);
	    	System.out.println("  > Transaction " + transactionNumber + " failed to commit.");
	    	return;
	      }
		}
	  }
	}
	// Clear resources held by transaction.
	for (Transaction transaction : transactions) {
	  if (transaction.transactionNumber == transactionNumber) {
		transaction.commitSuccess = true;
		finalizeCommittedTransaction(transactionNumber);
		System.out.println("  > Transaction " + transactionNumber + " comitted successfully.");
		return;
	  }
	}
  }
	
  /**
   * Mark the specified site as failed at the specified time.
   * @param time the time the operation was issued.
   * @param siteNumber the site to operate on.
   */
  public void fail(int time, int siteNumber) {
	// Set the site's status to failed and set clear all locks in the lock table.
	System.out.println("Site " + siteNumber + " failed at time " + time + ".");
    sites[siteNumber - 1].status = Status.failed;
    Iterator i = sites[siteNumber - 1].lockTable.entrySet().iterator();
    HashMap.Entry value;
    while (i.hasNext()) {
      value = (HashMap.Entry)i.next();
      value.setValue(0);
    }
    
    // Mark all data in the site as inconsistent.
    for (int data = 0; data < sites[siteNumber - 1].listOfData.size(); data++) {
      sites[siteNumber - 1].listOfData.get(data).hasBeenWrittenToAfterRecovery = false;
    }
  }

  /**
   * Mark the specified site as recovered at the specified time.
   * @param time the time the operation was issued.
   * @param siteNumber the site to operate on.
   */
  public void recover(int time, int siteNumber) {
	// Set the site's status to active but not consistent and set the site's recovery time.
	Site currentSite = sites[siteNumber - 1];
	currentSite.status = Status.activeNotConsistent;
	currentSite.lastRecoverTime = time;
	System.out.println("Site " + siteNumber + " recovered at time " + time + ".");
  }
  
  /**
   * Output whether each transaction committed successfully or failed to commit.
   */
  public void printSummary() {
	System.out.println("\nCommit Summary");
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
  
  /**
   * Get a reference to the transaction specified.
   * @param transactionNumber the transaction to get.
   * @return a reference to the transaction specified.
   */
  public Transaction getTransaction(int transactionNumber) {
    for (Transaction transaction : transactions) {
      if (transaction.transactionNumber == transactionNumber) {
        return transaction;
      }
    }
    return null;
  }
  
  /**
   * Mark the specified transaction as aborted and indicate the abort reason.
   * @param transactionNumber the transaction to abort.
   * @param reason the reason the transaction was aborted.
   * @return whether the abortion was successful or not.
   */
  public boolean abortTransaction(int transactionNumber, String reason) {
    for (Transaction transaction : transactions) {
      if (transaction.transactionNumber == transactionNumber) {
    	transaction.commitSuccess = false;
    	transaction.abortReason = reason;
    	
    	cleanUpAbortedTransaction(transactionNumber);
    	
    	return true;
      }
    }
    return false;
  }
  
  /**
   * Free locks held and erase temporary values written by aborted transactions. 
   * @param transactionNumber the transaction aborted.
   */
  public void cleanUpAbortedTransaction(int transactionNumber) {
	// Remove all buffered operations from the transaction.
  	for (int operation = bufferOfOperations.size() - 1; operation >= 0; operation--) {
  	  if (bufferOfOperations.get(operation).transactionNumber == transactionNumber) {
  		bufferOfOperations.remove(operation);
  	  }
  	}
  	
  	// Free locks held by the transaction and erase the temporary values written.
  	for (int site = 0; site < numberOfSites; site++) {
  	  for (int data = 0; data < sites[site].listOfData.size(); data++) {
  		if (!sites[site].listOfData.get(data).waitingQueue.isEmpty()
  		    && sites[site].listOfData.get(data).waitingQueue.get(0) == transactionNumber) {
  		  Data currentData = sites[site].listOfData.get(data);
  		  sites[site].lockTable.put(currentData.name, 0);
  		  if (sites[site].listOfData.get(data).isTemporary) {
  			sites[site].listOfData.get(data).values.remove(currentData.values.size() - 1);
  			sites[site].listOfData.get(data).modifiedTimes.remove(currentData.modifiedTimes.size() - 1);
  		  }
  		  sites[site].listOfData.get(data).waitingQueue.remove(0);
  		  // The next transaction in line for the lock on a data item moves up and now holds the lock.
  		}
  	  }
  	}
  }
  
  /**
   * Free locks held by and make values written by the committed transaction permanent.
   * @param transactionNumber the transaction that was committed successfully.
   */
  public void finalizeCommittedTransaction(int transactionNumber) {
	if (getTransaction(transactionNumber).transactionType == "readonly") {
	  return;
	}
	for (int site = 0; site < numberOfSites; site++) {
	  for (int data = 0; data < sites[site].listOfData.size(); data++) {
		if (!sites[site].listOfData.get(data).waitingQueue.isEmpty()
		    && sites[site].listOfData.get(data).waitingQueue.get(0) == transactionNumber) {
		  Data currentData = sites[site].listOfData.get(data);
		  sites[site].lockTable.put(currentData.name, 0);
		  if (sites[site].listOfData.get(data).isTemporary) {
			sites[site].listOfData.get(data).isTemporary = false;
		  }
		  sites[site].listOfData.get(data).waitingQueue.remove(0);
		// The next transaction in line for the lock on a data item moves up and now holds the lock.
		}
	  }
	}
  }
  
  /**
   * Record the site and the time that the specified transaction accessed.
   * @param transactionNumber the transaction to update.
   * @param siteNumber the site that the transaction accessed.
   * @param accessTime the time the specified site was accessed by the transaction.
   */
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
  
  /**
   * Determine whether the specified transaction is committed or aborted, or neither.
   * @param transactionNumber the transaction to check.
   * @return whether the specified transaction is committed or aborted or neither.
   * @throws Exception if the transaction specified does not exist.
   */
  public boolean isTransactionCommittedOrAborted(int transactionNumber) throws Exception {
	for (Transaction transaction : transactions) {
	  if (transaction.transactionNumber == transactionNumber) {
		if (transaction.commitSuccess == null) {
	      return false;
		} else {
		  return true;
		}
	  }
	}
	throw new Exception("Operation from non-existent transaction: " + transactionNumber);
  }
}