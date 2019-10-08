package xyz.ivyxjc.libra.core.mock.biz

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration
import org.springframework.context.annotation.PropertySource
import xyz.ivyxjc.libra.core.StartupInit
import xyz.ivyxjc.libra.core.endpoint.AqEndpointListener
import xyz.ivyxjc.libra.core.endpoint.ArtemisEndpointListener

@SpringBootApplication(exclude = [JmsAutoConfiguration::class], scanBasePackageClasses = [StartupInit::class])
@PropertySource(value = ["private-endpoint.properties", "private-jdbc.properties"])
@MapperScan("xyz.ivyxjc.libra.core.dao")
open class ApplicationRunner


fun main() {
    val context = SpringApplication.run(ApplicationRunner::class.java)
    println(context)
    val listener1 = context.getBean("listener1") as ArtemisEndpointListener
    val listener2 = context.getBean("listener2") as AqEndpointListener
    listener1.start()
    listener2.start()
    Thread.sleep(1000000)
}