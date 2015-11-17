# Overview #

People developing distributed Java applications know the importance of wire formats for objects. Native Java serialization has only one advantage—it is built in. It is relatively slow, not very compact, and has other quirks. Starting with version 3.2, Oracle Coherence is offering its own proprietary binary wire format for objects — [POF serialization](http://coherence.oracle.com/display/COH35UG/The+Portable+Object+Format). POF is not only cross platform, but also much more compact and faster compared to built-in serialization. Both compactness and speed are extremely important for data grid application. The only disadvantage of POF is that you should write custom serialization/deserialization code for each of your mobile objects. Not only domain objects which are stored in cache should have serializers, but also all agents such as entry processors, aggregators, filters etc should also have POF serialization/deserialization code. The amount of code you have to write may look daunting and force you to stick with built-in Java serialization.

But there is a simple way to get best of both worlds. In a recent project I have implemented a generic POF serializer. It uses reflection and doesn't require any changes in code, although you may still need to register classes in `coherence-pof-config.xml` unless you are using `AutoPofSerializer`. Still it offers the advantages of the POF format – compact object size and performance. While performance is a slightly slower compared to handmade serializers due to use of reflection (but still much faster than Java serialization), the sizes of serialized objects are similar to a handmade POF serializer.

Some people believe that reflection is slow. Indeed in may be slow if it is not used properly. I have performed some speed tests between Java reflection, a handmade POF serializer, and a reflection-based POF serializer and reflection POF serializer is just about 20% slower compared to handmade POF still considerably faster than Java serialization.

## `PofExtractor` support ##

Cohernce 3.5 has introduced major improvment for partitioned cache, which allow access to stored objects without deserialization [POF Extractors](http://coherence.oracle.com/display/COH35UG/PofExtractors+and+PofUpdaters)
. But using this technique requires much codeing and results are hard to justify (practice has shown what in some cases it's faster to desireialize object than parse it's binary presentation).

Support for [POF Extractors](http://coherence.oracle.com/display/COH35UG/PofExtractors+and+PofUpdaters) has been included as [ReflectionPofExtractor](ReflectionPofExtractor.md) class which can be used for filtering and indexing in conjunction with `ReflectionPofExtractor`

## No need for public no arg constructor ##
Unlike `PortableObject` interface, `ReflectionPofSerializer` does not force your domain objects to have public no arg constructor. It is still requires no arg constructor but it may be private or protected. This way you can keep your domain object more protected for acidental misuse.

## Prescise collections support in POF ##
POF protocol is known for bad habbit of substitute collections and maps with its own implementations, which may not necessary be compatible application needs. A few complains I was hearing about ReflectionPofSerializer is bad support for collections. Actually these complains should have been addressed to POF protocol implementation in Coherence, ReflectionPofSerializer just were using it as it is.

As of version 1.1 `ReflectionPofSerializer` support serialization of collections and map (both standard JDK collections and custom collections). If you are having problems with your collections being replaced with classes like `ImmutableList` or sorted maps replaced by unordered maps, you can just add collection classes you need to preserve in `pof-config.xml` and it will protect them from conversion.
```
<user-type>
    <type-id>2000</type-id>
    <class-name>java.util.ArrayList</class-name>
    <serializer>
        <class-name>org.gridkit.coherence.utils.pof.ReflectionPofSerializer</class-name>
    </serializer>
</user-type>

<user-type>
    <type-id>2001</type-id>
    <class-name>java.util.LinkedList</class-name>
    <serializer>
        <class-name>org.gridkit.coherence.utils.pof.ReflectionPofSerializer</class-name>
    </serializer>
</user-type>

<user-type>
    <type-id>2002</type-id>
    <class-name>java.util.TreeMap</class-name>
    <serializer>
        <class-name>org.gridkit.coherence.utils.pof.ReflectionPofSerializer</class-name>
    </serializer>
</user-type>

<user-type>
    <type-id>2003</type-id>
    <class-name>java.util.HashSet</class-name>
    <serializer>
        <class-name>org.gridkit.coherence.utils.pof.ReflectionPofSerializer</class-name>
    </serializer>
</user-type>

<user-type>
    <type-id>2004</type-id>
    <class-name>java.util.TreeSet</class-name>
    <serializer>
        <class-name>org.gridkit.coherence.utils.pof.ReflectionPofSerializer</class-name>
    </serializer>
</user-type>
```

## Support for typed object arrays ##
If base type of object array is registered in `pof-config.xml`, ReflectionPofSerializer will preserve exact array type.

## [AutoPofSerializer](AutoPofSerializer.md) ##
Even if you are using `ReflectionPofSerializer` you still have to maintain class to ID mapping in `pof-config.xml`. In some cases it may be a problem. E.g. you have an application using TopLink and want to try Coherence as second level cache. Your application may have hundreds of classes to be registered in `pof-config.xml`. This enter cost may prevent you from trying.

`AutoPofSerializier` does not require to register all classes in `pof-config.xml`, instead it can add classes to mapping in runtime using special Coherence cache for synchronizing mapping between all nodes.

TODO usage examples

## Well Known Objects (WKO) feature ##
TODO

# Source code #
Trunk
**svn co** https://gridkit.googlecode.com/svn/coherence-tools/trunk/reflection-pof-serializer


# Releases #

## 1.3 ##
SVN: **svn co** https://gridkit.googlecode.com/svn/coherence-tools/tags/reflection-pof-serializer-1.3

JAR: https://gridkit.googlecode.com/svn/repo/org/gridkit/coherence/utils/reflection-pof-serializer/1.3/reflection-pof-serializer-1.3.jar

MAVEN:
```
<dependency>
	<groupId>org.gridkit.coherence.utils</groupId>
	<artifactId>reflection-pof-serializer</artifactId>
	<version>1.3</version>
</dependency>
```

See MavenRepo for details of project Maven repository

## 0.5.1 ##
You can checkout project sources for SVN url https://gridkit.googlecode.com/svn/releases/reflection-pof-serializer-0.5.1