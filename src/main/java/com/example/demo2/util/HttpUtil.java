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

public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 连接池最大连接数
     **/
    private static final int HTTPCLIENT_MAX_TOTAL = 200;
    /**
     * 单个路由最大连接数
     **/
    private static final int HTTPCLIENT_DEFAULT_MAX_PER_ROUTE = 20;
    /**
     * 从连接管理器获取连接的超时时间
     **/
    private static final int HTTPCLIENT_CONNECTION_REQUEST_TIMEOUT = 5000;
    /**
     * 建立连接的超时时间
     **/
    private static final int HTTPCLIENT_CONNECT_TIMEOUT = 5000;
    /**
     * 请求获取数据的超时时间
     **/
    private static final int HTTPCLIENT_SOCKET_TIMEOUT = 5000;
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

    private HttpUtil() {
        // no constructor function
    }

    private static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * GET请求，针对文本
     */
    public static String get(String url) {
        if (StringUtils.isEmpty(url)) {
            log.error("GET请求不合法，请检查uri参数!");
            return null;
        }

        log.info("Get start");

        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = null;
        CloseableHttpResponse response;
        String respContent = null;
        try {
            // 创建GET请求
            httpGet = new HttpGet(url);
            // 执行GET请求
            response = httpClient.execute(httpGet);
            // 获取响应内容
            respContent = getRespContent(response, "GET");
        } catch (IOException e) {
            log.error("GET请求异常, url: " + url + ", errMsg: " + e.getMessage());
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }

        return respContent;
    }

    /**
     * GET请求，针对多媒体，请求成功后，会直接从对方服务器下载多媒体文件到本地磁盘
     */
    public static void get(String url, String des) {
        log.info("-------HttpClient GET开始---------");
        log.info("GET: " + url);
        if (StringUtils.isEmpty(url)) {
            log.error("GET请求不合法，请检查uri参数!");
            return;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = null;
        CloseableHttpResponse response;
        try {
            // 创建GET请求
            httpGet = new HttpGet(url);
            // 执行GET请求
            response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            String reasonPhrase = statusLine.getReasonPhrase();

            if (statusCode == HttpStatus.SC_OK) {
                log.info("get ok");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }

        log.info("-------HttpClient GET结束----------");
    }

    /**
     * 获取响应，针对文本
     */
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
    public static String post(String url, String jsonStr) {
        if (StringUtils.isEmpty(url)) {
            log.error("POST请求不合法，请检查uri参数!");
            return null;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = null;
        CloseableHttpResponse response;
        String respContent = null;
        try {
            // 创建POST请求
            httpPost = new HttpPost(url);
            // 执行POST请求
            StringEntity entity = new StringEntity(jsonStr, StandardCharsets.UTF_8);
            entity.setContentType("application/json; charset=UTF-8");
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            // 获取响应内容
            respContent = getRespContent(response, "POST");
        } catch (IOException e) {
            log.error("POST请求异常, url: " + url + ", params: " + jsonStr + ", errMsg: " + e.getMessage());
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }

        return respContent;
    }

    /**
     * POST请求，请求参数为表单
     */
    public static String post(String url, Map<String, String> params) {
        if (StringUtils.isEmpty(url)) {
            log.error("POST请求不合法，请检查uri参数!");
            return null;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = null;
        CloseableHttpResponse response;
        String respContent = null;
        try {
            // 创建POST请求
            httpPost = new HttpPost(url);
            // 设置POST请求
            List<BasicNameValuePair> postData = new ArrayList<>();
            params.forEach((key, value) -> postData.add(new BasicNameValuePair(key, value)));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, StandardCharsets.UTF_8);
            entity.setContentType("application/x-www-form-urlencoded");
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            // 执行POST请求
            response = httpClient.execute(httpPost);
            // 获取响应内容
            respContent = getRespContent(response, "POST");
        } catch (IOException e) {
            log.error("POST请求异常, url: " + url + ", params: " + params + ", errMsg: " + e.getMessage());
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }

        return respContent;
    }

    /**
     * POST请求，请求参数为Json格式
     */
    public static int postAndGetStatusCode(String url, String jsonStr) {
        if (StringUtils.isEmpty(url)) {
            log.error("POST请求不合法，请检查uri参数!");
            return 400;
        }

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = null;
        CloseableHttpResponse response;
        int respStatusCode = 200;
        try {
            // 创建POST请求
            httpPost = new HttpPost(url);
            // 执行POST请求
            StringEntity entity = new StringEntity(jsonStr, StandardCharsets.UTF_8);
            entity.setContentType("application/json; charset=UTF-8");
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            // 获取响应内容
            respStatusCode = getRespStatusCode(response);
        } catch (IOException e) {
            log.error("POST请求异常, url: " + url + ", params: " + jsonStr + ", errMsg: " + e.getMessage());
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }

        return respStatusCode;
    }

    /**
     * 获取响应状态码
     */
    private static int getRespStatusCode(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        return statusLine.getStatusCode();
    }
}
