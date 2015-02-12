package net.timbusproject.extractors.modules.linuxhardware.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.ParseException;
import org.codehaus.jettison.json.JSONException;

import com.jcraft.jsch.JSchException;

/**
 * Created by miguel on 31-01-2014.
 */
public class Test {

    public static void main(String[] args) throws IOException, ParseException, JSONException, JSchException {

        System.out.println(getCapacityOnController("00:02.1 Display controller: Intel Corporation Mobile GM965/GL960 Integrated Graphics Controller (secondary) (rev 0c)\nSubsystem: Lenovo ThinkPad T61/R61\nFlags: bus master, fast devsel, latency 0\n                          Memory at f8200000 (64-bit, non-prefetchable) [size=1M]\nCapabilities: <access denied>"));

    }

    public static double getCapacityOnController(String controller) {
        String regex = "\\[size=(\\d+\\p{Alpha}{1,2})\\]";
        Pattern pattern = Pattern.compile(regex);
        String[] splitController = controller.split(System.getProperty("line.separator"));
        ArrayList<Double> capacities = new ArrayList<Double>();

        for(String a : splitController)
            if(a.trim().startsWith("Memory at")){
                Matcher matcher = pattern.matcher(a.trim());
                matcher.find();
                String capacity = matcher.group(1);
                if(capacity.endsWith("K"))
                    capacities.add(Double.valueOf(capacity.substring(0, capacity.length() - 1))/1000);
                else
                    capacities.add(Double.valueOf(capacity.substring(0, capacity.length() - 1)));
            }
        double finalResult = 0;
        for(double c : capacities)
            finalResult += c;

        return finalResult;
    }
}
