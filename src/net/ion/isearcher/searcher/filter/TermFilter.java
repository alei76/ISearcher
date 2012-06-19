package net.ion.isearcher.searcher.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.ion.framework.util.StringUtil;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.OpenBitSet;


public class TermFilter extends Filter {

	private static final long serialVersionUID = -6690325079949112622L;
	private Set<Term> terms = new TreeSet<Term>();
	
	public TermFilter(){
	}
	
	public TermFilter(String field, String value){
		addTerm(new Term(field, value)) ;
	}
	
	public TermFilter addTerm(Term term) {
		terms.add(term);
		return this ;
	}

	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		OpenBitSet result = new OpenBitSet(reader.maxDoc());
		TermDocs td = reader.termDocs();
		
		try {
			for (Term term : terms) {
				td.seek(term);
				while (td.next()) {
					result.set(td.doc());
				}
			}
		} finally {
			td.close();
		}
		return result;
	}
	
	public boolean containsField(String field){
		for (Term term : terms) {
			if (StringUtil.equals(field, term.field())) return true ;
		}
		return false ;
	}

	public boolean equals(Object _that) {
		if (this == _that)
			return true;
		if (! (_that instanceof TermFilter))
			return false;
		
		TermFilter that = (TermFilter) _that;
		return (terms == that.terms || (terms != null && terms.equals(that.terms)));
	}

	public int hashCode() {
		int hash = 9;
		for (Iterator<Term> iter = terms.iterator(); iter.hasNext();) {
			Term term = (Term) iter.next();
			hash = 31 * hash + term.hashCode();
		}
		return hash;
	}


	public String toString(){
		return getClass() + "[" + this.terms + "]" ;
	}
}
