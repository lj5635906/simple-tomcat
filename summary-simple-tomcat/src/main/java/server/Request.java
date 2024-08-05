package server;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;

/**
 * 把请求信息封装 Request 对象,根据InputStream输入流封装
 *
 * @author jie.luo
 * @since 2024/8/5
 */
@Data
public class Request {
    /**
     * 请求方法  GET/POST/DELETE/PUT
     */
    private String method;
    /**
     * 请求地址  /  | /index.html
     */
    private String url;
    /**
     * 请求的应用名
     */
    private String context;
    /**
     * 输入流，其他属性从输入流中获取
     */
    private InputStream inputStream;

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;

        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }
        byte[] bytes = new byte[count];
        inputStream.read(bytes);

        String inputStr = new String(bytes);
        System.out.println("========> 请求数据 : \n" + inputStr);

        // GET / HTTP/1.1
        String firstLineStr = inputStr.split("\\n")[0];
        String[] strings = firstLineStr.split(" ");
        this.method = strings[0];

        // /demo1/index.html   -->  /应用名/servlet-url
        this.url = strings[1].substring(strings[1].indexOf("/", 1));
        this.context = strings[1].substring(0, strings[1].indexOf("/", 1));

        System.out.println("==========> method : " + this.method);
        System.out.println("==========> url : " + this.url);
        System.out.println("==========> context : " + this.context);


    }
}
