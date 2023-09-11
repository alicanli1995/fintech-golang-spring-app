package com.paylinkfusion.gateway.aspects;


import com.paylinkfusion.gateway.security.services.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class FeignAspects {


    @Pointcut("execution(* com.paylinkfusion.gateway.client.PaymentClient.handlePaymentRequest(..))")
    public void modifyPaymentRequest() {
    }

    @Around("modifyPaymentRequest()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        final var args = joinPoint.getArgs();
        final var paymentRequest = (com.paylinkfusion.gateway.models.dto.request.PaymentRequest) args[0];
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        paymentRequest.setAccountId(principal.getId());
        paymentRequest.setMail(principal.getEmail());
        log.info("Payment request modified: {}", paymentRequest);
        return joinPoint.proceed(args);

    }
}
