package com.ivyxjc.libra.core.platforms

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.dao.RawTransMapper
import com.ivyxjc.libra.core.models.RawTransaction
import com.ivyxjc.libra.core.service.SourceConfigService
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service
import javax.jms.Queue


/**
 * Should be Thread-safe
 */
@Service("transmissionPlatform")
class TransmissionPlatform : Dispatcher<RawTransaction>, InitializingBean {

    companion object {
        @JvmStatic
        private val log = loggerFor(TransmissionPlatform::class.java)
    }

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @Autowired
    private lateinit var sourceConfigService: SourceConfigService

    @Autowired
    private lateinit var rawTransMapper: RawTransMapper

    private lateinit var queue: Queue

    override fun afterPropertiesSet() {
    }

    override fun dispatch(trans: RawTransaction) {
        log.debug("receive trans: {}", trans)
//        rawTransMapper.insertRaw(trans)
        val sourceConfig = sourceConfigService.getSourceConfig(trans.sourceId)
        val transformationQueue = sourceConfig!!.transformationQueue!!
        val t1 = System.currentTimeMillis()
        jmsTemplate.convertAndSend(transformationQueue, trans)
        val t2 = System.currentTimeMillis()
        println("================${t2 - t1}======================")
    }
}

