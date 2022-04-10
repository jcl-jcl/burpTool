package burp;
import org.apache.log4j.Logger;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class BurpExtender implements IBurpExtender, IHttpListener{
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private PrintWriter stdout;
    private static Logger logger = Logger.getLogger(BurpExtender.class);
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        callbacks.setExtensionName("jcl-tool");
        callbacks.registerHttpListener(this);

    }

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo){
        if (messageIsRequest){
            String request = new String(messageInfo.getRequest());
            String host = messageInfo.getHttpService().getHost();
            IRequestInfo iRequestInfo = helpers.analyzeRequest(messageInfo.getRequest());
            List<IParameter> parameters = iRequestInfo.getParameters();
            HashMap myParameters = new HashMap();
            parameters.forEach(x->{
                try {
                    myParameters.put(x.getName(), URLDecoder.decode(x.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            byte contentType = iRequestInfo.getContentType();
            String method = iRequestInfo.getMethod();
            List<String> headers = iRequestInfo.getHeaders();
            Map<String, Object> params = new HashMap();
            params.put("param", myParameters);
            params.put("contentType",contentType);
            params.put("method",method);
            params.put("headers",headers);
            params.put("host",host);
            if(host.contains(Constant.regex)){
                logger.info("=========================");
                logger.info(params);
                logger.info("=========================");
                ThreadPoolExecutor instance = BurpThreadPoolUtil.getInstance();
                instance.execute(()->{
                    MyHttpClient.post("http://10.4.13.40:8080/BurpService/saveRequest", params);
                });
            }else {
                logger.warn(headers);
            }
        }
    }
}
