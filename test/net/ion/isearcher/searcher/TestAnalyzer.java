package net.ion.isearcher.searcher;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ion.framework.db.cache.Cache;
import net.ion.framework.util.Debug;
import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.impl.Central;
import net.ion.isearcher.impl.ISearcher;
import net.ion.isearcher.indexer.write.IWriter;
import net.ion.isearcher.searcher.processor.StdOutProcessor;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.kr.KoreanAnalyzer;
import org.apache.lucene.analysis.kr.morph.WordEntry;
import org.apache.lucene.analysis.kr.utils.DictionaryUtil;
import org.apache.lucene.analysis.kr.utils.FileUtil;
import org.apache.lucene.analysis.kr.utils.KoreanEnv;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.instantiated.InstantiatedIndex;
import org.apache.lucene.store.instantiated.InstantiatedIndexReader;
import org.apache.lucene.util.Version;

import junit.framework.Test;
import junit.framework.TestCase;

public class TestAnalyzer extends TestCase {

	public void testTokenStream() throws Exception {

		String think = "사람이 존재하는 이유는 생각하기 때문인가";
		Debug.debug(DictionaryUtil.existJosa("은"), DictionaryUtil.existEomi(think), DictionaryUtil.getCNoun(think)) ;
		Debug.debug(DictionaryUtil.getAdverb(think), DictionaryUtil.getWord("사람"), DictionaryUtil.getVerb(think)) ;
		WordEntry word = DictionaryUtil.getWord("사람");
		Debug.debug(word, (word == null ? "" : word.getWord())) ;
	}
	
	public void testDicRead() throws Exception {
		
		List strList = FileUtil.readLines(KoreanEnv.getInstance().getValue("dictionary.dic"), "UTF-8");
		Debug.debug(strList.size()) ;
	}
	
//	public void testAnalyzer() throws Exception {
//		long start = System.nanoTime() ;
//		MyKoreanAnalyzer anal = new MyKoreanAnalyzer();
//		String stmt = "재주_기예 사람 은존재한다. 서울e플러스펀드 SCH_B500 1(주식)종류A";
//		TokenStream tokenStream = anal.tokenStream("abc", new StringReader(stmt));
//		Debug.line((System.nanoTime() - start) / 1000000, "nano time") ;
//		
//		List<Token> store = new ArrayList<Token>() ;
//		Token t = null ;
//		
//		while((t = tokenStream.next()) != null){
//			if (t != null) store.add(t) ;
//		}
//		Collections.sort(store, new Comparator(){
//			public int compare(Object left, Object right) {
//				return ((Token)left).startOffset() -((Token)right).startOffset() ;
//			}
//		}) ;
//		
//		Debug.debug(store) ;
//		
//		assertEquals(true, findKeyword(true, anal, stmt, "e플러스")) ;
//		assertEquals(true, findKeyword(false, anal, stmt, "B500")) ;
//		assertEquals(true, findKeyword(false, anal, stmt, "SCH")) ;
//		assertEquals(true, findKeyword(false, anal, stmt, "SCH-B500")) ;
//		assertEquals(true, findKeyword(false, anal, stmt, "종류a")) ;
//	}
	
	public void testQuery() throws Exception {
		Analyzer anal = new MyKoreanAnalyzer() ;
		ISearchRequest mreq = SearchRequest.create("사람은 존재한다", null, anal);
		ISearchRequest kreq= SearchRequest.create("사람은 존재한다", null, new KoreanAnalyzer());
		
		Debug.debug(mreq.getQuery()) ;
		Debug.debug(kreq.getQuery()) ;
	}
	
	
	
	
	
	
	public boolean findKeyword(boolean printTerm, Analyzer anal, String stmt, String term) throws Exception {
		Directory dir = new RAMDirectory();
		Central c = Central.createOrGet(dir);

		IWriter writer = c.testIndexer(anal);
		writer.begin("my");

		MyDocument doc = MyDocument.testDocument();
		doc.add(MyField.text("name", stmt));
		writer.insertDocument(doc);
		writer.end() ;

		if (printTerm) printTerm(c.newReader().getIndexReader(), "name") ;
		
		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		ISearcher searcher = c.newSearcher();
		searcher.addPostListener(stdOutProcessor);

		ISearchRequest req = SearchRequest.create(term, null, anal);
		return searcher.search(req).getTotalCount() > 0;
	}
	
	private void printTerm(IndexReader reader, String name) throws Exception {
		InstantiatedIndex iidx = new InstantiatedIndex(reader) ;
		TermEnum tenum = new InstantiatedIndexReader(iidx).terms() ;
		
		// Debug.debug(reader.maxDoc()) ;
		while(tenum.next()){
			Term term = tenum.term();
			if (term.field().toString().equals(name))
				Debug.debug(term, term.field(), term.text()) ;
		}
	}
	
	
	public void testPrintType() throws Exception {
		Debug.debug("COMBINING_SPACING_MARK", Character.COMBINING_SPACING_MARK) ;
		Debug.debug("CONNECTOR_PUNCTUATION", Character.CONNECTOR_PUNCTUATION) ;
		Debug.debug("CONTROL", Character.CONTROL) ;
		Debug.debug("CURRENCY_SYMBOL", Character.CURRENCY_SYMBOL) ;
		Debug.debug("DASH_PUNCTUATION", Character.DASH_PUNCTUATION) ; 
		Debug.debug("DECIMAL_DIGIT_NUMBER", Character.DECIMAL_DIGIT_NUMBER) ;
		Debug.debug("ENCLOSING_MARK", Character.ENCLOSING_MARK) ;
		Debug.debug("END_PUNCTUATION", Character.END_PUNCTUATION) ;
		Debug.debug("FINAL_QUOTE_PUNCTUATION", Character.FINAL_QUOTE_PUNCTUATION) ; 
		Debug.debug("FORMAT", Character.FORMAT) ;
		Debug.debug("INITIAL_QUOTE_PUNCTUATION", Character.INITIAL_QUOTE_PUNCTUATION) ;
		Debug.debug("LETTER_NUMBER", Character.LETTER_NUMBER) ;
		Debug.debug("LINE_SEPARATOR", Character.LINE_SEPARATOR) ;
		Debug.debug("LOWERCASE_LETTER", Character.LOWERCASE_LETTER) ; 
		Debug.debug("MATH_SYMBOL", Character.MATH_SYMBOL) ;
		Debug.debug("MODIFIER_LETTER", Character.MODIFIER_LETTER) ;
		Debug.debug("MODIFIER_SYMBOL", Character.MODIFIER_SYMBOL) ;
		Debug.debug("NON_SPACING_MARK", Character.NON_SPACING_MARK) ; 
		Debug.debug("OTHER_LETTER", Character.OTHER_LETTER) ; 
		Debug.debug("OTHER_NUMBER", Character.OTHER_NUMBER) ; 
		Debug.debug("OTHER_PUNCTUATION", Character.OTHER_PUNCTUATION) ;
		Debug.debug("OTHER_SYMBOL", Character.OTHER_SYMBOL) ;
		Debug.debug("PARAGRAPH_SEPARATOR", Character.PARAGRAPH_SEPARATOR) ;
		Debug.debug("PRIVATE_USE", Character.PRIVATE_USE) ;
		Debug.debug("SPACE_SEPARATOR", Character.SPACE_SEPARATOR) ;
		Debug.debug("START_PUNCTUATION", Character.START_PUNCTUATION ) ; 
		Debug.debug("SURROGATE", Character.SURROGATE) ; 
		Debug.debug("TITLECASE_LETTER", Character.TITLECASE_LETTER) ;
		Debug.debug("UNASSIGNED", Character.UNASSIGNED) ;
		Debug.debug("UPPERCASE_LETTER", Character.UPPERCASE_LETTER) ;
	}
	
}
