package gitlet;
import java.util.Date;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;

/** @author Matthew Lee
 * Collaborated lightly with Kathlee Wong. */

public class Commit implements Serializable {
    /** Commit.
     * @param s value.
     * @param h value.
     * @param sl value.
     * @param bool value. */
    public Commit(String s, HashMap<String, String> h,
                  String[] sl, boolean bool) {
        _val = s;
        _vals = h;
        _appa = sl;
        Date dateObj;
        if (!bool) {
            _hourAndMin = _mA;
            _prob = helperCommit();
        } else {

            dateObj = new Date();
            _hourAndMin = TIME.format(dateObj) + " -0800";
            _prob = helperCommit();
        }
    }

    /** How to store commits.
     * @return */
    public String helperCommit() {
        String files;
        if (_vals != null) {
            files = _vals.toString();
            return Utils.sha1(_val, files, _hourAndMin, Arrays.toString(_appa));
        } else {
            files = "";
            return Utils.sha1(_val, files, _hourAndMin, Arrays.toString(_appa));
        }
    }

    /** Returns message.
     * @return */
    public String retrieveCode() {
        return _val;
    }

    /** First commit.
     * @return */
    public static Commit firstOne() {
        return new Commit("initial commit", null, null, false);
    }

    /** Returns hash data structure.
     * @return  */
    public HashMap<String, String> retrieveThem() {
        return _vals;
    }

    /** Retrieves the time.
     * @return  */
    public String queHoraEs() {
        return _hourAndMin;
    }

    /** Gets id of previous commit.
     * @return  */
    public String getPat() {
        if (_appa != null) {
            int j = 0;
            return _appa[j];
        } else {
            return null;
        }
    }

    /** Gets parents.
     * @return  */
    public String[] getPatriot() {
        return _appa;
    }

    /** Gets overall ID of commit.
     * @return  */
    public String myI() {
        return _prob;
    }

    /** Value. */
    private String _val;

    /** Value. */
    private String _hourAndMin;

    /** Value. */
    private HashMap<String, String> _vals;

    /** Value. */
    private String[] _appa;

    /** Value. */
    private String _prob;

    /** Value. */
    private String _mA = "Wed Dec 31 16:00:00 1969 -0800";

    /** Value. */
    public static final SimpleDateFormat TIME =
            new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
}
