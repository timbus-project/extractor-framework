#Debian Software Extractor

SSH Wrapper Extractor is a tool that is able to send any list of commands to a remote machine and capture its output.

&nbsp;

##How to get the code

	git clone https://@opensourceprojects.eu/git/p/timbus/context-population/extractors/wrapper-ssh timbus-context-population-extractors-wrapper-ssh
 

&nbsp;

##Install Requirements

1. [Oracle Java JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

##Requirements for the extraction target

1. [Debian based distro](http://en.wikipedia.org/wiki/list_of_Linux_distributions#Debian-based)
2. [SSH server running with authenticated user](http://www.cyberciti.biz/faq/how-to-installing-and-using-ssh-client-server-in-linux/)

&nbsp;

##Collected Information

Dpkg is a package manager for Debian Linux wich is used to install/manage individual packages, the extractor converts the information provided by this package manager in JSON format for easier parsing and for a converter to be able to parse it.

&nbsp;

##How to execute

	#!bash
	java -jar debian-software-extractor.jar user@targetHostAddress 
&nbsp;

Expected output
	 
	As the Extractor Wrapper is aimed to capture the output of any local extractor or remote file containing a previously performed extraction, it does not expect any specific output. From version  

&nbsp;

##Generated Concepts and Properties

This output has a main purpose to be converted to an OWL Ontology through [debian-software-converter](https://opensourceprojects.eu/p/timbus/context-model/converters/json-xml/deb-converter/).

##TIMBUS Use Cases

This extractor is relevant to all use cases concerning linux enviroments:
- OpenSource Workflows
- RCAAP DSpace - *Open Source Digital Repositories*
- Phaidra - Permanent Hosting. Archiving and Indexing of Digital Resources and Assets
- e-Health - Medical digital information


##Author

Luís Marques <luis.marques@caixamagica.pt>

&nbsp;

##License

Copyright (c) 2014, Caixa Magica Software Lda (CMS).
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.
