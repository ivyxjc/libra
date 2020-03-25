package com.ivyxjc.libra.core.service

import com.ivyxjc.libra.core.models.UseCaseTxn
import com.ivyxjc.libra.core.process.LibraProcessor
import org.springframework.stereotype.Service

interface UseCaseService {

    fun getProcess(ucTxn: UseCaseTxn): LibraProcessor

}

@Service
class UseCaseServiceImpl : UseCaseService {
    override fun getProcess(ucTxn: UseCaseTxn): LibraProcessor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
