package com.example.demo2.util;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 连接池最大连接数
     **/
    private static final int HTTPCLIENT_MAX_TOTAL = 2000;
    /**
     * 单个路由最大连接数
     **/
    private static final int HTTPCLIENT_DEFAULT_MAX_PER_ROUTE = 2000;
    /**
     * 从连接管理器获取连接的超时时间
     **/
    private static final int HTTPCLIENT_CONNECTION_REQUEST_TIMEOUT = 1000;
    /**
     * 建立连接的超时时间
     **/
    private static final int HTTPCLIENT_CONNECT_TIMEOUT = 1000;
    /**
     * 请求获取数据的超时时间
     **/
    private static final int HTTPCLIENT_SOCKET_TIMEOUT = 30000;
    /**
     * 请求重试次数
     **/
    private static final int HTTPCLIENT_RETRY_COUNT = 3;

    // 连接管理器
    private static final PoolingHttpClientConnectionManager connectionManager;
    // 请求配置
    private static final RequestConfig requestConfig;
    // http客户端
    private static final CloseableHttpClient httpClient;

    // 请求重试机制
    private static final HttpRequestRetryHandler requestRetryHandler = (exception, executionCount, context) -> {
        if (executionCount > HTTPCLIENT_RETRY_COUNT) {
            // Do not retry if over 3 retry count
            return false;
        }
        if (exception instanceof InterruptedIOException) {
            // Timeout
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host
            return false;
        }
        if (exception instanceof SSLException) {
            // SSL handshake exception
            return false;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        // Retry if the request is considered idempotent
        return !(request instanceof HttpEntityEnclosingRequest);
    };

    static {
        // 创建http连接管理器，使用连接池管理http连接
        connectionManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connectionManager.setMaxTotal(HTTPCLIENT_MAX_TOTAL);
        // 设置每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(HTTPCLIENT_DEFAULT_MAX_PER_ROUTE);
        // 创建请求配置
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(HTTPCLIENT_CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(HTTPCLIENT_CONNECT_TIMEOUT)
                .setSocketTimeout(HTTPCLIENT_SOCKET_TIMEOUT)
                .build();
        // 创建http客户端构建器
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(requestRetryHandler)   // 默认重试3次
                .build();
    }

    private HttpClientUtil() {
        // no constructor function
    }

    private static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * GET请求
     */
    public static String get(String url) {
        if (StringUtils.isEmpty(url)) {
            log.error("GET请求不合法，请检查uri参数!");
            return null;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        String respContent = null;
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            respContent = getRespContent(response, "GET");
        } catch (IOException e) {
            log.error("GET请求异常", e);
        }
        return respContent;
    }

    private static String getRespContent(HttpResponse response, String method) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reasonPhrase = statusLine.getReasonPhrase();
        String respContent = null;

        if (statusCode == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            respContent = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } else {
            log.error("{}:statusCode[{}], desc[{}]", method, statusCode, reasonPhrase);
        }

        return respContent;
    }

    /**
     * POST请求，请求参数为json格式
     */
    public static String post(String url, String jsonStr, Map<String, String> headers) {
        if (StringUtils.isEmpty(url)) {
            log.error("POST请求不合法，请检查uri参数!");
            return null;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);

        if (headers != null) {
            headers.forEach(httpPost::addHeader);
        }

        StringEntity entity = new StringEntity(jsonStr, StandardCharsets.UTF_8);
        entity.setContentType("application/json; charset=UTF-8");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);

        String respContent = null;

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            respContent = getRespContent(response, "POST");
        } catch (IOException e) {
            log.error("POST请求异常", e);
        }

        return respContent;
    }

    /**
     * POST请求，请求参数为表单
     */
    public static String post(String url, Map<String, String> params, Map<String, String> headers) {
        if (StringUtils.isEmpty(url)) {
            log.error("POST请求不合法，请检查uri参数!");
            return null;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);

        if (headers != null) {
            headers.forEach(httpPost::addHeader);
        }

        List<BasicNameValuePair> postData = new ArrayList<>();
        params.forEach((key, value) -> postData.add(new BasicNameValuePair(key, value)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, StandardCharsets.UTF_8);
        entity.setContentType("application/x-www-form-urlencoded");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);

        String respContent = null;

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            respContent = getRespContent(response, "POST");
        } catch (IOException e) {
            log.error("POST请求异常", e);
        }

        return respContent;
    }
}
