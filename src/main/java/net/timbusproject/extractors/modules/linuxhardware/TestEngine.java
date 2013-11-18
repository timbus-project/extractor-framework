package net.timbusproject.extractors.modules.linuxhardware;

import com.jcraft.jsch.JSchException;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: cmdesktop
 * Date: 12-11-2013
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public class TestEngine {
    public static void main(String... args) throws IOException, JSchException, JSONException {
        SSHManager instance = new SSHManager(
                "cmdesktop",
                "cmdesktop",
                "localhost",
                "",
                ""
        );
        instance.connect();
//        Engine engine =  new Engine();
//        engine.run(instance);
//        instance.sendCommandSudo("cat /sys/class/dmi/id/board_serial", "cmdesktop");
        String result = instance.sendCommandSudo("dmidecode --type baseboard", "cmdesktop");
        instance.close();
        System.out.println("RESULT: " + result);
    }
}
