package it.polito.wa2.g13.server.observer.config

import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import it.polito.wa2.g13.server.observer.aop.AbstractObserveAroundMethodHandler
import it.polito.wa2.g13.server.observer.aop.DefaultObserveAroundMethodHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration(proxyBeanMethods = false)
class ObserveConfiguration {
    @Bean
    @ConditionalOnMissingBean(AbstractObserveAroundMethodHandler::class)
    fun observeAroundMethodHandler(): AbstractObserveAroundMethodHandler {
        return DefaultObserveAroundMethodHandler()
    }

    @Bean
    @ConditionalOnMissingBean(ObservedAspect::class)
    fun observedAspect(observationRegistry: ObservationRegistry?): ObservedAspect {
        return ObservedAspect(observationRegistry!!)
    }
}