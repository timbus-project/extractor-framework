SSH Wrapper Extractor
---------------------

SSH Wrapper Extractor is a tool that is able to send any list of commands to a remote machine and capture its output.

##Install Requirements

1. [Oracle Java JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

##Requirements for the extraction target

1. Any operating system with [authenticated user running SSH server](http://www.cyberciti.biz/faq/how-to-installing-and-using-ssh-client-server-in-linux/)

##Collected Information

SSH, standing for Secure Shell, is part of the TCP/IP protocol suite and offers a nearly unanimous safe way to administrate UNIX remote machines. It can also be used in other operating systems such as Windows by using proper emulators.
Example: [PuTTY](http://en.wikipedia.org/wiki/PuTTY)

##Relevant parameters

1. Typical SSH authentication parameters: User, Password
2. Computer fqdn and port (defaults to 22)
3. List of commands to be performed in machine 

##Expected output
	 
As the Extractor Wrapper is aimed to capture the output of any local extractor or remote file containing a previously performed extraction, it does not expect any specific output. In this moment, it is designed to perform punctual remote-to-local extractions.
Version 0.0.3 of Extractors API will include a functionality in which it expects each extraction's output to contain a header which explicitly declares the format of the output.   

##TIMBUS Use Cases

This extractor is possibly relevant to all use cases which might contain sensitive data.

###Example usage 

Pre-conditions:

* The target machine has a .jar local extractor located in ~/Documents
* The origin and remote machines are in the same network and the remote one has an open SSH port

JSON Request:

	#!json
	{
    "extractions": [
        {
            "module": "SSH Wrapper Extractor",
            "parameters": {
                "fqdn": "localhost",
                "port":"22",
                "user": "timbususer",
                "password": "securepassword",
                "commands": "[\"java -jar ~/Documents/someLocalExtractor.jar\"]"
            }
        }
    ]
	}

As you can see, the "commands" argument is actually a String representing an array, so it has to be escaped with the proper backslashes. In this case, it requests for only one extraction. If we had added more commands, the output would be an Array containing the output of each command.
 
&nbsp;

##Author

Miguel Gama Nunes <miguel.nunes@caixamagica.pt>

&nbsp;

##License

Copyright (c) 2014, Caixa Magica Software Lda (CMS).
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.