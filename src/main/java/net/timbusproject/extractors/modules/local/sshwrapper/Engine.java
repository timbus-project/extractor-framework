

package net.timbusproject.extractors.modules.local.sshwrapper;

import com.jcraft.jsch.JSchException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;

public class Engine {

    public JSONObject runWithPath(SSHManager instance, String path) {
        String command;
        path.trim();
        if (path.endsWith(".jar"))
            command = "java -jar " + path;
        else if (path.endsWith(".pl"))
            command = "perl " + path;
        else if (path.endsWith(".py"))
            command = "python " + path;
        else
            command = path;
        return run(instance, command);
    }

    public JSONObject run(SSHManager instance, String command) {
        try {
            instance.connect();
            /*String result = instance.sendCommand("java -jar " + path);
            JSONArray array = new JSONArray(result);*/
            JSONObject s = new JSONObject(instance.sendCommand(command));
            System.out.println(s);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            instance.close();
        }
        return null;
    }
}
