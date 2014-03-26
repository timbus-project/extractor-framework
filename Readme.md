#Hardware Linux Extractor

Hardware Linux Extractor is a tool developed in Java and extracts hardware information of a linux machine.

&nbsp;

##How to get the code

	git clone https://opensourceprojects.eu/git/p/timbus/context-population/extractors/debian-software timbus-context-population-extractors-debian-software 

&nbsp;

##Install Requirements

1. [Oracle Java JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

##Requirements for the extraction target

1. [Linux installed](http://en.wikipedia.org/wiki/list_of_Linux_distributions)
2. [lshw installed](http://ezix.org/project/wiki/HardwareLiSter)
3. [SSH server running with authenticated user](http://www.cyberciti.biz/faq/how-to-installing-and-using-ssh-client-server-in-linux/)

&nbsp;

##Collected Information

This extractor gathers hardware information from a linux machine using the [lshw cli tool](http://ezix.org/project/wiki/HardwareLiSter). 
This is helpful when the target is a remote machine and you dont have physical acess to the machine.

&nbsp;

##How to execute

	#!bash
	java -jar linux-hardware-extractor.jar
&nbsp;

##lshw Output Layout

	#!bash
	system information
        motherboard information
        cpu information
	cache, logical cpu
        memory
	capacity, total size, individual bank information
        pci slot information
        ide slot information
	disk information
	total size, partition,
        usb slot information
        network
.... 



Note: It is recommend that you have sudo privilieges in the target machine, if you dont have the extractor will not provide information of the motherboard.

&nbsp;

The output will be in JSON format for easier parsing to a converter or other tool that requires structured output.

&nbsp;

##Example output:
	#!json
	{
              "id" : "usb:7",
              "class" : "bus",
              "claimed" : true,
              "handle" : "PCI:0000:00:1d.7",
              "description" : "USB controller",
              "product" : "82801I (ICH9 Family) USB2 EHCI Controller #1",
              "vendor" : "Intel Corporation",
              "physid" : "1d.7",
              "businfo" : "pci@0000:00:1d.7",
              "version" : "03",
              "width" : 32,
              "clock" : 33000000,
              "configuration" : {
                "driver" : "ehci-pci",
                "latency" : "0"
              },
              "capabilities" : {
                "pm" : "Power Management",
                "debug" : "Debug port",
                "ehci" : "Enhanced Host Controller Interface (USB2)",
                "bus_master" : "bus mastering",
                "cap_list" : "PCI capabilities listing"
              }
            },
            {
              "id" : "pci:6",
              "class" : "bridge",
              "claimed" : true,
              "handle" : "PCIBUS:0000:08",
              "description" : "PCI bridge",
              "product" : "82801 Mobile PCI Bridge",
              "vendor" : "Intel Corporation",
              "physid" : "1e",
              "businfo" : "pci@0000:00:1e.0",
              "version" : "93",
              "width" : 32,
              "clock" : 33000000,
              "capabilities" : {
                "pci" : true,
                "subtractive_decode" : true,
                "bus_master" : "bus mastering",
                "cap_list" : "PCI capabilities listing"
              }
            },
            {
              "id" : "isa",
              "class" : "bridge",
              "claimed" : true,
              "handle" : "PCI:0000:00:1f.0",
              "description" : "ISA bridge",
              "product" : "ICH9M LPC Interface Controller",
              "vendor" : "Intel Corporation",
              "physid" : "1f",
              "businfo" : "pci@0000:00:1f.0",
              "version" : "03",
              "width" : 32,
              "clock" : 33000000,
              "configuration" : {
                "driver" : "lpc_ich",
                "latency" : "0"
              },
              "capabilities" : {
                "isa" : true,
                "bus_master" : "bus mastering",
                "cap_list" : "PCI capabilities listing"
              }
            },
            {
              "id" : "ide:0",
              "class" : "storage",
              "claimed" : true,
              "handle" : "PCI:0000:00:1f.2",
              "description" : "IDE interface",
              "product" : "82801IBM/IEM (ICH9M/ICH9M-E) 2 port SATA Controller [IDE mode]",
              "vendor" : "Intel Corporation",
              "physid" : "1f.2",
              "businfo" : "pci@0000:00:1f.2",
              "version" : "03",
              "width" : 32,
              "clock" : 66000000,
              "configuration" : {
                "driver" : "ata_piix",
                "latency" : "0"
              },
              "capabilities" : {
                "ide" : true,
                "pm" : "Power Management",
                "bus_master" : "bus mastering",
                "cap_list" : "PCI capabilities listing"
              }


##Generated Concepts and Properties

All the components that are obtained by the extractor are related to a single machine. This allows to map all the inviduals pieces that constitute the target machine and converting to an ontology. 

&nbsp;

##TIMBUS Use Cases

###Industrial Case Study: Civil Engineering Infrastructure

This extractor is applied to an industrial use, namely infrastructure. In enviroments where the hardware runs Linux enviroment, e.g servers, datacenters and other backend hardware, it is critical to business processes to keep this information in cases of hardware failure.
Each company, university or and institution run a different hardware enviroment according to its needs, this specificity needs to be stored and kept in order to maintain, repair or replacement of components.

##Author

Luís Marques <luis.marques@caixamagica.pt>

&nbsp;

##License

Copyright (c) 2014, Caixa Magica Software Lda (CMS).
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.
