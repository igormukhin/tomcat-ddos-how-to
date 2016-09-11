package sample.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class Utils {

    /**
     * http://www.nurkiewicz.com/2014/07/building-extremely-large-in-memory.html
     *
     * @param sample
     * @param times
     * @return
     */
    public static InputStream repeat(byte[] sample, int times) {
        return new InputStream() {
            private long pos = 0;
            private final long total = (long) sample.length * times;

            public int read() throws IOException {
                return pos < total ?
                        sample[(int) (pos++ % sample.length)] :
                        -1;
            }
        };
    }

    public static InputStream repeatWithFeedback(byte[] sample, int times,
                                                 int feedbackEveryBytes, Consumer<Long> feedback) {
        return new InputStream() {
            private long pos = 0;
            private final long total = (long) sample.length * times;

            public int read() throws IOException {
                if ((pos + 1) % feedbackEveryBytes == 0) {
                    feedback.accept(pos);
                }

                return pos < total ?
                        sample[(int) (pos++ % sample.length)] :
                        -1;
            }
        };
    }

}
