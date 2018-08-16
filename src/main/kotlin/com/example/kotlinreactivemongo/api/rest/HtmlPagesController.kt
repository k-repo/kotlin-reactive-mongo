package com.example.kotlinreactivemongo.api.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(path = arrayOf("/test"))
class HtmlPagesController {
    @GetMapping("/ws")
    fun websocket(): String {
        return "websocket"
    }
}
