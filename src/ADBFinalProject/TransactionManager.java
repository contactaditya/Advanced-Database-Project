package ADBFinalProject;

import java.util.ArrayList;
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
  
  public void begin(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " begins at time " + time);
	String transactionType = "readwrite";
    Transaction newTransaction = new Transaction(transactionNumber, transactionType, time);
    transactions.add(newTransaction);
  }
  
  public void beginRO(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " (readonly) begins at time " + time);
	String transactionType = "readonly";
	Transaction newTransaction = new Transaction(transactionNumber, transactionType, time);
	transactions.add(newTransaction);
  }
	
  public int read(Transaction transaction, String data) throws Exception{
	  Data sitewithdata = null;
		
		for(int i =0; i < sites.length;i++)
		{
			try{
			Site currentSite = sites[i];
			for(Data d : currentSite.listOfData)
			{
		      if(d.name.equals(data))
		      {
		    	 sitewithdata = d; 
		      }
			}
			}
			catch(Exception e) {
			System.out.println(e);	
			}
		}
		
		if(transaction.transactionType.equals("readOnly")){
			for(int i : sitewithdata.modifiedTimes)
			{
				if(i < transaction.arrivalTime) {
					
					return sitewithdata.values.get(i);
				}
			}
			
		}
			
			return 5;
  }

  public void read(int time, int transactionNumber, String dataName) {
	read(time, transactionNumber, dataName, -1);
  }
	
  public void read(int time, int transactionNumber, String dataName, int lastSiteChecked) {
	System.out.println("Transaction " + transactionNumber + " wants to read " + dataName + " at time " + time);
	
	// Find a site that contains D
	int currentSiteToCheck = lastSiteChecked++;
	if (currentSiteToCheck == numberOfSites) {
	  // DATA NOT FOUND IN ANY SITE
	}
	for (int site = 0; site < numberOfSites; site++) {
	  Site currentSite = sites[site];
	  for (Data d : currentSite.listOfData) {
		if (d.name.equals(dataName));
	  }
	}
  }
	
  public void write(int time, int transactionNumber, String dataName, int value) {	
	System.out.println("Transaction " + transactionNumber + " wants to write " + value + " to " + dataName + " at time " + time);
	
  }
	
  public void dump() {
	System.out.println("DUMP called");
	
	for (int site = 0; site < numberOfSites; site++) {
	  dump(site, null);
	}
  }
  
  public void dump(int siteNumber) {
	System.out.println("DUMP called for site " + siteNumber);
	dump(siteNumber, null);
  }
  
  public void dump(String dataName) {
	System.out.println("DUMP called for data " + dataName);
	for (int site = 0; site < numberOfSites; site++) {
	  dump(site, dataName); 
	}
  }
	
  public void dump(int siteNumber, String key) {
	System.out.println("Site " + siteNumber);
	for (int data = 0; data < sites[siteNumber].listOfData.size(); data++) {
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
	System.out.println("Transaction " + transactionNumber + " ends at time " + time);
  }
	
  public void fail(int time, int siteNumber) {
	System.out.println("Site " + siteNumber + " failed at time " + time);
  }
	
  public void recover(int time, int siteNumber) {
	System.out.println("Site " + siteNumber + " recovered at time " + time);
  }
  
  public void printSummary() {
	// Output whether each transaction committed successfully or failed to commit.
	System.out.println("Commit Summary");
	for (Transaction transaction : transactions) {
	  if (transaction.commitSuccess) {
	    System.out.println("Transaction " + transaction.transactionNumber + " committed successfully.");
	  } else {
		System.out.println("Transaction " + transaction.transactionNumber + " aborted because " +
	        transaction.abortReason);
	  }
	}
  }
}
	
	

