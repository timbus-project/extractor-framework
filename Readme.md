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
2. [SSH server running with authenticated user](http://www.cyberciti.biz/faq/how-to-installing-and-using-ssh-client-server-in-linux/)

&nbsp;

##Collected Information

[Dpkg](http://en.wikipedia.org/wiki/Dpkg) is a package manager for Debian Linux wich is used to install/manage individual packages. The extractor uses *Dpkg* to gather all the metadata of the installed packages in JSON format for easier parsing and for a converter to transform the output to an ontology.

The software responsible for the conversion from JSON to OWL(ontology) can be found [here](https://opensourceprojects.eu/p/timbus/context-model/converters/json-xml/deb-converter/).

&nbsp;

##How to execute
###For a local extraction
	#!bash
	java -jar debian-software-extractor.jar "fileOutputName.txt"

The output of the extraction will be put in file with the provided name.

If no arguments are given then the output will be a default file "extraction.json"

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

##Generated Concepts and Properties

This output has a main purpose to be converted to an OWL Ontology through [debian-software-converter](https://opensourceprojects.eu/p/timbus/context-model/converters/json-xml/deb-converter/).

##TIMBUS Use Cases

This extractor is relevant to all use cases concerning debian linux enviroments:
- OpenSource Workflows
- RCAAP DSpace - *Open Source Digital Repositories*
- Phaidra - Permanent Hosting. Archiving and Indexing of Digital Resources and Assets

Each of these use cases has a debian linux enviroment therefore it they have an unique configuration. In order to preserve information of the software installed in their enviroment it is necessary to retrieve it. Each software package and its dependencies metadata is extracted to be possible in the future to build the same system again.

Example case:
The extraction reveals the following installed package:

{
    "Package": "gnome-shell-extension-weather",
    "Status": "install ok installed",
    "Priority": "extra",
    "Section": "gnome",
    "Installed-Size": "444",
    "Maintainer": "Christian METZLER <neroth@xeked.com>",
    "Architecture": "all",
    "Version": "0.2-0+20130514~raring1xcm1",
    "Depends": [
      [
        {"Package": "dconf-gsettings-backend"},
        {"Package": "gsettings-backend"}
      ],
      {"Package": "gnome-shell"},
      {"Package": "seed"}
    ],
    "Description": " extension for GNOME Shell A simple extension for displaying weather informations from several cities in GNOME Shell"
  }

Has shown in the JSON above, the package "gnome-shell-extension-weather", has the following the dependencies: "dconf-gsetting-backend", "gesettings-backend", "gnome-shell", "seed".

This package and its dependencies will be mapped in the ontology in the following way ![image](http://imgur.com/PM322BN). 

The main package is identified with its name and version. Each dependency is identified with the id of the main package plus its name and version. This is due to the ontology restriction that each relationship must be unique in order to reduce conflicts between the entities envolved.

This is very useful to preserve the installed software in a production enviroment, not only storing its information but also the dependencies and relationships that come with it.


 

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
