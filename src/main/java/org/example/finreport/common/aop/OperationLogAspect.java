package org.example.finreport.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.finreport.module.system.entity.SysOperLog;
import org.example.finreport.module.system.service.SysOperLogService;
import org.example.finreport.security.auth.LoginUserHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperLogService sysOperLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(org.example.finreport.common.aop.Log)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = point.getSignature().getName();
        String className = point.getTarget().getClass().getName();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log logAnno = method.getAnnotation(Log.class);

        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(logAnno != null ? logAnno.title() : "");
        operLog.setBusinessType(logAnno != null ? logAnno.businessType() : "OTHER");
        operLog.setMethod(className + "." + methodName);
        operLog.setOperName(LoginUserHolder.get() != null ? LoginUserHolder.get().getUsername() : "anonymous");
        operLog.setOperTime(LocalDateTime.now());

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        operLog.setRequestMethod(request.getMethod());
        operLog.setOperUrl(request.getRequestURI());
        operLog.setOperIp(request.getRemoteAddr());

        try {
            Object result = point.proceed();
            operLog.setStatus("SUCCESS");
            operLog.setCostTime(System.currentTimeMillis() - start);
            sysOperLogService.save(operLog);
            return result;
        } catch (Exception e) {
            operLog.setStatus("FAIL");
            operLog.setErrorMsg(e.getMessage());
            operLog.setCostTime(System.currentTimeMillis() - start);
            sysOperLogService.save(operLog);
            throw e;
        }
    }
}
