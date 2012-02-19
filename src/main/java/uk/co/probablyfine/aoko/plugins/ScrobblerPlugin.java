package uk.co.probablyfine.aoko.plugins;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import uk.co.probablyfine.aoko.domain.QueueItem;

@Aspect
public class ScrobblerPlugin {

	@Pointcut("execution(* uk.co.probablyfine.aoko.dao.QueueItemDao.finishedPlaying(..))")
	public void trackFinishedPlaying() {}
	
	@Around("trackFinishedPlaying()")
	public Object logDao(ProceedingJoinPoint pjp) throws Throwable {
		
		QueueItem qi = (QueueItem) pjp.getArgs()[0];
		
		System.out.println("Oh look, I'm scrobbling "+qi.getFile().getMetaData().get("originalname"));;
		
		return pjp.proceed();
	}
	
}
