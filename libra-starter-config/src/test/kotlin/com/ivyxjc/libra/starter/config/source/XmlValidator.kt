package com.ivyxjc.libra.starter.config.source

import com.ivyxjc.libra.starter.config.source.model.inner.SourceConfigStr
import org.junit.Assert
import org.junit.Test
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class XmlValidator {

    @Test
    fun validateXMl() {
        val status = validateXMLSchema(
            "xsd/source-config.xsd",
            "source-config.xml"
        )
        Assert.assertTrue(status)
    }

    private fun validateXMLSchema(xsdPath: String?, xmlPath: String?): Boolean {
        val factory =
            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val xsdUrl = SourceConfigStr::class.java.classLoader.getResource(xsdPath)!!
        val xmlStream = SourceConfigStr::class.java.classLoader.getResourceAsStream(xmlPath)!!
        val schema: Schema = factory.newSchema(xsdUrl)
        val validator: Validator = schema.newValidator()
        validator.validate(StreamSource(xmlStream))
        return true
    }

}



