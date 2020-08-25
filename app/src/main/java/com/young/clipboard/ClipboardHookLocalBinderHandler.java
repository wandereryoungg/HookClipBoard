package com.young.clipboard;

import android.content.ClipData;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;



public class ClipboardHookLocalBinderHandler implements InvocationHandler {
    private Object localProxyBinder;

    public ClipboardHookLocalBinderHandler(IBinder remoteBinder, Class<?> stubClass) {
        try {
            Method asInterfaceMethod = stubClass.getMethod("asInterface", IBinder.class);
            localProxyBinder = asInterfaceMethod.invoke(null, remoteBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d("LocalBinderHandler", method.getName() + "() is invoked");
        String methodName = method.getName();
        if ("setPrimaryClip".equals(methodName)) {
            //这里对setPrimaryClip()进行了拦截

            if (args[0] instanceof ClipData) {
                ClipData data = (ClipData) args[0];
                String text = data.getItemAt(0).getText().toString();
                text += "   -- this is shared from ClipboardHookService by hook";
                args[0] = ClipData.newPlainText(data.getDescription().getLabel(), text);
            }
        }

        return method.invoke(localProxyBinder, args);
    }
}
