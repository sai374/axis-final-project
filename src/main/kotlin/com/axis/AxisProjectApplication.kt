package com.axis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
class AxisProjectApplication

fun main(args: Array<String>) {
	runApplication<AxisProjectApplication>(*args)
}
