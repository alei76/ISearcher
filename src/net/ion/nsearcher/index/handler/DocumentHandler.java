package net.ion.nsearcher.index.handler;

import java.io.IOException;

import net.ion.nsearcher.common.WriteDocument;
import net.ion.nsearcher.index.event.CollectorEvent;

public interface DocumentHandler {
	
	public final static float HEAD_BOOST = 2f;
	

	WriteDocument[] makeDocument(CollectorEvent event) throws IOException;

}
