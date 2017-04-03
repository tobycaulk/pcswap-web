package pcswapweb.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import java.net.URI

class HttpClient

val restTemplate = RestTemplate()
val mapper = jacksonObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

inline fun <reified T: Any> typeRef(): TypeReference<T> = object: TypeReference<T>(){}

fun <T, R> post(url: String, request: T, responseType: TypeReference<R>): R? {
    var headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    headers.accept = listOf(MediaType.APPLICATION_JSON)
    var requestEntity = RequestEntity<T>(request, headers, HttpMethod.POST, URI.create(url))
    var response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)

    return mapper.readValue(response.body, responseType)
}