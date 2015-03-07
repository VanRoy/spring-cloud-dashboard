package com.github.vanroy.cloud.dashboard.stream;

import com.github.vanroy.cloud.dashboard.repository.ApplicationRepository;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Proxy an EventStream request (data.stream via proxy.stream) since EventStream does not yet support CORS (https://bugs.webkit.org/show_bug.cgi?id=61862)
 * so that a UI can request a stream from a different server.
 * @author Julien Roy
 */
public class CircuitBreakerStreamServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerStreamServlet.class);

    private final HttpClient httpClient;
    private final ApplicationRepository repository;

    public CircuitBreakerStreamServlet(HttpClient httpClient, ApplicationRepository repository) {
        super();
        this.httpClient = httpClient;
        this.repository = repository;
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String proxyUrl;
        String appName = request.getParameter("appName");
        String instanceId = request.getParameter("instanceId");

        if(appName == null && instanceId == null) {
            response.setStatus(500);
            response.getWriter().println("Please use appName or instanceId to select data to stream");
            return;
        }

        if(appName != null) {
            proxyUrl = repository.getApplicationCircuitBreakerStreamUrl(appName);
            if (proxyUrl == null) {
                response.setStatus(500);
                response.getWriter().println("Application cluster circuit breaker not found");
                return;
            }
        } else {
            proxyUrl = repository.getInstanceCircuitBreakerStreamUrl(instanceId);
            if (proxyUrl == null) {
                response.setStatus(500);
                response.getWriter().println("Instance circuit breaker not found");
                return;
            }
        }

        HttpUriRequest httpRequest = null;
        InputStream is = null;

        logger.info("\n\nProxy opening connection to: " + proxyUrl + "\n\n");
        try {
            if(HttpHead.METHOD_NAME.equalsIgnoreCase(request.getMethod())) {
                httpRequest = new HttpHead(proxyUrl);
            } else {
                httpRequest = new HttpGet(proxyUrl);
            }

            HttpResponse httpResponse = httpClient.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                // writeTo swallows exceptions and never quits even if outputstream is throwing IOExceptions (such as broken pipe) ... since the inputstream is infinite
                // httpResponse.getEntity().writeTo(new OutputStreamWrapper(response.getOutputStream()));
                // so I copy it manually ...
                if(httpResponse.getEntity() != null) {

                    is = httpResponse.getEntity().getContent();

                    // set headers
                    for (Header header : httpResponse.getAllHeaders()) {
                        response.addHeader(header.getName(), header.getValue());
                    }

                    // copy data from source to response
                    OutputStream os = response.getOutputStream();
                    int b;
                    while ((b = is.read()) != -1) {
                        try {
                            os.write(b);
                            if (b == 10 /** flush buffer on line feed */) {
                                os.flush();
                            }
                        } catch (Exception e) {
                            if (e.getClass().getSimpleName().equalsIgnoreCase("ClientAbortException")) {
                                // don't throw an exception as this means the user closed the connection
                                logger.debug("Connection closed by client. Will stop proxying ...");
                                // break out of the while loop
                                break;
                            } else {
                                // received unknown error while writing so throw an exception
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            } else {
                response.setStatus(statusCode);
            }
        } catch (Exception e) {
            logger.error("Error proxying request: " + proxyUrl, e);
            response.setStatus(500);
        } finally {
            if (httpRequest != null) {
                try {
                    httpRequest.abort();
                } catch (Exception e) {
                    logger.error("failed aborting proxy connection.", e);
                }
            }

            // httpget.abort() MUST be called first otherwise is.close() hangs (because data is still streaming?)
            if (is != null) {
                // this should already be closed by httpget.abort() above
                try {
                    is.close();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
    }
}