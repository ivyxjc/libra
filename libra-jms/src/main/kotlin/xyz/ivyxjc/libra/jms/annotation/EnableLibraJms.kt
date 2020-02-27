package xyz.ivyxjc.libra.jms.annotation

import org.springframework.context.annotation.Import
import org.springframework.jms.annotation.EnableJms

@Import(LibraJmsBootstrapConfiguration::class)
@EnableJms
annotation class EnableLibraJms

