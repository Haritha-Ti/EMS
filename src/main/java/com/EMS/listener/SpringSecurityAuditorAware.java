package com.EMS.listener;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.EMS.model.UserModel;

public class SpringSecurityAuditorAware  implements AuditorAware<Long>{

	@Override
	public Optional<Long> getCurrentAuditor() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		  if (authentication == null || !authentication.isAuthenticated()) {
		   return null;
		  }
          UserModel  user =  (UserModel) authentication.getPrincipal();
          Long userId = new Long(user.getUserId());
		////  return Optional.ofNullable(((UserModel) authentication.getPrincipal()).getUserId()+"");
          return  Optional.ofNullable(userId);
	}

}
