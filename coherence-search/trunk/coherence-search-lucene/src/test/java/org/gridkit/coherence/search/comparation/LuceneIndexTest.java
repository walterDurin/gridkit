package org.gridkit.coherence.search.comparation;

import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.gridkit.coherence.search.lucene.LuceneDocumentExtractor;
import org.gridkit.coherence.search.lucene.LuceneSearchFactory;

import com.tangosol.util.Filter;

/**
 * @author Alexander Solovyov
 */

public class LuceneIndexTest extends IndexComparisonTestBase {

    private LuceneSearchFactory factory;

    @Override
    protected void setUp() {
        LuceneDocumentExtractor extractor = new LuceneDocumentExtractor();

        for (int i = 0; i < N; i++) {
            extractor.addText("stringField" + i, stringFieldExtractors[i]);
            extractor.addText("intField" + i, intFieldExtractors[i]);
        }

        factory = new LuceneSearchFactory(extractor);
        factory.getEngineConfig().setIndexUpdateQueueSizeLimit(1024);
        factory.getEngineConfig().setIndexUpdateDelay(60000);

        factory.createIndex(cache);
    }

    @Override
    protected Set entrySet() {

        BooleanQuery query = new BooleanQuery();

        for (int i = 0; i < N; i++) {
            query.add(new TermQuery(new Term("stringField" + i, "A")), BooleanClause.Occur.MUST);
            query.add(new TermQuery(new Term("intField" + i, "0")), BooleanClause.Occur.MUST);
        }

        Filter filter = factory.createFilter(query);
        
        return cache.entrySet(filter);
    }
}
