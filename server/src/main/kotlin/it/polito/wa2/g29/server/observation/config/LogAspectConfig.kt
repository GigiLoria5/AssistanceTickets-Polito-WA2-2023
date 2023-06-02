package it.polito.wa2.g29.server.observation.config

import it.polito.wa2.g29.server.observation.aop.AbstractLogAspect
import it.polito.wa2.g29.server.observation.aop.DefaultLogAspect
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration(proxyBeanMethods = false)
class LogAspectConfig {
    @Bean
    @ConditionalOnMissingBean
    fun defaultLogAspect(): AbstractLogAspect {
        return DefaultLogAspect()
    }
}