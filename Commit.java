package gitlet;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;


/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Haoqing Xuan
 */

public class Commit implements Serializable {
    /** the commit message. */
    private String commitMessage;
    /** the commit's SHA code. */
    private String commitSHA;
    /** the commit's date.*/
    private String commitDate;
    /** the hashmap that contains all filenames and
     * information within files in this commit. */
    private Map<String, Blobs> blobs;
    /** the current branch. */
    private String currentBranch;
    /** the SHA code of the previous commit. */
    private String parentSHA;
    /** the first parent of this commit. */
    private String firstParentSHA;
    /** the second parent of this commit. */
    private String secondParentSHA;
    /** returns the boolean indicating whether the commit is merged.*/
    private boolean merge = false;




    /** initiate the commit constructor.
     * @param message commit message
     * @param previousCommit the parent commit. */
    public Commit(String message, Commit previousCommit) {
        this.commitMessage = message;
        blobs = new HashMap<>();
        Date currentTime;
        if (previousCommit == null) {
            currentTime = new Date(0);
            this.currentBranch = "master";
            this.parentSHA = null;
        } else {
            currentTime = new Date();
            this.parentSHA = previousCommit.commitSHA;
            this.currentBranch = previousCommit.currentBranch;
            for (String k : previousCommit.blobs.keySet()) {
                this.blobs.put(k, previousCommit.blobs.get(k));
            }
        }
        commitDate = String.format("%ta %tb %td %tT %tY %tz",
                currentTime, currentTime, currentTime,
                currentTime, currentTime, currentTime);
        commitSHA = Utils.sha1(commitMessage + currentTime);
    }




    /** return commitMessage. */
    public String getCommitMessage() {
        return commitMessage;
    }
    /** return blobs. */
    public Map<String, Blobs> getBlobs() {
        return blobs;
    }
    /** return if the commit is merged. */
    public Boolean isMerged() {
        return merge;
    }
    /** return parentSHA. */
    public String getParent() {
        return parentSHA;
    }
    /** return CommitSHA. */
    public String getCommitSHA() {
        return commitSHA;
    }
    /** return parentSHA. */
    public String getParentSHA() {
        return parentSHA;
    }
    /** set firstParent.
     * @param firstParentID  first parent ID. */
    public void setFirstParentSHA(String firstParentID) {
        this.firstParentSHA = firstParentID;
    }
    /** set secondParent.
     * @param secondParentID  second parent id. */
    public void setSecondParentSHA(String secondParentID) {
        this.secondParentSHA = secondParentID;
    }
    /** return secondParent.
     * @return secondParentSHA.*/
    public String getSecondParentSHA() {
        return secondParentSHA;
    }

    /** toString helper.
     * @return formatter. */
    public String toString() {
        return "===\n"
                + "commit " + commitSHA + "\n"
                + "Date: " + commitDate + "\n"
                + commitMessage;
    }
}
