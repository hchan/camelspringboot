package com.henry.chan.play.camelspringboot;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.mutable.MutableBoolean;

/**
 * Route Utils
 */
public class RouteUtils {
    public static void addAdviceWithDoOnceInterceptor(CamelContext camelContext, RouteDefinition routeDefinition,
                                                      Processor processor, MutableBoolean doOnceFlag) throws Exception {
        routeDefinition.adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                intercept().process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        if (!doOnceFlag.booleanValue()) {
                            processor.process(exchange);
                            doOnceFlag.setTrue();
                        }
                    }
                })
                        .end();
            }
        });
    }
}
