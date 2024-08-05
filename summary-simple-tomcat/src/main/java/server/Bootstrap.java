package server;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 启动主函数
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class Bootstrap {

    private int port = 8080;
    // Map<context, Map<String, HttpServlet>>
    // <应用根目录,<url,servlet>>
    private Map<String, Map<String, HttpServlet>> contextServlet = new HashMap<>();

    private String appBase;

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动 SimpleTomcat
            bootstrap.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {

        // 加载 SimpleTomcat server.xml 配置信息
        loadSimpleTomcatServer();
        // 加载 webapps下 servlet
        loadWebappsServlet();

        // 核心线程数,基本大小
        int corePoolSize = 10;
        // 最大线程数
        int maximumPoolSize = 50;
        // 多长时间对空闲线程进行销毁
        long keepAliveTime = 100L;
        // 对空闲线程进行销毁单位
        TimeUnit unit = TimeUnit.SECONDS;
        // 等待队列
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        // 线程工厂
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        // 线程池拒绝策略：线程池抛异常
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        // 定义线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler);

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("======> Minicat start on port: " + port);

        while (true) {
            Socket socket = serverSocket.accept();

            threadPoolExecutor.execute(new RequestProcessor(socket, appBase, contextServlet));
        }
    }

    // SimpleTomcat 配置信息
    private final Map<String, String> hostMap = new HashMap<>();

    /**
     * 加载 SimpleTomcat server.xml 配置信息
     */
    private void loadSimpleTomcatServer() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");

        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            // 获取根节点
            Element rootElement = document.getRootElement();

            // 获取所有的servlet节点
            Element connectorElement = (Element) rootElement.selectSingleNode("Connector");

            // 获取监听端口
            String port = connectorElement.attributeValue("port");
            this.port = Integer.parseInt(port);

            // 获取所有Host
            List<Element> hostElements = rootElement.selectNodes("//Host");
            for (Element hostElement : hostElements) {
                String name = hostElement.attributeValue("name");
                String appBase = hostElement.attributeValue("appBase");
                hostMap.put(name, appBase);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载 webapps下 servlet
     */
    private void loadWebappsServlet() throws Exception {

        if (hostMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> stringStringEntry : hostMap.entrySet()) {
            // 当前host下配置信息
            appBase = stringStringEntry.getValue();

            File file = new File(appBase);
            if (file.exists() && file.isDirectory()) {
                // 如果存在同时为文件夹

                // 获取 webapps 下所有 应用
                File[] contextFiles = file.listFiles();
                for (File contextFile : contextFiles) {

                    if (contextFile.isDirectory()) {
                        // 应用下web.xml地址
                        File contextWebFile = new File(contextFile.getAbsolutePath() + "/web.xml");
                        InputStream inputStream = new FileInputStream(contextWebFile);

                        SAXReader saxReader = new SAXReader();

                        Document document = saxReader.read(inputStream);
                        // 获取根节点
                        Element rootElement = document.getRootElement();

                        Map<String, HttpServlet> contextServletMap = new HashMap<>();
                        // 获取所有的servlet节点
                        List<Element> selectNodes = rootElement.selectNodes("//servlet");
                        for (int i = 0; i < selectNodes.size(); i++) {
                            Element element = selectNodes.get(i);

                            // <servlet-name>custom</servlet-name>
                            Element servletNameElement = (Element) element.selectSingleNode("servlet-name");
                            // 获取配置的servlet-name
                            String servletName = servletNameElement.getStringValue();

                            // <servlet-class>server.CustomServlet</servlet-class>
                            Element servletClassElement = (Element) element.selectSingleNode("servlet-class");
                            // 获取配置的servlet-class
                            String servletClass = servletClassElement.getStringValue();

                            // 根据servlet-name的值找到url-pattern
                            Element servletMappingElement = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                            // <url-pattern>/custom</url-pattern>
                            Element urlPatternElement = (Element) servletMappingElement.selectSingleNode("url-pattern");
                            // 获取ur-pattern
                            String urlPattern = urlPatternElement.getStringValue();

                            CustomClassLoader classLoader = new CustomClassLoader("myClassLoader");
                            classLoader.setPath(contextFile.getAbsolutePath() + "\\");

                            contextServletMap.put(urlPattern, (HttpServlet) classLoader.loadClass(servletClass).newInstance());
                        }

                        contextServlet.put("/" + contextFile.getName(), contextServletMap);

                    }
                }
            }
        }
    }
}
