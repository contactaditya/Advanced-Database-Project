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
  public Status status;
  public ArrayList<Data> listOfData;
  public HashMap<String, Integer> lockTable;
  public Integer lastFailedTime;

  /***
   * Create a new site with default values.
   */
  public Site() {
    status = Status.activeAndConsistent;
    listOfData = new ArrayList<Data>();
    lockTable = new HashMap<String, Integer>();
    lastFailedTime = null;
  }
  
  public void addData(Data dataToAdd) {
	listOfData.add(dataToAdd);
  }
  
  public boolean lockData(String dataName) throws Exception {
	if (isDataLocked(dataName) == 1) {
      return false;
	}
	lockTable.put(dataName, 1);
	return true;
  }
  
  public int isDataLocked(String dataName) throws Exception {
	if (lockTable.get(dataName) != null && lockTable.get(dataName) == 0) {
	  return 0;
	} else if (lockTable.get(dataName) != null && lockTable.get(dataName) != 0) {
	  return 1;
	} else if (lockTable.get(dataName) == null) {
	  return -1;
	} else {
	  throw new Exception("Site.isDataLocked(String dataName) is in an invalid state!");
	}
  }
  
  public Data getData(String dataName) {
    for (int data = 0; data < listOfData.size(); data++) {
      if (listOfData.get(data).name.equals(dataName)) {
        return listOfData.get(data);
      }
    }
    return null;
  }
  
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
}