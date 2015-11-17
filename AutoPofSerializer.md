# Introduction #
Coherence POF wire format requires `pof-config.xml` which should include all serializable classes (including filters, entry processors etc).

Using this file Coherence can avoid using full class names in binary stream (though [SafeConfigurablePofContext](http://download.oracle.com/docs/cd/E18686_01/coh.37/e18683/com/tangosol/io/pof/SafeConfigurablePofContext.html) can use both IDs and full class names).

ReflectionPofSerializer will allow you to serialize most normal POJO and collections but it is still requiring to put every single class to `pof-config.xml`

`AutoPofSerializer` works by automatically generating class to ID mapping in runtime. It is using `pof-config.xml` but if class is not present it will generate new ID and share it with other nodes using ... guest what ... special Coherence cache.

With `AutoPofSerializer` you have just to configure it for cache ...
```
<distributed-scheme>
    ...
    <serializer>
        <class-name>org.gridkit.coherence.utils.pof.AutoPofSerializer</class-name>
    </serializer>
    ...
</distributed-scheme>
```
... and it should work.

`AutoPofSerializer` will automatically serialize all objects implementing `PortableObject` and all others using `ReflectionPofSerializer` (`java.lang.Throwable` is a special case, Java built-in serialization is used for expections).


# Limitations #
  * Works only with Java
  * All nodes should have same version of code base

# Using `AutoPofSerializer` with Coherence\*Extends #
`AutoPofSerializer` could work with extend, but more complicated configuration id required.
See samples in unit tests.