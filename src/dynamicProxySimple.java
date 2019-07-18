import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class dynamicProxySimple {

    public static void main(String[] args){
        RealSubject rs = new RealSubject();
        InvocationHandler ds = new DynamicSubject(rs);
        //Class cls = rs.getClass();
        Class cls = RealSubject.class;
        Subject subject = (Subject) Proxy.newProxyInstance(cls.getClassLoader(),cls.getInterfaces(),ds);
        subject.request();
    }
}
interface Subject{
    public void  request();
}
class RealSubject implements Subject{
    public RealSubject(){};
    @Override
    public void request() {
        System.out.println("From real subject");
    }
}
class DynamicSubject implements InvocationHandler{
    private Object sub;
    public DynamicSubject(){};
    public DynamicSubject(Object obj){
        this.sub = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
        System.out.println("before calling "+ method);
        method.invoke(sub,args);
        System.out.println("after  calling "+ method);
        return null;
    }
}