
package gitlet;
import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import static gitlet.Utils.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Haoqing Xuan
 */

public class Repo implements Serializable {

    private static final long serialVersionUID = 1893683620536302203L;
    /** hashmap store the branches and their commit ids.  */
    private HashMap<String, String> gitBranches;
    /** track the current commit id. */
    private String currentCommit;
    /** track the current branch name. */
    private String currentBranchName;
    /** track the current commit id. */
    private String head;
    /** arraylist that track all commits. */
    private ArrayList<String> commitHistory;
    /** the current commit. */
    private Commit recentCommit;
    /** the first parent of this commit. */
    private String firstParentSHA;
    /** the second parent of this commit. */
    private String secondParentSHA;

    /** set up directories for gitlet.  */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** set up directories for gitlet.  */
    private static final File GIT_DIR = new File(CWD, ".gitlet");
    /** set up directories for gitlet.  */
    private static final File COMMIT_DIR = new File(".gitlet/commits");
    /** set up directories for gitlet.  */
    private static final File BLOB_DIR = new File(".gitlet/blobs");
    /** set up directories for gitlet.  */
    private static final File BRANCH_DIR = new File(".gitlet/branches");
    /** set up directories for gitlet.  */
    private static final File STAGE_DIR = new File(".gitlet/stage");
    /** set up directories for gitlet.  */
    private static final File STAGE_ADD = join(STAGE_DIR, "/add");
    /** set up directories for gitlet.  */
    private static final File STAGE_RM = join(STAGE_DIR, "/rm");


    /** search for the current commit stored in the commit directory.
     * @return commit. */
    private Commit searchCommit() {
        return Utils.readObject(join(COMMIT_DIR, "/", currentCommit),
                Commit.class);
    }
    /** search for the current commit
     * stored in the commit directory.
     * @param commitID  given ID
     * @return commit. */
    private Commit searchCommit(String commitID) {
        File commit = new File(".gitlet/commits/" + commitID);
        return Utils.readObject(commit, Commit.class);
    }


    /** initialize the gitlet directory by making
     * directories and call the first commit with given
     * commit message with no previous commit record.*/
    public void init() {
        if (GIT_DIR.exists()) {
            System.out.println("A Gitlet version-control system"
                    + "already exists in the current directory.");
        } else {
            GIT_DIR.mkdir();
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();
            BRANCH_DIR.mkdir();
            STAGE_DIR.mkdir();
            STAGE_ADD.mkdir();
            STAGE_RM.mkdir();

            gitBranches = new HashMap<>();
            commitHistory = new ArrayList<>();
            Commit initialCommit = new Commit("initial commit", null);
            currentCommit = initialCommit.getCommitSHA();
            String initID = initialCommit.getCommitSHA();
            gitBranches.put("master", initID);
            saveCommit(initialCommit);
            commitHistory.add(initialCommit.getCommitSHA());
            currentBranchName = "master";
            head = initID;
            recentCommit = initialCommit;
        }
    }

    /** write the commit's id into the commit directory.
     * @param commit given commit. */
    public void saveCommit(Commit commit) {
        Utils.writeObject(join(COMMIT_DIR, "/", commit.getCommitSHA()), commit);
    }

    /** implement the add method, compare the current
     * blob SHA in commit with the blob SHA
     * from the previous commit and check whether
     * the file need to be removed from the
     * adding stage.
     * @param filename  given filename. */
    public void add(String filename) throws IOException {
        if (new File(filename).exists()) {
            byte[] contents = Utils.readContents(new File(filename));
            String contentSHA = Utils.sha1((Object) contents);
            File filesStage = join(STAGE_ADD, "/", filename);
            File removal = join(STAGE_RM, "/", filename);
            Blobs blobs = getCurrentCommit().getBlobs().get(filename);
            if (removal.exists()) {
                join(STAGE_RM, "/", filename).delete();
            } else if (filesStage.exists()) {
                Blobs previousBlobs = Utils.readObject(filesStage, Blobs.class);
                if (contentSHA.equals(previousBlobs.getContentID())) {
                    filesStage.delete();
                } else {
                    Blobs blob = new Blobs(new File(filename));
                    Utils.writeObject(filesStage, blob);
                }
            } else {
                if (getCurrentCommit().getBlobs().containsKey(filename)
                        && blobs.getContentID().equals(contentSHA)) {
                    join(STAGE_ADD, "/", filename).delete();
                } else {
                    Blobs blob = new Blobs(new File(filename));
                    Utils.writeObject(filesStage, blob);
                }
            }
        } else {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }






    /** implement the commit method
     *  consider both addition and removal conditions.
     *  @return commit.
     *  @param message given commit message. */
    public Commit commit(String message) {
        Commit newCommit = new Commit(message, searchCommit());
        if (Utils.plainFilenamesIn(STAGE_ADD).isEmpty()
                && Utils.plainFilenamesIn(STAGE_RM).isEmpty()) {
            System.out.println("No changes added to the commit.");
        }
        for (String filename
                : Objects.requireNonNull(plainFilenamesIn(STAGE_ADD))) {
            Blobs blob = Utils.readObject(join(STAGE_ADD, "/",
                    filename), Blobs.class);
            newCommit.getBlobs().put(filename, blob);
        }
        for (String filename
                : Objects.requireNonNull(plainFilenamesIn(STAGE_ADD))) {
            join(STAGE_ADD, "/", filename).delete();
        }
        for (String filename
                : Objects.requireNonNull(plainFilenamesIn(STAGE_RM))) {
            try {
                newCommit.getBlobs().remove(filename);
                join("./", filename).delete();
            } catch (NullPointerException exception) {
                String hello;
            }
        }
        for (String filename
                : Objects.requireNonNull(plainFilenamesIn(STAGE_RM))) {
            join(STAGE_RM, "/", filename).delete();
        }
        currentCommit = newCommit.getCommitSHA();
        head = newCommit.getCommitSHA();
        Utils.writeObject(join(COMMIT_DIR, "/",
                newCommit.getCommitSHA()), newCommit);
        gitBranches.put(currentBranchName, currentCommit);
        commitHistory.add(newCommit.getCommitSHA());
        recentCommit = newCommit;
        return newCommit;

    }
    /** set up the merge commit.
     * @return commit.
     * @param commitMessage given message.
     * @param firstParentID first parent id.
     * @param secondParentID  second parent id. */
    public Commit mergeCommit(String commitMessage,
                              String firstParentID, String secondParentID) {
        Commit commit = commit(commitMessage);
        commit.setFirstParentSHA(firstParentID);
        commit.setSecondParentSHA(secondParentID);
        return commit;
    }


    /** get the current commit.
     *@return commit.  */
    public Commit getCurrentCommit() {
        return searchCommit(head);
    }
    /** print all commit informations. */
    public void log() {
        Commit currCommit = searchCommit();
        while (currCommit.getParentSHA() != null) {
            System.out.println(currCommit);
            System.out.println();
            currCommit =
                    Utils.readObject(join(COMMIT_DIR, "/",
                                    currCommit.getParentSHA()),
                            Commit.class);
        }
        System.out.println(currCommit);
    }
    /** print all commits. */
    public void globalLog() {
        List<String> commits = Utils.plainFilenamesIn(COMMIT_DIR);
        assert commits != null;
        for (String commitSHA : commits) {
            Commit commit =
                    Utils.readObject(join(COMMIT_DIR, "/",
                            commitSHA), Commit.class);
            System.out.println(commit);
        }

    }
    /** checkout the given file.
     * @param filename given filename. */
    public void checkoutFileName(String filename) throws IOException {
        checkout(currentCommit, filename);
    }
    /** checkout the file in the commit.
     * @param filename given filename.
     * @param commitId given commit id. */
    public void checkout(String commitId, String filename) throws IOException {
        for (String commitSHA
                : Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR))) {
            if (commitSHA.startsWith(commitId)) {
                Commit commit =
                        Utils.readObject(join(COMMIT_DIR, "/", commitSHA),
                                Commit.class);
                if (!commit.getBlobs().containsKey(filename)) {
                    System.out.println("File does not exist in that commit.");
                } else {
                    Utils.writeContents(join("./", filename),
                            commit.getBlobs().get(filename).getContent());
                    recentCommit = commit;
                }
                return;
            }
        }
        System.out.println("No commit with that id exists.");
    }
    /** checkout the branch.
     * @param branchName  given branch name. */
    public void checkoutBranch(String branchName) throws IOException {
        String branchID = gitBranches.get(branchName);
        if (branchName.equals(currentBranchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else if (!gitBranches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }

        Commit branchCcmmit = searchCommit(branchID);
        List<String> fileNames = Utils.plainFilenamesIn("./");
        Map<String, Blobs> branchBlob = branchCcmmit.getBlobs();
        Map<String, Blobs> currentBlob = getCurrentCommit().getBlobs();

        for (String fileName : fileNames) {
            if (!currentBlob.containsKey(fileName)
                    && branchBlob.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way;"
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        for (String fileName : branchBlob.keySet()) {
            File targetFile = new File("./" + fileName);
            byte[] contents = branchBlob.get(fileName).getContent();
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            Utils.writeContents(targetFile, (Object) contents);
        }

        for (String fileName : currentBlob.keySet()) {
            if (!branchBlob.containsKey(fileName)
                    && currentBlob.containsKey(fileName)) {
                Utils.restrictedDelete(fileName);
            }
        }

        for (String fileName : Utils.plainFilenamesIn(STAGE_ADD)) {
            join(STAGE_ADD, "/", fileName).delete();
        }

        for (String fileName : Utils.plainFilenamesIn(STAGE_RM)) {
            join(STAGE_RM, "/", fileName).delete();
        }

        recentCommit = branchCcmmit;
        head = branchID;
        currentBranchName = branchName;
        currentCommit = branchID;

    }
    /** set up the branch.
     * @param branchName  given branch name. */
    public void branch(String branchName) {
        if (gitBranches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
        }
        gitBranches.put(branchName, head);


    }
    /** remove the branch.
     * @param branchName  given branch name. */
    public void removeBranch(String branchName) {
        if (!gitBranches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if (currentBranchName.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            gitBranches.remove(branchName);
        }
    }
    /** reset the directory to the given commit.
     * @param commitID  given commit id. */
    public void reset(String commitID) throws IOException {
        String commitId = "";
        for (String id : Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR))) {
            if (id.startsWith(commitID)) {
                commitId = id;
            }
        }
        if (commitId.equals("")) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Commit branchCcmmit = searchCommit(commitID);
        List<String> fileNames = Utils.plainFilenamesIn("./");
        Map<String, Blobs> branchBlob = branchCcmmit.getBlobs();
        Map<String, Blobs> currentBlob = getCurrentCommit().getBlobs();

        for (String fileName : fileNames) {
            if (branchBlob.containsKey(fileName)
                    && !currentBlob.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way;"
                        + "delete it, or add and commit it first.");
                return;
            }
        }
        for (String fileName : currentBlob.keySet()) {
            if (!branchBlob.containsKey(fileName)) {
                Utils.restrictedDelete(fileName);
            }
        }
        for (String filename : branchBlob.keySet()) {
            File targetFile = new File("./" + filename);
            byte[] contents = branchBlob.get(filename).getContent();
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            Utils.writeContents(targetFile, contents);
        }
        for (String fileName : Utils.plainFilenamesIn(STAGE_ADD)) {
            join(STAGE_ADD, "/", fileName).delete();
        }

        for (String fileName : Utils.plainFilenamesIn(STAGE_RM)) {
            join(STAGE_RM, "/", fileName).delete();
        }
        recentCommit = searchCommit(commitID);
        head = commitID;
    }
    /** remove a certain file.
     * @param fileName  given filename. */
    public void remove(String fileName) {
        File filesRemove = join(STAGE_RM, "/", fileName);
        Map<String, Blobs> blobs = getCurrentCommit().getBlobs();
        if (Utils.plainFilenamesIn(STAGE_ADD).contains(fileName)) {
            join(STAGE_ADD, "/", fileName).delete();
        } else if (blobs.containsKey(fileName)) {
            if (!Utils.plainFilenamesIn(STAGE_RM).contains(fileName)
                    || !new File(fileName).exists()) {
                Utils.writeContents(filesRemove, "delete");
            }
            join("./", fileName).delete();
        } else if (!Utils.plainFilenamesIn(STAGE_ADD).contains(fileName)) {
            System.out.println("No reason to remove the file.");
            return;
        }
    }
    /** find the commit id from branchname.
     * @param message given commit message. */
    public void find(String message) {
        ArrayList<String> goodCommits = new ArrayList<>();
        for (String commitID : commitHistory) {
            if (searchCommit(commitID).getCommitMessage().equals(message)) {
                goodCommits.add(commitID);
            }
        }
        if (!goodCommits.isEmpty()) {
            for (String commitID : goodCommits) {
                System.out.println(commitID);
            }
        } else {
            System.out.println("Found no commit with that message.");
        }
    }
    /** print out information of each class. */
    public void status() {


        printTitle("Branches");
        for (String branchname : gitBranches.keySet()) {
            if (currentBranchName.equals(branchname)) {
                System.out.println("*" + branchname);
            } else {
                System.out.println(branchname);
            }
        }
        System.out.println();


        printTitle("Staged Files");
        for (String stagedfiles
                : Objects.requireNonNull(plainFilenamesIn(STAGE_ADD))) {
            System.out.println(stagedfiles);
        }
        System.out.println();


        printTitle("Removed Files");
        for (String removingFiles
                : Objects.requireNonNull(plainFilenamesIn(STAGE_RM))) {
            System.out.println(removingFiles);
        }
        System.out.println();


        printTitle("Modifications Not Staged For Commit");
        for (String fileName : modifiedHelper().keySet()) {
            System.out.println(fileName + " " + modifiedHelper().get(fileName));
        }
        System.out.println();

        printTitle("Untracked Files");
        for (String fileName : untracked()) {
            System.out.println(fileName);
        }
    }
    /** track modified files.
     * @return hashmap of modified files. */
    public HashMap<String, String> modifiedHelper() {
        Commit commit = getCurrentCommit();
        Map<String, Blobs> blobs = commit.getBlobs();
        List<String> fileNames = Utils.plainFilenamesIn(new File("./"));
        List<String> removingList = Utils.plainFilenamesIn(STAGE_RM);
        HashMap<String, String> result = new HashMap<>();
        if (!fileNames.isEmpty()) {
            for (String name : fileNames) {
                String fileSHA =
                        Utils.sha1(Utils.readContents(new File("./" + name)));
                if (blobs.keySet().contains(name)) {
                    if (!blobs.get(name).getContentID().equals(fileSHA)) {
                        result.put(name, "(modified)");
                    }
                }
            }
        }
        for (String filename : blobs.keySet()) {
            if (!fileNames.contains(filename)) {
                assert removingList != null;
                if (!removingList.contains(filename)) {
                    result.put(filename, "(deleted)");
                }
            }
        }
        return result;
    }
    /** store untracked files.
     * @return arraylist of untrack files. */
    public ArrayList<String> untracked() {
        Commit commit = getCurrentCommit();
        Map<String, Blobs> blobs = commit.getBlobs();
        List<String> addingList = Utils.plainFilenamesIn(STAGE_ADD);
        List<String> fileNames =
                Utils.plainFilenamesIn("./");
        ArrayList<String> result = new ArrayList<>();
        if (!fileNames.isEmpty()) {
            for (String filename : fileNames) {
                if (!blobs.keySet().contains(filename)) {
                    assert addingList != null;
                    if (!addingList.contains(filename)) {
                        result.add(filename);
                    }
                }
            }
        }
        return result;
    }
    /** printing help method.
     * @param statusName given status. */
    public void printTitle(String statusName) {
        System.out.println("=== " + statusName + " ===");
    }
    /**
     public void merge(String branchName) throws IOException {
     List<String> untracked = untracked();
     HashMap<String, String> modified = modifiedHelper();
     Set<String> allFiles = new HashSet<>();
     String branchB, currentB, splitB;
     Boolean encounterConflict = false;


     if (!Utils.plainFilenamesIn(STAGE_ADD).isEmpty()
     && !Utils.plainFilenamesIn(STAGE_RM).isEmpty()) {
     System.out.println("You have uncommitted changes.");
     return;
     }
     if (!gitBranches.containsKey(branchName)) {
     System.out.println("A branch with that name does not exist.");
     return;
     }
     if (currentBranchName.equals(branchName)) {
     System.out.println("Cannot merge a branch with itself.");
     return;
     }
     if (!untracked.isEmpty() && !modified.isEmpty()) {
     System.out.println("There is an untracked file in the way;
     delete it, or add and commit it first.");
     return;
     }

     Commit branchCommit = searchCommit(gitBranches.get(branchName));
     Commit currentCommit = getCurrentCommit();
     Commit splitCommit =
     searchCommit(getSplitIDHelper(branchCommit.getCommitSHA(),
     currentCommit.getCommitSHA()));

     if (splitCommit.getCommitSHA().equals(branchCommit.getCommitSHA())) {
     System.out.println("Given branch is an
     ancestor of the current branch.");
     return;
     }

     if (splitCommit.getCommitSHA().equals(currentCommit.getCommitSHA())) {
     checkoutBranch(branchName);
     System.out.println("Current branch fast-forwarded.");
     }

     Map<String, Blobs> branchBlob = branchCommit.getBlobs();
     Map<String, Blobs> currentBlob = currentCommit.getBlobs();
     Map<String, Blobs> splitBlob = splitCommit.getBlobs();

     allFiles.addAll(branchBlob.keySet());
     allFiles.addAll(currentBlob.keySet());
     allFiles.addAll(splitBlob.keySet());

     for (String filename : allFiles) {
     branchB = branchBlob.get(filename).getContentID();
     currentB = currentBlob.get(filename).getContentID();
     splitB = splitBlob.get(filename).getContentID();

     if (branchB != null && currentB != null && splitB != null) {
     if (!branchB.equals(splitB) && currentB.equals(splitB)) {
     checkout(branchCommit.getCommitSHA(), filename);
     add(filename);
     } else if (!currentB.equals(splitB)
     && branchB.equals(splitB)) {
     String hello;
     } else if (currentB.equals(splitB)
     && branchB.equals(currentB)) {
     String hello;
     } else if (!currentB.equals(splitB)
     && !branchB.equals(splitB)) {
     conflictHelper(filename, branchB, currentB);
     encounterConflict = true;
     }
     } else if (branchB == null && currentB != null && splitB != null) {
     if (currentB.equals(splitB)) {
     remove(filename);
     } else if (!currentB.equals(splitB)) {
     conflictHelper(filename, null, currentB);
     encounterConflict = true;
     }
     } else if (branchB != null && currentB == null && splitB != null) {
     if (branchB.equals(splitB)) {
     String hello;
     } else if (!branchB.equals(splitB)) {
     conflictHelper(filename, branchB, null);
     encounterConflict = true;
     }
     } else if (branchB != null && currentB != null && splitB == null) {
     if (branchB.equals(currentB)) {
     String hello;
     } else if (!branchB.equals(currentB)) {
     conflictHelper(filename, branchB, currentB);
     encounterConflict = true;
     }
     } else if (branchB == null && currentB != null && splitB == null) {
     String hello;
     } else if (branchB != null && currentB == null && splitB == null) {
     checkout(branchCommit.getCommitSHA(), filename);
     add(filename);
     } else if (branchB == null && currentB == null && splitB != null) {
     String hello;
     }
     }
     logHelper(branchName, encounterConflict);




     }

     public ArrayList<String> parentCommitsHelper(String commitID) {
     assert commitID != null;
     ArrayList<String> parentCommits = new ArrayList<>();
     ArrayList<String> temp = new ArrayList<>();
     Commit currentCommit =
     Utils.readObject(new File("./.gitlet/Commits/" + commitID),
     Commit.class);
     if (currentCommit == null) {
     return parentCommits;
     }

     if (currentCommit.isMerged()) {
     temp = parentCommitsHelper(currentCommit.getSecondParentSHA());
     }
     ArrayList<String> temp1 =
     parentCommitsHelper(currentCommit.getParent());
     parentCommits.add(commitID);
     parentCommits.addAll(temp1);
     if (!temp.isEmpty()) {
     parentCommits.addAll(temp);
     }
     return parentCommits;
     }

     public String getSplitIDHelper(String ID1, String ID2) {
     ArrayList<String> ancestors = parentCommitsHelper(ID1);
     ArrayList<String> secondIDTree = new ArrayList<>();
     ArrayList<String> splits = new ArrayList<>();
     String commitID;
     secondIDTree.add(ID2);

     while (!secondIDTree.isEmpty()) {
     commitID = secondIDTree.get(0);
     secondIDTree.remove(0);
     Commit commit =
     Utils.readObject(new File("./.gitlet/Commits/" + commitID),
     Commit.class);
     if (ancestors.contains(commit.getCommitSHA())) {
     return commit.getCommitSHA();
     }
     if (!splits.contains(commit.getCommitSHA())) {
     secondIDTree.add(commit.getParent());
     if (commit.isMerged()) {
     secondIDTree.add(commit.getSecondParentSHA());
     }
     splits.add(commitID);
     }
     }
     return null;


     }



     public void conflictHelper(String filename,
     String branchB, String currentB) throws IOException {
     File branchFile = new File("./.gitlet/Blobs/" + branchB);
     File currentFile = new File("./.gitlet/Blobs/" + currentB);
     String branchcontent = "", currentcontent = "";
     if (branchFile.exists()) {
     branchcontent = Utils.readContentsAsString(branchFile);
     }
     if (currentFile.exists()) {
     currentcontent = Utils.readContentsAsString(currentFile);
     }
     Utils.writeContents(new File(filename),
     "<<<<<<< HEAD\n" + currentcontent
     + "=======\n" + branchcontent + ">>>>>>>\n");
     add(filename);
     }

     public void logHelper(String givenBranch, Boolean encounterConflict) {
     String message = "Merged " + givenBranch + " into "
     + currentBranchName + ".";
     Commit mergeCommit =
     mergeCommit(message, getCurrentCommit().getCommitSHA(),
     gitBranches.get(givenBranch));
     String mergeID = Utils.sha1(Utils.serialize(mergeCommit));
     commitHistory.add(mergeID);
     Utils.writeObject(join("./.gitlet/commits", "/", mergeID), mergeCommit);
     head = mergeID;
     gitBranches.put(currentBranchName, mergeID);
     if (encounterConflict) {
     System.out.println("Encountered a merge conflict.");
     }
     } */



}
