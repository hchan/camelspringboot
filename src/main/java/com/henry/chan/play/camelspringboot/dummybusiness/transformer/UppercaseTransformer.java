package com.henry.chan.play.camelspringboot.dummybusiness.transformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Transformer Bean to make a string uppercase
 */
@Slf4j
@Component
public class UppercaseTransformer implements StringTransformer {
    public String transform (String s) {
       return s.toUpperCase();
    }
}
