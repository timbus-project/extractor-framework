/**
 * Copyright (c) 2013, Caixa Magica Software Lda (CMS).
 * The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
 * TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological
 * development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without
 * limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR
 * PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise,
 * unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including
 * any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this
 * License or out of the use or inability to use the Work.
 * See the License for the specific language governing permissions and limitation under the License.
 */
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
