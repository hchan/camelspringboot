package com.henry.chan.play.camelspringboot.dummybusiness.transformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Transform Bean that reverses a string
 */

@Slf4j
@Component
public class ReverseTransformer implements StringTransformer {

    public String transform (String str) {
        log.info("str : {}", str);
        String reverse = "";
        for (int i = str.length() - 1; i >= 0; i--) {
            reverse = reverse + str.charAt(i);
        }
        return reverse;

    }
}
