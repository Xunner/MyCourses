package service.impl;

import dao.*;
import enums.ClassState;
import enums.OperationType;
import enums.PublishMethod;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.*;
import service.ClassService;
import service.CourseService;
import service.UserService;
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
	private final UserService userService;
	private final CourseService courseService;
	private final CourseDao courseDao;
	private final ClassDao classDao;
	private final UserDao userDao;
	private final StudentDao studentDao;
	private final TeacherDao teacherDao;
	private final HomeworkDao homeworkDao;
	private final LogDao logDao;
	private final PostDao postDao;
	private final MessageDao messageDao;
	private final CoursewareDao coursewareDao;
	private final ReplyDao replyDao;
	private final Map<Long, ClassPO> classesToReviewed = Collections.synchronizedMap(new HashMap<>());
	private Long id = 0L;
	private final Boolean lock = true;

	@Autowired
	public ClassServiceImpl(UserService userService, CourseService courseService, CourseDao courseDao, ClassDao classDao, UserDao userDao, StudentDao studentDao, TeacherDao teacherDao, HomeworkDao homeworkDao, LogDao logDao, PostDao postDao, MessageDao messageDao, CoursewareDao coursewareDao, ReplyDao replyDao) {
		this.userService = userService;
		this.courseService = courseService;
		this.courseDao = courseDao;
		this.classDao = classDao;
		this.userDao = userDao;
		this.studentDao = studentDao;
		this.teacherDao = teacherDao;
		this.homeworkDao = homeworkDao;
		this.logDao = logDao;
		this.postDao = postDao;
		this.messageDao = messageDao;
		this.coursewareDao = coursewareDao;
		this.replyDao = replyDao;
	}

	@Override
	public List<ClassProfile> getMyClassProfiles(Long userId) {
		StudentPO student = studentDao.findOne(userId);
		if (student == null) {
			return null;
		}
		List<ClassProfile> ret = new ArrayList<>();
		for (ClassPO classPO : student.getClassScores().keySet()) {
			if (classPO.getClassState() == ClassState.NOT_STARTED) continue;
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
			ret.add(new ClassProfile(classPO.getId(), courseDao.findOne(classPO.getCourseId()).getName(), homeworkProfiles));
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
					ClassPO classPO = classDao.save(entry.getValue());
					CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
					messageDao.save(new MessagePO(null, coursePO.getTeacherId(), LocalDateTime.now(),
							"发布课程审核通过", "您发布的课程《" + coursePO.getName() + "》已通过管理员审核，到开课时间后会自动开课。", true));
				} else {
					CoursePO coursePO = courseDao.findOne(entry.getValue().getCourseId());
					messageDao.save(new MessagePO(null, coursePO.getTeacherId(), LocalDateTime.now(),
							"发布课程审核未通过", "您发布的课程《" + coursePO.getName() + "》未能通过管理员审核，请与管理员联系了解详情。", true));
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
			studentDao.save(studentPO);
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
	public ClassInfo getClassInfo(Long userId, Long classId) {
		ClassPO classPO = classDao.findOne(classId);
		if (classPO == null) {
			return null;
		}
		// 装载ClassInfo
		CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
		TeacherPO teacherPO = teacherDao.findOne(coursePO.getTeacherId());
		ClassInfo ret = new ClassInfo(classPO.getId(), coursePO.getName(), teacherPO.getName(), coursePO.getGrade(),
				classPO.getTerm(), classPO.getClassOrder(), classPO.getStartTime(), classPO.getEndTime(),
				classPO.getStudentScores().size(), classPO.getPublishMethod());
		// 装载homework
		List<HomeworkVO> homework = new ArrayList<>();
		for (HomeworkPO homeworkPO : classPO.getHomework()) {
			Long submissionId = 0L;
			for (SubmissionPO submissionPO : homeworkPO.getSubmissions()) {
				if (submissionPO.getStudentId().equals(userId)) {
					submissionId = submissionPO.getId();
					break;
				}
			}
			List<String> typeRestriction = new ArrayList<>(Arrays.asList(homeworkPO.getTypeRestriction().split("/")));
			homework.add(new HomeworkVO(homeworkPO.getId(), classId, homeworkPO.getName(), homeworkPO.getDescription(),
					homeworkPO.getDeadline(), submissionId, homeworkPO.getSizeLimit(), typeRestriction,
					homeworkPO.getSubmissions().size(), homeworkPO.getPublishMethod()));
		}
		Collections.sort(homework);
		ret.homework = homework;
		// 装载coursewares
		List<CoursewareVO> coursewares = new ArrayList<>();
		for (CoursewarePO coursewarePO : coursePO.getCoursewares()) {
			coursewares.add(new CoursewareVO(coursewarePO.getId(), coursewarePO.getName()));
		}
		Collections.sort(coursewares);
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
			Collections.sort(replies);
			postVO.replies = replies;
			posts.add(postVO);
		}
		Collections.sort(posts);
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
	public List<ClassToMessageVO> getClassesToMessage(Long teacherId) {
		List<ClassToMessageVO> ret = new ArrayList<>();
		for (CoursePO coursePO : courseDao.findAllByTeacherId(teacherId)) {
			classDao.findAllByCourseId(coursePO.getId()).forEach(classPO ->
					ret.add(new ClassToMessageVO(classPO.getId(), coursePO.getName(), classPO.getStartTime().toLocalDate(),
							classPO.getClassOrder())));
		}
		return ret;
	}

	@Override
	public TakeClassesVO getClassesToTake(Long studentId) {
		StudentPO studentPO = studentDao.findOne(studentId);
		if (studentPO == null) {
			return new TakeClassesVO(Result.NOT_EXIST, null, null);
		}
		List<ClassPO> classPOS = classDao.findAll();
		Set<ClassPO> selected = studentPO.getClassScores().keySet();
		List<ClassToTakeVO> unselectedClass = new ArrayList<>();
		List<ClassToTakeVO> selectedClass = new ArrayList<>();
		for (ClassPO classPO : classPOS) {  // 遍历所有课
			CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
			if (selected.contains(classPO)) {   // 已经申请选课
				selectedClass.add(new ClassToTakeVO(classPO.getId(), coursePO.getName(),
						teacherDao.findOne(coursePO.getTeacherId()).getName(), coursePO.getGrade(), classPO.getTerm(),
						classPO.getClassOrder(), classPO.getStartTime(), classPO.getEndTime(),
						new ClassNumberVO(classPO.getStudentScores().size(), classPO.getMaxNumber()), false));
			} else {    // 尚未申请选课
				boolean canTake = classPO.getClassState() == ClassState.NOT_STARTED
						|| classPO.getClassState() == ClassState.STARTED && classPO.getStudentScores().size() < classPO.getMaxNumber();
				unselectedClass.add(new ClassToTakeVO(classPO.getId(), coursePO.getName(),
						teacherDao.findOne(coursePO.getTeacherId()).getName(), coursePO.getGrade(), classPO.getTerm(),
						classPO.getClassOrder(), classPO.getStartTime(), classPO.getEndTime(),
						new ClassNumberVO(classPO.getStudentScores().size(), classPO.getMaxNumber()), canTake));
			}
		}
		return new TakeClassesVO(Result.SUCCESS, unselectedClass, selectedClass);
	}

	@Override
	public List<TeacherCourseVO> getTeacherCourses(Long teacherId) {
		TeacherPO teacherPO = teacherDao.findOne(teacherId);
		if (teacherPO == null) {
			return null;
		}
		List<TeacherCourseVO> ret = new ArrayList<>();
		// 查询审核通过的课
		List<CoursePO> coursePOS = courseDao.findAllByTeacherId(teacherId);
		for (CoursePO coursePO : coursePOS) {
			List<ClassPO> classPOS = classDao.findAllByCourseId(coursePO.getId());
			List<TeacherClassVO> classes = new ArrayList<>();
			// 查询审核通过的班
			for (ClassPO classPO : classPOS) {
				classes.add(new TeacherClassVO(classPO.getId(), classPO.getTerm(), classPO.getClassOrder(),
						classPO.getStartTime(), classPO.getEndTime(), true));
			}
			// 查询待审核的班
			this.classesToReviewed.forEach((id, classPO) -> {
				if (classPO.getCourseId().equals(coursePO.getId())) {
					classes.add(new TeacherClassVO(id, classPO.getTerm(), classPO.getClassOrder(),
							classPO.getStartTime(), classPO.getEndTime(), false));
				}
			});
			// 装载所有帖子
			List<PostVO> posts = new ArrayList<>();
			postDao.findAllByCourseId(coursePO.getId()).forEach(postPO ->{
				PostVO postVO = new PostVO(postPO.getId(), userDao.findOne(postPO.getUserId()).getName(),
						postPO.getTitle(), postPO.getText(), postPO.getTime());
				// 装载所有回帖
				List<ReplyVO> replies = new ArrayList<>();
				replyDao.findAllByPostId(postPO.getId()).forEach(replyPO ->
						replies.add(new ReplyVO(replyPO.getId(), userDao.findOne(replyPO.getUserId()).getName(),
								replyPO.getText(), replyPO.getTime())));
				postVO.replies = replies;
				posts.add(postVO);
			});
			// 装载所有课件
			List<CoursewareVO> coursewares = new ArrayList<>();
			coursewareDao.findAllByCourseId(coursePO.getId()).forEach(coursewarePO ->
					coursewares.add(new CoursewareVO(coursewarePO.getId(), coursewarePO.getName())));
			ret.add(new TeacherCourseVO(coursePO.getId(), coursePO.getName(), coursePO.getGrade(), true,
					posts, coursewares, classes));
		}
		// 查询待审核的课
		ret.addAll(courseService.getCourseToReviewByTeacherId(teacherId));
		return ret;
	}

	@Override
	public Map<String, Object> getReview(Long adminId) {
		Map<String, Object> ret = new HashMap<>();
		UserPO adminPO = userDao.findOne(adminId);
		if (adminPO == null) {
			ret.put("result", Result.NOT_EXIST);
			return ret;
		}
		// 装载待审核课程
		ret.put("checkNewCourse", courseService.getAllCoursesToReview());
		// 装载待审核开课
		List<NewClassVO> classVOS = new ArrayList<>();
		this.classesToReviewed.forEach((id, classPO) -> {
			CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
			classVOS.add(new NewClassVO(id, coursePO.getName(), classPO.getTerm(),
					classPO.getClassOrder(), classPO.getStartTime(), classPO.getEndTime(),
					teacherDao.findOne(coursePO.getTeacherId()).getName()));
		});
		ret.put("checkNewClass", classVOS);
		ret.put("result", Result.SUCCESS);
		return ret;
	}

	@Override
	public HomeworkVO publishHomework(HomeworkVO vo) {
		StringBuilder sb = new StringBuilder();
		vo.typeRestriction.forEach(type -> sb.append(type).append('/'));
		sb.deleteCharAt(sb.length() - 1);
		HomeworkPO homeworkPO = homeworkDao.save(new HomeworkPO(vo.classId, vo.name, vo.description, vo.deadline,
				vo.sizeLimit, sb.toString()));
		vo.id = homeworkPO.getId();
		vo.numberSubmitted = 0;
		vo.publishMethod = PublishMethod.NOT_YET;
		return vo;
	}

	@Override
	public Long getCourseIdByClassId(Long classId) {
		return classDao.findOne(classId).getCourseId();
	}

	@Override
	public List<HomeworkScore> getHomeworkScores(Long userId, Long HomeworkId) {
		UserPO userPO = userDao.findOne(userId);
		HomeworkPO homeworkPO = homeworkDao.findOne(HomeworkId);
		if (userPO == null || homeworkPO == null) return null;
		List<HomeworkScore> homeworkScores = new ArrayList<>();
		final Map<StudentPO, Double> studentScores = homeworkPO.getStudentScores();
		if (homeworkPO.getPublishMethod() == PublishMethod.ONLY_FOR_ME && userPO instanceof StudentPO) {
			// 仅给学生查看本人成绩
			Double score = studentScores.get(userPO);
			String sid = this.getStrId(userPO.getEmail());
			System.out.println("仅给学生查看本人成绩: " + score + "-" + sid);
			homeworkScores.add(new HomeworkScore(userPO.getId(), sid, score == null ? 0d : score));
		} else {
			// 完全公开或教师查看
			System.out.println("完全公开或教师查看");
			for (StudentPO studentPO : classDao.findOne(homeworkPO.getClassId()).getStudentScores().keySet()) {
				Double score = studentScores.get(studentPO);
				String sid = this.getStrId(studentPO.getEmail());
				homeworkScores.add(new HomeworkScore(0L, sid, score == null ? 0d : score));
			}
		}
		System.out.println(homeworkScores);
		return homeworkScores;
	}

	@Override
	public Result updateHomeworkScores(Long homeworkId, PublishMethod publishMethod, List<HomeworkScore> scores) {
		HomeworkPO homeworkPO = homeworkDao.findOne(homeworkId);
		if (homeworkPO == null) return Result.NOT_EXIST;
		homeworkPO.setPublishMethod(publishMethod);
		for (HomeworkScore homeworkScore : scores) {
			boolean found = false;
			Map<StudentPO, Double> homeworkPOStudentScores = homeworkPO.getStudentScores();
			for (Map.Entry<StudentPO, Double> entry : homeworkPOStudentScores.entrySet()) {
				String sid = userService.getEmailPrefixById(entry.getKey().getId());
				System.out.println(homeworkScore.studentId + "-" + sid);
				if (sid.equals(homeworkScore.studentId)) {
					entry.setValue(homeworkScore.score);
					found = true;
					break;
				}
			}
			if (!found) {
				StudentPO studentPO = studentDao.findByEmailAndDeletedFalse(homeworkScore.studentId + "@smail.nju.edu.cn");
				if (studentPO == null) {
					System.out.println("错误，未找到未销号学生：" + homeworkScore.studentId + "@smail.nju.edu.cn");
				} else {
					homeworkPOStudentScores.put(studentPO, homeworkScore.score);
				}
			}
		}
		homeworkDao.save(homeworkPO);
		return Result.SUCCESS;
	}

	@Override
	public List<ClassScore> getClassScores(Long userId, Long classId) {
		UserPO userPO = userDao.findOne(userId);
		ClassPO classPO = classDao.findOne(classId);
		if (userPO == null || classPO == null) return null;
		List<ClassScore> classScores = new ArrayList<>();
		final Map<StudentPO, Double> studentScores = classPO.getStudentScores();
		if (classPO.getPublishMethod() == PublishMethod.ONLY_FOR_ME && userPO instanceof StudentPO) {
			// 仅给学生查看本人成绩
			Double score = studentScores.get(userPO);
			String sid = this.getStrId(userPO.getEmail());
			classScores.add(new ClassScore(userPO.getId(), sid, score == null ? 0d : score));
		} else {
			// 完全公开或教师查看
			for (Map.Entry<StudentPO, Double> entry : studentScores.entrySet()) {
				Double score = entry.getValue();
				String sid = this.getStrId(entry.getKey().getEmail());
				classScores.add(new ClassScore(entry.getKey().getId(), sid, score == null ? 0d : score));
			}
		}
		return classScores;
	}

	@Override
	public Result updateClassScores(Long classId, PublishMethod publishMethod, List<ClassScore> scores) {
		ClassPO classPO = classDao.findOne(classId);
		if (classPO == null) return Result.NOT_EXIST;
		classPO.setPublishMethod(publishMethod);
		for (Map.Entry<StudentPO, Double> entry : classPO.getStudentScores().entrySet()) {
			for (ClassScore ClassScore : scores) {
				if (entry.getKey().getId().equals(ClassScore.studentId)) {
					entry.setValue(ClassScore.score);
					break;
				}
			}
		}
		classDao.save(classPO);
		return Result.SUCCESS;
	}

	private String getStrId(String email) {
		return email.substring(0, email.indexOf('@'));
	}
}
