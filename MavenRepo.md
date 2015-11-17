We are publishing most of our artifacts in Maven central, so they should be available right away.

Still, some artifacts may not be available in Maven central. In this case you may need to add GridKit's own repo to your maven setup.

GridKit's own repository URL is http://gridkit.googlecode.com/svn/repo/

You can add reference to our repository to your POM to download [GridKit](http://code.google.com/p/gridkit/) artifacts automatically.

```
<repositories>
    ...
    <repository>
        <id>gridkit.org</id>
        <name>GridKit.org repository</name>
        <url>http://gridkit.googlecode.com/svn/repo/</url>
	<releases>
	    <enabled>true</enabled>
	    <updatePolicy>never</updatePolicy>
	</releases>
	<snapshots>
	    <enabled>false</enabled>
	</snapshots>
    </repository>
</repositories>
```

Please note we do not publish snapshot artifacts, if you want to work with latest snapshot you should build project from source.