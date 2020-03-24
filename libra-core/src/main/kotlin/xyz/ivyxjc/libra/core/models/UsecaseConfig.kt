package xyz.ivyxjc.libra.core.models

import xyz.ivyxjc.libra.common.ErrorConstants
import xyz.ivyxjc.libra.common.utils.loggerFor
import xyz.ivyxjc.libra.core.exception.LibraConfigConflictException
import xyz.ivyxjc.libra.core.exception.LibraMissingConfigException
import xyz.ivyxjc.libra.core.process.LibraProcessor


enum class UsecaseType(val value: String) {
    Simple("simple"),
    Status("status")

}

class UsecaseConfig(
    name: String,
    type: String,
    simpleList: List<LibraProcessor>?,
    statusProcessorMap: Map<String, LibraProcessor>?
) {
    companion object {
        private val log = loggerFor(UsecaseConfig::class.java)
    }

    val type: UsecaseType
    val simpleProcessors: List<LibraProcessor>?
    val statusProcessorMap: Map<String, LibraProcessor>?

    init {
        val ucType = UsecaseType.valueOf(type)
        when (ucType) {
            UsecaseType.Simple -> {
                this.type = UsecaseType.Simple
                if (simpleList.isNullOrEmpty()) {
                    log.error("Fail to build one simple usecase without simple list. usecase name is: {}", name)
                    throw LibraMissingConfigException(
                        "Simple UcType must have config simpleList",
                        ErrorConstants.SOURCE_CONFIG,
                        "simple-list"
                    )
                }
                if (!statusProcessorMap.isNullOrEmpty()) {
                    log.error("Fail to build one simple usecase with simple list. usecase name is: {}", name)
                    throw LibraMissingConfigException(
                        "Simple UcType must not have config StatusProcessor",
                        ErrorConstants.SOURCE_CONFIG,
                        "status-processor"
                    )
                }
                this.simpleProcessors = simpleList
                this.statusProcessorMap = null
            }
            UsecaseType.Status -> {
                this.type = UsecaseType.Status
                if (!simpleList.isNullOrEmpty()) {
                    log.error("Fail to build one status usecase with simple list. usecase name is: {}", name)
                    throw LibraConfigConflictException(
                        "Status UcType must not have config simple-list",
                        ErrorConstants.SOURCE_CONFIG,
                        "simple-list"
                    )
                }
                this.simpleProcessors = null
                if (statusProcessorMap.isNullOrEmpty()) {
                    throw LibraMissingConfigException(
                        "Status UcType must have config statusProcessorMap",
                        ErrorConstants.SOURCE_CONFIG,
                        "status-processor"
                    )
                }
                this.statusProcessorMap = statusProcessorMap
            }

        }
    }
}