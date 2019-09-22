package xyz.ivyxjc.libra

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@MapperScan("xyz.ivyxjc.libra.core.dao")
@ComponentScan("xyz.ivyxjc.libra.core")
@EnableTransactionManagement
open class TestStartRunner