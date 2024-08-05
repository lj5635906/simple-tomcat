# simple-tomcat

简版 tomcat

**简版 Tomcat 启动流程**

1、解析 server.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<Server>
    <Connector port="8081"></Connector>
    <Engine>
        <Host name="localhost" appBase="D:\workspace\summary\simple-tomcat\summary-simple-tomcat\src\main\webapps">
        </Host>
    </Engine>
</Server>
```

根据 server.xml 解析监听的端口、应用根目录

2、加载 webapps 下所有的应用信息，解析 web.xml ，解析web.xml中所有的 servlet 映射

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<web-app>

    <servlet>
        <servlet-name>demo1</servlet-name>
        <servlet-class>com.summary.servlet.SummaryServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>demo1</servlet-name>
        <url-pattern>/demo1</url-pattern>
    </servlet-mapping>

</web-app>
```

解析web.xml，将所有的servlet信息，通过类加载并实例化到内存中。

```java
// Map<context, Map<String, HttpServlet>>
// <应用根目录,<url,servlet>>
private Map<String, Map<String, HttpServlet>> contextServlet = new HashMap<>();
```



**主要实现思路**

1、通过Socket通信，提供服务，接收请求。

2、通过线程池技术提供并发请求。

3、请求封装为Request对象，解析http请求。

​		解析浏览器的http请求流信息

```
GET /classes/demo?a=1 HTTP/1.1
Host: localhost:8081
Connection: keep-alive
Cache-Control: max-age=0
sec-ch-ua: "Google Chrome";v="129", "Not=A?Brand";v="8", "Chromium";v="129"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "Windows"
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Sec-Fetch-Site: none
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br, zstd
Accept-Language: zh-CN,zh;q=0.9
```

根据http请求流信息解析第一行的： 方法、请求地址、请求参数 信息。

4、封装Request执行器，判定请求为静态资源还是动态请求。

​      静态资源根据请求页面+根目录返回静态页面

​      动态资源根据url获取内存中的HttpServlet，执行server方法。

5、封装Response对象，将资源返回到客户端浏览器。



请求地址

```
http://localhost:8081/demo/index.html
http://localhost:8081/demo/demo1
```

