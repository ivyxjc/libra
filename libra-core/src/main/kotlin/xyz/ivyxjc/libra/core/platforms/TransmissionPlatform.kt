package xyz.ivyxjc.libra.core.platforms

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.common.utils.loggerFor
import xyz.ivyxjc.libra.core.dao.RawTransMapper
import xyz.ivyxjc.libra.core.models.RawTransaction
import xyz.ivyxjc.libra.core.service.SourceConfigService
import xyz.ivyxjc.libra.jms.core.LibraJmsTemplate
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
    private lateinit var jmsTemplate: LibraJmsTemplate

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

