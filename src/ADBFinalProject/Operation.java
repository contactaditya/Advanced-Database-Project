package ADBFinalProject;

/***
 * A database operation in the simulation.
 * @author Shikuan Huang
 *
 */
public class Operation {
  public int transactionNumber;
  public String operationType;
  public String nameOfDataToOperateOn;
  public Integer valueToWrite;
  public int waitingLine;
  
  /***
   * Construct an operation object with a transaction number, the type of operation, and the name
   * of the data to read or write.
   * @param transactionNum the transaction number.
   * @param type the type of transaction.
   * @param data the name of the data to read from or write to.
   */
  public Operation(int transactionNum, String type, String dataName) {
	this(transactionNum, type, dataName, null);
  }
  
  /***
   * Construct an operation object with a transaction number, the type of operation, the name of
   * the data to read or write, and the value to write.
   * @param transactionNum the transaction number.
   * @param type the type of transaction.
   * @param data the name of the data
   * @param value the value to write to the data specified.
   */
  public Operation(int transactionNum, String type, String dataName, Integer value) {
    transactionNumber = transactionNum;
    operationType = type;
    nameOfDataToOperateOn = dataName;
    valueToWrite = value;
    waitingLine = -1;
  }
}
