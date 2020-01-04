package gitlet;
import java.io.File;
import java.util.Arrays;

/** @author Matthew Lee
 * Collaborated lightly with Jasmine Yong, Tasnim Tallman. */

public class Main {

    /** Main.
     * @param args value.*/
    public static void main(String... args) {
        try {
            if (args.length == 0) {
                Utils.message("Please enter a command.");
                throw new GitletException();
            }
            if (!oKay(args[0])) {
                Utils.message("No command with that name exists.");
                throw new GitletException();
            } else {
                String[] bot = Arrays.copyOfRange(args, 1, args.length);

                if (!repoThere()) {
                    if (!args[0].equals("init")) {
                        String s;
                        s = "Not in an initialized Gitlet directory.";
                        Utils.message(s);
                        throw new GitletException();
                    } else {
                        daRepository = new Repo();
                        File mr = new File(IT);
                        Utils.writeObject(mr, daRepository);
                    }
                } else {
                    daRepository = getItBack();
                    ultimate(args, bot);
                    File mr = new File(IT);
                    Utils.writeObject(mr, daRepository);
                }
            }
        } catch (GitletException e) {
            System.exit(0);
        }

    }

    /** Is repo already there?
     * @return */
    public static boolean repoThere() {
        File temp = new File(System.getProperty("user.dir") + "/.gitlet");
        return temp.exists();
    }

    /** Tells user whether the command exists in suite of commands.
     * @param val a string.
     * @return */
    private static boolean oKay(String val) {
        int i = 0;
        while (i < listOfCommands.length) {
            if (val.equals(listOfCommands[i])) {
                return true;
            }
            i++;
        }
        return false;
    }


    /** The thing that controls everything. */
    private static Repo daRepository;

    /** Helper method.
     * @param strLst value.
     * @param strLst2 value. */
    private static void ultimate(String[] strLst, String[] strLst2) {
        int zeroth = 0;
        int valid = 0;

        switch (strLst[zeroth]) {

        case "init":
            Utils.message(already);
            throw new GitletException();
        case "add":
            daRepository.add(strLst2[valid]);
            break;
        case "commit":
            daRepository.commit(strLst2[valid]);
            break;
        case "rm":
            daRepository.rm(strLst2[valid]);
            break;
        case "log":
            daRepository.log();
            break;
        case "global-log":
            daRepository.globalLog();
            break;
        case "find":
            daRepository.find(strLst2[valid]);
            break;
        case "status":
            daRepository.status();
            break;
        case "checkout":
            if (strLst2.length == 1) {
                daRepository.checkout(strLst2[valid]);
            } else {
                daRepository.checkout(strLst2);
            }
            break;
        case "branch":
            daRepository.branch(strLst2[valid]);
            break;
        case "rm-branch":
            daRepository.rmBranch(strLst2[valid]);
            break;
        case "reset":
            daRepository.reset(strLst2[valid]);
            break;
        case "merge":
            daRepository.merge(strLst2[valid]);
            break;
        default:
            Utils.message("Check it!");
        }
    }


    /** Public string. */
    private static String already = "A Gitlet version-control "
            + "system already exists in "
            + "the current directory.";

    /** Path to that Repo's file. */
    private static final String IT = ".gitlet/myrepo";

    /** Gets it back.
     * @return */
    public static Repo getItBack() {
        File file =  new File(IT);
        return Utils.readObject(file, Repo.class);
    }

    /** Array of possible valid commands. */
    private static String[] listOfCommands =
            new String[] {"init", "add", "commit", "rm", "log", "global-log",
                          "find", "status", "checkout", "branch",
                          "rm-branch", "reset", "merge"};


}
