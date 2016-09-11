package sample.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/")
public class HelloController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);

    public static final int DOWNLOAD_SIZE = 1_000_000;

    @GetMapping("hello")
    @ResponseBody
    public String hello() {
        return "Hello";
    }

    @GetMapping("download")
    @ResponseBody
    public InputStreamResource download(@RequestParam(name = "id", required = false, defaultValue = "-1") int id) {
        byte[] phrase = "Hello".getBytes(StandardCharsets.UTF_8);
        InputStream inp = Utils.repeatWithFeedback(phrase, DOWNLOAD_SIZE / phrase.length, 100_000,
                sent -> LOGGER.info("#{}: Controller sent {} bytes", id, sent));
        return new InputStreamResource(inp);
    }

}
