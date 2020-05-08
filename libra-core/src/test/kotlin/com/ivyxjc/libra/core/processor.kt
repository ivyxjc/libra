package com.ivyxjc.libra.core

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.flow.Workflow
import com.ivyxjc.libra.core.flow.WorkflowSession
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.processor.LibraProcessor
import com.ivyxjc.libra.core.retry.RetryType
import org.apache.ibatis.javassist.ClassPool
import org.apache.ibatis.javassist.CtField
import org.apache.ibatis.javassist.CtMethod
import java.net.URLDecoder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport

class BlankLibraProcessor : LibraProcessor {
    companion object {
        private val log = loggerFor(BlankLibraProcessor::class.java)
    }

    override fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession) {
        log.debug("receiver ucTxn: {}", ucTxn)
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1))
    }
}

//class BlockedRetryProcessorLibraProcessor : LibraProcessor {
//    companion object {
//        private val log = loggerFor(BlockedRetryProcessorLibraProcessor::class.java)
//    }
//
//    override fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession) {
//        log.debug("process...")
//        LockSupport.parkNanos(1000)
//        throw ProcessorBlockedRetryException(
//            "BlockedRetryProcessorLibraProcessor",
//            StopStrategies.StopAfterAttemptStrategy(6)
//        )
//    }
//}
//
//class BlockedRetryPlatformLibraProcessor : LibraProcessor {
//    companion object {
//        private val log = loggerFor(BlockedRetryPlatformLibraProcessor::class.java)
//    }
//
//    override fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession) {
//        log.debug("process...")
//        LockSupport.parkNanos(1000)
//        throw PlatformBlockedRetryException(
//            "BlockedRetryPlatformLibraProcessor",
//            StopStrategies.StopAfterAttemptStrategy(6)
//        )
//    }
//}
//
//
//class AsyncRetryInMemoryPlatformLibraProcessor : LibraProcessor {
//    companion object {
//        private val log = loggerFor(AsyncRetryInMemoryPlatformLibraProcessor::class.java)
//    }
//
//    override fun process(ucTxn: UsecaseTxn, flowStatus: Workflow, session: WorkflowSession) {
//        log.debug("process...")
//        LockSupport.parkNanos(1000)
//        throw PlatformAsyncInMemoryRetryException(
//            "AsyncRetryInMemoryPlatformLibraProcessor",
//            StopStrategies.StopAfterAttemptStrategy(6)
//        )
//    }
//}

class ProcessorsContainer {

    companion object {
        private val obj = Any()

        private val map = ConcurrentHashMap<ProcessorPattern, LibraProcessor>()

        @JvmStatic
        fun get(pattern: ProcessorPattern): LibraProcessor {
            if (map[pattern] == null) {
                synchronized(obj) {
                    return if (map[pattern] == null) {
                        val processor = buildLibraProcessor(pattern)
                        map[pattern] = processor
                        return processor
                    } else {
                        map[pattern]!!
                    }
                }
            } else {
                return map[pattern]!!
            }
        }

        @JvmStatic
        private fun buildLibraProcessor(pattern: ProcessorPattern): LibraProcessor {
            val pool = ClassPool.getDefault()
            val clzName = "com.ivyxjc.libra.core.processor.P${pattern}"
            val cc = pool.makeClass(clzName);
            val intf = pool.get(LibraProcessor::class.java.canonicalName)
            var str = ""

            when (pattern.retryType) {
                RetryType.PLATFORM -> {
                    str = when (pattern.syncFlg) {
                        0 -> "throw new com.ivyxjc.libra.core.retry.exception.PlatformBlockedRetryException(\"$clzName\");"
                        1 -> "throw new com.ivyxjc.libra.core.retry.exception.PlatformAsyncRetryException(\"$clzName\");"
                        2 -> "throw new com.ivyxjc.libra.core.retry.exception.PlatformAsyncInMemoryRetryException(\"$clzName\");"
                        else -> throw RuntimeException("Platform sync flag should <= 2")
                    }
                }
                RetryType.PROCESSOR -> {
                    when (pattern.syncFlg) {
                        0 -> str =
                                "throw new com.ivyxjc.libra.core.retry.exception.ProcessorBlockedRetryException(\"$clzName\");"
                        else -> throw RuntimeException("Processor sync flag should <= 1")
                    }
                }
            }

            cc.addInterface(intf)

            val logField = CtField.make(
                    """private static final com.ivyxjc.libra.common.log.LoggerProxy log = 
                com.ivyxjc.libra.common.log.LoggerProxy.create("$clzName");""".trimIndent(),
                    cc
            )
            cc.addField(logField)

            val methodStr = """
            public void process(com.ivyxjc.libra.core.models.UsecaseTxn usecaseTxn,com.ivyxjc.libra.core.flow.Workflow flow, com.ivyxjc.libra.core.flow.WorkflowSession session){
                log.debug("processing...");
                java.util.concurrent.locks.LockSupport.parkNanos(1000L);
                $str
            }        
    """.trimIndent()
            val method = CtMethod.make(methodStr, cc)

            cc.addMethod(method)


            var path = TestJavaPosition::class.java.classLoader.getResource("").path
            println(path)
            path = if (path.contains(":")) {
                path.substring(1)
            } else {
                path
            }
            path = URLDecoder.decode(path, "utf-8")

            cc.writeFile(path)

            val o = Class.forName(clzName)
            return o.getDeclaredConstructor().newInstance() as LibraProcessor
        }
    }


}

/**
 * sync: 0: Blocked 1: Async 2: AsyncInMemory
 */
class ProcessorPattern(val retryType: RetryType, val syncFlg: Int) {

    class Builder {
        private var retryType: RetryType = RetryType.PLATFORM
        private var syncFlg: Int = 0


        fun retryType(retryType: RetryType) = apply {
            this.retryType = retryType
        }

        /**
         * sync: 0: Blocked 1: Async 2: AsyncInMemory
         */
        fun sync(sync: Int) = apply {
            this.syncFlg = sync
        }

        fun build(): ProcessorPattern {
            return ProcessorPattern(retryType, syncFlg)
        }
    }

    override fun hashCode(): Int {
        return retryType.hashCode().and(syncFlg.hashCode())
    }

    override fun equals(other: Any?): Boolean {
        if (null == other) {
            return false;
        }
        if (other !is ProcessorPattern) {
            return false
        }
        return retryType == other.retryType
                && syncFlg == other.syncFlg
    }

    override fun toString(): String {
        return retryType.toString() + syncFlg
    }

}


fun main() {
    val processor =
            ProcessorsContainer.get(ProcessorPattern.Builder().retryType(RetryType.PROCESSOR).sync(0).build())
    processor.process(UsecaseTxn(), Workflow.create(), WorkflowSession.create())
}
