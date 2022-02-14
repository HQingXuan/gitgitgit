package gitlet;
import java.io.File;
import java.io.IOException;


/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Haoqing Xuan
 */
public class Main {
    /** current directory pathname. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** gitlet directory pathname. */
    private static final File GITLET_DIR = new File(CWD, ".gitlet");
    /** repo directory pathname. */
    private static final File REPO = Utils.join(GITLET_DIR, "/", "repo");

    /** run commands with different names.
     * @param args given input. */
    public static void main(String... args) throws IOException {
        String input = "";
        if (args != null && args.length != 0) {
            input = args[0];
        } else {
            System.out.println("Please enter a command");
            return;
        }
        Repo command;
        if (input.equals("init")) {
            command = new Repo();
            command.init();
        } else {
            if (!GITLET_DIR.exists()) {
                System.out.println("Not in an initialized Gitlet directory.");
                return;
            }
            command = (Repo) Utils.readObject(REPO, Repo.class);
            switch (input) {
            case "add":
                addhelper(command, args);
                break;
            case "commit":
                commithelper(command, args);
                break;
            case "log":
                loghelper(command, args);
                break;
            case "global-log":
                globalLogHelper(command, args);
                break;
            case "checkout":
                checkoutHelper(command, args);
                break;
            case "rm":
                rmHelper(command, args);
                break;
            case "find":
                findHelper(command, args);
                break;
            case "status":
                statusHelper(command, args);
                break;
            case "branch":
                branchHelper(command, args);
                break;
            case "rm-branch":
                rmBranchHelper(command, args);
                break;
            case "reset":
                resetHelper(command, args);
                break;
            default:
                System.out.println("No command with that name exists.");
                return;
            }
        }
        Utils.writeObject(REPO, command);
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void addhelper(Repo repo, String... args) throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands");
            System.exit(0);
        } else {
            repo.add(args[1]);
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void commithelper(Repo repo, String... args)
            throws IOException {
        if (args.length == 1 || args[1].isEmpty()) {
            System.out.println("Please enter a commit message.");
        }
        String message = "";
        for (int i = 1; i < args.length - 1; i++) {
            message += args[i] + " ";
        }
        message += args[args.length - 1];
        repo.commit(message);
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void loghelper(Repo repo, String... args)
            throws IOException {
        if (args.length != 1) {
            System.out.println("Incorrect operands");
        } else {
            repo.log();
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void globalLogHelper(Repo repo, String... args)
            throws IOException {
        if (args.length != 1) {
            System.out.println("Incorrect operands");
        } else {
            repo.globalLog();
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void checkoutHelper(Repo repo, String... args)
            throws IOException {
        if (args.length == 3) {
            repo.checkoutFileName(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            repo.checkout(args[1], args[3]);
        } else if (args.length == 2) {
            repo.checkoutBranch(args[1]);
        } else {
            System.out.println("Incorrect operands");
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void rmHelper(Repo repo, String... args)
            throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands");
        } else {
            repo.remove(args[1]);
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void findHelper(Repo repo, String... args)
            throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands");
        } else {
            repo.find(args[1]);
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void statusHelper(Repo repo, String... args)
            throws IOException {
        if (args.length != 1) {
            System.out.println("Incorrect operands");
        } else {
            repo.status();
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void branchHelper(Repo repo, String... args)
            throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands");
        } else {
            repo.branch(args[1]);
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void rmBranchHelper(Repo repo, String... args)
            throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands");
        } else {
            repo.removeBranch(args[1]);
        }
    }
    /** command helper.
     * @param repo the current repo.
     * @param args input. */
    public static void resetHelper(Repo repo, String... args)
            throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect operands");
        } else {
            repo.reset(args[1]);
        }
    }
}
