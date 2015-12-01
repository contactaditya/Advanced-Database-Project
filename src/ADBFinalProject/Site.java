package ADBFinalProject;

import ADBFinalProject.Status;
import ADBFinalProject.Data;

import java.util.HashMap;
import java.util.LinkedList;

/***
 * A site in the simulation.
 * @author Shikuan Huang
 */
public class Site {
  public Status status;
  public LinkedList<Data> listOfData;
  public HashMap<String, Integer> lockTable;
  public Integer lastFailedTime;

  /***
   * Create a new site with default values.
   */
  public Site() {
    status = Status.activeAndConsistent;
    listOfData = new LinkedList<Data>();
    lockTable = new HashMap<String, Integer>();
    lastFailedTime = null;
  }
  
  public void addData(Data dataToAdd) {
	listOfData.add(dataToAdd);
  }
}