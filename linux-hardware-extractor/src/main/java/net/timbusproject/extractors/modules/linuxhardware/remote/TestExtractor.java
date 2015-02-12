package net.timbusproject.extractors.modules.linuxhardware.remote;

import net.timbusproject.extractors.core.Endpoint;
import net.timbusproject.extractors.modules.linuxhardware.absolute.Engine;
import org.codehaus.jettison.json.JSONException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: cmdesktop
 * Date: 02-05-2014
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class TestExtractor {
    public static void main(String[] args) throws Exception {
        Engine engine = new Engine();
//        public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName,
//        int connectionPort, String privateKey) throws JSchException {
//            doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName, privateKey);
//            intConnectionPort = connectionPort;
//            intTimeOut = 60000;
//        }
        Endpoint endpoint = new Endpoint("134.191.240.68");
        endpoint.setProperty("user","timbus");
        endpoint.setProperty("password","T!mbu5=?");
        endpoint.setProperty("port","10022");

//        SSHManager instance = new SSHManager("root","timbus","10.10.96.59","",2123,"");
//        SSHManager instance = new SSHManager("cmdesktop","cmdesktop","localhost","","");
//        String result =  engine.run(instance, endpoint);
//        System.out.println(result);

        LinuxHardwareExtractor extractor = new LinuxHardwareExtractor();
        String result = extractor.extract(endpoint,false);
        System.out.println(result);
        writeToFile(result);

    }
    private static void writeToFile(String output) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        PrintWriter writer = new PrintWriter("eHealth-hardware.json", "UTF-8");
        writer.write(output);
        writer.close();
    }
}
