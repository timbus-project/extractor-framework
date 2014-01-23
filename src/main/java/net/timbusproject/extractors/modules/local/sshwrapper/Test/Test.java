package net.timbusproject.extractors.modules.local.sshwrapper.Test;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Arrays;

/**
 * Created by miguel on 22-01-2014.
 */
public class Test {
    public static void main(String[] args) throws JSONException {
        String command = "java -jar  /timbus";
        command = command.substring(1);
        command = command.substring(0, command.length() - 1);
        System.out.println(command);

//        ava -jar  /timbu
    }
}
