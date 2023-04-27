package ru.example.lam.server.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * LoggingAspect {@link Loggable} настройка логирования входящих/исходящих данных
 */
@Log4j2
@Aspect
@Component
public class LoggingAspect {

    @Value("${log.classes.names}")
    private List<String> classes;

    @Value("${log.packages.names}")
    private List<String> packages;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Точка присоединения
     */
    @Pointcut("@annotation(ru.example.lam.server.aspect.Loggable)")
    public void logMethods() {}

    /**
     * Логирование конкретного класса\пакета до и после точки соединения (pointcut)
     *
     * @param pjp дополнительный метод proceed()
     */
    @Around("logMethods()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        Object returnObject = pjp.proceed(pjp.getArgs());
        objectMapper.findAndRegisterModules();
        String methodName = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
        if (contains(pjp)) {
            StringWriter incomingWriter = new StringWriter();
            objectMapper.writeValue(incomingWriter, pjp.getArgs());
            log.info("Incoming data {} from {}", incomingWriter, methodName);
            StringWriter outgoingWriter = new StringWriter();
            objectMapper.writeValue(outgoingWriter, returnObject);
            log.info("Outgoing data {} from {}", outgoingWriter, methodName);
        }
        return returnObject;
    }

    /**
     * Проверка на включение конкретного класса\пакета функции логирования
     *
     * @param pjp дополнительный метод proceed()
     */
    private boolean contains(ProceedingJoinPoint pjp) {
        String[] partsName = (pjp.getSignature().getDeclaringTypeName()).split("\\.");
        String packageName = StringUtils.join((Arrays.copyOf(partsName, partsName.length - 1)), ".");
        String className = partsName[partsName.length - 1];
        return (packages.stream().filter(current -> current.contains("*")).map(current -> current.substring(0, current.indexOf('*'))).anyMatch(packageName::startsWith) || packages.stream().anyMatch(packageName::equals)
                || classes.stream().anyMatch(className::equals));
    }

}