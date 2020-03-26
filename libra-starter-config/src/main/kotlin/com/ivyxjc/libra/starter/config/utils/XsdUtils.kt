package com.ivyxjc.libra.starter.config.utils

import com.ivyxjc.libra.core.exception.LibraMissingConfigException
import com.ivyxjc.libra.starter.config.source.model.inner.SourceConfigStr
import com.ivyxjc.libra.starter.config.source.model.inner.UsecaseConfigStr
import com.ivyxjc.libra.starter.config.source.model.inner.xsds.*
import org.apache.commons.lang3.StringUtils
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
            val qPrefixMap = mutableMapOf<String, String>()
            val sourceList = mutableListOf<SourceConfigStr>()
            val usecaseList = mutableListOf<UsecaseConfigStr>()
            flowConfig.qPrefixOrSourceConfigOrUsecaseConfig.forEach {
                if (it is XsdQPrefix) {
                    qPrefixMap[it.id] = it.queue
                }
            }
            flowConfig.qPrefixOrSourceConfigOrUsecaseConfig.forEach {
                if (it is XsdUsecaseConfig) {
                    var qPrefix = ""
                    if (StringUtils.isBlank(it.qPrefix)) {
                        qPrefix = ConfigConstants.Q_PEFIX_DEFAULT
                    }
                    val queue = buildQueueName(qPrefixMap, qPrefix, it.queue)
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
                            tUsecaseConfigStr.queue = queue
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
                            tUsecaseConfigStr.queue = queue
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
                    var qPrefix = ""
                    if (StringUtils.isBlank(it.qPrefix)) {
                        qPrefix = ConfigConstants.Q_PEFIX_DEFAULT
                    }
                    val queue = buildQueueName(qPrefixMap, qPrefix, it.queue)
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
                    sourceConfigStr.transformationQueue = queue
                    sourceConfigStr.sourceId = tSourceId
                    sourceConfigStr.transformationProcessor = tProcessors
                    sourceConfigStr.usecases = tUsecases.toSet()
                    sourceList.add(sourceConfigStr)
                }
            }
            return Pair(sourceList, usecaseList)
        }

        private fun buildQueueName(qPrefixMap: Map<String, String>, qPrefixId: String, queue: String?): String {
            if (StringUtils.isNotBlank(queue)) {
                return queue!!
            }
            val qPrefix = qPrefixMap[qPrefixId]
            if (StringUtils.isBlank(qPrefix)) {
                TODO("throw config not correct exception")
            }
            return qPrefix!!
        }
    }


}

