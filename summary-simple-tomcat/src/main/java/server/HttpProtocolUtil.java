package server;

/**
 * HTTP协议工具类，主要提供响应头信息，这里我们只提供200和404的情况
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class HttpProtocolUtil {
    /**
     * 为响应码 200 提供请求头信息
     */
    public static String getHttpHeader200(long contextLength) {
        return "HTTP/1.1 200 OK \n" +
                "Content-Type: text/html;charset=utf-8 \n" +
                "Content-Length: " + contextLength + " \n" +
                "\r\n";
    }

    /**
     * 为响应码 404 提供请求头信息（此处也包含了数据内容）
     */
    public static String getHttpHeader404() {
        String str404 = "<h1>404 not found</h1>";
        return "HTTP/1.1 404 NOT FOUND \n" +
                "Content-Type: text/html;charset=utf-8 \n" +
                "Content-Length: " + str404.getBytes().length + " \n" +
                "\r\n" + str404;
    }
}
