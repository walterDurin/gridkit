[Coherence custom index overview](CoherenceCustomIndexOverview.md) provides some insight of how to implement custom index in Coherence.

Implementing index is quite tricky, Coherence-Search core module aims to abstract away Coherence specific quirks and provided simple API for creation of custom indexes.

# Creating custom index with Coherence-Search #
Coherence-Search abstracts all index operation behind single interface `PlugableSearchIndex`. `PlugableSearchIndex` is intended to be stateless helper class. Also Coherence-Search provides another class `SearchFactory`, this class serves as a factory to hide all `extractors/filters` complexity from you. `SearchFactory` implements generic `IndexAwareExtractor`, `MapIndex` and `IndexAwareFilter` classes. These generic implementations delegate actual work to `PlugableSearchIndex` instance.
### Index compatibility token ###
If you will have multiple custom indexes, filter needs to distinguish its index from other custom indexes. It is done by defining **index compatibility token** object.
Note what only one instance of index can exists per one token.
### Handling binary keys ###
Coherence-Search handles binary/object inconsistency for keys and other helper method which allows `PlugableSearchIndex` to convert key to binary, object or form native for cache.
### Delayed update queue ###
Coherence-Search implements delayed index update to ensure efficiency of complex indexes (e.g. full text). Update queue parameters can be configured via `SearchFactory` before creation of index.
### Sample ###
```
public class Sample {

	NamedCache cache;
	
	public void init() {
		// plugin for n-gram index
		NGramIndexPlugin plugin = new NGramIndexPlugin();

		// create index factory for "toString" attribute using n-gram index plugin with n-gram size 3
		SearchFactory<NGramIndex, Integer, String> nGramSearchFactory = new SearchFactory<NGramIndex, Integer, String>(plugin, 3, new ReflectionExtractor("toString"));

		// initialize index
		nGramSearchFactory.createIndex(cache);
		
		// query by n-gram index, looking to every object containing substring "text"
		cache.keySet(nGramSearchFactory.createFilter("text"));
		
	}
}
```
### Serialization considerations ###
TODO
