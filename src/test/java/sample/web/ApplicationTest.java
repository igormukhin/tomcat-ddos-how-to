package sample.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT) // RANDOM_PORT
public class ApplicationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void testHello() throws Exception {
        // when
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/hello", String.class);

        // then
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isEqualTo("Hello");
    }

    @Test
    public void testDownloadOnce() throws Exception {
        // when
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/download", String.class);

        // then
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).startsWith("HelloHello");
        assertThat(entity.getBody().length()).isCloseTo(1_000_000, withinPercentage(99));
    }

    @Test
    public void testDownloadOnceWithUrl() throws Exception {
        // when
        InputStream in = new URL("http://localhost:" + port + "/download").openStream();
        String body = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
        in.close();

        // then
        assertThat(body).startsWith("HelloHello");
        assertThat(body.length()).isCloseTo(1_000_000, withinPercentage(99));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(timeout = 1500_000L)
    public void ddosDownload() throws Exception {
        for (int requests = 0; requests < 10_000; requests++) {

            LOGGER.info("#{}: Requesting...", requests);
            InputStream in = new URL("http://localhost:" + port + "/download?id=" + requests).openStream();

            // read some data from the stream and leave it handing in the middle
            LOGGER.info("#{}: Reading some data...", requests);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StreamUtils.copyRange(in, out, 0, 10_000);
            assertThat(out.toString(StandardCharsets.UTF_8.name())).startsWith("HelloHelloHello");

            LOGGER.info("#{}: Some data read", requests);
        }
    }
}