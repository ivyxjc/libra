package com.ivyxjc.libra.starter.common.processors

import com.ivyxjc.libra.common.utils.loggerFor
import com.ivyxjc.libra.core.endpoint.BlankMessageListener
import com.ivyxjc.libra.core.endpoint.RawTransactionMessageListener
import com.ivyxjc.libra.core.endpoint.UsecaseTxnMessageListener
import com.ivyxjc.libra.core.platforms.*
import com.ivyxjc.libra.starter.common.model.LibraJmsListenerYaml
import com.ivyxjc.libra.starter.config.utils.ConfigConstants
import org.springframework.beans.factory.*
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.beans.factory.config.EmbeddedValueResolver
import org.springframework.jms.annotation.JmsListenerConfigurer
import org.springframework.jms.config.JmsListenerContainerFactory
import org.springframework.jms.config.JmsListenerEndpointRegistrar
import org.springframework.jms.config.SimpleJmsListenerEndpoint
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import org.springframework.util.StringValueResolver
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractLibraJmsAnnBeanPostProcessor(val name: String) : JmsListenerConfigurer, BeanFactoryAware,
    SmartInitializingSingleton {

    companion object {
        private val log = loggerFor(this::class.java)
    }

    private var beanFactory: BeanFactory? = null

    private var embeddedValueResolver: StringValueResolver? = null

    private val counter = AtomicInteger()


    override fun configureJmsListeners(registrar: JmsListenerEndpointRegistrar) {
        registrar.setBeanFactory(this.beanFactory!!)
        val listeners = processJmsListenerConfig()
        listeners.forEach {
            processJmsListener(registrar, it)
            log.info { "successfully register listener ${it.destination} in container factory ${it.containerFactory}. Listener detail is ${it}" }
        }
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
        if (beanFactory is ConfigurableBeanFactory) {
            this.embeddedValueResolver = EmbeddedValueResolver(beanFactory)
        }
    }

    internal abstract fun processJmsListenerConfig(): List<LibraJmsListenerYaml>

    private fun processJmsListener(
        registrar: JmsListenerEndpointRegistrar,
        libraJmsListenerYaml: LibraJmsListenerYaml
    ) {
        val endpoint: SimpleJmsListenerEndpoint = createSimpleJmsListenerEndpoint()
        endpoint.id = getEndpointId(libraJmsListenerYaml)
        endpoint.destination = resolve(libraJmsListenerYaml.destination)
        if (StringUtils.hasText(libraJmsListenerYaml.selector)) {
            endpoint.selector = resolve(libraJmsListenerYaml.selector)
        }
        if (StringUtils.hasText(libraJmsListenerYaml.subscription)) {
            endpoint.subscription = resolve(libraJmsListenerYaml.subscription)
        }
        if (StringUtils.hasText(libraJmsListenerYaml.concurrency)) {
            endpoint.concurrency = resolve(libraJmsListenerYaml.concurrency)
        }
        var factory: JmsListenerContainerFactory<*>? = null
        val containerFactoryBeanName = resolve(libraJmsListenerYaml.containerFactory)
        if (StringUtils.hasText(containerFactoryBeanName)) {
            Assert.state(
                beanFactory != null,
                "BeanFactory must be set to obtain container factory by bean name"
            )
            factory = try {
                beanFactory!!.getBean(
                    containerFactoryBeanName!!,
                    JmsListenerContainerFactory::class.java
                )
            } catch (ex: NoSuchBeanDefinitionException) {
                throw BeanInitializationException(
                    "Could not register JMS listener endpoint on [], no " + JmsListenerContainerFactory::class.java.simpleName +
                            " with id '" + containerFactoryBeanName + "' was found in the application context", ex
                )
            }
        } else {
            throw BeanInitializationException(
                "Could not get JMS container factory $containerFactoryBeanName , please config it"
            )
        }
        val msgListener = when (libraJmsListenerYaml.messageListener) {
            "rawTransactionMessageListener" -> RawTransactionMessageListener()
            "UsecaseTxnMessageListener" -> UsecaseTxnMessageListener()
            else -> BlankMessageListener()
        }
        val dispatcher = when (libraJmsListenerYaml.dispatcher) {
            ConfigConstants.TRANSMISSION_PLATFORM -> this.beanFactory!!.getBean(TransmissionPlatform::class.java)
            ConfigConstants.TRANSFORMATION_PLATFORM -> this.beanFactory!!.getBean(TransformationPlatform::class.java)
            ConfigConstants.REMEDIATION_PLATFORM -> this.beanFactory!!.getBean(RemediationPlatform::class.java)
            ConfigConstants.BLANK_RAW_TRANS_DISPATCHER -> this.beanFactory!!.getBean(BlankRawTransDispatcher::class.java)
            ConfigConstants.BLANK_USE_CASE_DISPATCHER -> this.beanFactory!!.getBean(BlankUcTxnDispatcher::class.java)
            else -> {
                if (msgListener !is BlankMessageListener) {
                    throw RuntimeException("MessageListener [${libraJmsListenerYaml.messageListener}] must have dispatcher")
                } else
                    null
            }
        }
        msgListener.dispatcher = dispatcher
        msgListener.sourceIdStr = libraJmsListenerYaml.sourceIds
        when (libraJmsListenerYaml.messageListener) {
            "rawTransactionMessageListener" -> RawTransactionMessageListener::class.java
            "usecaseTxnMessageListener" -> UsecaseTxnMessageListener::class.java
        }
        endpoint.messageListener = msgListener
        registrar.registerEndpoint(endpoint, factory)
    }

    private fun createSimpleJmsListenerEndpoint(): SimpleJmsListenerEndpoint {
        return SimpleJmsListenerEndpoint()
    }

    private fun getEndpointId(libraJmsListenerYaml: LibraJmsListenerYaml): String {
        return if (StringUtils.hasText(libraJmsListenerYaml.id)) {
            val id = resolve(libraJmsListenerYaml.id)
            id ?: ""
        } else {
            "org.springframework.jms.JmsListenerEndpointContainer[$name]#${this.counter.getAndIncrement()}"
        }
    }

    private fun resolve(value: String): String? {
        return if (this.embeddedValueResolver != null) {
            this.embeddedValueResolver!!.resolveStringValue(value)
        } else {
            value
        }
    }
}