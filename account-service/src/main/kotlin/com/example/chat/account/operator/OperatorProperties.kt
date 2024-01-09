package com.example.chat.account.operator

import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import java.time.Duration

@Validated
class OperatorProperties {
    @field:NotBlank
    var currentNamespace: String = "default"
    var pollingInterval: Duration = Duration.ofMinutes(5)
    @field:NotBlank
    var configMapName: String = ""

}