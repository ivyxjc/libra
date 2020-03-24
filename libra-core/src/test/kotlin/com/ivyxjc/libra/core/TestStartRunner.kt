package com.ivyxjc.libra.core

import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.PropertySource
import org.springframework.transaction.annotation.EnableTransactionManagement


@MapperScan("com.ivyxjc.libra.core.dao")
@EnableTransactionManagement
@PropertySource(value = ["private-jdbc.properties"])
open class TestStartRunner
