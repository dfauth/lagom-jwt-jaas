package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import thingy.Directive;
import thingy.DirectiveBuilder;

import java.io.IOException;

import static thingy.PrincipalType.USER;


public class DirectiveTest {

    private static final Logger logger = LoggerFactory.getLogger(DirectiveTest.class);

    @Test
    public void testIt() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String value = mapper.writeValueAsString(new Directive("test", USER.of("fred")));
            logger.info("directive: "+value);
            DirectiveBuilder builder = mapper.readValue(value, DirectiveBuilder.class);
            logger.info("builder: "+builder);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
