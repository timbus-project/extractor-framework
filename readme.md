# Extractors Core

This core library contains main communication interface between all extraction-related OSGi-based projects.

This project cannot be used as a standalone.


## How to get the code

	$ git clone https://opensourceprojects.eu/git/p/timbus/context-population/extractors-core


## Requirements

- [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads) (>=6)
- [Maven](http://maven.apache.org/download.cgi) (>=2)
- [_core_ and _osgi_ parents](http://opensourceprojects.eu/p/timbus/support/maven-parents/)
- Internet access **or** manually download and install [OSGi 4.3 core companion code](http://www.osgi.org/Download) to Maven local repository


### Installation steps of requirements

- Oracle JDK 7
	- in **Debian-based Linux**

			$ sudo add-apt-repository ppa:webupd8team/java
			$ sudo apt-get update
			$ sudo apt-get install oracle-java7-installer
			$ sudo update-alternatives --config java

	- in **RPM-based Linux** (download Oracle JDK 7 and execute as _root_ **or** _sudo_)

			$ rpm -ivh jdk-7u<version>-linux-<architecture>.rpm

	- in **Windows**
		- download Oracle JDK 7
		- double click downloaded file

- Maven (in **Debian-based Linux**)

		$ sudo apt-get install maven


## Installation to Maven local repository

From project's root folder:

	$ mvn install


##Author

- Luís Duarte
- Jorge Simões <jorge.simoes@caixamagica.pt>
- Miguel Nunes <miguel.nunes@caixamagica.pt>


##License

Copyright (c) 2014, Caixa Magica Software Lda (CMS).
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.

