package com.example.kotlinreactivemongo.config.web

import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.*
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer

@Configuration
@EnableWebFlux
class WebConfig : WebFluxConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources", "classpath:/static/")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/auth/token")
    }

    @Bean
    fun freeMarkerConfigurer(applicationContext: ReactiveWebApplicationContext): FreeMarkerConfigurer {
        val configurer = FreeMarkerConfigurer()
        configurer.setTemplateLoaderPath("classpath:/templates/")
        configurer.setResourceLoader(applicationContext)
        return configurer
    }

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.freeMarker()
    }
}