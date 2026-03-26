package com.hospital.auth.aspect;

import com.hospital.auth.dto.LoginRequestDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AccesoAspect {

    @Around("execution(* com.hospital.auth.controller.AuthController.login(..))")
    public Object monitorearLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        LoginRequestDTO request = (LoginRequestDTO) joinPoint.getArgs()[0];
        String username = request.getUsername();
        LocalDateTime hora = LocalDateTime.now();

        System.out.println("=== [ASPECTO] Intento de login ===");
        System.out.println("Usuario   : " + username);
        System.out.println("Hora      : " + hora);

        ResponseEntity<?> respuesta = (ResponseEntity<?>) joinPoint.proceed();

        if (respuesta.getStatusCode().is2xxSuccessful()) {
            System.out.println("Resultado : EXITOSO");
        } else {
            System.out.println("Resultado : FALLIDO - posible intruso");
            System.out.println("ALERTA    : Credenciales incorrectas para usuario [" + username + "]");
        }
        System.out.println("==================================");

        return respuesta;
    }
}

