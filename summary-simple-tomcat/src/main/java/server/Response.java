package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author jie.luo
 * @since 2024/8/5
 */
public class Response {

    private OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Response() {
    }

    /**
     * @param path url，根据url来获取到静态资源的绝对路径，进一步根据绝对路径读取该静态资源文件，最终通过输出流输出
     */
    public void outputHtml(String appBase, String context, String path) throws IOException {
        // 根据静态资源文件的最对路径
        String absoluteResourcePath = appBase + context + path;

        System.out.println("请求地址 ===》 " + absoluteResourcePath);

        // 输出静态资源文件
        File file = new File(absoluteResourcePath);
        if (file.exists() && file.isFile()) {
            // 存在
            // 读取静态资源文件，输出静态资源文件
            StaticResourceUtil.outputStaticResource(new FileInputStream(file), outputStream);
        } else {
            // 不存在，输出 404
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }

    /**
     * 输出指定字符串
     *
     * @param context 输出字符串
     */
    public void output(String context) throws IOException {
        outputStream.write(context.getBytes());
    }
}
