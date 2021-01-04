package com.axis

import com.google.common.base.Predicates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun docket(): Docket {
        return Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
            .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot"))).build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder().title("Config APIs")
            .description("These APIs will be used by frontend to get the config files.")
            .version("V1.0").build()
    }
}
