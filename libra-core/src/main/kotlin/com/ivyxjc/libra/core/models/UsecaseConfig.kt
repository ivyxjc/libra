package com.ivyxjc.libra.core.models

import com.ivyxjc.libra.common.ErrorConstants
import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.exception.LibraConfigConflictException
import com.ivyxjc.libra.core.exception.LibraMissingConfigException
import com.ivyxjc.libra.core.process.LibraProcessor
import org.apache.commons.lang3.StringUtils


enum class UsecaseType(val value: String) {
    Simple("simple"),
    Status("status")

}

class UsecaseConfig private constructor(
    val name: String,
    val type: UsecaseType,
    val queue: String,
    val simpleProcessors: List<LibraProcessor>,
    val statusProcessorMap: Map<String, LibraProcessor>
) {
    companion object {
        private val log = loggerFor(UsecaseConfig::class.java)
    }

    class Builder {
        private var name: String? = null
        private var type: UsecaseType? = null
        private var queue: String? = null
        private var simpleProcessors: List<LibraProcessor> = mutableListOf()
        private var statusProcessorMap: Map<String, LibraProcessor> = mutableMapOf()

        fun name(name: String) = apply {
            this.name = name
        }

        fun type(type: String) = apply {
            this.type = UsecaseType.valueOf(type)
        }

        fun queue(queue: String) = apply {
            this.queue = queue
        }

        fun simpleProcessors(list: List<LibraProcessor>) = apply {
            this.simpleProcessors = list
        }

        fun statusProcessorMap(map: Map<String, LibraProcessor>) = apply {
            this.statusProcessorMap = map
        }

        fun build(): UsecaseConfig {
            checkUsecaseConfig()
            return UsecaseConfig(this.name!!, this.type!!, this.queue!!, this.simpleProcessors, this.statusProcessorMap)
        }

        private fun checkUsecaseConfig() {
            if (StringUtils.isBlank(this.queue)) {
                throw LibraMissingConfigException(
                    "Usecase queue should not be empty",
                    ErrorConstants.USECASE_CONFIG,
                    "usecase-queue"
                )
            }
            when (this.type) {
                UsecaseType.Simple -> {
                    if (this.simpleProcessors.isNullOrEmpty()) {
                        log.error("Fail to build one simple usecase without simple list. usecase name is: {}", name)
                        throw LibraMissingConfigException(
                            "Simple UcType must have config simpleList",
                            ErrorConstants.USECASE_CONFIG,
                            "simple-list"
                        )
                    }
                    if (!this.statusProcessorMap.isNullOrEmpty()) {
                        log.error("Fail to build one simple usecase with simple list. usecase name is: {}", name)
                        throw LibraMissingConfigException(
                            "Simple UcType must not have config StatusProcessor",
                            ErrorConstants.USECASE_CONFIG,
                            "status-processor"
                        )
                    }
                }
                UsecaseType.Status -> {
                    if (!this.simpleProcessors.isNullOrEmpty()) {
                        log.error("Fail to build one status usecase with simple list. usecase name is: {}", name)
                        throw LibraConfigConflictException(
                            "Status UcType must not have config simple-list",
                            ErrorConstants.USECASE_CONFIG,
                            "simple-list"
                        )
                    }
                    if (this.statusProcessorMap.isNullOrEmpty()) {
                        throw LibraMissingConfigException(
                            "Status UcType must have config statusProcessorMap",
                            ErrorConstants.USECASE_CONFIG,
                            "status-processor"
                        )
                    }
                }
            }
        }
    }
}