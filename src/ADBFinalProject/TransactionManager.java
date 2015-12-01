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

  public TransactionManager(int numOfSites) {
	transactions = new LinkedList<Transaction>();
	numberOfSites = numOfSites;
    sites = new Site[numOfSites];
    bufferOfOperations = new ArrayList<Operation>();
	for (int site = 0; site < numOfSites; site++) {
	  sites[site] = new Site();
	}
  }
  
  public void initializeDataAtSite(int siteNumber, String dataName, int dataValue, boolean isReplicated) {
	Data dataToAdd = new Data(dataName, dataValue, 0, isReplicated);
	sites[siteNumber].addData(dataToAdd);
  }
  
  public void begin(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " begins at time " + time);
  }
  
  public void beginRO(int time, int transactionNumber) {
	System.out.println("Transaction " + transactionNumber + " (readonly) begins at time " + time);
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
	System.out.println("Transaction " + transactionNumber + " wants to read " + dataName + " at time " + time);
  }
	
  public void write(int time, int transactionNumber, String dataName, int value) {	
	System.out.println("Transaction " + transactionNumber + " wants to write " + value + " to " + dataName + " at time " + time);
  }
	
  public void dump() {
	System.out.println("DUMP called");
  }
	
  public void dump(int siteNumber) {
	System.out.println("DUMP called for site " + siteNumber);
  }
	
  public void dump(String dataName) {
	System.out.println("DUMP called for data " + dataName);
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
	/*
	 * For all transactions
	 *   If commitSuccess
	 *     output successful commit
	 *   else
	 *     output abort reason
	 */
	System.out.println("*SUMMARY TO GO HERE*");
  }
}
	
	

