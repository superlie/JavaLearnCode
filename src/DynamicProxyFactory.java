import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyFactory {

    public static void main(String[] args){
        SomeClass obj = (SomeClass)SomeClassFactory.getDynamicSomeClassProxy();
        obj.SomeMethod();
        obj.SomeOtherMethod("hello world!");


        InvocationHandler handler = Proxy.getInvocationHandler(obj);
        if(handler instanceof MethodCountingHandler){
            System.out.println(((MethodCountingHandler)handler).getInvocationCount());
        }
    }
}
interface SomeClass{
    public void SomeMethod();
    public void SomeOtherMethod(final String text);
}
class SomeClassImpl implements SomeClass{
    private String name;
    public SomeClassImpl(final String name){
        this.name = name;
    }

    @Override
    public void SomeMethod() {
        System.out.println(this.name);
    }

    @Override
    public void SomeOtherMethod(String text) {
        System.out.println(text);
    }
}

//
class SomeClassProxy implements SomeClass{
    private final  SomeClassImpl impl;
    public SomeClassProxy(SomeClassImpl impl){
        this.impl = impl;
    }

    @Override
    public void SomeMethod() {
        this.impl.SomeMethod();
    }

    @Override
    public void SomeOtherMethod(String text) {
        this.impl.SomeOtherMethod(text);
    }
}

class SomeClassCountingProxy implements SomeClass{
    private final SomeClassImpl impl;
    private int invocationCount  =0;
    public SomeClassCountingProxy(SomeClassImpl impl){
        this.impl = impl;
    }

    @Override
    public void SomeMethod() {
        this.invocationCount++;
        this.impl.SomeMethod();
    }

    @Override
    public void SomeOtherMethod(String text) {
        this.invocationCount++;
        this.impl.SomeOtherMethod(text);
    }
     public int getInvocationCount(){
        return invocationCount;
     }
     public void setInvocationCount(int invocationCount){
        this.invocationCount = invocationCount;
     }
}

class  SomeClassFactory{
    public static final SomeClass getDynamicSomeClassProxy(){
        SomeClassImpl impl  = new SomeClassImpl(System.getProperty("user.name"));
        ClassLoader loader = SomeClassImpl.class.getClassLoader();
        Class[] interfaces = new Class[]{SomeClass.class};
        InvocationHandler handler = new MethodCountingHandler(impl);
        SomeClass proxy = (SomeClass) Proxy.newProxyInstance(loader,interfaces,handler);
        return  proxy;

    }
}

class MethodCountingHandler implements InvocationHandler{
    private final Object impl;
    private int invocationCount = 0;
    public MethodCountingHandler(final Object impl){
        this.impl = impl;
    }
    public int getInvocationCount(){
        return  this.invocationCount;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws  Throwable{
        try{
            this.invocationCount++;
            Object result = method.invoke(impl,args);
            return  result;
        }catch (final InvocationTargetException e){
            throw e.getTargetException();
        }

    }
}