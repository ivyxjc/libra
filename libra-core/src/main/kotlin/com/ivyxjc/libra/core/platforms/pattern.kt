package com.ivyxjc.libra.core.platforms

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.exception.RetryableException
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.process.LibraProcessor
import com.ivyxjc.libra.core.process.Workflow
import com.ivyxjc.libra.core.process.WorkflowSession


interface LibraPattern {
    fun process(usecaseTxn: UsecaseTxn, processor: LibraProcessor, workflow: Workflow, session: WorkflowSession)

    companion object {
        @JvmStatic
        fun newPattern(): LibraPattern {
            return LibraPatternImpl()
        }

    }
}

class LibraPatternImpl : LibraPattern {

    companion object {
        @JvmStatic
        private val log = loggerFor(LibraPattern::class.java)
    }

    override fun process(
        usecaseTxn: UsecaseTxn,
        processor: LibraProcessor,
        workflow: Workflow,
        session: WorkflowSession
    ) {
        try {
            processor.process(usecaseTxn, workflow, session)
        } catch (e: Throwable) {
            if (e is RetryableException) {
                TODO("not implemented")
            } else {
                // todo exception handler
                log.error("LibraPattern throws Not-Retryable exception", e)
            }
        }
    }
}