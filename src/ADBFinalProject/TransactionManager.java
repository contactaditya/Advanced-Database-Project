package ADBFinalProject;

import java.util.ArrayList;

import ADBFinalProject.Data;
import ADBFinalProject.Transaction;

//Author : Aditya Gupta (ag4479)

public class TransactionManager {
	
	
	ArrayList<Operation> bufferOfOperations = new ArrayList<Operation>();
	ArrayList<Site> sites = new ArrayList<Site>();
	
	public int read(Transaction transaction, String data) throws Exception{
		Data sitewithdata = null;
		
		for(int i =0; i < sites.size();i++)
		{
			try{
			Site currentSite = sites.get(i);
			for(Data d : currentSite.listOfData)
			{
		      if(d.dataName.equals(data))
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
			for(int i : sitewithdata.modifiedTime)
			{
				if(i < transaction.arrivalTime) {
					return sitewithdata.dataValue[i];
				}
			}
			
		}
			
			return 5;
	}
	
	
	public void write(Transaction transaction, Data data, int value){
		
	}
	
	public void end(Transaction transaction){
		
	}
	
	public void dump()
	{
		
	}
	
	public void dump(int x)
	{
		
	}
	
	public void dump(String s)
	{
		
	}
	
	public void fail(int site)
	{
		
	}
	
	public void recover(int recover)
	{
		
	}
	
}
	
	

