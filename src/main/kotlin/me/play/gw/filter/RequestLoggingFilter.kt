package me.play.gw.filter

import mu.KotlinLogging
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/global-filters.html
 * https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/gatewayfilter-factories/modifyrequestbody-factory.html
 * */
@Component
class RequestLoggingFilter(
    private val modifyRequestBodyGatewayFilterFactory: ModifyRequestBodyGatewayFilterFactory
) : GlobalFilter, Ordered {
    private val log = KotlinLogging.logger {}

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return modifyRequestBodyGatewayFilterFactory
            .apply(modifyRequestBodyGatewayFilterConfig())
            .filter(exchange, chain)
    }

    private fun modifyRequestBodyGatewayFilterConfig(): ModifyRequestBodyGatewayFilterFactory.Config {
        return ModifyRequestBodyGatewayFilterFactory.Config()
            .setRewriteFunction(String::class.java, String::class.java) { exchange, body ->
                log.info(
                    "[Request] Request Id: {}, URI: {}, QueryParams: {}, Body: {}",
                    exchange.request.id,
                    exchange.request.uri,
                    exchange.request.queryParams,
                    body
                )
                Mono.justOrEmpty(body)
            }
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE

}