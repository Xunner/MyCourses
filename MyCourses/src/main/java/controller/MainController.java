package controller;

import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import po.ClassPO;
import po.MessagePO;
import po.StudentPO;
import po.UserPO;
import service.ClassService;
import service.CourseService;
import service.MessageService;
import service.UserService;
import vo.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private final CourseService courseService;
	private final ClassService classService;
	private final MessageService messageService;

	@Autowired
	public MainController(UserService userService, CourseService courseService, ClassService classService, MessageService messageService) {
		this.userService = userService;
		this.courseService = courseService;
		this.classService = classService;
		this.messageService = messageService;
	}

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Map<String, Object> login(@RequestBody LogInVO logInVO) {
		System.out.println(logInVO);
		Map<String, Object> ret = new HashMap<>();
		UserPO user = userService.login(logInVO.email, logInVO.password);
		if (user == null) {
			ret.put("result", Result.NOT_EXIST);
		} else {
			ret.put("result", Result.SUCCESS);
			ret.put("userId", user.getId());
			ret.put("userType", user.getClass().getSimpleName().replace("PO", ""));
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

	@ResponseBody
	@RequestMapping(value = "/MyClasses", method = RequestMethod.GET)
	public MyClassesVO getMyClasses(@RequestParam(value = "userId") Long userId) {
		List<ClassProfile> myClasses = classService.getMyClassProfiles(userId);
		if (myClasses == null) {
			return new MyClassesVO(Result.FAILED, null);
		} else {
			return new MyClassesVO(Result.SUCCESS, myClasses);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/TakeClasses", method = RequestMethod.GET)
	public List<CourseProfile> getTakeClasses(@RequestParam(value = "userId") Long userId) {
		return courseService.getCourseProfilesToTake(userId);
	}

	@ResponseBody
	@RequestMapping(value = "/MyInformation", method = RequestMethod.GET)
	public MyInformationVO getMyInformation(@RequestParam(value = "userId") Long userId, @RequestParam(value = "userType") String userType) {
		System.out.println("MyInformation: " + userId + ", " + userType);
		// 准备userInfo
		UserPO user = userService.findById(userId);
		List<Pair> userInfo = new ArrayList<>();
		userInfo.add(new Pair("姓名", user.getName()));
		userInfo.add(new Pair("邮箱", user.getEmail()));
		if (user instanceof StudentPO) {
			userInfo.add(new Pair("身份", ((StudentPO) user).getStudentType().getValue()));
			userInfo.add(new Pair("学号", ((StudentPO) user).getStudentId()));
		} else {
			userInfo.add(new Pair("身份", ((StudentPO) user).getStudentType().getValue()));
		}
		// 准备classesTaken TODO
		List<TreeNode> classesTaken = new ArrayList<>();
		// 准备classesQuit
		List<TreeNode> classesQuit = new ArrayList<>();
		// 准备scores
		List<TreeNode> scores = new ArrayList<>();
		// 准备messages
		List<MessagePO> messagePOs = messageService.getAllMessagesByReceiverId(userId);
		List<MessageVO> messages = new ArrayList<>();
		for (MessagePO message : messagePOs) {
			messages.add(new MessageVO(message.getId(), message.getTitle(), message.getSenderId(), message.getTime(), message.getMessage()));
		}
		return new MyInformationVO(Result.SUCCESS, userInfo, classesTaken, classesQuit, scores, messages);
	}

	@ResponseBody
	@RequestMapping(value = "/deleteMessages", method = RequestMethod.POST)
	public Result deleteMessages(@RequestBody List<Long> userIds) {
		System.out.println(userIds.toString());
		return messageService.deleteMessages(userIds);
	}

	@ResponseBody
	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public Result updateUserInfo(@RequestBody UserInfo userInfo) {
		System.out.println("updateUserInfo: " + userInfo.toString());
		return userService.updateUserInfo(userInfo.userId, userInfo.name, userInfo.studentId);
	}

	@ResponseBody
	@RequestMapping(value = "/class", method = RequestMethod.GET)
	public Map<String, Object> getClass(@RequestParam(value = "userId") Long userId, @RequestParam(value = "classId") Long classId) {
		Map<String, Object> ret = new HashMap<>();
		ClassInfo classInfo = classService.getClassInfo(userId, classId);
		ret.put("classInfo", classInfo);
		if (classInfo == null) {
			ret.put("result", Result.NOT_EXIST);
		} else {
			ret.put("result", Result.SUCCESS);
		}
		return ret;
	}
}