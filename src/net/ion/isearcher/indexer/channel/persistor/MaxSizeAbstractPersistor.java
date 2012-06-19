package net.ion.isearcher.indexer.channel.persistor;

public abstract class MaxSizeAbstractPersistor<E> extends AbstractPersistor<E> {

	protected int maxSize;

	public MaxSizeAbstractPersistor(int maxSize) {
		this.maxSize = maxSize;
	}

	public int getDataSize() {
		return this.maxSize;
	}

}
