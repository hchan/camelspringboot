package com.henry.chan.play.camelspringboot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A bean that returns a message when you call the {@link #saySomething()} method.
 * <p/>
 * Uses <tt>@Component("myBean")</tt> to register this bean with the name <tt>myBean</tt>
 * that we use in the Camel route to lookup this bean.
 *
 * NOTE this file was created via the Maven archetype :
 * https://mvnrepository.com/artifact/org.apache.camel.archetypes/camel-archetype-spring-boot/3.0.0-M2
 */
@Component("myBean")
public class MySpringBean {

    @Value("${greeting}")
    private String say;


    public String saySomething() {
        return say;
    }


}
