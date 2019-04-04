package controller;

import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
@RestController
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

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public Result register(@RequestBody RegisterVO registerVO) {
		System.out.println(registerVO);
		switch (registerVO.userType) {
			case "student":
				return userService.registerStudent(registerVO.email, registerVO.name, registerVO.password, registerVO.studentId);
			case "teacher":
				return userService.registerTeacher(registerVO.email, registerVO.name, registerVO.password);
			default:
				System.out.println("错误：未知用户类型 " + registerVO.userType);
		}
		return Result.FAILED;
	}

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

	@RequestMapping(value = "/MyClasses", method = RequestMethod.GET)
	public MyClassesVO getMyClasses(@RequestParam(value = "userId") Long userId) {
		List<ClassProfile> myClasses = classService.getMyClassProfiles(userId);
		if (myClasses == null) {
			return new MyClassesVO(Result.FAILED, null);
		} else {
			return new MyClassesVO(Result.SUCCESS, myClasses);
		}
	}

	@RequestMapping(value = "/TakeClasses", method = RequestMethod.GET)
	public TakeClassesVO getTakeClasses(@RequestParam(value = "studentId") Long studentId) {
		return classService.getClassesToTake(studentId);
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
			ret.put("classes", classService.getClassesToMessage(userId));
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

	@RequestMapping(value = "/deleteMessages", method = RequestMethod.POST)
	public Result deleteMessages(@RequestBody List<Long> messageIds) {
		System.out.println(messageIds.toString());
		return messageService.deleteMessages(messageIds);
	}

	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public Result updateUserInfo(@RequestBody UserInfo userInfo) {
		System.out.println("updateUserInfo: " + userInfo.toString());
		return userService.updateUserInfo(userInfo.userId, userInfo.name, userInfo.studentId);
	}

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

	@RequestMapping(value = "/deleteAccount", method = RequestMethod.POST)
	public Result deleteAccount(@RequestBody Long userId) {
		System.out.println("deleteAccount: " + userId);
		return userService.deleteAccount(userId);
	}

	@RequestMapping(value = "/addPost", method = RequestMethod.POST)
	public Map<String, Object> addPost(@RequestBody Map<String, Object> params) {
		System.out.println("addPost: " + params.toString());
		Map<String, Object> ret = new HashMap<>();
		Object o = params.get("courseId");
		Long courseId;
		if (o == null) {
			courseId = classService.getCourseIdByClassId(((Integer) params.get("classId")).longValue());
		} else {
			courseId = ((Integer) o).longValue();
		}
		PostVO post = courseService.addPost(Long.valueOf((String) params.get("userId")), courseId,
				(String) params.get("title"), (String) params.get("text"));
		ret.put("result", (post == null ? Result.FAILED : Result.SUCCESS));
		ret.put("post", post);
		return ret;
	}

	@RequestMapping(value = "/addReply", method = RequestMethod.POST)
	public Map<String, Object> addReply(@RequestBody Map<String, Object> params) {
		System.out.println("addReply: " + params.toString());
		Map<String, Object> ret = new HashMap<>();
		ReplyVO reply = courseService.addReply(Long.valueOf((String) params.get("userId")),
				((Integer) params.get("postId")).longValue(), (String) params.get("text"));
		ret.put("result", (reply == null ? Result.FAILED : Result.SUCCESS));
		ret.put("reply", reply);
		return ret;
	}

	@RequestMapping(value = "/addCourse", method = RequestMethod.POST)
	public Result addCourse(@RequestBody Map<String, Object> params) {
		System.out.println("addCourse: " + params.toString());
		return courseService.createCourse(Long.valueOf((String) params.get("teacherId")), (String) params.get("name"),
				(Integer) params.get("grade"));
	}

	@RequestMapping(value = "/publishClasses", method = RequestMethod.POST)
	public Result publishClasses(@RequestBody Map<String, Object> params) {
		System.out.println("publishClasses: " + params.toString());
		Long courseId = ((Integer) params.get("courseId")).longValue();
		LocalDateTime startTime = ZonedDateTime.parse((String) params.get("startTime")).toLocalDateTime();
		LocalDateTime endTime = ZonedDateTime.parse((String) params.get("endTime")).toLocalDateTime();
		return classService.publishClasses(courseId, startTime, endTime, (Integer) params.get("classNumber"),
				(Integer) params.get("term"), (Integer) params.get("maxNumber"));
	}

	@RequestMapping(value = "/reviewCourse", method = RequestMethod.POST)
	public Result reviewCourse(@RequestBody Map<String, Object> params) {
		System.out.println("reviewCourse: " + params.toString());
		Long courseId = ((Integer) params.get("courseId")).longValue();
		boolean pass = ((boolean) params.get("pass"));
		return courseService.reviewCourse(courseId, pass);
	}

	@RequestMapping(value = "/reviewClass", method = RequestMethod.POST)
	public Result reviewClass(@RequestBody Map<String, Object> params) {
		System.out.println("reviewClass: " + params.toString());
		Long classId = ((Integer) params.get("classId")).longValue();
		boolean pass = ((boolean) params.get("pass"));
		return classService.reviewClass(classId, pass);
	}

	@RequestMapping(value = "/selectClass", method = RequestMethod.POST)
	public Result selectClass(@RequestBody Map<String, Object> params) {
		System.out.println("selectClass: " + params.toString());
		Long classId = ((Integer) params.get("classId")).longValue();
		Long studentId = Long.valueOf((String) params.get("studentId"));
		return classService.takeClass(studentId, classId);
	}

	@RequestMapping(value = "/cancelClassSelection", method = RequestMethod.POST)
	public Result cancelClassSelection(@RequestBody Map<String, Object> params) {
		System.out.println("cancelClassSelection: " + params.toString());
		Long classId = ((Integer) params.get("classId")).longValue();
		Long studentId = Long.valueOf((String) params.get("studentId"));
		return classService.quitClass(studentId, classId);
	}

	@RequestMapping(value = "/review", method = RequestMethod.GET)
	public Map<String, Object> getReview(@RequestParam(value = "adminId") Long adminId) {
		return classService.getReview(adminId);
	}

	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public Map<String, Object> getStatistics(@RequestParam(value = "adminId") Long adminId) {
		return userService.getStatistics(adminId);
	}

	@RequestMapping(value = "/TeacherCourses", method = RequestMethod.GET)
	public Map<String, Object> getTeacherCourses(@RequestParam(value = "teacherId") Long teacherId) {
		Map<String, Object> ret = new HashMap<>();
		List<TeacherCourseVO> teacherCourseVOS = classService.getTeacherCourses(teacherId);
		if (teacherCourseVOS == null) {
			ret.put("result", Result.NOT_EXIST);
		} else {
			ret.put("result", Result.SUCCESS);
			ret.put("teacherCourses", teacherCourseVOS);
		}
		return ret;
	}

	@RequestMapping(value = "/addMessages", method = RequestMethod.POST)
	public Map<String, Object> addMessages(@RequestBody NewMessagesVO newMessagesVO) {
		System.out.println("addMessages: " + newMessagesVO.toString());
		Map<String, Object> ret = new HashMap<>();
		List<String> failedEmails = messageService.sendMessages(newMessagesVO);
		if (failedEmails.isEmpty()) {
			ret.put("result", Result.SUCCESS);
		} else {
			ret.put("failedEmails", failedEmails);
			ret.put("result", Result.NOT_EXIST);
		}
		return ret;
	}

	@RequestMapping(value = "/getClassScores", method = RequestMethod.GET)
	public Map<String, Object> getClassScores(@RequestParam(value = "userId") Long userId, @RequestParam(value = "classId") Long classId) {
		System.out.println("getClassScores: " + userId + "-" + classId);
		Map<String, Object> ret = new HashMap<>();
		List<ClassScore> scores = classService.getClassScores(userId, classId);
		if (scores == null) {
			ret.put("result", Result.FAILED);
		} else {
			ret.put("result", Result.SUCCESS);
			ret.put("scores", scores);
		}
		return ret;
	}

	@RequestMapping(value = "/updateScores", method = RequestMethod.POST)
	public Result updateScores(@RequestBody ClassScorePublishVO vo) {
		System.out.println("updateScores: " + vo.toString());
		return classService.updateClassScores(vo.classId, vo.publishMethod, vo.scores);
	}
}