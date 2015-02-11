# Extractors API

This API is responsible for all external access and internal management of given requests. It shall start a process for each request and return corresponding result.

This project cannot be used as a standalone.

&nbsp;

## How to get the code

	$ git clone https://opensourceprojects.eu/git/p/timbus/context-population/extraction-manager extractors-api

&nbsp;

## Requirements

- [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads) (>= 1.7) _[how to install](/p/timbus/wiki/How%20to%20install:%20Java/)_
- [Maven](http://maven.apache.org/download.cgi) (>= 2)
- [_core_, _osgi_ and _osgi-jersey-webservice_ parents](/p/timbus/support/maven-parents/)
- [Extractors Core](/p/timbus/context-population/extractors-core) library
- Internet access **or** manually download and install dependencies to Maven local repository

&nbsp;

## Usage

### Virgo server

In order to be able to deploy this project to Virgo, execute the following command from project's root folder:

	#!bash
	
	$ mvn clean package

Once the API is built and Virgo is running, access the Virgo's control panel and deploy the resulting .war file.

&nbsp;

### Working with extractors
As mentioned, this project is responsible for delegating extractions to all extractor modules. The [Osgi Framework](http://www.osgi.org/Main/HomePage) is used to modularize the Context Population Framework and, hence, the Extractors API searches through OSGI for any extractor module that implements the *IExtractor* interface - present in [Extractors Core](https://opensourceprojects.eu/p/timbus/context-population/extractors-core) - and making its service available. Further information on how to implement an extractor that Extractors API recognizes and is able to use can be found in this **[guide](https://opensourceprojects.eu/p/timbus/context-population/extractors/wiki/How%20to%20create%20a%20new%20Extractor/)**.
The API offers a Web Service with a set of endpoints to query information from the framework or order *Jobs* - Each Extraction is delegated as a *Job*, which consists of an independent thread in the system.
"/extractors/api/extract" endpoint accepts the following POST format requests:

	#!json

	{
	extractions: [
		{
			{
				parameters: {
					fqdn: "localhost",
					port: "22",
					user: "timbus",
					password: "password"
				},
				module: "Linux Hardware Extractor"
			}
		}
	],
	callback: {
		endpointPath : '/results',
		endpointPort: 3000,
		originRequestType: 'post'
		}
	}

This request orders a list of extraction jobs, each of them a single *Job*. As can be seen, besides the extractor's information the Extractors API also receives callback information: As the job response is assynchronous, Extractors API can be called to send a request to a certain endpoint upon finishing a Job. In this case, the callback is being asked in the form of a POST request - which contains the result of the extraction in its body - and it even specifies the endpoint path and port. 

In order to facilitate such requests, the [Context Population GUI](https://opensourceprojects.eu/p/timbus/context-population/context-population-gui/) was developed. This is a **nodejs** service which communicates with Extractors API and [Converters API](https://opensourceprojects.eu/p/timbus/context-model/converters-api).


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

