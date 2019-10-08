package xyz.ivyxjc.libra.core

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.yaml.snakeyaml.Yaml
import xyz.ivyxjc.libra.common.utils.ClassUtils
import xyz.ivyxjc.libra.core.connection.JmsConnectionUtils
import xyz.ivyxjc.libra.core.endpoint.BlankMessageListener
import xyz.ivyxjc.libra.core.endpoint.RawTransactionMessageListener
import xyz.ivyxjc.libra.core.endpoint.UsecaseTxnMessageListener
import java.util.regex.Pattern

@Configuration
open class StartupInit : BeanDefinitionRegistryPostProcessor, ApplicationContextAware, EnvironmentAware {

    companion object {
        @JvmStatic
        private val PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(.+)}")
    }

    private var env: Environment? = null

    private var context: ApplicationContext? = null

    override fun setApplicationContext(context: ApplicationContext) {
        this.context = context
    }

    override fun setEnvironment(env: Environment) {
        this.env = env
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
    }

    /**
     * if there are some exception throw during the init,
     * just throw it, the framework should not start up
     */
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val yaml = Yaml()
        val input = StartupInit::class.java.classLoader.getResourceAsStream("endpoint.yaml")
        input.use {
            val map: Map<String, *> = yaml.load(input)
            val connectionFactories = map["connectionFactories"] as? Map<*, *>
            val endpointListeners = map["endpointListeners"] as? Map<*, *>
            connectionFactories?.forEach {
                @Suppress("UNCHECKED_CAST")
                val t = it as Map.Entry<String, Map<String, String>>
                handlerConnectionFactory(registry, t.key, t.value)
            }
            endpointListeners?.forEach {
                @Suppress("UNCHECKED_CAST")
                val t = it as Map.Entry<String, Map<String, Any>>
                handleEndpointListener(registry, t.key, t.value)
            }
        }
    }

    private fun handlerConnectionFactory(registry: BeanDefinitionRegistry, name: String, map: Map<String, String>) {
        val url = map["url"] as String
        val funcName = map["funcname"] as String
        val methods = JmsConnectionUtils::class.java.declaredMethods
        val ctx = context as ConfigurableApplicationContext
        val match = PLACEHOLDER_PATTERN.matcher(url)
        var trueUrl = url
        if (match.find()) {
            val tmp = match.group(1)
            trueUrl = env!!.getRequiredProperty(tmp)
        }

        methods.forEach {
            if (it.name == funcName) {
                it.isAccessible = true
                ctx.beanFactory.registerSingleton(name, it.invoke(null, trueUrl))
            }
        }

    }

    private fun handleEndpointListener(registry: BeanDefinitionRegistry, name: String, map: Map<String, Any>) {
        val className = map["class"] as String
        val msgListenerKey = map["messageListener"] as String
        val address = map["address"] as String
        val sourceIdsStr = (map["sourceIds"] as String).split(",")
        val sourceIds = Array(sourceIdsStr.size) {
            sourceIdsStr[it].toLong()
        }


        val clz = Class.forName(className)
        val dispatcherStr = map["dispatcher"] as String

        val msgListenerClz = when (msgListenerKey) {
            "rawTransactionMessageListener" -> RawTransactionMessageListener::class.java
            "UsecaseTxnMessageListener" -> UsecaseTxnMessageListener::class.java
            else -> BlankMessageListener::class.java
        }

        val msgListenerBuilder = BeanDefinitionBuilder.rootBeanDefinition(msgListenerClz)
        val msgListenerFields = ClassUtils.getAllFields(msgListenerClz)
        msgListenerFields.forEach {
            when (it.name) {
                "sourceIds" -> msgListenerBuilder.addPropertyValue(it.name, sourceIds)
                "dispatcher" -> msgListenerBuilder.addPropertyReference(it.name, dispatcherStr)
                else -> null
            }
        }
        registry.registerBeanDefinition("${msgListenerClz.simpleName}-$sourceIdsStr-$dispatcherStr", msgListenerBuilder.rawBeanDefinition)


        val endpointListenerBuilder = BeanDefinitionBuilder.rootBeanDefinition(clz)
        val endpointListenerFields = ClassUtils.getAllFields(clz)
        endpointListenerFields.forEach {
            when (it.name) {
                "jmsConnectionFactory" -> endpointListenerBuilder.addPropertyReference(it.name, map[it.name] as String)
                "messageListener" -> endpointListenerBuilder.addPropertyReference(it.name, "${msgListenerClz.simpleName}-$sourceIdsStr-$dispatcherStr")
                "address" -> endpointListenerBuilder.addPropertyValue(it.name, address)
                else -> null
            }
        }
        registry.registerBeanDefinition(name, endpointListenerBuilder.rawBeanDefinition)
    }
}

