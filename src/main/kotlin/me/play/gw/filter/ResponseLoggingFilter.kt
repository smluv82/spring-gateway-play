package me.play.gw.filter

import mu.KotlinLogging
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/global-filters.html
 * https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/gatewayfilter-factories/modifyresponsebody-factory.html
 * */
@Component
class ResponseLoggingFilter(
    private val modifyResponseBodyGatewayFilterFactory: ModifyResponseBodyGatewayFilterFactory
) : GlobalFilter, Ordered {
    private val log = KotlinLogging.logger(this.javaClass.name)

    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        return modifyResponseBodyGatewayFilterFactory.apply(modifyFilterFactoryConfig()).filter(exchange, chain)
    }

    private fun modifyFilterFactoryConfig(): ModifyResponseBodyGatewayFilterFactory.Config {
        return ModifyResponseBodyGatewayFilterFactory.Config()
            .setRewriteFunction(String::class.java, String::class.java) { exchange: ServerWebExchange, body: String ->
                val maxBodyLogLength = 10000
                val truncatedBody = if (body.length > maxBodyLogLength) {
                    body.substring(0, maxBodyLogLength) + "..."
                } else {
                    body
                }

                log.info(
                    "[Response] Request Id: {}, URI: {}, statusCode: {},  Body: {}",
                    exchange.request.id,
                    exchange.request.uri,
                    exchange.response.statusCode,
                    truncatedBody
                )
                Mono.justOrEmpty(body)
            }
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}