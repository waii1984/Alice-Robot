package com.aiml;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import com.background.DBOperation;
import com.customexception.AppException;
import com.util.Util;

public class IndexResource {
	public DBOperation dbObject = null;
	public IndexWriter indexWriter = null;
	public IndexReader indexReader = null;
	public IndexSearcher indexSearcher = null;

	private static IndexResource resource = null;

	private IndexResource() {
		init();
	}

	private void init() {
		dbObject = DBOperation.getInstance();
		dbObject.linkDataBase();
		indexReader = buildIndexReader();
		indexSearcher = buildIndexSearcher(indexReader);
	}

	public IndexWriter getIndexWriterByMode(OpenMode openMode) {
		IndexWriterConfig indexConfig = new IndexWriterConfig(
				Util.LUCENVERSION, Util.analyzer);
		indexConfig.setOpenMode(openMode);
		try {
			indexWriter = new IndexWriter(FSDirectory.open(Util.INDEXFILE),
					indexConfig);
		} catch (CorruptIndexException e) {
			throw new AppException(e);
		} catch (LockObtainFailedException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException("[ExceptionInfo]在创建IndexWriter的时候出现了IO错误。",
					e);
		}
		return indexWriter;
	}

	public IndexReader buildIndexReader() {
		IndexReader reader = null;
		try {
			reader = IndexReader.open(FSDirectory.open(Util.INDEXFILE));
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reader;
	}

	public IndexSearcher buildIndexSearcher(IndexReader reader) {
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

	public void clean() { // 没有关闭他们
		if (dbObject != null) {
			dbObject.close();
		}

		if (indexWriter != null) {
			try {
				indexWriter.close();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (indexReader != null) {
			try {
				indexReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (indexSearcher != null) {
			try {
				indexSearcher.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static IndexResource getInstance() {
		if (resource == null)
			resource = new IndexResource();
		return resource;
	}

}
