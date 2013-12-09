package com.proptiger.data.mvc.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.proptiger.data.internal.dto.Login;
import com.proptiger.data.internal.dto.UserInfo;
import com.proptiger.data.pojo.ProAPIResponse;
import com.proptiger.data.pojo.ProAPISuccessCountResponse;
import com.proptiger.data.pojo.ProAPISuccessResponse;
import com.proptiger.data.service.portfolio.LoginService;

/**
 * Login controller for login and logout functionality
 * @author Rajeev Pandey
 *
 */
@Controller
@RequestMapping(value = "data/v1/entity/user")
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	@ResponseBody
	public ProAPIResponse login(@RequestBody Login login) {
		UserInfo userInfo = loginService.login(login.getEmail(), login.getPassword(), login.isRememberme());
		return new ProAPISuccessResponse(userInfo);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/logout")
	@ResponseBody
	public ProAPIResponse logout() {
		boolean status = loginService.logout();
		return new ProAPISuccessCountResponse(status, 1);
	}
}
