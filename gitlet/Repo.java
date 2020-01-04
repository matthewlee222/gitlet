package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Arrays;


/** @author Matthew Lee
 * Collaborated lightly with Jasmine Yong, Tasnim Tallman. */

public class Repo implements Serializable {

    /** Init. */

    public Repo() {
        Commit first = Commit.firstOne();
        File gitlet = new File(".gitlet");
        gitlet.mkdir();
        File commits = new File(".gitlet/commits");
        commits.mkdir();
        File staging = new File(".gitlet/staging");
        staging.mkdir();
        File theFirst = new File(".gitlet/commits/" + first.myI());
        Utils.writeContents(theFirst, Utils.serialize(first));
        _front = "master";
        _sprouts = new HashMap<>();
        _sprouts.put("master", first.myI());
        _notFollowed = new ArrayList<>();
        _container = new HashMap<>();

    }

    /** Log. */

    public void log() {
        String fro;
        fro = frontRetrieve();
        while (fro != null) {
            Commit first;
            first = theIdentifier(fro);
            printerHelper(fro);
            fro = first.getPat();
        }
    }


    /** Add.
     * @param str value. */

    public void add(String str) {
        File fale = new File(str);
        if (!fale.exists()) {
            Utils.message("File does not exist.");
            throw new GitletException();
        }
        String sat = Utils.sha1(Utils.readContentsAsString(fale));
        Commit beforeIt = theIdentifier(frontRetrieve());
        HashMap<String, String> too = beforeIt.retrieveThem();

        File blank = new File(".gitlet/staging/" + sat);
        boolean b;
        if (too == null) {
            b = true;
        } else {
            b = false;
        }
        if (b) {
            _container.put(str, sat);
            Utils.writeContents(blank, Utils.readContentsAsString(fale));
        } else if (!too.containsKey(str)) {
            _container.put(str, sat);
            Utils.writeContents(blank, Utils.readContentsAsString(fale));
        } else if (!too.get(str).equals(sat)) {
            _container.put(str, sat);
            Utils.writeContents(blank, Utils.readContentsAsString(fale));
        } else {
            if (blank.exists()) {
                _container.remove(str);
            }
        }
        if (_notFollowed.contains(str)) {
            _notFollowed.remove(str);
        }
    }

    /** Commit.
     * @param note value. */

    public void commit(String note) {
        if (note.trim().equals("")) {
            String m = "Please enter a commit message.";
            Utils.message(m);
            throw new GitletException();
        }
        Commit before = theIdentifier(frontRetrieve());
        HashMap<String, String> trm = before.retrieveThem();

        if (trm == null) {
            trm = new HashMap<String, String>();
        }

        if (_notFollowed.size() != 0 || _container.size() != 0) {
            for (String a : _container.keySet()) {
                trm.put(a, _container.get(a));
            }
            for (String fileName : _notFollowed) {
                trm.remove(fileName);
            }
        } else {
            String n = "No changes added to the commit.";
            Utils.message(n);
            throw new GitletException();
        }
        String[] lst = new String[]{before.myI()};
        Commit can = new Commit(note, trm, lst, true);
        File fl = new File(".gitlet/commits/" + can.myI());
        Utils.writeObject(fl, can);

        _notFollowed = new ArrayList<String>();
        _container = new HashMap<String, String>();
        _sprouts.put(_front, can.myI());
    }


    /** Commit for merge.
     * @param mark value.
     * @param raw value. */

    public void commit(String mark, String[] raw) {
        if (mark.trim().equals("")) {
            String str = "Please enter a commit message.";
            Utils.message(str);
            throw new GitletException();
        }
        Commit pre = theIdentifier(frontRetrieve());
        HashMap<String, String> cont = pre.retrieveThem();

        if (cont == null) {
            cont = new HashMap<String, String>();
        }

        if (_notFollowed.size() != 0 || _container.size() != 0) {
            for (String s : _container.keySet()) {
                cont.put(s, _container.get(s));
            }
            for (String s : _notFollowed) {
                cont.remove(s);
            }
        } else {
            Utils.message("No changes added to the commit.");
            throw new GitletException();
        }
        Commit newCommit = new Commit(mark, cont, raw, true);
        String s = newCommit.myI();
        File pr = new File(".gitlet/commits/" + s);
        Utils.writeObject(pr, newCommit);

        _notFollowed = new ArrayList<String>();
        _container = new HashMap<String, String>();
        _sprouts.put(_front, newCommit.myI());
    }

    /** Remove.
     * @param point */

    public void rm(String point) {
        File clk = new File(point);
        Commit pre = theIdentifier(frontRetrieve());
        HashMap<String, String> vals = pre.retrieveThem();
        if (!clk.exists() && !vals.containsKey(point)) {
            String str = "File does not exist.";
            Utils.message(str);
            throw new GitletException();
        }
        boolean alt = false;
        if (_container.containsKey(point)) {
            _container.remove(point);
            alt = true;
        }
        if (vals.containsKey(point) && vals != null) {
            _notFollowed.add(point);
            File out = new File(point);
            alt = true;
            Utils.restrictedDelete(out);
        }
        if (!alt) {
            String st = "No reason to remove the file.";
            Utils.message(st);
            throw new GitletException();
        }
    }

    /** Global Log. */

    public void globalLog() {

        String pathway = ".gitlet/commits";
        File rep = new File(pathway);
        File[] reps = rep.listFiles();

        for (File a : reps) {
            printerHelper(a.getName());
        }
    }

    /** Find.
     * @param str value.  */

    public void find(String str) {
        String m = ".gitlet/commits";
        File rep = new File(m);
        File[] reps = rep.listFiles();
        boolean found = false;

        for (File a : reps) {
            Commit cla = theIdentifier(a.getName());
            if (cla.retrieveCode().equals(str)) {
                System.out.println(a.getName());
                found = true;
            }
        }
        if (!found) {
            String map = "Found no commit with that message.";
            Utils.message(map);
            throw new GitletException();
        }
    }

    /** Status. */

    public void status() {
        System.out.println("=== Branches ===");
        Object[] obj = _sprouts.keySet().toArray();
        Arrays.sort(obj);
        for (Object a : obj) {
            if (!a.equals(_front)) {
                System.out.println(a);
            } else {
                System.out.println("*" + a);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Object[] place = _container.keySet().toArray();
        Arrays.sort(place);
        for (Object b : place) {
            System.out.println(b);
        }
        System.out.println();
        String rem = "=== Removed Files ===";
        System.out.println(rem);
        Object[] vall = _notFollowed.toArray();
        Arrays.sort(vall);
        for (Object e : vall) {
            System.out.println(e);
        }
        System.out.println();
        String red = "=== Modifications Not Staged For Commit ===";
        System.out.println(red);
        System.out.println();
        String blu = "=== Untracked Files ===";
        System.out.println(blu);
        System.out.println();
    }


    /** Checkout.
     * @param strLst value. */
    public void checkout(String[] strLst) {
        String one;
        String sec;
        if (strLst.length == 3 && strLst[1].equals("--")) {
            one = strLst[0];
            sec = strLst[2];
        } else if (strLst.length == 2 && strLst[0].equals("--")) {
            sec = strLst[1];
            one = frontRetrieve();
        } else {
            String son = "Incorrect operands";
            Utils.message(son);
            throw new GitletException();
        }
        one = translation(one);
        Commit thisCommit = theIdentifier(one);
        HashMap<String, String> fill = thisCommit.retrieveThem();

        if (!fill.containsKey(sec)) {
            Utils.message("File does not exist in that commit.");
            throw new GitletException();
        } else {
            File fa = new File(sec);
            String blank = ".gitlet/staging/" + fill.get(sec);
            File ide = new File(blank);
            String contents = Utils.readContentsAsString(ide);
            Utils.writeContents(fa, contents);
        }
    }

    /** Method.
     * @param id value.
     * @return */
    private String translation(String id) {
        int i = Utils.UID_LENGTH;
        if (id.length() == i) {
            return id;
        }
        String str = ".gitlet/commits";
        File failed = new File(str);
        File[] a = failed.listFiles();

        for (File file : a) {
            if (file.getName().contains(id)) {
                return file.getName();
            }
        }
        String va = "No commit with that id exists.";
        Utils.message(va);
        throw new GitletException();
    }

    /** Checkout method.
     * @param str - string value. */
    public void checkout(String str) {
        if (!_sprouts.containsKey(str)) {
            String failure = "No such branch exists.";
            Utils.message(failure);
            throw new GitletException();
        }
        if (_front.equals(str)) {
            String s = "No need to checkout the current branch.";
            Utils.message(s);
            throw new GitletException();
        }
        Commit it2 = theIdentifier(_sprouts.get(str));
        HashMap<String, String> cup = it2.retrieveThem();
        String sStr = System.getProperty("user.dir");
        File fall = new File(sStr);
        checkingTracking(fall);
        File[] fList = fall.listFiles();
        for (File file : fList) {
            if (cup != null) {
                boolean b = !cup.containsKey(file.getName());
                if (b && !file.getName().equals(".gitlet")) {
                    Utils.restrictedDelete(file);
                }
            } else {
                Utils.restrictedDelete(file);
            }
        }
        if (cup != null) {
            for (String file : cup.keySet()) {
                String mess = ".gitlet/staging/";
                String mess2 = mess + cup.get(file);
                File file2 = new File(mess2);
                String read = Utils.readContentsAsString(file2);
                Utils.writeContents(new File(file), read);
            }
        }
        _container = new HashMap<String, String>();
        _notFollowed = new ArrayList<String>();
        _front = str;

    }

    /** Checks whether file is tracked.
     * @param f value. */
    private void checkingTracking(File f) {
        String str;
        str = "There is an untracked file in the way; delete it or add "
                + "it first.";
        Commit previousCommit = theIdentifier(frontRetrieve());
        HashMap<String, String> followed = previousCommit.retrieveThem();
        File[] t = f.listFiles();
        for (File a : t) {
            if (followed == null) {
                if (f.listFiles().length > 1) {
                    Utils.message(str);
                    throw new GitletException();
                }
            } else {
                boolean bool1;
                boolean bool2;
                bool1 = !followed.containsKey(a.getName());
                bool2 = !_container.containsKey(a.getName());
                if (bool2 && !a.getName().equals(".gitlet") && bool1) {
                    Utils.message(str);
                    throw new GitletException();
                }
            }
        }
    }

    /** Branch.
     * @param param value.
     */
    public void branch(String param) {
        if (_sprouts.containsKey(param)) {
            String s = "A branch with that name already exists.";
            Utils.message(s);
            throw new GitletException();
        } else {
            _sprouts.put(param, frontRetrieve());
        }
    }


    /** Remove Branch.
     * @param arg value.*/
    public void rmBranch(String arg) {
        String one = "Cannot remove the current branch.";
        String two = "A branch with that name does not exist.";
        if (_front.equals(arg)) {
            Utils.message(one);
            throw new GitletException();
        }
        if (!_sprouts.containsKey(arg)) {
            Utils.message(two);
            throw new GitletException();
        } else {
            _sprouts.remove(arg);
        }
    }

    /** Reset.
     * @param pev value.*/

    public void reset(String pev) {
        pev = translation(pev);
        String thePath = ".gitlet/staging/";
        Commit pho = theIdentifier(pev);
        HashMap<String, String> followed = pho.retrieveThem();

        String str = System.getProperty("user.dir");
        File pwd = new File(str);
        checkingTracking(pwd);
        File[] fileList = pwd.listFiles();
        for (File a : fileList) {
            if (!followed.containsKey(a.getName())) {
                Utils.restrictedDelete(a);
            }
        }
        for (String a : followed.keySet()) {
            File fal = new File(thePath + followed.get(a));
            String contents = Utils.readContentsAsString(fal);
            Utils.writeContents(new File(a), contents);
        }
        _container = new HashMap<String, String>();
        _sprouts.put(_front, pev);
    }








    /** Merge.
     * @param eelum value.*/
    public void merge(String eelum) {
        String aaa = "You have uncommitted changes.";
        String bbb = "A branch with that name does not exist.";
        String ddd = "Given branch is an ancestor of the current branch.";
        if (_container.size() > 0) {
            Utils.message(aaa);
            throw new GitletException();
        } else if (_notFollowed.size() > 0) {
            Utils.message(aaa);
            throw new GitletException();
        }
        if (!_sprouts.containsKey(eelum)) {
            Utils.message(bbb);
            throw new GitletException();
        }
        if (eelum.equals(_front)) {
            Utils.message("Cannot merge a branch with itself.");
            throw new GitletException();
        }
        String strang = divider(eelum, _front);
        if (strang.equals(_sprouts.get(eelum))) {
            Utils.message(ddd);
            return;
        }
        if (strang.equals(_sprouts.get(_front))) {
            _sprouts.put(_front, _sprouts.get(eelum));
            Utils.message("Current branch fast-forwarded.");
            return;
        }
        Commit strangs = theIdentifier(strang);
        HashMap<String, String> splitFiles = strangs.retrieveThem();
        dTL(eelum);
        Commit overall = theIdentifier(frontRetrieve());
        HashMap<String, String> thisOne = overall.retrieveThem();
        Commit thatOne = theIdentifier(_sprouts.get(eelum));
        HashMap<String, String> truly = thatOne.retrieveThem();
        for (String eerum : truly.keySet()) {
            if (!splitFiles.containsKey(eerum)) {
                if (!thisOne.containsKey(eerum)) {
                    String str = _sprouts.get(eelum);
                    checkout(new String[] {str, "--", eerum});
                    _container.put(eerum, truly.get(eerum));
                } else if (!truly.containsKey(eerum)) {
                    int i = 2;
                } else if (mergeHelper2(eerum, truly, thisOne, 1)) {
                    File f1 = new File(".gitlet/staging/" + thisOne.get(eerum));
                    File f2 = new File(".gitlet/staging/" + truly.get(eerum));
                    String valued = "<<<<<<< HEAD\n";
                    valued += Utils.readContentsAsString(f1) + "=======\n";
                    valued += Utils.readContentsAsString(f2) + ">>>>>>>";
                    Utils.writeContents(new File(eerum), valued);
                    add(eerum);
                    Utils.message("Encountered a merge conflict.");
                }
            }
        }
        String[] daRent = new String[]{frontRetrieve(), _sprouts.get(eelum)};
        commit("Merged" + " " + eelum + " into " + _front + ".", daRent);
    }

    /** Helps divide the Merge dtl.
     * @param dt value. */
    private void dTL(String dt) {
        String pointing = divider(dt, _front);
        Commit pointer = theIdentifier(pointing);
        HashMap<String, String> pointF = pointer.retrieveThem();
        Commit cupped = theIdentifier(frontRetrieve());
        HashMap<String, String> thisBranchAve = cupped.retrieveThem();
        Commit sweg = theIdentifier(_sprouts.get(dt));
        HashMap<String, String> gotten = sweg.retrieveThem();

        String pointingTo = System.getProperty("user.dir");
        File all = new File(pointingTo);
        checkingTracking(all);

        for (String meth : pointF.keySet()) {
            String line = "--";
            boolean third = mergeHelper2(meth, pointF, gotten, 5);
            boolean bool1 = gotten.containsKey(meth);
            boolean bool2 = mergeHelper2(meth, pointF, thisBranchAve, 4);
            if (!bool2) {
                if (!bool1) {
                    Utils.restrictedDelete(new File(meth));
                    rm(meth);
                    continue;
                }
                if (third) {
                    String b = _sprouts.get(dt);
                    checkout(new String[]{b, line, meth});
                    add(meth);
                }
            }
            if (bool2) {
                if (third) {
                    if (mergeHelper2(meth, gotten, thisBranchAve, 2)) {
                        mergeConflict(dt, meth);
                    }
                }
            }
        }
    }

    /** A problem with merge.
     * @param eerum value.
     * @param eelim value.
     */
    private void mergeConflict(String eerum, String eelim) {
        String strangs = divider(eerum, _front);
        Commit strungCom = theIdentifier(strangs);
        HashMap<String, String> stringHashHash = strungCom.retrieveThem();
        Commit commitOfInterest = theIdentifier(frontRetrieve());
        HashMap<String, String> current = commitOfInterest.retrieveThem();
        Commit otherCommit = theIdentifier(_sprouts.get(eerum));
        HashMap<String, String> gave = otherCommit.retrieveThem();
        String stranger = ".gitlet/staging/";
        File fileImp;
        String valuation;
        if (current.containsKey(eelim)) {
            fileImp = new File(stranger + current.get(eelim));
            valuation = Utils.readContentsAsString(fileImp);
        } else {
            fileImp = null;
            valuation = "";
        }
        File fileImp2;
        String valueOfFiles;
        if (gave.containsKey(eelim)) {
            fileImp2 = new File(stranger + gave.get(eelim));
            valueOfFiles = Utils.readContentsAsString(fileImp2);
        } else {
            fileImp2 = null;
            valueOfFiles = "";
        }
        String arc = "Encountered a merge conflict.";
        String contents = "<<<<<<< HEAD\n" + valuation;
        contents += "=======\n" + valueOfFiles + ">>>>>>>\n";
        Utils.writeContents(new File(eelim), contents);
        add(eelim);
        Utils.message(arc);
    }
    /** Merge helper method.
     * @param branch1 value.
     * @param branch2 value.
     * @return */
    private String divider(String branch1, String branch2) {
        String appa = _sprouts.get(branch1);
        String ummu = _sprouts.get(branch2);
        ArrayList<String> firstOnes = new ArrayList<String>();
        ArrayList<String> firstOnes2 = new ArrayList<String>();

        while (appa != null) {
            int i = 1;
            firstOnes.add(appa);
            Commit comm1 = theIdentifier(appa);
            appa = comm1.getPat();
        }
        while (ummu != null) {
            int i = 2;
            firstOnes2.add(ummu);
            Commit comm2 = theIdentifier(ummu);
            ummu = comm2.getPat();
        }
        for (String a : firstOnes) {
            int i = 4;
            if (firstOnes2.contains(a)) {
                return a;
            }
        }
        return "";
    }

    /** Another merge helper.
     * @param strang value.
     * @param has value.
     * @param str value.
     * @param e value.
     * @return*/
    boolean mergeHelper2(String strang, HashMap<String, String> has,
                         HashMap<String, String> str, int e) {
        boolean t = str.containsKey(strang);
        boolean u = has.containsKey(strang);
        if (u && t) {
            String marked = str.get(strang);
            String marking = has.get(strang);
            if (!marking.equals(marked)) {
                return true;
            }
        } else if (u) {
            return true;
        } else if (t) {
            return true;
        }
        return false;
    }







    /** Commit print helper.
     * @param t value. */
    public void printerHelper(String t) {
        Commit badge;
        badge = theIdentifier(t);
        String imp = "===";
        String comm = "commit";
        String space = " ";
        String date = "Date:";
        String merg = "Merge:";

        if (badge.getPatriot() != null && badge.getPatriot().length > 1) {
            System.out.println(imp);
            System.out.println(comm + space + t);
            String short1 = badge.getPatriot()[0].substring(0, 7);
            String short2 = badge.getPatriot()[1].substring(0, 7);
            System.out.println(merg + space + short1 + space + short2);
            System.out.println(date + space + badge.queHoraEs());
            System.out.println(badge.retrieveCode());
            System.out.println();
        } else {
            System.out.println(imp);
            System.out.println(comm + space + t);
            System.out.println(date + space + badge.queHoraEs());
            System.out.println(badge.retrieveCode());
            System.out.println();
        }
    }

    /** Helping method across different commands.
     * @param str value.
     * @return */
    public Commit theIdentifier(String str) {
        String pathValue = ".gitlet/commits/";
        File fil = new File(pathValue + str);

        if (!fil.exists()) {
            Utils.message("No commit with that id exists.");
            throw new GitletException();
        } else {
            return Utils.readObject(fil, Commit.class);
        }
    }

    /** Gets head of a specific branch.
     * @return */
    public String frontRetrieve() {
        return _sprouts.get(_front);
    }

    /** Branches of the commit tree. */
    private HashMap<String, String> _sprouts;

    /** Front point.  */
    private String _front;

    /** Place where staging occurs in Gitlet directory. */
    private HashMap<String, String> _container;

    /** Files that have not been followed previously. */
    private ArrayList<String> _notFollowed;

}
