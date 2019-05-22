package com.henry.chan.play.camelspringboot.dummybusiness.router;

import com.henry.chan.play.camelspringboot.dummybusiness.transformer.ReverseTransformer;
import com.henry.chan.play.camelspringboot.dummybusiness.transformer.UppercaseTransformer;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Henry's router to make a string reversed
 */
@Component
public class ReverseRouter extends RouteBuilder {

    public static final String ROUTEID = ReverseRouter.class.getSimpleName();

    @Override
    public void configure() {
        fromF("direct:%s", ROUTEID).routeId(ROUTEID)
                .transform().method(ReverseTransformer.class);
    }

}
