package service.impl;

import dao.*;
import enums.OperationType;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
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
	private final List<ClassPO> classesToReviewed = Collections.synchronizedList(new ArrayList<>());

	@Autowired
	public ClassServiceImpl(CourseDao courseDao, ClassDao classDao, UserDao userDao, StudentDao studentDao, TeacherDao teacherDao, HomeworkDao homeworkDao, LogDao logDao) {
		this.courseDao = courseDao;
		this.classDao = classDao;
		this.userDao = userDao;
		this.studentDao = studentDao;
		this.teacherDao = teacherDao;
		this.homeworkDao = homeworkDao;
		this.logDao = logDao;
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
	public Result publishClass(Long teacherId, Long courseId, LocalDateTime startTime, LocalDateTime endTime, List<Integer> classOrders, Integer term) {
		for (Integer classOrder : classOrders) {
			classesToReviewed.add(new ClassPO(startTime, endTime, classOrder, term));
		}
		return Result.SUCCESS;
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
		studentPO.getClassScores().put(classDao.findOne(classId), 0d);
		return Result.SUCCESS;
	}

	@Override
	public Result quitClass(Long studentId, Long classId) {
		StudentPO studentPO = studentDao.findOne(studentId);
		studentPO.getClassScores().entrySet().removeIf(entry -> entry.getKey().getId().equals(classId));
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
		CoursePO coursePO = courseDao.findOne(classPO.getCourseId());
		TeacherPO teacherPO = teacherDao.findOne(coursePO.getTeacherId());
		ClassInfo ret = new ClassInfo();
		ret.id = classPO.getId();
		ret.name = coursePO.getName();
		ret.teacherName = teacherPO.getName();
		ret.grade = coursePO.getGrade();
		ret.term = classPO.getTerm();
		ret.classOrder = classPO.getClassOrder();
		ret.startTime = classPO.getStartTime();
		ret.endTime = classPO.getEndTime();
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
		List<CoursewareVO> coursewares = new ArrayList<>();
		for (CoursewarePO coursewarePO : coursePO.getCoursewares()) {
			coursewares.add(new CoursewareVO(coursewarePO.getId(), coursewarePO.getName()));
		}
		ret.coursewares = coursewares;
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
}
