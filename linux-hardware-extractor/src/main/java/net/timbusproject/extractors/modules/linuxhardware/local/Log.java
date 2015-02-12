package net.timbusproject.extractors.modules.linuxhardware.local;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: jorge
 * Date: 1/14/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
class Log {

    private static final String defaultLineBreak = "\r\n";
    private final OutputStream out;

    Log(OutputStream out) {
        this.out = out != null ? out : System.out;
    }

    Log out(String... strings) throws IOException {
        return out(false, false, strings);
    }

    Log out(boolean ln, String... strings) throws IOException {
        return out(false, ln, strings);
    }

    Log out(boolean error, boolean ln, String... strings) throws IOException {
        if (strings != null)
            for (String s : strings)
                write((error ? "error: " : "") + s + (ln ? getLineBreak() : ""));
        return this;
    }

    OutputStream getOut() {
        return out;
    }

    String getLineBreak() {
        return defaultLineBreak;
    }

    private void write(String string) throws IOException {
        out.write(string.getBytes());
    }

    static void out(OutputStream out, String... strings) throws IOException {
        out(out, false, false, strings);
    }

    static void out(OutputStream out, boolean ln, String... strings) throws IOException {
        out(out, false, ln, strings);
    }

    static void out(OutputStream out, boolean error, boolean ln, String... strings) throws IOException {
        if (strings != null)
            for (String s : strings)
                write(out, (error ? "error: " : "") + s + (ln ? defaultLineBreak : ""));
    }

    private static void write(OutputStream out, String string) throws IOException {
        out.write(string.getBytes());
    }

}
