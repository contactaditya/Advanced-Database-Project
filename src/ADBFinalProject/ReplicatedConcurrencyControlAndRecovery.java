/**
 * Shikuan Huang, Aditya Gupta
 * Advanced Database Systems
 * Professor Dennis Shasha
 * 8, December 2015
 * 
 * Replicated Concurrency Control and Recovery Project
 */
package ADBFinalProject;

import java.util.ArrayList;

import ADBFinalProject.Site;

/***
 * Performs the replicated concurrency control and recovery simulation.
 * @author Shikuan Huang
 */
public class ReplicatedConcurrencyControlAndRecovery {
  public static void main(String[] args) {
	/* Set up sites 1 to 10
	 * Assign data x1 to x20 initial values
	 * Put data in sites
	 */
	ArrayList<Site> sites = new ArrayList<Site>(10);
	for (int siteNumber = 0; siteNumber < 10; siteNumber++) {
	}
	  
	/*
	 * If an input file is specified
	 *   Set inputFile flag
	 * While more input
	 *   Read input from appropriate source
	 *     If input file provided
	 *       read from file
	 *     Else
	 *       read from standard input
	 *   Convert input and store each operation in an array of instructions
	 *   For each operation in array of instructions
	 *     switch operation
	 *       case read:
	 *         call transactionManager's read()
	 *       case write:
	 *         call transactionManager's write()
	 *       case begin:
	 *         create a new transaction T
	 *       case end:
	 *         call transaction manager's end()
	 *       case dump:
	 *         call transaction manager's appropriate dump
	 *       case fail:
	 *         call transaction manager's fail()
	 *       case recover:
	 *         call transaction manager's recovered()
	 */
  }
}
