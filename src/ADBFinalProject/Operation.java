package ADBFinalProject;

/***
 * A database operation in the simulation.
 * @author Shikuan Huang
 */

 public class Operation {
   /**
    * The number of the transaction.
    */	
    public int transactionNumber;
  
   /**
    * The type of the transaction.
    */	
    public String transactionType;
  
   /**
    * The type of the operation.
    */
    public String operationType;
  
   /**
    * The name of the data item in the operation.
    */
    public String dataName;
  
   /**
    * The value that needs to be written.
    */
    public Integer valueToWrite;
  
   /***
    * Construct an operation object with a transaction number, the type of operation, and the name
    * of the data to read or write.
    * @param transactionNum the transaction number.
    * @param transType the type of transaction.
    * @param opType the type of operation.
    * @param dataToOperateOn the name of the data to read from or write to.
    */
  
    public Operation(int transactionNum, String transType, String opType, String dataToOperateOn) {
      this(transactionNum, transType, opType, dataToOperateOn, null);
    }
  
   /***
    * Construct an operation object with a transaction number, the type of operation, the name of
    * the data to read or write, and the value to write.
    * @param transactionNum the transaction number.
    * @param transType the type of transaction.
    * @param opType the type of operation.
    * @param dataToOperateOn the name of the data
    * @param value the value to write to the data specified.
    */
    public Operation(int transactionNum, String transType, String opType, String dataToOperateOn, Integer value) {
      transactionNumber = transactionNum;
      transactionType = transType;
      operationType = opType;
      dataName = dataToOperateOn;
      valueToWrite = value;
    }
 }
