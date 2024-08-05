package server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * @author jie.luo
 * @since 2024/8/5
 */
public class CustomClassLoader extends ClassLoader{
    private String path = "d:\\";

    private final String fileType = ".class";

    // 类加载器名字
    private String name = null;

    public CustomClassLoader(String name) {
        super();
        this.name = name;
    }

    public CustomClassLoader(ClassLoader parent, String name) {
        super(parent);
        this.name = name;
    }

    // 调用getClassLoader()时返回此方法，如果不重载，则显示MyClassLoader的引用地址
    @Override
    public String toString() {
        return this.name;
    }

    // 设置文件加载路径
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    protected Class findClass(String name) throws ClassNotFoundException {
        byte[] data = loadClassData(name);
        // 参数off代表什么？
        return defineClass(name, data, 0, data.length);
    }

    // 将.class文件读入内存中，并且以字节数形式返回
    private byte[] loadClassData(String name) throws ClassNotFoundException {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        byte[] data = null;
        try {
            // 读取文件内容
            name = name.replaceAll("\\.", "\\\\");
            System.out.println("加载文件名：" + name);
            // 将文件读取到数据流中
            fis = new FileInputStream(path + name + fileType);
            baos = new ByteArrayOutputStream();
            int ch = 0;
            while ((ch = fis.read()) != -1) {
                baos.write(ch);
            }
            data = baos.toByteArray();
        } catch (Exception e) {
            throw new ClassNotFoundException("Class is not found:" + name, e);
        } finally {
            // 关闭数据流
            try {
                fis.close();
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
