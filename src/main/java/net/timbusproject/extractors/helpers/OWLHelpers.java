package net.timbusproject.extractors.helpers;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class OWLHelpers {

	/** Create an {@link OWLNamedIndividual} in the namespace of the given {@link OWLOntology} using the identifier */
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

	public static void main(String[] args) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.createOntology(IRI.create("http://timbusproject.net/test"));
		OWLNamedIndividual machineNode = createMachineNode(ontology, new MachineID().getUUID());
		System.out.println(machineNode);
	}
}
