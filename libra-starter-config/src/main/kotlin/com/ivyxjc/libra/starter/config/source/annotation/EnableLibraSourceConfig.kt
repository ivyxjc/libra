package com.ivyxjc.libra.starter.config.source.annotation

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(LibraSourceConfigBootstrapConfiguration::class)
annotation class EnableLibraSourceConfig