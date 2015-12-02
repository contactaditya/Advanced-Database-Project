/**
 * Shikuan Huang, Aditya Gupta
 * Advanced Database Systems
 * Professor Dennis Shasha
 * 8, December 2015
 * 
 * Replicated Concurrency Control and Recovery Project
 */
package ADBFinalProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/***
 * Performs the replicated concurrency control and recovery simulation.
 * @author Shikuan Huang
 */

public class ReplicatedConcurrencyControlAndRecovery {
  public static void main(String[] args) throws Exception {
	int numberOfSites = 10;
	TransactionManager transactionManager = new TransactionManager(numberOfSites);
	
	// Initialize sites with data.
	for (int dataNumber = 1; dataNumber <= 20; dataNumber++) {
	  String dataName = "x" + String.valueOf(dataNumber);
	  int initialValue = 10 * dataNumber;
	  
	  if (dataNumber % 2 != 0) {
		// Initialize odd indexed data.
		boolean isReplicated = false;
		int siteNumberToInsert = dataNumber % numberOfSites;
		transactionManager.initializeDataAtSite(siteNumberToInsert, dataName, initialValue, isReplicated);
		//System.out.println(dataName + "(" + isReplicated + ")" + " with value " + initialValue + " added to site " + (siteNumberToInsert + 1));
	  }
	  else {
		// Initialize even indexed data.
		boolean isReplicated = true;
		for (int site = 0; site < numberOfSites; site++) {
		  transactionManager.initializeDataAtSite(site, dataName, initialValue, isReplicated);
		  //System.out.println(dataName + "(" + isReplicated + ")" + " with value " + initialValue + " added to site " + (site + 1));
		}
	  }
	}

	// Read a line of input and call transaction manager to process it.
	Scanner input = null;
	
	if (args.length > 0) {
	  File inputFile = new File(args[0]);
	  input = new Scanner(inputFile);
	} else {
      input = new Scanner(System.in);
	}
	
	int time = 1;
	
	while (input.hasNextLine()) {
	  ArrayList<Operation> operationList = new ArrayList<Operation>();
	  String inputLine = input.nextLine();
	  String[] commands = inputLine.replaceAll("\\s+", "").split(";");
	  for (int currentCommand = 0; currentCommand < commands.length; currentCommand++) {
		String command = commands[currentCommand];
		String[] commandTokens = command.split("[,\\(\\)]");
		String commandType = commandTokens[0];
		switch(commandType) {
		  case "begin":
			String transactionName = commandTokens[1];
			int transactionNumber = Integer.parseInt(
			    transactionName.substring(1, transactionName.length()));
			transactionManager.begin(time, transactionNumber);
			break;
		  case "beginRO":
			transactionName = commandTokens[1];
			transactionNumber = Integer.parseInt(
			    transactionName.substring(1, transactionName.length()));
			transactionManager.beginRO(time, transactionNumber);
			break;
		  case "R":
			transactionName = commandTokens[1];
			transactionNumber = Integer.parseInt(
				transactionName.substring(1, transactionName.length()));
			String dataName = commandTokens[2];
			transactionManager.read(time, transactionNumber, dataName);
			break;
		  case "W":
			transactionName = commandTokens[1];
		    transactionNumber = Integer.parseInt(
			  transactionName.substring(1, transactionName.length()));
		    dataName = commandTokens[2];
		    int value = Integer.parseInt(commandTokens[3]);
		    transactionManager.write(time, transactionNumber, dataName, value);
			break;
		  case "dump":
			if (commandTokens.length == 1) {
			  transactionManager.dump();
			} else if (commandTokens[1].length() == 1) {
		      transactionManager.dump(1);
			} else if (commandTokens[1].length() == 2) {
		      transactionManager.dump(commandTokens[1]);
			} else {
			  throw new Exception("Invalid arguments to dump!");
			}
			break;
		  case "end":
			transactionName = commandTokens[1];
            transactionNumber = Integer.parseInt(
		        transactionName.substring(1, transactionName.length()));
            transactionManager.end(time, transactionNumber);
			break;
		  case "fail":
			transactionName = commandTokens[1];
	        transactionNumber = Integer.parseInt(
			    transactionName.substring(1, transactionName.length()));
	        transactionManager.fail(time, transactionNumber);
			break;
		  case "recover":
			transactionName = commandTokens[1];
		    transactionNumber = Integer.parseInt(
				transactionName.substring(1, transactionName.length()));
		    transactionManager.fail(time, transactionNumber);
			break;
		  default:
			throw new Exception("Invalid command type!");
		}
	  }
	  time++;
	}

	// Output whether each transaction committed successfully or failed to commit.
	transactionManager.printSummary();
	
	input.close();
  }
}
