package controller;

import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import po.UserPO;
import service.UserService;
import vo.LogInVO;
import vo.RegisterVO;

import java.util.HashMap;
import java.util.Map;

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
	public Object login(@RequestBody LogInVO logInVO) {
		System.out.println(logInVO);
		Map<String, Object> ret = new HashMap<>();
		UserPO user = userService.login(logInVO.email, logInVO.password);
		if (user == null) {
			ret.put("result", Result.NOT_EXIST);
		} else {
			ret.put("result", Result.SUCCESS);
			ret.put("userId", user.getId());
			ret.put("userType", user.getClass());
		}
		return ret;
	}

	@ResponseBody
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@RequestBody RegisterVO registerVO) {
		System.out.println(registerVO);
		switch (registerVO.userType) {
			case "student":
				return userService.registerStudent(registerVO.email, registerVO.name, registerVO.password, registerVO.studentId).name();
			case "teacher":
				return userService.registerTeacher(registerVO.email, registerVO.name, registerVO.password).name();
			default:
				System.out.println("错误：未知用户类型 " + registerVO.userType);
		}
		return Result.FAILED.name();
	}

	@ResponseBody
	@RequestMapping(value = "/activate/{code}", method = RequestMethod.GET)
	public String activate(@PathVariable("code") String code) {
		String result;
		switch (userService.activateAccount(code)) {
			case SUCCESS:
				result = "Activate account successfully!";
				break;
			case NOT_EXIST:
				result = "Activation failed and the link has expired.";
				break;
			default:
				result = "Something is wrong...";
				System.out.println("未知错误：" + code);
		}
		return "<html><head></head><body><h1>" + result + "<br>Click on the link below to jump to our website:" +
				"</h1><h3><a href='http://localhost:8080/MyCourses/'>http://localhost:8080/MyCourses/</a></h3></body></html>";
	}


}
