package com.henry.chan.play.camelspringboot.dummybusiness.router;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Henry's Dummy Business Router that will contain the starting business logic
 * to transform a string to
 * Uppercase
 * Reverse a string
 * Done
 */
@Slf4j
@Component
public class DummyBusinessRouter extends RouteBuilder {
    public static final String ROUTEID = DummyBusinessRouter.class.getSimpleName();
    public static final String DEFAULT_PAYLOAD = "Hello World!";
    public static String PAYLOAD = null;

    @Override
    public void configure() {
                from("quartz2://" + ROUTEID + "?cron={{dummybusinessrouter.cron}}")
                        .from("direct:" + ROUTEID)
                .routeId(ROUTEID)
                .process(exchange -> {
                    if (PAYLOAD == null) {
                        exchange.getMessage().setBody(DEFAULT_PAYLOAD);
                    }
                })
                .toF("direct:%s", UppercaseRouter.ROUTEID)
                .process(exchange -> {
                    log.info("After {} : {}", UppercaseRouter.ROUTEID, exchange.getMessage().getBody()); // logging example with Lombok (I like this more)
                })
                .toF("direct:%s", ReverseRouter.ROUTEID)
                .log(LoggingLevel.INFO, "After " + ReverseRouter.ROUTEID + " : ${body}") // logging with Camel
                .to("stream:out")
                .toF("direct:%s", DoneRouter.ROUTEID)
                ;
    }

}
