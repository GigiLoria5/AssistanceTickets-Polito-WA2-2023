package it.polito.wa2.g29.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DummyController {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello world"
    }
}