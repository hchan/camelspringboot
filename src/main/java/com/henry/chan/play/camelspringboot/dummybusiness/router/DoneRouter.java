package com.henry.chan.play.camelspringboot.dummybusiness.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Henry's router to mark things as done
 */
@Component
public class DoneRouter extends RouteBuilder {

    public static final String ROUTEID = DoneRouter.class.getSimpleName();

    @Override
    public void configure() {
        fromF("direct:%s", ROUTEID).routeId(ROUTEID)
                .log("Done : ${body}");
    }

}
