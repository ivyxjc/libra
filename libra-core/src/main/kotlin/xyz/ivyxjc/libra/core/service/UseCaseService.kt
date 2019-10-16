package xyz.ivyxjc.libra.core.service

import org.springframework.stereotype.Service
import xyz.ivyxjc.libra.core.models.UseCaseTxn
import xyz.ivyxjc.libra.core.process.LibraProcessor

interface UseCaseService {

    fun getProcess(ucTxn: UseCaseTxn): LibraProcessor

}

@Service
class UseCaseServiceImpl : UseCaseService {
    override fun getProcess(ucTxn: UseCaseTxn): LibraProcessor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
