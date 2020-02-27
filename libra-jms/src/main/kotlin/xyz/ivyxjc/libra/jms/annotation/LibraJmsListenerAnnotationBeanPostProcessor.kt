package xyz.ivyxjc.libra.jms.annotation

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
import org.yaml.snakeyaml.Yaml
import xyz.ivyxjc.libra.core.endpoint.BlankMessageListener
import xyz.ivyxjc.libra.core.endpoint.RawTransactionMessageListener
import xyz.ivyxjc.libra.core.endpoint.UsecaseTxnMessageListener
import xyz.ivyxjc.libra.core.platforms.*
import xyz.ivyxjc.libra.jms.model.inner.LibraJmsListenerYaml
import java.util.concurrent.atomic.AtomicInteger

class LibraJmsListenerAnnotationBeanPostProcessor : JmsListenerConfigurer, BeanFactoryAware,
    SmartInitializingSingleton {

    private var beanFactory: BeanFactory? = null

    private var embeddedValueResolver: StringValueResolver? = null

    private val counter = AtomicInteger()

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
        if (beanFactory is ConfigurableBeanFactory) {
            this.embeddedValueResolver = EmbeddedValueResolver(beanFactory)
        }
    }

    override fun configureJmsListeners(registrar: JmsListenerEndpointRegistrar) {
        registrar.setBeanFactory(this.beanFactory!!)
        val listeners = processJmsListenerConfig()
        listeners.forEach {
            processJmsListener(registrar, it)
        }
    }

    override fun afterSingletonsInstantiated() {

    }

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
        }
        val msgListener = when (libraJmsListenerYaml.messageListener) {
            "rawTransactionMessageListener" -> RawTransactionMessageListener()
            "UsecaseTxnMessageListener" -> UsecaseTxnMessageListener()
            else -> BlankMessageListener()
        }
        val dispatcher = when (libraJmsListenerYaml.dispatcher) {
            "transmissionPlatform" -> this.beanFactory!!.getBean(TransmissionPlatform::class.java)
            "transformationPlatform" -> this.beanFactory!!.getBean(TransformationPlatform::class.java)
            "remediationPlatform" -> this.beanFactory!!.getBean(RemediationPlatform::class.java)
            "blankRawTransDispatcher:" -> this.beanFactory!!.getBean(BlankRawTransDispatcher::class.java)
            "blankUcTxnDispatcher" -> this.beanFactory!!.getBean(BlankUcTxnDispatcher::class.java)
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

    private fun processJmsListenerConfig(): List<LibraJmsListenerYaml> {
        val yaml = Yaml()
        val input = LibraJmsBootstrapConfiguration::class.java.classLoader.getResourceAsStream("endpoint.yaml")
        val res = mutableListOf<LibraJmsListenerYaml>()
        input.use {
            val endpointMap: Map<String, *> = yaml.load(input)
            @Suppress("UNCHECKED_CAST")
            val endpointListenersMap = endpointMap["endpointListeners"] as? Map<String, Map<String, String>>
            endpointListenersMap?.forEach {
                val tmpListenerYaml = LibraJmsListenerYaml()
                val listenerMap = it.value
                tmpListenerYaml.id = it.key
                tmpListenerYaml.subscription = listenerMap["subscription"] ?: ""
                tmpListenerYaml.selector = listenerMap["selector"] ?: ""
                tmpListenerYaml.concurrency = listenerMap["concurrency"] ?: "4-4"
                tmpListenerYaml.sourceIds = listenerMap["sourceIds"] ?: "ALL"
                tmpListenerYaml.containerFactory = listenerMap.getValue("containerFactory")
                tmpListenerYaml.destination = listenerMap.getValue("destination")
                tmpListenerYaml.messageListener = listenerMap.getValue("messageListener")
                tmpListenerYaml.dispatcher = listenerMap.getValue("dispatcher")
                res.add(tmpListenerYaml)
            }
        }
        return res
    }

    private fun getEndpointId(libraJmsListenerYaml: LibraJmsListenerYaml): String {
        return if (StringUtils.hasText(libraJmsListenerYaml.id)) {
            val id = resolve(libraJmsListenerYaml.id)
            id ?: ""
        } else {
            "org.springframework.jms.JmsListenerEndpointContainer#" + this.counter.getAndIncrement()
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