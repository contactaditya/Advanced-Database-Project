package ADBFinalProject;

import ADBFinalProject.Status;

import java.util.HashMap;
import java.util.LinkedList;

/***
 * A site in the simulation.
 * @author Shikuan Huang
 */
public class Site {
  public Status status;
  public LinkedList<Integer> listOfData;
  public HashMap<String, Integer> lockTable;
  public Integer lastFailedTime;

  /***
   * Create a new site with default values.
   */
  public Site() {
    status = Status.activeAndConsistent;
    listOfData = new LinkedList<Integer>();
    lockTable = new HashMap<String, Integer>();
    lastFailedTime = null;
  }
}