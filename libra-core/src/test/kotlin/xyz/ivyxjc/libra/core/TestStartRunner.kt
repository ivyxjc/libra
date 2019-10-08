package xyz.ivyxjc.libra.core

import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.PropertySource
import org.springframework.transaction.annotation.EnableTransactionManagement


@MapperScan("xyz.ivyxjc.libra.core.dao")
@EnableTransactionManagement
@PropertySource(value = ["private-jdbc.properties"])
open class TestStartRunner
