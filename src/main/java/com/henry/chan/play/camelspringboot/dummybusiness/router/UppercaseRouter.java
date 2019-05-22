package com.henry.chan.play.camelspringboot.dummybusiness.router;

import com.henry.chan.play.camelspringboot.dummybusiness.transformer.UppercaseTransformer;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Henry's router to make a string uppercase
 */
@Component
public class UppercaseRouter extends RouteBuilder {

    public static final String ROUTEID = UppercaseRouter.class.getSimpleName();

    @Override
    public void configure() {
        fromF("direct:%s", ROUTEID).routeId(ROUTEID)
                .transform().method(UppercaseTransformer.class);
    }

}
