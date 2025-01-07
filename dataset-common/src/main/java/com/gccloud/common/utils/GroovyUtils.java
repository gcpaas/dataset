package com.gccloud.common.utils;

import com.gccloud.common.exception.GlobalException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.transform.TimedInterrupt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.syntax.Types;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * 函数执行
 *
 * @author liuchengbiao
 * @date 2019-07-14 21:15
 */
@Slf4j
public class GroovyUtils {
    /**
     * 缓存编译好的类
     * key: 脚本
     * value: 编译好的class
     */
    static final Cache<String, Class> CACHE_CLASS = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    /**
     * 执行函数
     *
     * @param groovyScript 传入的脚本
     * @param params       传入的参数
     */
    public static Object run(String groovyScript, Map<String, Object> params) {
        Class clazz = buildClass(groovyScript);
        if (clazz == null) {
            return null;
        }
        Binding binding = new Binding();
        // 设置变量
        Map variables = binding.getVariables();
        if (params != null) {
            variables.putAll(params);
        }
        try {
            Script script = InvokerHelper.createScript(clazz, binding);
            Object result = script.run();
            return result;
        } catch (Exception e) {
            // 执行失败，说明脚本有安全问题或其他问题，从缓存中移除
            CACHE_CLASS.invalidate(groovyScript);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("脚本执行失败：" + e.getMessage(), e);
        }
    }

    /**
     * 脚本编译
     *
     * @param groovyScript
     * @return
     */
    public static Class buildClass(String groovyScript) {
        if (StringUtils.isBlank(groovyScript)) {
            return null;
        }
        Class clazz = CACHE_CLASS.get(groovyScript, (script) -> {
            try {
                return buildClassSafe(script);
            } catch (Exception e) {
                log.error("脚本 {} 编译失败:{}", script, e);
            }
            return null;
        });
        return clazz;
    }


    public static Class<?> buildClassSafe(String groovyScript) {
        // 自定义配置
        CompilerConfiguration config = new CompilerConfiguration();
        // 添加线程中断拦截器，可拦截循环体（while）、方法和闭包的首指令
        SecureASTCustomizer secure = new SecureASTCustomizer();
        // 禁止使用闭包
        secure.setClosuresAllowed(true);
        List<Integer> tokensBlacklist = new ArrayList<>();
        // 添加关键字黑名单 while和goto
        tokensBlacklist.add(Types.KEYWORD_WHILE);
        tokensBlacklist.add(Types.KEYWORD_GOTO);
        secure.setTokensBlacklist(tokensBlacklist);
        config.addCompilationCustomizers(secure);
        // statement 黑名单，不能使用while循环块
        List<Class<? extends Statement>> statementBlacklist = new ArrayList<>();
        statementBlacklist.add(WhileStatement.class);
        secure.setStatementsBlacklist(statementBlacklist);
        // 添加线程中断拦截器，可中断超时线程，当前定义超时时间为3s
        Map<String, Object> timeoutArgs = new HashMap<>();
        timeoutArgs.put("value", 3);
        config.addCompilationCustomizers(new ASTTransformationCustomizer(timeoutArgs, TimedInterrupt.class));
        // 沙盒环境
        config.addCompilationCustomizers(new SandboxTransformer());
        // 注册拦截器前，检查是否已经有了对应的拦截器，如果没有，则创建并注册
        List<GroovyInterceptor> interceptors = GroovyInterceptor.getApplicableInterceptors();
        Map<Class<? extends GroovyInterceptor>, Supplier<GroovyInterceptor>> interceptorMap = new HashMap<>();
        interceptorMap.put(GroovyNotSupportInterceptor.class, GroovyNotSupportInterceptor::new);
        // 遍历Map，检查是否已经有了对应的拦截器，如果没有，则创建并注册
        interceptorMap.forEach((clazz, supplier) -> {
            boolean hasInterceptor = interceptors.stream().anyMatch(interceptor -> interceptor.getClass().equals(clazz));
            if (!hasInterceptor) {
                supplier.get().register();
            }
        });
        ClassLoader parent = GroovyUtils.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent,config);
        Class groovyClass = loader.parseClass(groovyScript);
        try {
            loader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return groovyClass;
    }

    /**
     * 方法、类限制拦截器
     */
    static class GroovyNotSupportInterceptor extends GroovyInterceptor {

        /**
         * 默认方法黑名单
         */
        private final List<String> defaultMethodBlacklist = Arrays.asList("getClass", "class", "wait", "notify",
                "notifyAll", "invokeMethod", "finalize", "execute");

        /**
         * 默认类黑名单，主要禁止文件操作
         */
        private final List<Class> defaultClassBlacklist = Arrays.asList(
                java.io.File.class,
                java.io.FileInputStream.class,
                java.io.FileOutputStream.class,
                java.io.FileReader.class,
                java.io.FileWriter.class,
                java.io.RandomAccessFile.class,
                java.io.BufferedReader.class,
                java.io.BufferedWriter.class,
                java.net.URL.class,
                java.nio.file.Files.class
        );

        /**
         * 静态方法拦截
         */
        @Override
        public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args)
                throws Throwable {
            if (receiver == System.class && "exit".equals(method)) {
                // System.exit(0)
                throw new GlobalException("由于安全限制，禁止使用方法: System.exit()");
            }
            if (receiver == Runtime.class) {
                // 通过Java的Runtime.getRuntime().exec()方法执行shell, 操作服务器…
                throw new GlobalException("由于安全限制，禁止使用方法: Runtime.getRuntime().exec()");
            }
            if (receiver == Class.class && "forName".equals(method)) {
                // Class.forName
                throw new GlobalException("由于安全限制，禁止使用方法: Class.forName()");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }

        /**
         * 普通方法拦截
         */
        @Override
        public Object onMethodCall(GroovyInterceptor.Invoker invoker, Object receiver, String method, Object... args)
                throws Throwable {
            if (defaultClassBlacklist.contains(receiver.getClass())) {
                // 类黑名单
                throw new GlobalException("由于安全限制，禁止使用类: " + receiver.getClass().getName());
            }
            if (defaultMethodBlacklist.contains(method)) {
                // 方法列表黑名单
                throw new GlobalException("由于安全限制，禁止使用方法: " + method);
            }
            return super.onMethodCall(invoker, receiver, method, args);
        }

        /**
         * 实例化拦截
         */
        @Override
        public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
            if (defaultClassBlacklist.contains(receiver)) {
                // 类黑名单
                throw new GlobalException("由于安全限制，禁止使用类: " + receiver.getName());
            }
            if (receiver.getName().startsWith("java.nio.file")) {
                throw new GlobalException("由于安全限制，禁止使用类: " + receiver.getName());
            }
            return super.onNewInstance(invoker, receiver, args);
        }
    }



}
