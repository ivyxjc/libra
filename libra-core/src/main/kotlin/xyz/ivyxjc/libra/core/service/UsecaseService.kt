package xyz.ivyxjc.libra.core.service

import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.models.UsecaseTxn
import xyz.ivyxjc.libra.core.process.LibraProcessor

interface UsecaseService {

    fun getProcess(ucTxn: UsecaseTxn): LibraProcessor

}

@Service
class UsecaseServiceImpl : UsecaseService {
    override fun getProcess(ucTxn: UsecaseTxn): LibraProcessor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
