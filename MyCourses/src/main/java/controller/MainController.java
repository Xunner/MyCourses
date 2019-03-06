package controller;

import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.UserService;
import vo.LogInVO;
import vo.RegisterVO;

/**
 * 主控制器
 * <br>
 * created on 2019/02/01
 *
 * @author 巽
 **/
@Controller
public class MainController {
	private final UserService userService;

	@Autowired
	public MainController(UserService userService) {
		this.userService = userService;
	}

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestBody LogInVO logInVO) {
		System.out.println(logInVO);
		return userService.login(logInVO.email, logInVO.password).name();
	}

	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@RequestBody RegisterVO registerVO) {
		System.out.println(registerVO);
		switch (registerVO.type) {
			case 0:
				return userService.registerStudent(registerVO.email, registerVO.name, registerVO.password, registerVO.studentId).name();
			case 1:
				return userService.registerTeacher(registerVO.email, registerVO.name, registerVO.password).name();
			default:
				System.out.println("错误：未知用户类型 " + registerVO.type);
		}
		return Result.FAILED.name();
	}
}
