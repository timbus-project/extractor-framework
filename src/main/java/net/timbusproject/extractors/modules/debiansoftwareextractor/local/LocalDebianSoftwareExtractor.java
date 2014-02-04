package net.timbusproject.extractors.modules.debiansoftwareextractor.local;

import com.jcraft.jsch.JSchException;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by miguel on 03-02-2014.
 */
public class LocalDebianSoftwareExtractor {


    public static void main(String[] args) throws JSONException, ParseException, JSchException, org.apache.commons.cli.ParseException, IOException {

        new CommonsEngine2(args, System.out);
    }


}
