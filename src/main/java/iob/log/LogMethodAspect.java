package iob.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Profile("enableMethodLogging")
public class LogMethodAspect {
	private Log logger = LogFactory.getLog(LogMethodAspect.class);

	@Around("@annotation(iob.log.LogMethod)")
	public Object printMethodNameWithStars(ProceedingJoinPoint jp) throws Throwable {
		// Pre processing
		Object targetObject = jp.getTarget();
		Class<?> targetClass = targetObject.getClass();
		String className = targetClass.getName();

		String methodName = jp.getSignature().getName();

		this.logger.trace(className + "." + methodName + "() - begins");

		// invoke method
		try {
			Object rv = jp.proceed();
			// Success Post processing
			this.logger.info(className + "." + methodName + "() - success");
			return rv;
		} catch (Throwable t) {
			// Failure Post processing
			this.logger.error(className + "." + methodName + "() - failure");

			throw t;
		}

	}

}
