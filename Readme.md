#Hardware Linux Extractor

The Hardware Linux Extractor is a tool developed in Java and extracts hardware information of a Linux
machine.
Utilizing a set of external tools, this module extracts information from a target machine and structures it
with the aim of subsequently populating an ontology with all the machine's hardware dependencies.
The extractor is composed by two different tools: A bundle to be deployed in Virgo container and used
through the framework and a local CLI tool to be used locally, through the Terminal

&nbsp;

##How to get the code

	git clone https://opensourceprojects.eu/git/p/timbus/context-population/extractors/debian-software timbus-context-population-extractors-debian-software 

&nbsp;

##Install Requirements

1. [Oracle Java JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

##Requirements for the extraction target

1. [Linux installed](http://en.wikipedia.org/wiki/list_of_Linux_distributions)
2. [lshw installed](http://ezix.org/project/wiki/HardwareLiSter)
3. [lspci Installed](http://en.wikipedia.org/wiki/Lspci) - included in most linux distributions
4. Running sftp server
5. [SSH server running with authenticated user](http://www.cyberciti.biz/faq/how-to-installing-and-using-ssh-client-server-in-linux/)

&nbsp;

##Collected Information

This extractor gathers hardware information from a linux machine using the [lshw cli tool](http://ezix.org/project/wiki/HardwareLiSter). 
This is helpful when the target is a remote machine and you dont have physical acess to the machine.

&nbsp;

##How to execute
Locally:

	#!bash

	java -jar linux-hardware-extractor.jar

Available options:
-f,--text-file load request from text file
-h,--help print help (no arguments)
-j,--json-file  load request from JSON file
-l,--local-extraction Do local extraction (no arguments)
-o,--out file to save output to
-s,--show-extraction Show extraction in stdout (no arguments)
&nbsp;
Remotely:
The [Context Population GUI](testbed.timbusproject.net:3001) was developed as an interface for the [Core Extraction Manager tool](https://opensourceprojects.eu/p/timbus/context-population/extraction-manager/). 
 To perform an extraction on a remote machine through the GUI, all is needed is to access the GUI, select Linux HW module in the extractors selection box and provide the target machine's information.
  Further information on how to install and use the Extractors Manager and Context Population GUI is available [here](http://timbusproject.net/portal).
&nbsp;

##Collected information
In order to collect all relevant information, the extractor goes through three phases to capture different
pieces of data from the target machine:
1. Through the lshw cli tool – This is used to gather the majority of the machine's hardware metadata.
It produces a structured output of the most relevant hardware components – This includes
processor information, RAM, Motherboard, audio and graphical controllers, etc.;
2. The i-nex-cpuid tool (http://i-nex.linux.pl) – As this is an external tool (not included in any
Linux distribution's base packages), the extractor needs to upload this binary file through sftp into
the target machine (removing it after usage) and runs it remotely, capturing the output. This tool
provides further detailed information about the machine's processor;
3. The lspci tool – It provides information concerning various hardware components. For most of
them, the aforementioned lshw tool provides more detailed information, however the machine's
GPU is better described in this tool's output, particularly its memory capacity.

lshw Output Layout
	
	#!JSON

	"children" : [
		{
			"id" : "display:0",
			"class" : "display",
			"claimed" : true,
			"handle" : "PCI:0000:00:02.0",
			"description" : "VGA compatible controller",
			"product" : "Mobile GM965/GL960 Integrated Graphics Controller (primary)",
			"vendor" : "Intel Corporation",
			"physid" : "2",
			"businfo" : "pci@0000:00:02.0",
			"version" : "0c",
			"width" : 64,
			"clock" : 33000000,
			"configuration" : {
			"driver" : "i915",
			"latency" : "0"
		},
		"capabilities" : {
			"vga_controller" : true,
			"bus_master" : "bus mastering",
			"cap_list" : "PCI capabilities listing",
			"rom" : "extension ROM"
			}
		},
 
i-nex-cpuid Output Layout 

	#!JSON

	"VENDOR_STR": "GenuineIntel",
	"CPU_CODENAME": "Merom (Core 2 Duo) 2048K",
	"BRAND_STR": "Intel(R) Core(TM)2 Duo CPU T7250 @ 2.00GHz",
	"NUM_CORES": "2",
	"NUM_LOGICAL_CPUS": "2",
	"TOTAL_LOGICAL_CPUS": "2",
	"FAMILY": "6",
	"MODEL": "15",
	"STEPPING": "13",
	"EXT_FAMILY": "6",
	"EXT_MODEL": "15",
	"CPU_CLOCK": "2000",
	"CPU_CLOCK_BY_OS": "2000",
	"CPU_CLOCK_BY_IC": "-2",
	"CPU_CLOCK_MEASURE": "1994",
	"MARK_TSC": "2950",
	"MARK_SYS_CLOCK": "1",
	"Flags": {
		"1": {
			"VALUE": 0,
			"NAME": "CPU_FEATURE_MMXEXT",
			"FEATURE": "mmxext",
			"WEBSITE": "http://en.wikipedia.org/wiki/MMX_(instruction_set)",
			"HAVEWEBSITE": 1,
			"DESC": "AMD MMX-extended instructions supported"
		}, 
lspci output layout
	
	#!bash
	
	00:02.0 VGA compatible controller: Intel Corporation Mobile GM965/GL960 Integrated Graphics
	Controller (primary) (rev 0c) (prog-if 00 [VGA controller])
	Subsystem: Lenovo ThinkPad T61/R61
	Flags: bus master, fast devsel, latency 0, IRQ 47
	Memory at f8100000 (64-bit, non-prefetchable) [size=1M]
	Memory at e0000000 (64-bit, prefetchable) [size=256M]
	I/O ports at 1800 [size=8]
	Expansion ROM at <unassigned> [disabled]
	Capabilities: <access denied>
	Kernel driver in use: i915

&nbsp;


##Generated Concepts and Properties

All the components that are obtained by the extractor are related to a single machine. This allows to map all the inviduals pieces that constitute the target machine and converting to an ontology. 

&nbsp;

##TIMBUS Use Cases

###Industrial Case Study: Civil Engineering Infrastructure

This extractor is applied to an industrial use, namely infrastructure. In enviroments where the hardware runs Linux enviroment, e.g servers, datacenters and other backend hardware, it is critical to business processes to keep this information in cases of hardware failure.
Each company, university or and institution run a different hardware enviroment according to its needs, this specificity needs to be stored and kept in order to maintain, repair or replacement of components.




##Authors

Luís Marques <luis.marques@caixamagica.pt>
Miguel Gama Nunes <miguel.nunes@caixamagica.pt>

&nbsp;

##License

Copyright (c) 2014, Caixa Magica Software Lda (CMS).
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.
