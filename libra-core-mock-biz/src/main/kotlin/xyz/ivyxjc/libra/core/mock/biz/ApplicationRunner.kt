package xyz.ivyxjc.libra.core.mock.biz

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration
import org.springframework.context.annotation.PropertySource
import xyz.ivyxjc.libra.core.StartupInit

@SpringBootApplication(exclude = [JmsAutoConfiguration::class], scanBasePackageClasses = [StartupInit::class])
@PropertySource(value = ["private-endpoint.properties", "private-jdbc.properties"])
@MapperScan("xyz.ivyxjc.libra.core.dao")
open class ApplicationRunner


fun main() {
    SpringApplication.run(ApplicationRunner::class.java)
    Thread.sleep(1000000)
}