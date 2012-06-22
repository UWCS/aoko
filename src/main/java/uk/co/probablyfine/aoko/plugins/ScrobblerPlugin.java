package uk.co.probablyfine.aoko.plugins;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;

import uk.co.probablyfine.aoko.domain.QueueItem;

@Aspect
public class ScrobblerPlugin {

	@Value("${scrobble.name}")
	String username;
	
	@Value("${scrobble.secret}")
	String secret;
	
	@Value("${scrobble.password}")
	String password;
	
	@Value("${scrobble.apikey}")
	String apiKey;
	
	@Pointcut("execution(* uk.co.probablyfine.aoko.dao.QueueItemDao.finishedPlaying(..))")
	public void trackFinishedPlaying() {}
	
	@Around("trackFinishedPlaying()")
	public Object logDao(ProceedingJoinPoint pjp) throws Throwable {
		
		final QueueItem qi = (QueueItem) pjp.getArgs()[0];
		
		System.out.println("Scrobbling "+qi.toString()+" to "+username);
		
		return pjp.proceed();
	}
	
}
