package ADBFinalProject;

import java.util.ArrayList;

public class Data 
{
  /**
   * The name of the data item.
   */
  String name;

  /**
   * The committed and temporary values of the data item.
   */
  ArrayList<Integer> values;
	 
  /**
   * The times when the committed and temporary values of the data item were written.
   */
  ArrayList<Integer> modifiedTimes;
	
  /**
   * Whether the last value written is temporary or not.
   */
  boolean isTemporary;
	
  /**
   * Whether the data has been replicated on other sites or not.
   */
  boolean isReplicated;
	
  /**
   * Indicates whether data has been written to after the site recovered.
   */
  boolean hasBeenWrittenToAfterRecovery;
	
  /**
   * The list of transactions waiting for the lock on the data item.
   */
  ArrayList<Integer> waitingQueue;
	
  /**
   * Create a new data item with the specified name, value, initial time modified, and whether the data.
   * item is replicated or not.
   * @param dataName the name of the data item.
   * @param value the value of the data item.
   * @param modifiedTime the initial modified time of the data item.
   * @param isReplicated whether the data item is replicated on other sites or not.
   */
  public Data(String dataName, int value, int modifiedTime, boolean isReplicated) {
    this(dataName, value, modifiedTime, false, isReplicated, true);
  }
	
  /**
   * Create a new data item with the specified name, value, initial time modified, whether the data item is
   * replicated or not, and whether the data has been written to after recovery.
   * @param dataName the name of the data item.
   * @param value the value of the data item.
   * @param modifiedTime the initial modified time of the data item.
   * @param isTemporary whether the value of the data item is temporary or not.
   * @param isReplicated whether the data item is replicated on other sites or not. 
   * @param hasBeenWrittenToAfterRecovery the default value of whether the data item has been written to
   * after recovery.
   */
  public Data(String dataName, int value, int modifiedTime, boolean isTemporary, boolean isReplicated, boolean hasBeenWrittenToAfterRecovery) {
	values = new ArrayList<Integer>();
	modifiedTimes = new ArrayList<Integer>();
	waitingQueue = new ArrayList<Integer>();
		
	name = dataName;
	values.add(value);
	modifiedTimes.add(modifiedTime);
	this.isTemporary = isTemporary;
	this.isReplicated = isReplicated;
	this.hasBeenWrittenToAfterRecovery = hasBeenWrittenToAfterRecovery;
  }
  
  /**
   * Write the specified value to the data item.
   * @param modifiedTime the time the value was written.
   * @param value the value to write to the data item.
   */
  public void write(int modifiedTime, int value) {
	values.add(value);
	modifiedTimes.add(modifiedTime);
	isTemporary = true;
  }
}