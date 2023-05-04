package it.polito.wa2.g29.server.config

import jakarta.servlet.MultipartConfigElement
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.MultipartConfigFactory
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.util.unit.DataSize
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ServerConfig : WebMvcConfigurer {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/notFound").setViewName("forward:/index.html")
    }

    @Bean
    fun containerCustomizer(): WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        return WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> { factory ->
            factory?.addErrorPages(ErrorPage(HttpStatus.NOT_FOUND, "/notFound"))
        }
    }

    @Bean
    fun multipartConfigElement(): MultipartConfigElement {
        val factory = MultipartConfigFactory()
        factory.setMaxFileSize(DataSize.ofMegabytes(10))
        factory.setMaxRequestSize(DataSize.ofMegabytes(10))
        return factory.createMultipartConfig()
    }

}