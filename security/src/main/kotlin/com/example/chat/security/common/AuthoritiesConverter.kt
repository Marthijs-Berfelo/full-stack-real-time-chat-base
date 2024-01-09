package com.example.chat.security.common

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder

interface AuthoritiesConverter : Converter<Jwt, AbstractAuthenticationToken> {
    var decoder: ReactiveJwtDecoder
}