package ADBFinalProject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

// Author : Aditya Gupta (ag4479)

public class Data 
{
  // This is the variable used for storing the different names of the data.
  String name;

  // This is the variable used for storing the value of the actual data.
  ArrayList<Integer> values;
	
  // This is the variable used for storing last time when the data was modified.
  ArrayList<Integer> modifiedTimes;
	
  // This boolean is used to check whether the data that is stored is temporary or not.
  boolean isTemporary;
	
  // This boolean is used to indicate whether the data has been replicated on other sites or not
  boolean isReplicated;
	
  // This boolean is used to indicate whether data has been written to a transaction site after recovery.
  boolean hasBeenWrittenToAfterRecovery;
	
  // This queue will store the transaction number which are waiting to be executed.
  Queue<Integer> waitingQueue;
	
  // A constructor which will allow us to pass three values and set the default values.
  public Data(String dataName, int value, int modifiedTime, boolean isReplicated) {
    this(dataName, value, modifiedTime, false, isReplicated, true);
  }
	
  // A Constructor which will initialise all the data members.
  public Data(String dataName, int value, int modifiedTime, boolean isTemporary, boolean isReplicated, boolean hasBeenWrittenToAfterRecovery) {
	values = new ArrayList<Integer>();
	modifiedTimes = new ArrayList<Integer>();
	waitingQueue = new LinkedList<Integer>();
		
	name = dataName;
	values.add(value);
	modifiedTimes.add(modifiedTime);
	this.isTemporary = isTemporary;
	this.isReplicated = isReplicated;
	this.hasBeenWrittenToAfterRecovery = hasBeenWrittenToAfterRecovery;
  }
}