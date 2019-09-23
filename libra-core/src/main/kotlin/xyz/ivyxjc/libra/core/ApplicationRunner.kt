package xyz.ivyxjc.libra.core

import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.Yaml
import xyz.ivyxjc.libra.common.utils.ClassUtils
import xyz.ivyxjc.libra.core.connection.JmsConnectionUtils
import xyz.ivyxjc.libra.core.endpoint.ArtemisEndpointListener
import java.util.regex.Pattern

@SpringBootApplication(exclude = [JmsAutoConfiguration::class])
@PropertySource(value = ["private-endpoint.properties", "private-jdbc.properties"])
@MapperScan("xyz.ivyxjc.libra.core.dao")
open class ApplicationRunner


fun main() {
    val context = SpringApplication.run(ApplicationRunner::class.java)
    val bean = context.getBean("listener1") as ArtemisEndpointListener
    bean.start()
    Thread.sleep(1000000)
}

@Component
class StartupInit : BeanDefinitionRegistryPostProcessor, ApplicationContextAware, EnvironmentAware {

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
        val cfName = map["jmsConnectionFactory"] as String
        val address = map["address"] as String
        val sourceIdInt = map["sourceId"]
        val sourceId = sourceIdInt.toString().toLong()
        val clz = Class.forName(className)

        val beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clz)

        val fields = ClassUtils.getAllFields(clz)

        fields.forEach {
            val tmp: Any? = when (it.name) {
                "jmsConnectionFactory", "platform", "messageListener" -> beanDefinitionBuilder.addPropertyReference(it.name, map[it.name] as String)
                "address" -> beanDefinitionBuilder.addPropertyValue(it.name, address)
                "sourceId" -> beanDefinitionBuilder.addPropertyValue(it.name, sourceId)
                else -> null
            }
        }
        registry.registerBeanDefinition(name, beanDefinitionBuilder.rawBeanDefinition)
    }
}

