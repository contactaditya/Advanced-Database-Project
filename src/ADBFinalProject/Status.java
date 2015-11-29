package ADBFinalProject;

/***
 * Status for sites.
 * @author Shikuan Huang
 */
public enum Status {
  // The site is active and in a consistent state.
  activeAndConsistent,
  // The site has recovered and is not in a consistent state.
  activeNotConsistent,
  // The site has failed.
  failed,
}