package server;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * 请求处理器
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class RequestProcessor extends Thread {

    private Socket socket;
    private String appBase;
    private Map<String, Map<String, HttpServlet>> contextServlet;

    public RequestProcessor(Socket socket, String appBase, Map<String, Map<String, HttpServlet>> contextServlet) {
        this.socket = socket;
        this.appBase = appBase;
        this.contextServlet = contextServlet;
    }

    @Override
    public void run() {

        try {
            // 从输入流中获取请求信息
            InputStream inputStream = socket.getInputStream();

            // 封装 Request、Response
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            Map<String, HttpServlet> contextHttpServletMap = contextServlet.get(request.getContext());

            // 静态资源请求
            // 验证请求是否为 SimpleTomcat 请求
            if (contextHttpServletMap.get(request.getUrl()) == null) {

                response.outputHtml(appBase, request.getContext(), request.getUrl());

            } else {
                // 动态资源请求
                HttpServlet httpServlet = contextHttpServletMap.get(request.getUrl());
                httpServlet.service(request, response);
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
