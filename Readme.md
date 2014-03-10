#Debian Software Extractor

Debian Software Extractor is a tool that extracts information of installed packages and its dependencies from a remote or local debian distro based linux.

&nbsp;

##How to get the code

	git clone https://opensourceprojects.eu/git/p/timbus/context-population/extractors/debian-software timbus-context-population-extractors-debian-software 

&nbsp;

##Install Requirements

1. [Oracle Java JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

##Requirements for the extraction target

1. [Debian based distro](http://en.wikipedia.org/wiki/list_of_Linux_distributions#Debian-based)
2. [dpkg installed](https://wiki.debian.org/dpkg)
3. [SSH server running with authenticated user](http://www.cyberciti.biz/faq/how-to-installing-and-using-ssh-client-server-in-linux/)

&nbsp;

##Collected Information

Dpkg is a package manager for Debian Linux wich is used to install/manage individual packages, the extractor converts the information provided by this package manager in JSON format for easier parsing and for a converter to be able to parse it.

&nbsp;

##How to execute

	#!bash
	java -jar debian-software-extractor.jar user@targetHostAddress 
&nbsp;

##Expected output - an example package

	#!json
	{
  	  "Package": "diffutils",
 	  "Essential": "yes",
	  "Status": "install ok installed",
   	  "Priority": "required",
   	  "Section": "utils",
	   "Installed-Size": "420",
	   "Maintainer": "Ubuntu Developers <ubuntu-devel-discuss@lists.ubuntu.com>",
	   "Architecture": "amd64",
	   "Version": "1:3.2-8",
	   "Replaces": [{"Package": "diff"}],
	   "Depends": [[
	    {
		"Package": "dpkg",
		"Comparator": ">=",
		"Version": "1.15.4"
	    },
	      {"Package": "install-info"}
	    ]],
	    "Pre-Depends": [],
	    "Suggests": [
	      {"Package": "diffutils-doc"},
	      {"Package": "wdiff"}
	    ],
            "Description": " comparison utilities The diffutils package provides the diff, diff3, sdiff, and cmp programs. . `diff' shows differences between two files, or each corresponding file in two directories.  `cmp' shows the offsets and line numbers where two files differ.  `cmp' can also show all the characters that differ between the two files, side by side.  `diff3' shows differences among three files.  `sdiff' merges two files interactively. . The set of differences produced by `diff' can be used to distribute updates to text files (such as program source code) to other people. This method is especially useful when the differences are small compared to the complete files.  Given `diff' output, the `patch' program can update, or \"patch\", a copy of the file."
           }
.... 
Note: This is an example of a package, in other extraction this may vary due to the installed packages present in a extraction target.

&nbsp;

##Author

Luís Marques <luis.marques@caixamagica.pt>

&nbsp;

##License

Copyright (c) 2014, Caixa Magica Software Lda (CMS).
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.
