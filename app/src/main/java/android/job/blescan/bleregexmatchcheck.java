package android.job.blescan;
import java.util.regex.*;
public class bleregexmatchcheck {
    public boolean isValidMACAddress(String str)
    {

        String regex = "^([0-9A-Fa-f]{2}[:-])"
                + "{5}([0-9A-Fa-f]{2})|"
                + "([0-9a-fA-F]{4}\\."
                + "[0-9a-fA-F]{4}\\."
                + "[0-9a-fA-F]{4})$";


        Pattern p = Pattern.compile(regex);


        if (str == null)
        {
            return false;
        }



        Matcher m = p.matcher(str);


        return m.matches();
    }
}
