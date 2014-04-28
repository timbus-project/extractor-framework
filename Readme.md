#Debian Software Extractor

Debian Software Extractor is a tool that extracts information of installed packages and its dependencies from a remote or local debian distro based linux.


##How to get the code

	git clone https://opensourceprojects.eu/git/p/timbus/context-population/extractors/debian-software timbus-context-population-extractors-debian-software 


##Install Requirements

1. [Oracle Java JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

##Requirements for the extraction target

1. [Debian based distro](http://en.wikipedia.org/wiki/list_of_Linux_distributions#Debian-based)
2. [SSH server running with authenticated user](http://www.cyberciti.biz/faq/how-to-installing-and-using-ssh-client-server-in-linux/)

##Collected Information

[Dpkg](http://en.wikipedia.org/wiki/Dpkg) is a package manager for Debian Linux wich is used to install/manage individual packages. The extractor uses *Dpkg* to gather all the metadata of the installed packages in JSON format for easier parsing and for a converter to transform the output to an ontology.

The software responsible for the conversion from JSON to OWL(ontology) can be found [here](https://opensourceprojects.eu/p/timbus/context-model/converters/json-xml/deb-converter/).

##How to execute

###For a local extraction

	java -jar debian-software-extractor.jar "fileOutputName.txt"

The output of the extraction will be put in file with the provided name.

If no arguments are given then the output will be a default file "extraction.json"

##Expected output - an example package

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

Note: This is an example of a package, in other extraction this may vary due to the installed packages present in a extraction target.

##Generated Concepts and Properties

This output has a main purpose to be converted to an OWL Ontology through [debian-software-converter](https://opensourceprojects.eu/p/timbus/context-model/converters/json-xml/deb-converter/).

##TIMBUS Use Cases

This extractor is relevant to all use cases concerning debian linux enviroments:

- OpenSource Workflows
- RCAAP DSpace *Open Source Digital Repositories*
- Phaidra - Permanent Hosting. Archiving and Indexing of Digital Resources and Assets

Each of these use cases has a debian linux enviroment therefore it they have an unique configuration. In order to preserve information of the software installed in their enviroment it is necessary to retrieve it. Each software package and its dependencies metadata is extracted to be possible in the future to build the same system again.

Example case:
The extraction reveals the following installed package:

	[{
	  "Package": "chromium-browser",
	  "Status": "install ok installed",
	  "Priority": "optional",
	  "Section": "web",
	  "Installed-Size": "107253",
	  "Maintainer": "Ubuntu Developers <ubuntu-devel-discuss@lists.ubuntu.com>",
	  "Architecture": "amd64",
	  "Version": "28.0.1500.52-0xcm1.13.04.3",
	  "Replaces": [{"Package": "chromium-browser-inspector"}],
	  "Provides": [
	    {"Package": "chromium-browser-inspector"},
	    {"Package": "www-browser"}
	  ],
	  "Depends": [
	    {"Package": "gconf-service"},
	    {
	      "Package": "libasound2",
	      "Comparator": ">=",
	      "Version": "1.0.16"
	    },
	    {
	      "Package": "libatk1.0-0",
	      "Comparator": ">=",
	      "Version": "1.12.4"
	    },
	    {
	      "Package": "libc6",
	      "Comparator": ">=",
	      "Version": "2.15"
	    },
	    {
	      "Package": "libcairo2",
	      "Comparator": ">=",
	      "Version": "1.6.0"
	    },
	    {
	      "Package": "libcups2",
	      "Comparator": ">=",
	      "Version": "1.4.0"
	    },
	    {
	      "Package": "libdbus-1-3",
	      "Comparator": ">=",
	      "Version": "1.2.14"
	    },
	    {
	      "Package": "libexpat1",
	      "Comparator": ">=",
	      "Version": "2.0.1"
	    },
	    {
	      "Package": "libfontconfig1",
	      "Comparator": ">=",
	      "Version": "2.9.0"
	    },
	    {
	      "Package": "libfreetype6",
	      "Comparator": ">=",
	      "Version": "2.3.9"
	    },
	    {
	      "Package": "libgcc1",
	      "Comparator": ">=",
	      "Version": "1:4.1.1"
	    },
	    {
	      "Package": "libgconf-2-4",
	      "Comparator": ">=",
	      "Version": "2.31.1"
	    },
	    {
	      "Package": "libgcrypt11",
	      "Comparator": ">=",
	      "Version": "1.4.5"
	    },
	    {
	      "Package": "libgdk-pixbuf2.0-0",
	      "Comparator": ">=",
	      "Version": "2.22.0"
	    },
	    {
	      "Package": "libglib2.0-0",
	      "Comparator": ">=",
	      "Version": "2.35.9"
	    },
	    {
	      "Package": "libgnome-keyring0",
	      "Comparator": ">=",
	      "Version": "2.22.2"
	    },
	    {
	      "Package": "libgtk2.0-0",
	      "Comparator": ">=",
	      "Version": "2.24.0"
	    },
	    [
	      {
		"Package": "libnspr4",
		"Comparator": ">=",
		"Version": "2:4.9-2~"
	      },
	      {
		"Package": "libnspr4-0d",
		"Comparator": ">=",
		"Version": "1.8.0.10"
	      }
	    ],
	    [
	      {
		"Package": "libnss3",
		"Comparator": ">=",
		"Version": "2:3.13.4-2~"
	      },
	      {
		"Package": "libnss3-1d",
		"Comparator": ">=",
		"Version": "3.12.6"
	      }
	    ],
	    {
	      "Package": "libpango1.0-0",
	      "Comparator": ">=",
	      "Version": "1.22.0"
	    },
	    {
	      "Package": "libstdc++6",
	      "Comparator": ">=",
	      "Version": "4.6"
	    },
	    {
	      "Package": "libudev1",
	      "Comparator": ">=",
	      "Version": "183"
	    },
	    {
	      "Package": "libx11-6",
	      "Comparator": ">=",
	      "Version": "2:1.4.99.1"
	    },
	    {
	      "Package": "libxcomposite1",
	      "Comparator": ">=",
	      "Version": "1:0.3-1"
	    },
	    {
	      "Package": "libxdamage1",
	      "Comparator": ">=",
	      "Version": "1:1.1"
	    },
	    {"Package": "libxext6"},
	    {"Package": "libxfixes3"},
	    {
	      "Package": "libxrandr2",
	      "Comparator": ">=",
	      "Version": "2:1.2.0"
	    },
	    {"Package": "libxrender1"},
	    {"Package": "libxss1"},
	    {
	      "Package": "libnss3-1d",
	      "Comparator": ">=",
	      "Version": "3.12.3"
	    },
	    {"Package": "xdg-utils"},
	    [
	      {
		"Package": "chromium-codecs-ffmpeg-extra",
		"Comparator": ">=",
		"Version": "0.6"
	      },
	      {
		"Package": "chromium-codecs-ffmpeg",
		"Comparator": ">=",
		"Version": "0.6"
	      }
	    ]
	  ],
	  "Pre-Depends": [{
	    "Package": "dpkg",
	    "Comparator": ">=",
	    "Version": "1.15.6"
	  }],
	  "Recommends": [{"Package": "chromium-browser-l10n"}],
	  "Suggests": [
	    {"Package": "webaccounts-chromium-extension"},
	    {"Package": "unity-chromium-extension"}
	  ],
	  "Conflicts": [{"Package": "chromium-browser-inspector"}],
	  "Conffiles": [{
	    "file": "\/etc\/chromium-browser\/default",
	    "hash": "00f6b60b3a6a0b3ec9816ac3bae285eb"
	  }],
	  "Description": " browser Chromium is an open-source browser project that aims to build a safer, faster, and more stable way for all Internet users to experience the web. . Chromium serves as a base for Google Chrome, which is Chromium rebranded (name and logo) with very few additions such as usage tracking and an auto-updater system. . This package contains the Chromium browser"
	}]

Has shown in the JSON above, the package "chromium-browser" has several dependencies, some may have version and other do not. That depends on the information present in the extraction.

This package and its dependencies will be mapped in the ontology in the following way [image](http://imgur.com/ah1u7YG). The main package is identified with its name and version. Each dependency is identified with the id of the main package plus its name and version.

For example the dependency  

		{
		"Package": "chromium-codecs-ffmpeg",
		"Comparator": ">=",
		"Version": "0.6"
	        }

In the ontology will be refered as chromium-browser28.0.1500.52-0xcm1.13.04.3chromium-codecs-ffmpeg. 
First comes the package "chromium-browser" then its version "28.0.1500.52-0xcm1.13.04.3" and finally the dependency itself "chromium-codecs-ffmpeg".

This is due to the ontology restriction that each relationship must be unique in order to reduce conflicts between the entities envolved.

---

##Author

Luís Marques <luis.marques@caixamagica.pt>

---

##License

Copyright (c) 2014, Caixa Magica Software Lda (CMS).
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.

