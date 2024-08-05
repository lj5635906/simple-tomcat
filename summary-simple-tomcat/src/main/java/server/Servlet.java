package server;

/**
 * Servlet 顶层接口
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public interface Servlet {

    void init() throws Exception;

    void destory() throws Exception;

    void service(Request request, Response response) throws Exception;

}
