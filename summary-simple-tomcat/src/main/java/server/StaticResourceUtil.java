package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 静态资源工具类
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class StaticResourceUtil {
    /**
     * 获取静态资源文件的绝对路径
     *
     * @param path 文件url
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path) {
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        return absolutePath.replaceAll("\\\\", "/") + path;
    }

    /**
     * 读取静态资源文件输入流，通过输出流输出
     *
     * @param inputStream  静态资源文件输入流
     * @param outputStream 输出流
     */
    public static void outputStaticResource(InputStream inputStream, OutputStream outputStream) throws IOException {

        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }

        int resourceSize = count;

        // 输出 HTTP 请求头，在输出具体内容
        outputStream.write(HttpProtocolUtil.getHttpHeader200(resourceSize).getBytes());

        // 读取内容并输出

        // 已经读取的内容长度
        long written = 0;
        // 计划每次缓存的长度
        int byteSize = 1024;

        byte[] bytes = new byte[byteSize];
        while (written < resourceSize) {
            if (written + byteSize > resourceSize) {
                // 说明剩余未读取大小不足一个1024长度，就按真实长度处理
                // 剩余的文件长度
                byteSize = (int) (resourceSize - written);
                bytes = new byte[byteSize];
            }

            inputStream.read(bytes);
            outputStream.write(bytes);

            outputStream.flush();
            written += byteSize;
        }

    }
}
