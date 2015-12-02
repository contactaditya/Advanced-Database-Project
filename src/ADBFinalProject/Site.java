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
}