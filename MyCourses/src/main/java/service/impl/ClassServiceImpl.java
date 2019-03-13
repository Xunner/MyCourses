package service.impl;

import dao.*;
import enums.ClassState;
import enums.OperationType;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.*;
import service.ClassService;
import vo.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 班次服务实现类
 * <br>
 * created on 2019/03/09
 *
 * @author 巽
 **/
@Service
@Transactional
public class ClassServiceImpl implements ClassService {
	private final CourseDao courseDao;
	private final ClassDao classDao;
	private final UserDao userDao;
	private final StudentDao studentDao;
	private final TeacherDao teacherDao;
	private final HomeworkDao homeworkDao;
	private final LogDao logDao;
	private final PostDao postDao;
	private final MessageDao messageDao;
	private final Map<Long, ClassPO> classesToReviewed = Collections.synchronizedMap(new HashMap<>());
	private Long id = 0L;
	private final Boolean lock = true;

	@Autowired
	public ClassServiceImpl(CourseDao courseDao, ClassDao classDao, UserDao userDao, StudentDao studentDao, TeacherDao teacherDao, HomeworkDao homeworkDao, LogDao logDao, PostDao postDao, MessageDao messageDao) {
		this.courseDao = courseDao;
		this.classDao = classDao;
		this.userDao = userDao;
		this.studentDao = studentDao;
		this.teacherDao = teacherDao;
		this.homeworkDao = homeworkDao;
		this.logDao = logDao;
		this.postDao = postDao;
		this.messageDao = messageDao;
	}

	@Override
	public List<ClassProfile> getMyClassProfiles(Long userId) {
		StudentPO student = studentDao.findOne(userId);
		if (student == null) {
			return null;
		}
		List<ClassProfile> ret = new ArrayList<>();
		for (ClassPO classPO : student.getClassScores().keySet()) {
			List<HomeworkProfile> homeworkProfiles = new ArrayList<>();
			out:
			for (HomeworkPO homeworkPO : classPO.getHomework()) {
				for (SubmissionPO submissionPO : homeworkPO.getSubmissions()) {
					if (submissionPO.getStudentId().equals(student.getId())) {
						continue out;
					}
				}
				homeworkProfiles.add(new HomeworkProfile(homeworkPO.getName(), homeworkPO.getDeadline()));
			}
			ret.add(new ClassProfile(courseDao.findOne(classPO.getCourseId()).getName(), homeworkProfiles));
		}
		return ret;
	}

	@Override
	public Result publishClasses(Long courseId, LocalDateTime startTime, LocalDateTime endTime, Integer classNumber, Integer term, Integer maxNumber) {
		for (int classOrder = 1; classOrder <= classNumber; classOrder++) {
			synchronized (lock) {
				classesToReviewed.put(++id, new ClassPO(courseId, startTime, endTime, classOrder, term, ClassState.NOT_STARTED, maxNumber));
			}
		}
		return Result.SUCCESS;
	}

	@Override
	public Result reviewClass(Long classId, boolean pass) {
		Iterator<Map.Entry<Long, ClassPO>> entries = classesToReviewed.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Long, ClassPO> entry = entries.next();
			if (classId.equals(entry.getKey())) {
				if (pass) {
					classDao.save(entry.getValue());
				}
				entries.remove();
				System.out.println("完成开班在线审核：" + classId);
				return Result.SUCCESS;
			}
		}
		return Result.NOT_EXIST;
	}

	@Override
	public Result takeClass(Long studentId, Long classId) {
		StudentPO studentPO = studentDao.findOne(studentId);
		boolean taken = false;
		for (ClassPO classPO : studentPO.getClassScores().keySet()) {
			if (classPO.getId().equals(classId)) {
				taken = true;
				break;
			}
		}
		if (taken) {
			return Result.EXIST;
		}
		ClassPO classPO = classDao.findOne(classId);
		if (classPO.getClassState() == ClassState.NOT_STARTED || classPO.getStudentScores().size() < classPO.getMaxNumber()) {
			studentPO.getClassScores().put(classPO, 0d);
			return Result.SUCCESS;
		}
		return Result.FAILED;
	}

	@Override
	public Result quitClass(Long studentId, Long classId) {
		StudentPO studentPO = studentDao.findOne(studentId);
		studentPO.getClassScores().entrySet().removeIf(entry -> entry.getKey().getId().equals(classId));
		studentDao.save(studentPO);
		logDao.save(new LogPO(LocalDateTime.now(), studentId, classId, ClassPO.class.getName(), OperationType.DELETE));
		return Result.SUCCESS;
	}

	@Override
	public Result publishHomework(Long classId, String name, String description, LocalDateTime deadline, Integer sizeLimit, String typeRestriction) {
		HomeworkPO homeworkPO = new HomeworkPO(classId, name, description, deadline, sizeLimit, typeRestriction);
		homeworkDao.save(homeworkPO);
		return Result.SUCCESS;
	}

	@Override
	public ClassInfo getClassInfo(Long userId, Long classId) {
		ClassPO classPO = classDao.findOne(classId);
		if (classPO == null) {
			return null;
		}
		// 装载ClassInfo
		CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
		TeacherPO teacherPO = teacherDao.findOne(coursePO.getTeacherId());
		ClassInfo ret = new ClassInfo(classPO.getId(), coursePO.getName(), teacherPO.getName(), coursePO.getGrade(),
				classPO.getTerm(), classPO.getClassOrder(), classPO.getStartTime(), classPO.getEndTime());
		// 装载homework
		List<HomeworkVO> homework = new ArrayList<>();
		for (HomeworkPO homeworkPO : classPO.getHomework()) {
			boolean submitted = false;
			for (SubmissionPO submissionPO : homeworkPO.getSubmissions()) {
				if (submissionPO.getStudentId().equals(userId)) {
					submitted = true;
					break;
				}
			}
			homework.add(new HomeworkVO(homeworkPO.getId(), homeworkPO.getName(), homeworkPO.getDescription(),
					homeworkPO.getDeadline(), submitted, homeworkPO.getSizeLimit(), homeworkPO.getTypeRestriction()));
		}
		ret.homework = homework;
		// 装载coursewares
		List<CoursewareVO> coursewares = new ArrayList<>();
		for (CoursewarePO coursewarePO : coursePO.getCoursewares()) {
			coursewares.add(new CoursewareVO(coursewarePO.getId(), coursewarePO.getName()));
		}
		ret.coursewares = coursewares;
		// 装载posts
		List<PostVO> posts = new ArrayList<>();
		for (PostPO postPO : coursePO.getPosts()) {
			PostVO postVO = new PostVO(postPO.getId(), userDao.findOne(postPO.getUserId()).getName(),
					postPO.getTitle(), postPO.getText(), postPO.getTime());
			List<ReplyVO> replies = new ArrayList<>();
			for (ReplyPO replyPO : postPO.getReplies()) {
				replies.add(new ReplyVO(replyPO.getId(), userDao.findOne(replyPO.getUserId()).getName(),
						replyPO.getText(), replyPO.getTime()));
			}
			postVO.replies = replies;
			posts.add(postVO);
		}
		ret.posts = posts;
		return ret;
	}

	@Override
	public List<ClassStatisticVO> getClassStatistics(Long studentId) {
		List<ClassStatisticVO> ret = new ArrayList<>();
		StudentPO studentPO = studentDao.findOne(studentId);
		// 装载选上的课
		studentPO.getClassScores().forEach((classPO, score) -> {
			CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
			ret.add(new ClassStatisticVO(coursePO.getGrade(), classPO.getTerm(), coursePO.getName(),
					teacherDao.findOne(coursePO.getTeacherId()).getName(), score, false));
		});
		// 装载退掉的课
		List<LogPO> logs = logDao.findAllByUserIdAndObjectClassAndOperationType(
				studentId, "ClassPO", OperationType.DELETE);
		for (LogPO logPO : logs) {
			ClassPO classPO = classDao.findOne(logPO.getObjectId());
			CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
			ret.add(new ClassStatisticVO(coursePO.getGrade(), classPO.getTerm(), coursePO.getName(),
					teacherDao.findOne(coursePO.getTeacherId()).getName(), 0d, true));
		}
		return ret;
	}

	@Override
	public PostVO addPost(Long userId, String title, String text) {
		PostPO postPO = new PostPO(userId, title, text, LocalDateTime.now());
		postDao.save(postPO);
		if (postPO.getId() == null) {
			return null;
		}
		return new PostVO(postPO.getId(), userDao.findOne(userId).getName(), title, text, postPO.getTime());
	}

	@Override
	public TakeClassesVO getClassesToTake(Long studentId) {
		StudentPO studentPO = studentDao.findOne(studentId);
		List<ClassPO> classPOS = classDao.findAllByClassState(ClassState.NOT_STARTED);
		Set<ClassPO> selected = studentPO.getClassScores().keySet();
		List<ClassToTakeVO> unselectedClass = new ArrayList<>();
		List<ClassToTakeVO> selectedClass = new ArrayList<>();
		for (ClassPO classPO : classPOS) {  // 遍历所有未开的课
			CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
			if (selected.contains(classPO)) {   // 已经申请选课
				selectedClass.add(new ClassToTakeVO(classPO.getId(), coursePO.getName(),
						teacherDao.findOne(coursePO.getTeacherId()).getName(), coursePO.getGrade(), classPO.getTerm(),
						classPO.getClassOrder(), classPO.getStartTime(), classPO.getEndTime(),
						new ClassNumberVO(classPO.getStudentScores().size(), classPO.getMaxNumber())));
			} else {    // 尚未申请选课
				unselectedClass.add(new ClassToTakeVO(classPO.getId(), coursePO.getName(),
						teacherDao.findOne(coursePO.getTeacherId()).getName(), coursePO.getGrade(), classPO.getTerm(),
						classPO.getClassOrder(), classPO.getStartTime(), classPO.getEndTime(),
						new ClassNumberVO(classPO.getStudentScores().size(), classPO.getMaxNumber())));
			}
		}
		return new TakeClassesVO(unselectedClass, selectedClass);
	}

	@Scheduled(fixedRate = 60000)   // 60秒执行一次
	public void handleStartingClass() {
		System.out.println("自动开课定时检测");
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
				while (newStudents.size() < maxNumber) {
					Random rand = new Random();
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
}
