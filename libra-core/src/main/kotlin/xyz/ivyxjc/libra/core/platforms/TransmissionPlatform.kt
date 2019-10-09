package xyz.ivyxjc.libra.core.platforms

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.dao.RawTransMapper
import xyz.ivyxjc.libra.core.loggerFor
import xyz.ivyxjc.libra.core.models.RawTransaction

@Service("transmissionPlatform")
class TransmissionPlatform : Dispatcher<RawTransaction> {

    companion object {
        @JvmStatic
        private val log = loggerFor(TransmissionPlatform::class.java)
    }


    @Autowired
    private lateinit var rawTransMapper: RawTransMapper

    override fun dispatch(trans: RawTransaction) {
        log.debug("receive trans: $trans")
        rawTransMapper.insertRaw(trans)
    }
}

