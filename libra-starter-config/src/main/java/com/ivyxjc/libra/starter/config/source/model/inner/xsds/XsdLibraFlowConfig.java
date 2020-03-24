//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.03.11 at 11:41:35 PM CST 
//


package com.ivyxjc.libra.starter.config.source.model.inner.xsds;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{source-config.xsd}description" minOccurs="0"/&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element ref="{source-config.xsd}source-config"/&gt;
 *           &lt;element ref="{source-config.xsd}usecase-config"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element ref="{source-config.xsd}libra-flow-config" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "description",
        "sourceConfigOrUsecaseConfig",
        "libraFlowConfig"
})
@XmlRootElement(name = "libra-flow-config")
public class XsdLibraFlowConfig {

    protected XsdDescription description;
    @XmlElements({
            @XmlElement(name = "source-config", type = XsdSourceConfig.class),
            @XmlElement(name = "usecase-config", type = XsdUsecaseConfig.class)
    })
    protected List<Object> sourceConfigOrUsecaseConfig;
    @XmlElement(name = "libra-flow-config")
    protected List<XsdLibraFlowConfig> libraFlowConfig;

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link XsdDescription }
     */
    public XsdDescription getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link XsdDescription }
     */
    public void setDescription(XsdDescription value) {
        this.description = value;
    }

    /**
     * Gets the value of the sourceConfigOrUsecaseConfig property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sourceConfigOrUsecaseConfig property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSourceConfigOrUsecaseConfig().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XsdSourceConfig }
     * {@link XsdUsecaseConfig }
     */
    public List<Object> getSourceConfigOrUsecaseConfig() {
        if (sourceConfigOrUsecaseConfig == null) {
            sourceConfigOrUsecaseConfig = new ArrayList<Object>();
        }
        return this.sourceConfigOrUsecaseConfig;
    }

    /**
     * Gets the value of the libraFlowConfig property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the libraFlowConfig property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLibraFlowConfig().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XsdLibraFlowConfig }
     */
    public List<XsdLibraFlowConfig> getLibraFlowConfig() {
        if (libraFlowConfig == null) {
            libraFlowConfig = new ArrayList<XsdLibraFlowConfig>();
        }
        return this.libraFlowConfig;
    }

}
