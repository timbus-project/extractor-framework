/**
 * Copyright (c) 2013, Caixa Magica Software Lda (CMS).
 * The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
 * TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological
 * development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without
 * limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR
 * PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise,
 * unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including
 * any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this
 * License or out of the use or inability to use the Work.
 * See the License for the specific language governing permissions and limitation under the License.
 */
package net.timbusproject.extractors.helpers;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class OWLHelpers {

	/** Create an {@link OWLNamedIndividual} in the namespace of the given {@link OWLOntology} using the identifier */
    @Deprecated
	public static OWLNamedIndividual createMachineNode(OWLOntology ontology, String identifier) {

		OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		IRI nodeIRI = IRI.create(ontology.getOntologyID().getOntologyIRI() + "#" + identifier);
		OWLNamedIndividualImpl node = new OWLNamedIndividualImpl(nodeIRI);

		// assign it to be of class Node
		OWLClass nodeClass = dataFactory.getOWLClass(IRI.create("http://timbus.teco.edu/ontologies/DIO.owl#Node"));
		OWLClassAssertionAxiom classAssertion = dataFactory.getOWLClassAssertionAxiom(nodeClass, node);
		ontology.getOWLOntologyManager().addAxiom(ontology, classAssertion);

		return node;
	}

    public static OWLNamedIndividual createMachineNode(OWLOntology ontology, String identifier, PrefixManager prefixManager){


        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLNamedIndividual node = dataFactory.getOWLNamedIndividual(identifier, prefixManager);

        // assign it to be of class Node
        OWLClass nodeClass = dataFactory.getOWLClass(IRI.create("http://timbus.teco.edu/ontologies/DIO.owl#Node"));
        OWLClassAssertionAxiom classAssertion = dataFactory.getOWLClassAssertionAxiom(nodeClass, node);
        ontology.getOWLOntologyManager().addAxiom(ontology, classAssertion);

        return node;
    }

	public static void main(String[] args) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.createOntology(IRI.create("http://timbusproject.net/test"));
		OWLNamedIndividual machineNode = createMachineNode(ontology, new MachineID().getUUID());
		System.out.println(machineNode);
	}
}
