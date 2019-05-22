package com.henry.chan.play.camelspringboot;

import com.henry.chan.play.camelspringboot.dummybusiness.router.DoneRouter;
import com.henry.chan.play.camelspringboot.dummybusiness.router.DummyBusinessRouter;
import com.henry.chan.play.camelspringboot.dummybusiness.router.ReverseRouter;
import com.henry.chan.play.camelspringboot.dummybusiness.router.UppercaseRouter;
import com.henry.chan.play.camelspringboot.dummybusiness.transformer.ReverseTransformer;
import com.henry.chan.play.camelspringboot.dummybusiness.transformer.UppercaseTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test Class with Camel @{@link RouteDefinition#adviceWith(CamelContext, RouteBuilder)}, and @{@link NotifyBuilder}
 * Note that I am using a @{@link CountDownLatch} to notify me when {@link DoneRouter} has ran
 */
//@RunWith(SpringJUnit4ClassRunner.class) ... only works with Junit4
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {MySpringBootApplication.class})
@ActiveProfiles({ "test" })
@Slf4j

public class MySpringBootApplicationIntegrationTest {
    public static final String PAYLOAD = "Payload from " + MySpringBootApplicationIntegrationTest.class.getSimpleName() + "!!";


    @Autowired
    private CamelContext camelContext;
    @Autowired
    private ReverseTransformer reverseTransformer;
    @Autowired
    private UppercaseTransformer uppercaseTransformer;

    private boolean addTestInterceptorsRan = false;
    private MutableBoolean uppercaseInterceptorRan = new MutableBoolean();
    private MutableBoolean reverseInterceptorRan = new MutableBoolean();
    private MutableBoolean doneInterceptorRan = new MutableBoolean();

    private Map<String, NotifyBuilder> notifyBuilders = new LinkedHashMap<>();
    private CountDownLatch countDownLatch = new CountDownLatch(1); // latch needed because of Camel's notifyBuilder bug
    @BeforeAll
    public static void beforeClass() {
        System.setProperty("dummybusinessrouter.cron", "0 0 0 1 1 ? 2099");
        DummyBusinessRouter.PAYLOAD = PAYLOAD;
    }

    @Test
    public void mainTest() throws Exception {
        log.info("Starting test!");
        addTestInterceptors();
        addNotifierBuilders();
        ProducerTemplate template = camelContext.createProducerTemplate();
        template.sendBody("direct:" + DummyBusinessRouter.ROUTEID, PAYLOAD);
        assertNotifyBulders();
        countDownLatch.await(30, TimeUnit.SECONDS);
        assertTrue(uppercaseInterceptorRan.booleanValue());
        assertTrue(reverseInterceptorRan.booleanValue());
        assertTrue(doneInterceptorRan.booleanValue());
    }

    private void addNotifierBuilders() {
        addNotifierBuilder(DummyBusinessRouter.ROUTEID);

        /* bug with Camel nested notifyBuilder ... sigh */
        /* http://camel.465427.n5.nabble.com/An-issue-with-NotifyBuilder-on-nested-routes-td5777774.html */
        /*
        addNotifierBuilder(UppercaseRouter.ROUTEID);
        addNotifierBuilder(ReverseRouter.ROUTEID);
        addNotifierBuilder(DoneRouter.ROUTEID);
        */
    }
    private void addNotifierBuilder(String routeId) {
        NotifyBuilder notifyBuilder = new NotifyBuilder(camelContext)
                .fromRoute(routeId)
                .whenDone(1)
                .create();
        notifyBuilders.put(routeId, notifyBuilder);
    }
    private void assertNotifyBulders() {
        Set<Map.Entry<String, NotifyBuilder>> entrySet = notifyBuilders.entrySet();
        for ( Map.Entry<String, NotifyBuilder> entry : entrySet) {
            String routeId = entry.getKey();
            NotifyBuilder notifyBuilder = entry.getValue();
            log.info("asserting ... {}", routeId);
            assertTrue(notifyBuilder.matches(1, TimeUnit.SECONDS));
            notifyBuilder.reset();
        }
    }

    @SuppressWarnings("unchecked")
    private void addTestInterceptors() {
        if (addTestInterceptorsRan) {
            return;
        } else {
            addTestInterceptorsRan = true;
        }
        try {
            RouteDefinition routeDefinition = camelContext.getRouteDefinition(UppercaseRouter.ROUTEID);
            RouteUtils.addAdviceWithDoOnceInterceptor(camelContext, routeDefinition,
                    exchange -> {
                               assertEquals(PAYLOAD, exchange.getMessage().getBody());
                    },
                    uppercaseInterceptorRan);

            routeDefinition = camelContext.getRouteDefinition(ReverseRouter.ROUTEID);
            RouteUtils.addAdviceWithDoOnceInterceptor(camelContext, routeDefinition,
                    exchange -> {
                        String expected = uppercaseTransformer.transform(PAYLOAD);
                        assertEquals(expected, exchange.getMessage().getBody());
                    },
                    reverseInterceptorRan);

            routeDefinition = camelContext.getRouteDefinition(DoneRouter.ROUTEID);
            RouteUtils.addAdviceWithDoOnceInterceptor(camelContext, routeDefinition,
                    exchange -> {
                        String expected = reverseTransformer.transform(uppercaseTransformer.transform(PAYLOAD));
                        assertEquals(expected, exchange.getMessage().getBody());
                        countDownLatch.countDown();
                    },
                    doneInterceptorRan);
        } catch (Exception e) {
            log.error("Error with Interceptors: ", e);
        }
    }

}
