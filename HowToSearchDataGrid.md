Example below explains usage of [GridSearch](GridSearch4Coherence.md) API using n-gram index as example.

```
/**
 * Sample of API usage
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class Sample {

	NamedCache cache;
	
	public void usingGridSearch() {
		// plugin for n-gram index
		NGramIndexPlugin plugin = new NGramIndexPlugin();

		// Create extrator for attribute to be indexed
		// any extractor returning string will do for n-gram index
		ValueExtractor extractor = new ReflectionExtractor("toString");
		
		// create index factory using n-gram index plugin with n-gram size 3
		SearchFactory<NGramIndex, Integer, String> nGramSearchFactory = new SearchFactory<NGramIndex, Integer, String>(plugin, 3, extractor);

		// search factory allows you to adjust some
		// configuration options for Coherence index
		// below we are limiting asynchronous update
		// max queue length to 100
		nGramSearchFactory.getEngineConfig().setIndexUpdateQueueSizeLimit(100);
		
		// initialize index for cache
		// this operation actually tells coherence
		// to create index structures on all
		// storage enabled nodes
		nGramSearchFactory.createIndex(cache);
		
		// query by n-gram index, looking to every object containing substring "text"
		// different custom indexes may use different types of queries
		// for n-gram index query is a plain java.lang.String object
		cache.keySet(nGramSearchFactory.createFilter("text"));
		
	}
}

```