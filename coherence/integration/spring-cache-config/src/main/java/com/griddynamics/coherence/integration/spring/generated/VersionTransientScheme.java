//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.16 at 09:47:17 PM MSD 
//


package com.griddynamics.coherence.integration.spring.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cacheNameSuffix",
    "replicatedSchemeOrDistributedScheme"
})
@XmlRootElement(name = "version-transient-scheme")
public class VersionTransientScheme {

    @XmlElement(name = "cache-name-suffix")
    protected String cacheNameSuffix;
    @XmlElements({
        @XmlElement(name = "replicated-scheme", required = true, type = ReplicatedScheme.class),
        @XmlElement(name = "distributed-scheme", required = true, type = DistributedScheme.class)
    })
    protected List<Object> replicatedSchemeOrDistributedScheme;

    /**
     * Gets the value of the cacheNameSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCacheNameSuffix() {
        return cacheNameSuffix;
    }

    /**
     * Sets the value of the cacheNameSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCacheNameSuffix(String value) {
        this.cacheNameSuffix = value;
    }

    /**
     * Gets the value of the replicatedSchemeOrDistributedScheme property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the replicatedSchemeOrDistributedScheme property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReplicatedSchemeOrDistributedScheme().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReplicatedScheme }
     * {@link DistributedScheme }
     * 
     * 
     */
    public List<Object> getReplicatedSchemeOrDistributedScheme() {
        if (replicatedSchemeOrDistributedScheme == null) {
            replicatedSchemeOrDistributedScheme = new ArrayList<Object>();
        }
        return this.replicatedSchemeOrDistributedScheme;
    }

}
