package com.ivyxjc.libra.core.platform

import com.ivyxjc.libra.core.ProcessorPattern
import com.ivyxjc.libra.core.ProcessorsContainer
import com.ivyxjc.libra.core.flow.Workflow
import com.ivyxjc.libra.core.flow.WorkflowSession
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.retry.RetryType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LibraPatternImplTest {
    private val testPatternImpl = LibraPatternImpl()

    @Test
    fun testBlockedRetry() {
        val processorPattern = ProcessorsContainer.get(
            ProcessorPattern.Builder()
                .retryType(RetryType.PROCESSOR)
                .sync(0)
                .build()
        )
        val ucTxn = UsecaseTxn()
        try {
            testPatternImpl.process(ucTxn, processorPattern, Workflow.create(), WorkflowSession.create())
        } catch (e: RuntimeException) {
            Assertions.assertEquals(6, ucTxn.patternAttempt)
        }
    }

}