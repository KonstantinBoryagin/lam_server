package ru.example.lam.server.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import ru.example.lam.server.configuration.annotation.BootTest;
import ru.example.lam.server.dto.journal.JournalRecordDTO;
import ru.example.lam.server.service.journal.implementation.JournalServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

/**
 * Тестовый класс для {@link LoggingAspect}
 */
@BootTest
public class LoggingAspectTest {

    private final static String CLASSES = "classes";

    private final static String PACKAGES = "packages";

    private final static String METHOD_NAME = "getJournalRecordById";

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Autowired
    private LoggingAspect loggingAspect;

    private JournalServiceImpl journalService = new JournalServiceImpl();

    private JournalRecordDTO journalRecordById = new JournalRecordDTO();

    @BeforeEach
    void init() {
        when(proceedingJoinPoint.getTarget()).thenReturn(journalService);
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getDeclaringTypeName()).thenReturn(journalService.getClass().getName());
        when(methodSignature.getName()).thenReturn(METHOD_NAME);
    }

    /**
     * Тест для логирования корректного пакета с *
     */
    @Test
    @DisplayName("test for logging the correct package with *")
    void testLoggingPackageWith() throws Throwable {
        getClassesAndPackages("", "ru.innopolis.*");

        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{journalRecordById.getId()});
        when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object[]{journalRecordById});

        Object logMethod = loggingAspect.logAround(proceedingJoinPoint);
        when(proceedingJoinPoint.proceed(new Object[]{journalRecordById})).thenReturn(logMethod);

        verify(proceedingJoinPoint, times(1)).proceed(new Object[]{journalRecordById.getId()});
        verify(methodSignature, times(1)).getName();
        verify(proceedingJoinPoint, atLeastOnce()).getSignature();
        verify(proceedingJoinPoint, atLeastOnce()).proceed(proceedingJoinPoint.getArgs());
    }

    /**
     * Тест для логирования некорректного пакета с *
     */
    @Test
    @DisplayName("test for logging the incorrect package with *")
    void testFailLoggingPackageWith() throws Throwable {
        getClassesAndPackages("", "ru.example.*");

        Object logMethod = loggingAspect.logAround(proceedingJoinPoint);
        when(proceedingJoinPoint.proceed(new Object[]{journalRecordById})).thenReturn(logMethod);

        verify(methodSignature, times(1)).getName();
        verify(proceedingJoinPoint, atLeastOnce()).getSignature();
        verify(proceedingJoinPoint, never()).proceed(new Object[]{journalRecordById});
    }

    /**
     * Тест для логирования корректного полного пакета
     */
    @Test
    @DisplayName("test for logging the correct full package")
    void testLoggingPackageFull() throws Throwable {
        getClassesAndPackages("", "ru.innopolis.lam.server.service.journal.implementation");

        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{journalRecordById.getId()});
        when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object[]{journalRecordById});

        Object logMethod = loggingAspect.logAround(proceedingJoinPoint);
        when(proceedingJoinPoint.proceed(new Object[]{journalRecordById})).thenReturn(logMethod);

        verify(proceedingJoinPoint, times(1)).proceed(new Object[]{journalRecordById.getId()});
        verify(methodSignature, times(1)).getName();
        verify(proceedingJoinPoint, atLeastOnce()).getSignature();
        verify(proceedingJoinPoint, atLeastOnce()).proceed(proceedingJoinPoint.getArgs());
    }

    /**
     * Тест для логирования некорректного полного пакета
     */
    @Test
    @DisplayName("test for logging the incorrect full package")
    void testFailLoggingPackageFull() throws Throwable {
        getClassesAndPackages("", "ru.innopolis");

        Object logMethod = loggingAspect.logAround(proceedingJoinPoint);
        when(proceedingJoinPoint.proceed(new Object[]{journalRecordById})).thenReturn(logMethod);

        verify(methodSignature, times(1)).getName();
        verify(proceedingJoinPoint, atLeastOnce()).getSignature();
        verify(proceedingJoinPoint, never()).proceed(new Object[]{journalRecordById});
    }

    /**
     * Тест для логирования корректного класса
     */
    @Test
    @DisplayName("test for logging the correct class")
    void testLoggingClass() throws Throwable {
        getClassesAndPackages("JournalServiceImpl", "");

        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{journalRecordById.getId()});
        when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object[]{journalRecordById});

        Object logMethod = loggingAspect.logAround(proceedingJoinPoint);
        when(proceedingJoinPoint.proceed(new Object[]{journalRecordById})).thenReturn(logMethod);

        verify(proceedingJoinPoint, times(1)).proceed(new Object[]{journalRecordById.getId()});
        verify(methodSignature, times(1)).getName();
        verify(proceedingJoinPoint, atLeastOnce()).getSignature();
        verify(proceedingJoinPoint, atLeastOnce()).proceed(proceedingJoinPoint.getArgs());
    }

    /**
     * Тест для логирования некорректного класса
     */
    @Test
    @DisplayName("test for logging the incorrect class")
    void testFailLoggingClass() throws Throwable {
        getClassesAndPackages("ExampleService", "");

        Object logMethod = loggingAspect.logAround(proceedingJoinPoint);
        when(proceedingJoinPoint.proceed(new Object[]{journalRecordById})).thenReturn(logMethod);

        verify(methodSignature, times(1)).getName();
        verify(proceedingJoinPoint, atLeastOnce()).getSignature();
        verify(proceedingJoinPoint, never()).proceed(new Object[]{journalRecordById});
    }

    /**
     * Определение и установка свойств для классов/пакетов
     *
     * @param someClass название класса
     * @param somePackage название пакета
     */
    public void getClassesAndPackages(String someClass, String somePackage) {
        List<String> classList = new ArrayList<>(Arrays.asList(someClass));
        List<String> packageList = new ArrayList<>(Arrays.asList(somePackage));
        ReflectionTestUtils.setField(loggingAspect, CLASSES, classList);
        ReflectionTestUtils.setField(loggingAspect, PACKAGES, packageList);
    }

}