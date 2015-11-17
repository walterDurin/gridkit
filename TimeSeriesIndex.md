# Overview #
Time series index, allow to index versioned data in a way allowing fast retrieval of particular version of data.

# Example #
```
// setup time series index
// helper object is used to keep config in one place
TimeSeriesHelper<String, SampleValue, Long> helper = new TimeSeriesHelper<String, SampleValue, Long>(
		new KeyExtractor("getSerieKey"), 
		new ReflectionExtractor("getTimestamp"));

// adding index to cache		
helper.createIndex(cache);

// filter to select most recent "surface" of data in cache
Filter surface = helper.floor(null);

// filter to select "surface" for day opeping time
Filter openingSurface = helper.floor(openingTime);

// filter to select most recent entry for series key 'k'
// this filtre will use affinity property to send filter to one node only
Filter latest = helper.floor(k, null);
```


# Source #
| Trunk | **SVN:** https://gridkit.googlecode.com/svn/grid-search/trunk/coherence-search-timeseries |
|:------|:------------------------------------------------------------------------------------------|