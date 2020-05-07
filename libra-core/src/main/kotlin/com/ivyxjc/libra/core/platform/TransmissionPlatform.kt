package com.ivyxjc.libra.core.platform

import com.ivyxjc.libra.aspect.LibraMetrics
import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.config.SourceConfigService
import com.ivyxjc.libra.core.dao.RawTransMapper
import com.ivyxjc.libra.core.id.IdGenerator
import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.platform.internal.protobufFromRawTrans
import org.springframework.jms.core.JmsTemplate
import javax.jms.ConnectionFactory


/**
 * Should be Thread-safe
 */
open class TransmissionPlatform(
    private val idGenerator: IdGenerator<Long>,
    private val sourceConfigService: SourceConfigService,
    private val connectionFactory: ConnectionFactory,
    private val rawTransMapper: RawTransMapper,
    private val jmsTemplate: JmsTemplate
) : Dispatcher<RawTransaction> {

    companion object {
        @JvmStatic
        private val log = loggerFor(TransmissionPlatform::class.java)
    }

    @LibraMetrics
    override fun dispatch(trans: RawTransaction) {
        log.debug("receive trans: {}", trans)
        trans.guid = idGenerator.getId().toString()
        rawTransMapper.insertRaw(trans)
        val sourceConfig = sourceConfigService.getSourceConfig(trans.sourceId)
        val transformationQueue = sourceConfig!!.transformationQueue
        val protobuf = protobufFromRawTrans(trans)
        jmsTemplate.convertAndSend(transformationQueue, protobuf.toByteArray())
    }
}

