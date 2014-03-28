package net.timbusproject.extractors.modules.debiansoftwareextractor.local;

import com.jcraft.jsch.JSchException;
import org.apache.commons.cli.ParseException;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;

/**
 * Created by miguel on 31-01-2014.
 */
public class Test {

    public static void main(String[] args) throws IOException, ParseException, JSONException, JSchException, java.text.ParseException {

        CommonsEngine2 engine = new CommonsEngine2(new String[]{"-l","-s", "-o", "extraction.json"}, System.out);
    }
}
