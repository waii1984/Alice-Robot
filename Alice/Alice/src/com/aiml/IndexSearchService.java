package com.aiml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;

import com.customexception.AppException;
import com.job.CreateIndexTask;
import com.util.Util;

public class IndexSearchService {
	private IndexResource resource = null;
	CreateIndexTask indexTask = null;

	public IndexSearchService() {
		resource = IndexResource.getInstance();
		indexTask = new CreateIndexTask(resource);
	}

	public void indexTaskSetup() {
		indexTask.fullIndexSetup();
		indexTask.deltaIndexSetup();
	}

	public List<Document> search(String keyword) {
		List<Document> docs = new ArrayList<Document>();

		if (!Util.INDEXFILE.exists()) {
			Util.INDEXFILE.mkdir();
		}
		IndexSearcher isearcher = null;
		try {
			isearcher = resource.indexSearcher;
			isearcher.setSimilarity(new IKSimilarity()); // setSimilarity是什么意思？？
			Query query = getSearchQuery(Util.FIELDSNAME, keyword);
			TopDocs topDocs = isearcher.search(query, 5);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < scoreDocs.length; i++) {
				Document targetDoc = isearcher.doc(scoreDocs[i].doc);
				docs.add(targetDoc);
			}
		} catch (CorruptIndexException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException("[ExceptionInfo]在Search 索引的时候出现了IO错误。", e);
		}
		return docs;
	}

	private static Query getSearchQuery(String fieldName, String keyword) {
		Query query = null;
		try {
			query = IKQueryParser.parse(fieldName, keyword);
			// System.out.println("query:" + query.toString());
		} catch (IOException e) {
			throw new AppException("[ExceptionInfo]创建Query的时候出现IO错误。", e);
		}
		return query;
	}
}
