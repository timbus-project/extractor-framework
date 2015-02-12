package net.timbusproject.extractors.modules.linuxhardware.local;

import com.jcraft.jsch.JSchException;
import org.apache.commons.cli.ParseException;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;

/**
 * Created by miguel on 31-01-2014.
 */
public class LocalLinuxHardwareExtractor {

    public static void main(String[] args) throws IOException, ParseException, JSONException, JSchException, InterruptedException {
        CommonsEngine2 engine = new CommonsEngine2(args, System.out);
    }


}
