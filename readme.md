# Debian Software Extractor
## Table of Contents
[TOC]

---

## About the extractor
This tool allows extracting software packages metadata from [Debian-based Linux distributions][debian-based].

It features two extracting methods:

- **installed packages extraction:** extracts all installed packages on the target machine; and
- **universe extraction:** extracts all known packages to the target machine, including installed remote repositories (or [Personal Package Archives (PPA)][ppa])

Collected information mainly refers to:

- **package control information** (name, version, dependencies, etc);
- **licenses**;
- **installers remote location**; and
- **distribution information** (id, release, codename, architecture)

This tool is able to extract from local or remote target machines.

---

## Requirements

### Requirements for running the tool
On the server or machine running the command-line interface:

- Java Runtime Environment (JRE) 1.6 ([OpenJDK][] or [Oracle JRE][])

On the target machines:

- [Debian-based distribution][debian-based] with:
    - low-level [dpkg][];
    - higher-level [apt][] _(optional, allows extracting installers remote location)_;
    - [devscripts][] package _(optional, allows extracting packages' licenses)_; and
    - [lsb-release][] package _(optional, allows extracting operating system information)_
- **(if remotely:)** [SSH server running][ssh-server] with authenticated user

### Requirements for building the tool
- Java Development Kit (JDK) 1.6 ([OpenJDK][] or [Oracle JDK][]); and
- [Apache Maven][maven]

---

## How to get the code
    :::bash
    $ git clone https://opensourceprojects.eu/git/p/timbus/context-population/extractors/debian-software

---

## Using the tool

### Using the tool from the GUI (Graphical User Interface)
Run the [Context Population GUI][], open it on a web browser and follow on-screen instructions.

### Using the tool from the CLI (Command-Line Interface)
The command-line interface offers a range of parameters:

    java -jar debian-software-extractor-cli-{version}.jar [--debug | -q] [-h]
    [-l | -r <user@host:port>] [-o <file>] [-p] [-u]
         --debug                   Provides more details
      -h,--help                    Shows help
      -l,--local                   Extracts locally
      -o,--output <file>           Outputs results to file
      -p,--pretty                  Returns a formatted result
      -q,--quiet                   Provides less details
      -r,--remote <user@host:port> Extracts remotely
      -u,--universe                Extracts universe

**Local extraction** does not need extra input or authentication. **Remote extraction** allows multiple targets to be set.

By default, result is printed to the _standard output_. If output file is set, it is then saved to file and not printed. For **multiple extractions**, output files are named after remote location and port (this is appended).

---

## Extracted information
This tool gathers all the information into JSON format, which is afterwards sent for conversion, using the [Debian Software Converter][], into ontology.

### Example extraction result
    :::json
    {
      "extractor": "Debian Software Extractor",
      "format": {
        "id": "d17250e8-af6e-5b84-8fab-404d5ecee47f",
        "multiple": false
      },
      "uuid": "xxxxxxxx-xxxx-1xxx-xxxx-xxxxxxxxxxxx",
      "result": {
        "isUniverse": false,
        "machineId": "xri:\/\/...",
        "operatingSystem": {
          "distribution": "Ubuntu",
          "release": "12.04",
          "codename": "precise",
          "architecture": "x86_64"
        },
        "data": [
          {
            "Package": "lsb-release",
            "Status": "install ok installed",
            "Multi-Arch": "foreign",
            "Priority": "extra",
            "Section": "misc",
            "Installed-Size": "111",
            "Maintainer": "Ubuntu Developers <ubuntu-devel-discuss@lists.ubuntu.com>",
            "Architecture": "all",
            "Source": "lsb",
            "Version": "4.0-0ubuntu20.2",
            "Depends": [
              {"Package": "python2.7"},
              {
                "Package": "python",
                "Comparator": ">=",
                "Version": "2.7.1-0ubuntu2"
              },
              {
                "Package": "python",
                "Comparator": "<<",
                "Version": "2.8"
              }
            ],
            "Recommends": [{"Package": "apt"}],
            "Suggests": [{"Package": "lsb"}],
            "Description": "Linux Standard Base version reporting utility\n The Linux Standard Base (http:\/\/www.linuxbase.org\/) is a standard\n core system that third-party applications written for Linux can\n depend upon.\n .\n The lsb-release command is a simple tool to help identify the Linux\n distribution being used and its compliance with the Linux Standard Base.\n LSB conformance will not be reported unless the required metapackages are\n installed.\n .\n While it is intended for use by LSB packages, this command may also\n be useful for programmatically distinguishing between a pure Debian\n installation and derived distributions.",
            "Homepage": "http:\/\/www.linux-foundation.org\/en\/LSB",
            "Original-Maintainer": "Chris Lawrence <lawrencc@debian.org>",
            "Installer": "http:\/\/ie.archive.ubuntu.com\/ubuntu\/pool\/main\/l\/lsb\/lsb-release_4.0-0ubuntu20_all.deb"
          },
          ...
        ]
      }
    }

This is an example extraction, showing a single package on a specific system from a specific time. Extractions are expected to vary due to systems themselves and packages actually known to (or installed on) the target system.

---

## TIMBUS Use Cases
This extractor is relevant to all [TIMBUS][] use cases concerning [Debian-based Linux environments][debian-based]:

- Phaidra;
- OpenSource Workflows;
- GestBarragens’ Complex Maths Simulation nodes; and
- eHealth

Each of these use cases is run on top of a [Debian-based Linux environment][debian-based] with its own characteristics and specific configurations. In order to enable the redeployment of the use case in the future, all relevant software packages, corresponding metadata and interrelations with other packages must be preserved.

Taking the extraction depicted above, describing `lsb-release` package, it shows it has multiple dependencies. The same is expected to happen with other packages (as well as with `lsb-release`'s dependencies). During the process of conversion, this package and corresponding dependencies are linked one to the other on the ontology. The package being extracted is identified with its name and version and linked to its dependencies. Each dependency is identified with corresponding name and version restriction (both comparator and version). These dependencies are then linked to other concrete packages either installed or known to the package manager.

---

## Authors
- Luís Marques
- Jorge Simões (<jorge.simoes@caixamagica.pt>)

---

## License
Copyright (c) 2014, Caixa Magica Software Lda (CMS)
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.



[timbus]: http://www.timbusproject.net/ "TIMBUS Project - Timeless Business, Processes and Services"

[openjdk]: http://openjdk.java.net/install/ "OpenJDK 1.6"
[oracle jdk]: http://www.oracle.com/technetwork/java/javase/downloads "Oracle Java Development Kit 1.6"
[oracle jre]: http://java.com/en/download/manual.jsp "Oracle Java Runtime Environment 1.6"
[maven]: http://maven.apache.org/download.cgi#Installation "Apache Maven"

[debian-based]: http://www.debian.org/misc/children-distros "Debian-based distributions"
[ppa]: http://www.makeuseof.com/tag/ubuntu-ppa-technology-explained/ "What is an Ubuntu PPA?"
[dpkg]: http://en.wikipedia.org/wiki/Dpkg "Debian's package manager"
[apt]: http://en.wikipedia.org/wiki/Advanced_Packaging_Tool "Advanced Package Tool"
[devscripts]: http://packages.debian.org/sid/devscripts "devscripts"
[lsb-release]: https://packages.debian.org/sid/lsb-release "lsb-release"
[ssh-server]: http://www.cyberciti.biz/faq/debian-linux-install-openssh-sshd-server/#content "Installing and starting SSH server"

[context population gui]: http://opensourceprojects.eu/p/timbus/context-population/context-population-gui "Context Population GUI repository"
[debian software converter]: http://opensourceprojects.eu/p/timbus/context-model/converters/json-xml/deb-converter "Debian Software Converter repository"
