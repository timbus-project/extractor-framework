package net.timbusproject.extractors.helpers;

import java.io.InputStream;
import java.io.IOException;
import java.util.UUID;

public class MachineID {
	String hostid, hostname;

	private static String exec(String command) throws IOException {
		Process p = Runtime.getRuntime().exec(command);
		String stdout = drain(p.getInputStream());
		String stderr = drain(p.getErrorStream());
		return stdout; // TODO: return stderr also...
	}

	private static String drain(InputStream in) throws IOException {
		int b = -1;
		StringBuilder buf = new StringBuilder();
		while ((b = in.read()) != -1)
			buf.append((char) b);
		return buf.toString();
	}

	public MachineID() {
		try {
			this.hostname = exec("hostname").trim();
			this.hostid = exec("hostid").trim();
		} catch (Exception E) {
		}
	}

	public MachineID(String hostid, String hostname) {
		this.hostid = hostid;
		this.hostname = hostname;
	}

	public String getXRN() {
		return "xri://+machine?+hostid=" + hostid + "/+hostname=" + hostname;
	}

	public String toString() {
		return getXRN();
	}

	public String getUUID() {
		byte[] id = (hostname + hostid).getBytes();
		byte[] id_with_namespace;
		id_with_namespace = new byte[id.length + 16];
		System.arraycopy(id, 0, id_with_namespace, 16, id.length);

		return "urn:uuid:" + UUID.nameUUIDFromBytes(id_with_namespace);
	}

	public static void main(String args[]) {
		System.out.println(new MachineID());
	}
}
