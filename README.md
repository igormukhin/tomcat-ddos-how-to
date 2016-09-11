How to DDoS a Tomcat
===========================

See the method `ddosDownload` in `ApplicationTest.java`.

Explanation: if many clients are slow-reading a resource from you server, the thread pool will fill up and the server will stop answering. 