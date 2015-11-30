package ADBFinalProject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

// Author : Aditya Gupta (ag4479)

public class Data 
{

	// This is the variable used for storing the value of the actual data.
	int dataValue[];
	
	// This is the variable used for storing the different names of the data.
	String dataName;
	
	// This is the variable used for storing last time when the data was modified.
	int modifiedTime[];
	
	// This boolean is used to check whether the data that is stored is temporary or not.
	boolean isTemporary;
	
	// This boolean is used to indicate whether the data has been replicated on other sites or not
	boolean isReplicated;
	
	// This boolean is used to indicate whether data has been written to a transaction site after recovery.
	boolean hasBeenWrittenToAfterRecovery;
	
	// This queue will store the transaction number which are waiting to be executed.
	Queue<Integer> waitingQueue;
	
	// A constructor which will allow us to pass three values and set the default values.
	public Data(int dataValue[], String dataName, boolean isReplicated) {
		this(dataValue,dataName,null,false,isReplicated,false);
	}
	
	// A Constructor which will initialise all the data members.
	public Data(int dataValue[], String dataName, int[] modifiedTime, boolean isTemporary, boolean isReplicated, boolean hasBeenWrittenToAfterRecovery) {
		this.dataValue = dataValue;
		this.dataName = dataName;
		this.modifiedTime = modifiedTime;
		this.isTemporary = isTemporary;
		this.isReplicated = isReplicated;
		this.hasBeenWrittenToAfterRecovery = hasBeenWrittenToAfterRecovery;
		this.waitingQueue = new LinkedList<Integer>();
	}

}
