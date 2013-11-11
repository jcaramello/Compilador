package asema.entities;


import java.util.ArrayList;
import java.util.List;

import asema.exceptions.SemanticErrorException;

public class BlockNode extends SentenceNode {
	
	/**
	 * Contexto sobre el cual se ejecuta el bloque, Entry Base deberia ser o una entrada de constructor
	 * o una entrada de metodo
	 */
	public EntryBase Context;

	public List<SentenceNode> Sentences;
	
	
	/**
	 * Contructor
	 */
	
	public BlockNode(List<SentenceNode> sentences){
		this.Sentences = new ArrayList<SentenceNode>();
		if(sentences != null)
			this.Sentences = sentences;
	}
	
	public void addSentence(){
		
		// Ver el tema de que las sentencias se agregen en orden
		// no recuerdo si teniamos que insertar al principio o al final de la lista.
	}
	
	@Override
	public void check() throws SemanticErrorException {
		// TODO Auto-generated method stub

	}

}
