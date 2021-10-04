package jp.co.ogis_ri.nautible.app.stock.core.rest;

import javax.enterprise.inject.spi.CDI;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.logging.MDC;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * MDCに情報を設定するInterceptor。REST APIでの設定値は以下。
 * <ul>
 * <li>「x-request-id」:istioの分散トレーシングのrequest-id
 * <li>「url」:HttpのURL
 * <li>「method」:HttpMethod
 * </ul>
 */
@jp.co.ogis_ri.nautible.app.stock.core.rest.MDC
@Interceptor
public class MDCInterceptor {//implements ContainerRequestFilter, ContainerResponseFilter {

    @AroundInvoke
    Object logInvocation(InvocationContext context) throws Exception {
        RoutingContext routingContext = CDI.current().select(RoutingContext.class).get();
        HttpServerRequest request = routingContext.request();
        MDC.put("x-request-id", request.getHeader("x-request-id"));
        MDC.put("url", request.path());
        MDC.put("method", request.method());
        Object ret = null;
        try {
            ret = context.proceed();
        } finally {
            MDC.clear();
        }
        return ret;
    }

}
