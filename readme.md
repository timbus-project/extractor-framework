# Extractors Core

This core library contains main communication interface between all extraction-related OSGi-based projects.

This project cannot be used as a standalone.

&nbsp;

## How to get the code

	$ git clone https://opensourceprojects.eu/git/p/timbus/context-population/extractors-core

&nbsp;

## Requirements

- [Java Development Kit][req-java] (>= 1.6) _[how to install][osp-install-java]_
- [Maven][req-maven] (>= 2)
- [_core_ and _osgi_ parents][req-parents]
- Internet access **or** manually download and install [OSGi 4.3 core companion code](http://www.osgi.org/Download) to Maven local repository

&nbsp;

## Usage

### Virgo server

To deploy this project on Virgo from source code, execute the following command line in project's root folder:

	$ mvn clean package

&nbsp;

### Install to Maven local repository

In order to be able to develop new extractors or compile extraction-related projects, run the following command from project's root folder:

	$ mvn clean install
[This page](https://opensourceprojects.eu/p/timbus/context-population/extractors/wiki/How%20to%20create%20a%20new%20Extractor/) contains a comprehensive tutorial on how to develop an extractor that complies with TIMBUS architecture

&nbsp;

### Within an Extractor project

The most relevant component of Extractors Core is the interface IExtractor. To develop an extractor that is recognized within the Context Population Framework, it is mandatory to implement this class. The *extract* method returns a String and this is where the result of the Extraction must be returned.
In Timbus Project Extractors, the Spring Framework was used to implement the OSGI framework. A typical Timbus extractor declares the following dependency in the *pom.xml* file:

	<dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>3.2.3.RELEASE</version>
        <scope>provided</scope>
    </dependency>

With this dependency within the project build path, it is now possible to declare the *BundleContext* object within the main Extractor class that implements *IExtractor*:

	@Autowired
	private BundleContext bundleContext;

Further on, in order for this bundle to be recognized within the Osgi framework, two .xml files are added in the resources folder, under **META-INF --> spring**:

**bundle-context-osgi.xml**:
	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:osgi="http://www.springframework.org/schema/osgi"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
	                  http://www.springframework.org/schema/osgi http://www.springframework.org/schema/osgi/spring-osgi.xsd">

		<!-- definitions using elements of the osgi namespace can be included
		   in this file. There is no requirement to keep these definitions
		   in a separate file if you do not want to. The rationale for 
		   keeping these definitions separate is to facilitate integration
		   testing of the bundle outside of an OSGi container -->

		<osgi:service interface="net.timbusproject.extractors.core.IExtractor" ref="extractor"/>

		<osgi:reference id="log" interface="org.osgi.service.log.LogService"/>

	</beans>

**bundle-context.xml**:
	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
	       xmlns:context="http://www.springframework.org/schema/context"
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
	            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

	    <!-- regular spring configuration file defining the beans for this
	         bundle. We've kept the osgi definitions in a separate
	         configuration file so that this file can easily be used
	         for integration testing outside of an OSGi environment -->

	    <bean name="extractor" class="net.timbusproject.extractors.modules.linuxhardware.remote.LinuxHardwareExtractor"/>

	    <context:annotation-config/>
	</beans>

**Note:** These examples are from the Linux Hardware Extractor. To implement a new extractor values in the .xml files obviously have to be updated.

&nbsp;

##Author

- Luís Duarte
- Jorge Simões (<jorge.simoes@caixamagica.pt>)
- Miguel Nunes (<miguel.nunes@caixamagica.pt>)

&nbsp;

##License

Copyright (c) 2014, Caixa Magica Software Lda (CMS).
The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise, unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this License or out of the use or inability to use the Work.
See the License for the specific language governing permissions and limitation under the License.

[req-java]: http://www.oracle.com/technetwork/java/javase/downloads
[req-maven]: http://maven.apache.org/download.cgi
[req-parents]: /p/timbus/support/maven-parents/
[osp-install-java]: /p/timbus/wiki/How%20to%20install:%20Java/

