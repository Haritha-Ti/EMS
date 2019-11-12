package com.EMS.aop;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.EMS.exception.GeneralException;
import com.EMS.model.RoleApiMapping;
import com.EMS.repository.RoleApiMappingRepository;
import com.EMS.security.jwt.JwtTokenProvider;

@Aspect
@Configuration
public class AopAspect {

	@Autowired
	HttpServletRequest request;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	RoleApiMappingRepository roleApiMappingRepository;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	//authenticate all methods inside com.EMS.controller package && not annotated with @NotAuthenticable
	@Before("execution(* com.EMS.controller.*.*(..))&&!@annotation(com.EMS.utility.NotAuthenticable)")
	public void before(JoinPoint joinPoint) {

		String header = null;
		String token = null;
		Long roleId = null;

		header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer")) {
			token = header.replace("Bearer", "");
			roleId = jwtTokenProvider.getRoleId(token);
			String methodName = joinPoint.getSignature().getName();

			LOGGER.info("roleId :: " + roleId);
			LOGGER.info("Executing method name :: " + methodName);

			if (roleId != null) {
				RoleApiMapping roleApiMappings = roleApiMappingRepository.findByMethodName(methodName);
				//only authenticate api's present role_api_mapping table
				if (roleApiMappings != null) {
					List<String> roleIdList = roleApiMappings.getRoleIds();
					if (!roleIdList.contains(roleId.toString())) {
						throw new GeneralException("roleId " + roleId + " is not authorised to access url method "+methodName, 480);
					}

				}

			}

		}

	}

}
