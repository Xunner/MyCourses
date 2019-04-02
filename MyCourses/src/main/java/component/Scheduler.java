package component;

import dao.ClassDao;
import dao.CourseDao;
import dao.MessageDao;
import enums.ClassState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import po.ClassPO;
import po.CoursePO;
import po.MessagePO;
import po.StudentPO;
import service.UserService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 定时任务类
 * <br>
 * created on 2019/03/31
 *
 * @author 巽
 **/
@Component
public class Scheduler {
	private final UserService userService;
	private final CourseDao courseDao;
	private final ClassDao classDao;
	private final MessageDao messageDao;

	@Autowired
	public Scheduler(UserService userService, CourseDao courseDao, ClassDao classDao, MessageDao messageDao) {
		this.userService = userService;
		this.courseDao = courseDao;
		this.classDao = classDao;
		this.messageDao = messageDao;
	}

	@Scheduled(fixedRate = 60000)   // 60秒执行一次
	public void handleStartingClass() {
		System.out.println(LocalDateTime.now() + " 自动开课定时检测");
		List<ClassPO> classPOs = classDao.findAllByClassStateAndStartTimeBefore(ClassState.NOT_STARTED, LocalDateTime.now());
		for (ClassPO classPO : classPOs) {
			System.out.println("检测到可自动开课班次：" + classPO);
			CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
			Map<StudentPO, Double> studentScores = classPO.getStudentScores();
			Integer maxNumber = classPO.getMaxNumber();
			if (studentScores.size() > maxNumber) { // 选课人数超过上限，随机分配
				List<StudentPO> studentList = new ArrayList<>(studentScores.keySet());
				int number = studentList.size();
				Map<StudentPO, Double> newStudents = new HashMap<>();
				Random rand = new Random();
				while (newStudents.size() < maxNumber) {
					newStudents.put(studentList.get(rand.nextInt(number)), 0d);
				}
				// 给学生发消息
				for (StudentPO studentPO : studentList) {
					if (!newStudents.containsKey(studentPO)) {
						messageDao.save(new MessagePO(null, studentPO.getId(), LocalDateTime.now(),
								"选课结果通知", "很遗憾，由于人数限制，您未能选上课程《" + coursePO.getName() + "》。", true));
					}
				}
				classPO.setStudentScores(newStudents);
			}
			// 给学生发消息
			for (StudentPO studentPO : classPO.getStudentScores().keySet()) {
				messageDao.save(new MessagePO(null, studentPO.getId(), LocalDateTime.now(),
						"选课结果通知", "恭喜，您成功选上了课程《" + coursePO.getName() + "》，该课程现已正式开课，请及时查看。", true));
			}
			// 给教师发消息
			messageDao.save(new MessagePO(null, coursePO.getTeacherId(), LocalDateTime.now(),
					"自动开课通知", "您发布的课程《" + coursePO.getName() + "》已自动开课。", true));
			classPO.setClassState(ClassState.STARTED);  // 开课
			classDao.save(classPO);
		}
	}

	@Scheduled(fixedRate = 60000)   // 60秒执行一次
	public void handlePassedActivation() {
		System.out.println(LocalDateTime.now() + " 自动处理过期注册激活");
		userService.handlePassedActivation();
	}
}
