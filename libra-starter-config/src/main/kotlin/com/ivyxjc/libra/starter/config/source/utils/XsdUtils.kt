package com.ivyxjc.libra.starter.config.source.model.inner.xsds

import com.ivyxjc.libra.starter.config.source.model.inner.SourceConfigStr
import com.ivyxjc.libra.starter.config.source.model.inner.UsecaseConfigStr
import com.ivyxjc.libra.starter.config.source.utils.ConfigConstants
import xyz.ivyxjc.libra.core.exception.LibraMissingConfigException
import javax.xml.bind.JAXBContext
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory


class XsdUtils {

    companion object {
        @JvmStatic
        fun parseXml(filePath: String): XsdLibraFlowConfig {
            val context = JAXBContext.newInstance(XsdLibraFlowConfig::class.java)
            val unmarshaller = context.createUnmarshaller()
            unmarshaller.schema =
                SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(XsdUtils::class.java.classLoader.getResource("xsd/source-config.xsd"))
            val source: Source =
                StreamSource(XsdUtils::class.java.classLoader.getResourceAsStream(filePath))
            val libraFlowConfig = unmarshaller.unmarshal(
                source,
                XsdLibraFlowConfig::class.java
            )
            return libraFlowConfig.value
        }

        @JvmStatic
        internal fun parse(flowConfig: XsdLibraFlowConfig): Pair<List<SourceConfigStr>, List<UsecaseConfigStr>> {
            val sourceList = mutableListOf<SourceConfigStr>()
            val usecaseList = mutableListOf<UsecaseConfigStr>()
            flowConfig.getSourceConfigOrUsecaseConfig().forEach {
                if (it is XsdUsecaseConfig) {
                    when (it.type) {
                        XsdUsecaseType.SIMPLE -> {
                            // only attribute ref works
                            val tId = it.id!!
                            val tList = mutableListOf<String>()
                            it.processor.forEach { pc ->
                                tList.add(pc.ref!!)
                            }
                            // no need to verify, because there is a verify process before
                            val tUsecaseConfigStr = UsecaseConfigStr()
                            tUsecaseConfigStr.type = ConfigConstants.USECASE_TYPE_SIMPLE
                            tUsecaseConfigStr.name = tId
                            tUsecaseConfigStr.addAllSimpleProcessors(tList)
                            usecaseList.add(tUsecaseConfigStr)
                        }
                        XsdUsecaseType.STATUS -> {
                            val tMap = mutableMapOf<String, String>()
                            val tId = it.id
                            it.processor.forEach { pc ->
                                // no need to verify, because there is a verify process before
                                tMap[pc.status!!] = pc.ref
                            }
                            val tUsecaseConfigStr = UsecaseConfigStr()
                            tUsecaseConfigStr.type = ConfigConstants.USECASE_TYPE_STATUS
                            tUsecaseConfigStr.name = tId
                            tUsecaseConfigStr.putAllStatusProcessorMap(tMap)
                            usecaseList.add(tUsecaseConfigStr)
                        }
                        else -> throw LibraMissingConfigException(
                            "usecase must has attribute: [type]",
                            "source-config",
                            "type"
                        )
                    }
                }

                if (it is XsdSourceConfig) {
                    val tSourceId = it.id
                    val tProcessors = mutableListOf<String>()
                    val tUsecases = mutableListOf<String>()
                    it.processorOrUsecase.forEach { pu ->
                        when (pu) {
                            is XsdProcessorType -> {
                                tProcessors.add(pu.ref)
                            }
                            is XsdSourceConfig.XsdUsecase -> {
                                pu.id.forEach { i -> tUsecases.add(i) }
                            }
                        }
                    }
                    val sourceConfigStr = SourceConfigStr()
                    sourceConfigStr.sourceId = tSourceId
                    sourceConfigStr.transformationProcessor = tProcessors
                    sourceConfigStr.usecases = tUsecases
                    sourceList.add(sourceConfigStr)
                }
            }
            return Pair(sourceList, usecaseList)
        }
    }
}

fun main() {
    val libraFlowConfig = XsdUtils.parseXml("source-config.xml")
    val map = XsdUtils.parse(libraFlowConfig)
    println(map)
}