package controller;

import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import po.*;
import service.ClassService;
import service.CourseService;
import service.MessageService;
import service.UserService;
import vo.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
	public TakeClassesVO getTakeClasses(@RequestParam(value = "studentId") Long studentId) {
		return classService.getClassesToTake(studentId);
	}

	@ResponseBody
	@RequestMapping(value = "/information", method = RequestMethod.GET)
	public Map<String, Object> getInformation(@RequestParam(value = "userId") Long userId) {
		System.out.println("information: " + userId);
		Map<String, Object> ret = new HashMap<>();
		// 装载userInfo
		UserPO user = userService.findById(userId);
		List<Pair> userInfo = new ArrayList<>();
		userInfo.add(new Pair("姓名", user.getName()));
		userInfo.add(new Pair("邮箱", user.getEmail()));
		if (user instanceof StudentPO) {
			userInfo.add(new Pair("身份", ((StudentPO) user).getStudentType().getValue()));
			userInfo.add(new Pair("学号", ((StudentPO) user).getStudentId()));
			// 装载classesStatistic
			ret.put("classesStatistic", classService.getClassStatistics(userId));
		} else if (user instanceof TeacherPO) {
			userInfo.add(new Pair("身份", "教师"));
			// 装载 TODO

		} else {
			System.out.println("未知类别：" + user.getClass());
			userInfo.add(new Pair("身份", "未知"));
		}
		ret.put("userInfo", userInfo);
		// 装载messages
		List<MessagePO> messagePOs = messageService.getAllMessagesByReceiverId(userId);
		List<MessageVO> messages = new ArrayList<>();
		for (MessagePO message : messagePOs) {
			String sender = "系统";
			if (message.getSenderId() != null) {
				sender = userService.findById(message.getSenderId()).getName();
			}
			messages.add(new MessageVO(message.getId(), message.getTitle(), sender, message.getTime(), message.getMessage()));
		}
		ret.put("messages", messages);
		ret.put("result", Result.SUCCESS);
		return ret;
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

	@ResponseBody
	@RequestMapping(value = "/deleteAccount", method = RequestMethod.POST)
	public Result deleteAccount(@RequestBody Long userId) {
		System.out.println("deleteAccount: " + userId);
		return userService.deleteAccount(userId);
	}

	@ResponseBody
	@RequestMapping(value = "/addPost", method = RequestMethod.POST)
	public Map<String, Object> addPost(@RequestBody Map<String, Object> params) {
		System.out.println("addPost: " + params.toString());
		Map<String, Object> ret = new HashMap<>();
		PostVO post = classService.addPost(((Integer) params.get("userId")).longValue(), (String) params.get("title"), (String) params.get("text"));
		ret.put("result", (post == null ? Result.FAILED : Result.SUCCESS));
		ret.put("post", post);
		return ret;
	}

	@ResponseBody
	@RequestMapping(value = "/uploadCourseware", method = RequestMethod.POST)
	public Map<String, Object> uploadCourseware(@RequestBody Map<String, Object> params) {
		System.out.println("uploadCourseware: " + params.toString());
		Map<String, Object> ret = new HashMap<>();
//		PostVO post = classService.addPost((Long) params.get("userId"), (String) params.get("title"), (String) params.get("text"));
//		ret.put("result", (post == null ? Result.FAILED : Result.SUCCESS));
//		ret.put("post", post);
		// TODO
		return ret;
	}

	@ResponseBody
	@RequestMapping(value = "/addCourse", method = RequestMethod.POST)
	public Map<String, Object> addCourse(@RequestBody Map<String, Object> params) {
		System.out.println("addCourse: " + params.toString());
		Map<String, Object> ret = new HashMap<>();
		Result result = courseService.createCourse(((Integer) params.get("teacherId")).longValue(),
				(String) params.get("name"),(Integer) params.get("grade"));
		ret.put("result", result);
		return ret;
	}

	@ResponseBody
	@RequestMapping(value = "/publishClasses", method = RequestMethod.POST)
	public Map<String, Object> publishClasses(@RequestBody Map<String, Object> params) {
		System.out.println("publishClasses: " + params.toString());
		Map<String, Object> ret = new HashMap<>();
		Long courseId = ((Integer) params.get("courseId")).longValue();
		LocalDateTime startTime = ZonedDateTime.parse((String) params.get("startTime")).toLocalDateTime();
		LocalDateTime endTime = ZonedDateTime.parse((String) params.get("endTime")).toLocalDateTime();
		Result result = classService.publishClasses(courseId, startTime, endTime, (Integer) params.get("classNumber"),
				(Integer) params.get("term"), (Integer) params.get("maxNumber"));
		ret.put("result", result);
		return ret;
	}

	@ResponseBody
	@RequestMapping(value = "/reviewCourse", method = RequestMethod.POST)
	public Result reviewCourse(@RequestBody Map<String, Object> params) {
		System.out.println("reviewCourse: " + params.toString());
		Long courseId = ((Integer) params.get("courseId")).longValue();
		boolean pass = ((boolean) params.get("pass"));
		return courseService.reviewCourse(courseId, pass);
	}

	@ResponseBody
	@RequestMapping(value = "/reviewClass", method = RequestMethod.POST)
	public Result reviewClass(@RequestBody Map<String, Object> params) {
		System.out.println("reviewClass: " + params.toString());
		Long classId = ((Integer) params.get("classId")).longValue();
		boolean pass = ((boolean) params.get("pass"));
		return classService.reviewClass(classId, pass);
	}

	@ResponseBody
	@RequestMapping(value = "/selectClass", method = RequestMethod.POST)
	public Result selectClass(@RequestBody Map<String, Object> params) {
		System.out.println("selectClass: " + params.toString());
		Long classId = ((Integer) params.get("classId")).longValue();
		Long studentId = ((Integer) params.get("userId")).longValue();
		return classService.takeClass(studentId, classId);
	}

	@ResponseBody
	@RequestMapping(value = "/cancelClassSelection", method = RequestMethod.POST)
	public Result cancelClassSelection(@RequestBody Map<String, Object> params) {
		System.out.println("cancelClassSelection: " + params.toString());
		Long classId = ((Integer) params.get("classId")).longValue();
		Long studentId = ((Integer) params.get("userId")).longValue();
		return classService.quitClass(studentId, classId);
	}

	@ResponseBody
	@RequestMapping(value = "/review", method = RequestMethod.GET)
	public Map<String, Object> getReview(@RequestParam(value = "adminId") Long adminId) {
		return classService.getReview(adminId);
	}

	@ResponseBody
	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public Map<String, Object> getStatistics(@RequestParam(value = "adminId") Long adminId) {
		// TODO
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/TeacherCourses", method = RequestMethod.GET)
	public Map<String, Object> getTeacherCourses(@RequestParam(value = "teacherId") Long teacherId) {
		Map<String, Object> ret = new HashMap<>();
		List<TeacherCourseVO> teacherCourseVOS = classService.getTeacherCourses(teacherId);
		ret.put("teacherCourses", teacherCourseVOS);
		if (teacherCourseVOS == null) {
			ret.put("result", Result.NOT_EXIST);
		} else {
			ret.put("result", Result.SUCCESS);
		}
		return ret;
	}

	// TODO /downloadCourseware
	// TODO /downloadSubmission
}