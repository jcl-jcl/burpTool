package burp;

import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
import com.arronlong.httpclientutil.common.HttpMethods;
import com.arronlong.httpclientutil.common.HttpResult;
import com.arronlong.httpclientutil.common.SSLs;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

public class MyHttpClient {
    private static Logger logger = Logger.getLogger(MyHttpClient.class);
    private static Header[] headers = HttpHeader.custom()
            .other("Content-Type","application/json")
            .build();
    private static HCB hcb;

    static {
        try {
            hcb = HCB.custom()
                        .pool(100, 10) //启用连接池，每个路由最大创建10个链接，总连接数限制为100个
                        .sslpv(SSLs.SSLProtocolVersion.TLSv1_2) 	//设置ssl版本号，默认SSLv3，也可以调用sslpv("TLSv1.2")
                        .ssl()//https，支持自定义ssl证书路径和密码，ssl(String keyStorePath, String keyStorepass)
                        .retry(5);

        } catch (HttpProcessException e) {
            e.printStackTrace();
        }
    }
    private static HttpClient client = hcb.build();

    //    private static HttpClient client = hcb.build();
    private static HttpConfig config = HttpConfig.custom()
            .headers(headers)
            .method(HttpMethods.POST)
            //设置headers，不需要时则无需设置
            .encoding("UTF-8") //设置请求和返回编码，默认就是Charset.defaultCharset()
            .client(client); //如果只是简单使用，无需设置，会自动获取默认的一个client对象

    public static HttpResult post(String url, Map<String, Object> data)  {
        JSONObject jsonObject = new JSONObject(data);
        config.url(url).json(jsonObject.toJSONString());
        HttpResult httpResult = null;
        try {
            httpResult = HttpClientUtil.sendAndGetResp(config);
        } catch (HttpProcessException e) {
            e.printStackTrace();
        } finally {
            config.map().clear();
        }
        return httpResult;
    }

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap();
        params.put("method", "request");
        HttpResult post = MyHttpClient.post("http://127.0.0.1:8080/BurpService/saveRequest", params);
    }

}
