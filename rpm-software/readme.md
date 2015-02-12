# RPM Software Extractor
This tool allows extracting all installed software packages metadata on a given target machine from [RPM-based Linux distributions][rpm-based].

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

- [RPM-based distribution][rpm-based] with:
    - low-level [rpm][];
    - higher-level [yum][] _(optional, allows extracting installers remote location)_;
    - [lsb-release][] package _(optional, allows extracting operating system information)_
- **(if remotely:)** [SSH server running][ssh-server] with authenticated user

### Requirements for building the tool
- Java Development Kit (JDK) 1.6 ([OpenJDK][] or [Oracle JDK][]); and
- [Apache Maven][maven]

---

## How to get the code
    :::bash
    $ git clone https://opensourceprojects.eu/git/p/timbus/context-population/extractors/rpm-extractor

---

## Using the tool

### Using the tool from the GUI (Graphical User Interface)
Run the [Context Population GUI][], open it on a web browser and follow on-screen instructions.

### Using the tool from the CLI (Command-Line Interface)
The command-line interface offers a range of parameters:

    java -jar rpm-software-extractor-cli-{version}.jar [--debug | -q] [-h]
    [-l | -r <user@host:port>] [-o <file>] [-p]
         --debug                   Provides more details
      -h,--help                    Shows help
      -l,--local                   Extracts locally
      -o,--output <file>           Outputs results to file
      -p,--pretty                  Returns a formatted result
      -q,--quiet                   Provides less details
      -r,--remote <user@host:port> Extracts remotely

**Local extraction** does not need extra input or authentication. **Remote extraction** allows multiple targets to be set.

By default, result is printed to the _standard output_. If output file is set, it is then saved to file and not printed. For **multiple extractions**, output files are named after remote location and port (this is appended).

---

## Extracted information
This tool gathers all the information into JSON format, which is afterwards sent for conversion, using the [Debian Software Converter][] (which is compatible), into ontology.

### Example extraction result
    :::json
    {
      "extractor": "RPM Software Extractor",
      "format": {
        "id": "d17250e8-af6e-5b84-8fab-404d5ecee47f",
        "multiple": false
      },
      "uuid": "xxxxxxxx-xxxx-1xxx-xxxx-xxxxxxxxxxxx",
      "result": {
        "isUniverse": false,
        "machineId": "xri:\/\/...",
        "operatingSystem": {
          "distribution": "CentOS",
          "release": "6.5",
          "codename": "Final",
          "architecture": "x86_64"
        },
        "data": [
          {
            "Id": "redhat-lsb-core-4.0-7.el6.centos.x86_64",
            "Package": "redhat-lsb-core",
            "Basename": "redhat_lsb_init",
            "Version": "4.0-7.el6.centos",
            "Installed-Size": "22825",
            "Architecture": "x86_64",
            "License": "GPL",
            "Section": "System Environment\/Base",
            "Vendor": "CentOS",
            "Maintainer": "CentOS BuildSystem <http:\/\/bugs.centos.org>",
            "Depends": [
              {"Package": "\/bin\/awk"},
              {"Package": "\/bin\/basename"},
              {"Package": "\/bin\/bash"},
              {"Package": "\/bin\/cat"},
              {"Package": "\/bin\/chgrp"},
              {"Package": "\/bin\/chmod"},
              {"Package": "\/bin\/chown"},
              {"Package": "\/bin\/cp"},
              {"Package": "\/bin\/cpio"},
              {"Package": "\/bin\/cut"},
              {"Package": "\/bin\/date"},
              {"Package": "\/bin\/dd"},
              {"Package": "\/bin\/df"},
              {"Package": "\/bin\/dmesg"},
              {"Package": "\/bin\/echo"},
              {"Package": "\/bin\/ed"},
              {"Package": "\/bin\/egrep"},
              {"Package": "\/bin\/env"},
              {"Package": "\/bin\/false"},
              {"Package": "\/bin\/fgrep"},
              {"Package": "\/bin\/find"},
              {"Package": "\/bin\/gettext"},
              {"Package": "\/bin\/grep"},
              {"Package": "\/bin\/gunzip"},
              {"Package": "\/bin\/gzip"},
              {"Package": "\/bin\/hostname"},
              {"Package": "\/bin\/kill"},
              {"Package": "\/bin\/ln"},
              {"Package": "\/bin\/ls"},
              {"Package": "\/bin\/mailx"},
              {"Package": "\/bin\/mkdir"},
              {"Package": "\/bin\/mknod"},
              {"Package": "\/bin\/mktemp"},
              {"Package": "\/bin\/more"},
              {"Package": "\/bin\/mount"},
              {"Package": "\/bin\/mv"},
              {"Package": "\/bin\/nice"},
              {"Package": "\/bin\/ps"},
              {"Package": "\/bin\/pwd"},
              {"Package": "\/bin\/rm"},
              {"Package": "\/bin\/rmdir"},
              {"Package": "\/bin\/sed"},
              {"Package": "\/bin\/sh"},
              {"Package": "\/bin\/sh"},
              {"Package": "\/bin\/sh"},
              {"Package": "\/bin\/sh"},
              {"Package": "\/bin\/sleep"},
              {"Package": "\/bin\/sort"},
              {"Package": "\/bin\/stty"},
              {"Package": "\/bin\/su"},
              {"Package": "\/bin\/sync"},
              {"Package": "\/bin\/tar"},
              {"Package": "\/bin\/touch"},
              {"Package": "\/bin\/true"},
              {"Package": "\/bin\/umount"},
              {"Package": "\/bin\/uname"},
              {"Package": "\/bin\/zcat"},
              {"Package": "\/sbin\/fuser"},
              {"Package": "\/sbin\/pidof"},
              {"Package": "\/sbin\/shutdown"},
              {"Package": "\/usr\/bin\/["},
              {"Package": "\/usr\/bin\/ar"},
              {"Package": "\/usr\/bin\/at"},
              {"Package": "\/usr\/bin\/batch"},
              {"Package": "\/usr\/bin\/bc"},
              {"Package": "\/usr\/bin\/chfn"},
              {"Package": "\/usr\/bin\/chsh"},
              {"Package": "\/usr\/bin\/cksum"},
              {"Package": "\/usr\/bin\/cmp"},
              {"Package": "\/usr\/bin\/col"},
              {"Package": "\/usr\/bin\/comm"},
              {"Package": "\/usr\/bin\/crontab"},
              {"Package": "\/usr\/bin\/csplit"},
              {"Package": "\/usr\/bin\/diff"},
              {"Package": "\/usr\/bin\/dirname"},
              {"Package": "\/usr\/bin\/du"},
              {"Package": "\/usr\/bin\/expand"},
              {"Package": "\/usr\/bin\/expr"},
              {"Package": "\/usr\/bin\/file"},
              {"Package": "\/usr\/bin\/fold"},
              {"Package": "\/usr\/bin\/gencat"},
              {"Package": "\/usr\/bin\/getconf"},
              {"Package": "\/usr\/bin\/groups"},
              {"Package": "\/usr\/bin\/head"},
              {"Package": "\/usr\/bin\/iconv"},
              {"Package": "\/usr\/bin\/id"},
              {"Package": "\/usr\/bin\/install"},
              {"Package": "\/usr\/bin\/ipcrm"},
              {"Package": "\/usr\/bin\/ipcs"},
              {"Package": "\/usr\/bin\/join"},
              {"Package": "\/usr\/bin\/killall"},
              {"Package": "\/usr\/bin\/locale"},
              {"Package": "\/usr\/bin\/localedef"},
              {"Package": "\/usr\/bin\/logger"},
              {"Package": "\/usr\/bin\/logname"},
              {"Package": "\/usr\/bin\/m4"},
              {"Package": "\/usr\/bin\/make"},
              {"Package": "\/usr\/bin\/man"},
              {"Package": "\/usr\/bin\/md5sum"},
              {"Package": "\/usr\/bin\/mkfifo"},
              {"Package": "\/usr\/bin\/msgfmt"},
              {"Package": "\/usr\/bin\/newgrp"},
              {"Package": "\/usr\/bin\/nl"},
              {"Package": "\/usr\/bin\/nohup"},
              {"Package": "\/usr\/bin\/od"},
              {"Package": "\/usr\/bin\/passwd"},
              {"Package": "\/usr\/bin\/paste"},
              {"Package": "\/usr\/bin\/patch"},
              {"Package": "\/usr\/bin\/pathchk"},
              {"Package": "\/usr\/bin\/pax"},
              {"Package": "\/usr\/bin\/perl"},
              {"Package": "\/usr\/bin\/pr"},
              {"Package": "\/usr\/bin\/printf"},
              {"Package": "\/usr\/bin\/python"},
              {"Package": "\/usr\/bin\/renice"},
              {"Package": "\/usr\/bin\/seq"},
              {"Package": "\/usr\/bin\/split"},
              {"Package": "\/usr\/bin\/strip"},
              {"Package": "\/usr\/bin\/tail"},
              {"Package": "\/usr\/bin\/tee"},
              {"Package": "\/usr\/bin\/test"},
              {"Package": "\/usr\/bin\/time"},
              {"Package": "\/usr\/bin\/tr"},
              {"Package": "\/usr\/bin\/tsort"},
              {"Package": "\/usr\/bin\/tty"},
              {"Package": "\/usr\/bin\/unexpand"},
              {"Package": "\/usr\/bin\/uniq"},
              {"Package": "\/usr\/bin\/wc"},
              {"Package": "\/usr\/bin\/xargs"},
              {"Package": "\/usr\/lib\/lsb\/install_initd"},
              {"Package": "\/usr\/lib\/lsb\/remove_initd"},
              {"Package": "\/usr\/sbin\/groupadd"},
              {"Package": "\/usr\/sbin\/groupdel"},
              {"Package": "\/usr\/sbin\/groupmod"},
              {"Package": "\/usr\/sbin\/sendmail"},
              {"Package": "\/usr\/sbin\/useradd"},
              {"Package": "\/usr\/sbin\/userdel"},
              {"Package": "\/usr\/sbin\/usermod"},
              {
                "Package": "config(redhat-lsb-core)",
                "Comparator": "=",
                "Version": "4.0-7.el6.centos"
              },
              {"Package": "gawk"},
              {"Package": "libc.so.6()(64bit)"},
              {"Package": "libcrypt.so.1()(64bit)"},
              {"Package": "libdl.so.2()(64bit)"},
              {"Package": "libgcc_s.so.1()(64bit)"},
              {"Package": "libm.so.6()(64bit)"},
              {"Package": "libncurses.so.5()(64bit)"},
              {"Package": "libnspr4.so()(64bit)"},
              {"Package": "libnss3.so()(64bit)"},
              {"Package": "libpam.so.0()(64bit)"},
              {"Package": "libpthread.so.0()(64bit)"},
              {"Package": "librt.so.1()(64bit)"},
              {"Package": "libssl3.so()(64bit)"},
              {"Package": "libstdc++.so.6()(64bit)"},
              {"Package": "libutil.so.1()(64bit)"},
              {"Package": "libz.so.1()(64bit)"},
              {"Package": "perl-CGI"},
              {"Package": "perl-ExtUtils-MakeMaker"},
              {"Package": "perl-Test-Harness"},
              {"Package": "perl-Test-Simple"},
              {
                "Package": "rpmlib(CompressedFileNames)",
                "Comparator": "<=",
                "Version": "3.0.4-1"
              },
              {
                "Package": "rpmlib(FileDigests)",
                "Comparator": "<=",
                "Version": "4.6.0-1"
              },
              {
                "Package": "rpmlib(PayloadFilesHavePrefix)",
                "Comparator": "<=",
                "Version": "4.0-1"
              },
              {
                "Package": "rpmlib(VersionedDependencies)",
                "Comparator": "<=",
                "Version": "3.0.3-1"
              },
              {
                "Package": "rpmlib(PayloadIsXz)",
                "Comparator": "<=",
                "Version": "5.2-1"
              }
            ],
            "Provides": [
              {
                "Package": "config(redhat-lsb-core)",
                "Comparator": "=",
                "Version": "4.0-7.el6.centos"
              },
              {
                "Package": "lsb-core-amd64",
                "Comparator": "=",
                "Version": "4.0"
              },
              {
                "Package": "lsb-core-noarch",
                "Comparator": "=",
                "Version": "4.0"
              },
              {
                "Package": "redhat-lsb-core",
                "Comparator": "=",
                "Version": "4.0-7.el6.centos"
              },
              {
                "Package": "redhat-lsb-core(x86-64)",
                "Comparator": "=",
                "Version": "4.0-7.el6.centos"
              }
            ],
            "Installer": "http:\/\/mirrors.vooservers.com\/centos\/6.6\/os\/x86_64\/Packages\/redhat-lsb-core-4.0-7.el6.centos.x86_64.rpm"
          },
          ...
        ]
      }
    }

This is an example extraction, showing a single package on a specific system from a specific time. Extractions are expected to vary due to systems themselves and packages actually known to (or installed on) the target system.

---

## TIMBUS Use Cases
This extractor is relevant to all [TIMBUS][] use cases concerning [RPM-based Linux environments][rpm-based]:

- RCAAP

Each of these use cases is run on top of an [RPM-based Linux environment][rpm-based] with its own characteristics and specific configurations. In order to enable the redeployment of the use case in the future, all relevant software packages, corresponding metadata and interrelations with other packages must be preserved.

Taking the extraction depicted above, describing `lsb-release` package, it shows it has multiple dependencies. The same is expected to happen with other packages (as well as with `lsb-release`'s dependencies). During the process of conversion, this package and corresponding dependencies are linked one to the other on the ontology. The package being extracted is identified with its name and version and linked to its dependencies. Each dependency is identified with corresponding name and version restriction (both comparator and version). These dependencies are then linked to other concrete packages either installed or known to the package manager.

---

## Authors
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

[rpm-based]: http://en.wikipedia.org/wiki/List_of_Linux_distributions#RPM-based "RPM-based distributions"
[rpm]: http://en.wikipedia.org/wiki/RPM_Package_Manager "RPM Package Manager"
[yum]: http://en.wikipedia.org/wiki/Yellowdog_Updater,_Modified "Yellowdog Updater, Modified"
[lsb-release]: http://en.wikipedia.org/wiki/Linux_Standard_Base "lsb-release"
[ssh-server]: http://docs.oracle.com/cd/E37670_01/E41138/html/ch25s03.html "Installing and starting SSH server"

[context population gui]: http://opensourceprojects.eu/p/timbus/context-population/context-population-gui "Context Population GUI repository"
[debian software converter]: http://opensourceprojects.eu/p/timbus/context-model/converters/json-xml/deb-converter "Debian Software Converter repository"
