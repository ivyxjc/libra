package com.ivyxjc.libra.core.platforms

import com.ivyxjc.libra.aspect.LibraMetrics
import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.dao.RawTransMapper
import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.service.SourceConfigService
import org.springframework.jms.core.JmsTemplate
import org.springframework.transaction.annotation.Transactional
import javax.jms.ConnectionFactory


/**
 * Should be Thread-safe
 */
open class TransmissionPlatform(
    private val sourceConfigService: SourceConfigService,
    private val connectionFactory: ConnectionFactory,
    private val rawTransMapper: RawTransMapper,
    private val jmsTemplate: JmsTemplate
) : Dispatcher<RawTransaction> {

    companion object {
        @JvmStatic
        private val log = loggerFor(TransmissionPlatform::class.java)
    }

    @Transactional
    @LibraMetrics
    override fun dispatch(trans: RawTransaction) {
        log.debug("receive trans: {}", trans)
        rawTransMapper.insertRaw(trans)
        val sourceConfig = sourceConfigService.getSourceConfig(trans.sourceId)
        val transformationQueue = sourceConfig!!.transformationQueue
        jmsTemplate.convertAndSend(transformationQueue, trans)
    }
}

