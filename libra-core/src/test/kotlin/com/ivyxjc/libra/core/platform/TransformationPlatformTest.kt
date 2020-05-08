package com.ivyxjc.libra.core.platform

import com.ivyxjc.libra.core.ProcessorPattern
import com.ivyxjc.libra.core.ProcessorsContainer
import com.ivyxjc.libra.core.config.SourceConfigService
import com.ivyxjc.libra.core.config.model.SourceConfig
import com.ivyxjc.libra.core.config.model.Transformation
import com.ivyxjc.libra.core.models.UsecaseTxn
import com.ivyxjc.libra.core.retry.RetryType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.random.Random

class TransformationPlatformTest {
    private val sourceConfigService = Mockito.mock(SourceConfigService::class.java)
    private val transformationPlatform = TransformationPlatform(sourceConfigService)
    private val sourceConfigBuilder = SourceConfig.Builder()
        .sourceId(1)
        .transformationQueue("mock-queue-1")

    private val ucTxn = UsecaseTxn()

    init {
        ucTxn.sourceId = 1
        ucTxn.guid = Random.nextLong().toString()
    }

    @BeforeEach
    fun init() {
    }

    @Test
    fun testBlockedRetryPlatformException() {
        val transformations =
            Transformation(
                listOf(
                    ProcessorsContainer.get(ProcessorPattern.Builder().retryType(RetryType.PLATFORM).sync(0).build()),
                    ProcessorsContainer.get(ProcessorPattern.Builder().retryType(RetryType.PROCESSOR).sync(0).build())
                )
            )

        val sourceConfig = sourceConfigBuilder.transformation(transformations).build()
        Mockito.`when`(sourceConfigService.getSourceConfig(1)).thenReturn(sourceConfig)
        try {
            transformationPlatform.dispatch(ucTxn)
        } catch (e: RuntimeException) {
            Assertions.assertEquals(6, ucTxn.platformAttempt)
        }

    }

    @Test
    fun testAsyncInMemoryRetryPlatformException() {
        val transformations =
            Transformation(
                listOf(
                    ProcessorsContainer.get(ProcessorPattern.Builder().retryType(RetryType.PLATFORM).sync(2).build())
                )
            )
        val sourceConfig = sourceConfigBuilder.transformation(transformations).build()
        Mockito.`when`(sourceConfigService.getSourceConfig(1)).thenReturn(sourceConfig)
        try {
            transformationPlatform.dispatch(ucTxn)
        } catch (e: RuntimeException) {
            // todo not support Async exception, so attempt is 1
            Assertions.assertEquals(1, ucTxn.platformAttempt)
        }
    }
}
