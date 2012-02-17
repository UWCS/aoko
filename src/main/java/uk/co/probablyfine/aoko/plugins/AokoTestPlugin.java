package uk.co.probablyfine.aoko.plugins;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AokoTestPlugin {
	
	@Around("execution(public * *(..))")
	public Object test(ProceedingJoinPoint pjp) throws Throwable {
		
		System.out.println("TEST PLUGIN! CALLED METHOD \""+pjp.toShortString()+"\" WITH "+pjp.getArgs().length+" ARGUMENTS");
		return pjp.proceed();
		
	}
}