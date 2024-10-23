package com.org.plane.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.org.plane.util.KafkaProducer;

@Aspect
@Component
public class ShipLoggingAspect {
	private final KafkaProducer shipKafkaProducer;
	private final Environment environment;

	@Autowired
    public ShipLoggingAspect(@Autowired(required = false) KafkaProducer shipKafkaProducer, Environment environment) {
        this.shipKafkaProducer = shipKafkaProducer;
        this.environment = environment;
    }
	
	@After("execution(* com.org.plane.controller.ShipController.getShipById(..)) && args(id)")
    public void logNegativeId(Long id) {
        if (id < 0) {
            System.out.println("Se ha intentado recuperar una nave con un ID negativo: " + id);
            if (!environment.acceptsProfiles("test")) {
                shipKafkaProducer.sendMessage("ship-topic", "Busqueda de Nave ID Negativo: " + id);
            }
        }
    }
	
	
}
