package service.impl;

import dao.*;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.*;
import service.CourseService;
import vo.NewCourseVO;
import vo.PostVO;
import vo.ReplyVO;
import vo.TeacherCourseVO;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 课程服务
 * <br>
 * created on 2019/03/09
 *
 * @author 巽
 **/
@Service
@Transactional
public class CourseServiceImpl implements CourseService {
	private final CourseDao courseDao;
	private final TeacherDao teacherDao;
	private final PostDao postDao;
	private final ReplyDao replyDao;
	private final UserDao userDao;
	private final MessageDao messageDao;
	private final CoursewareDao coursewareDao;
	private final Map<Long, CoursePO> coursesToReviewed = Collections.synchronizedMap(new HashMap<>());
	private Long id = 0L;
	private final Boolean lock = true;

	@Autowired
	public CourseServiceImpl(CourseDao courseDao, TeacherDao teacherDao, PostDao postDao, ReplyDao replyDao, UserDao userDao, MessageDao messageDao, CoursewareDao coursewareDao) {
		this.courseDao = courseDao;
		this.teacherDao = teacherDao;
		this.postDao = postDao;
		this.replyDao = replyDao;
		this.userDao = userDao;
		this.messageDao = messageDao;
		this.coursewareDao = coursewareDao;
	}

	@Override
	public Result createCourse(Long teacherId, String name, Integer grade) {
		synchronized (lock) {
			coursesToReviewed.put(++id, new CoursePO(teacherId, name, grade));
		}
		return Result.SUCCESS;
	}

	@Override
	public Result reviewCourse(Long courseId, boolean pass) {
		Iterator<Map.Entry<Long, CoursePO>> entries = coursesToReviewed.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Long, CoursePO> entry = entries.next();
			if (courseId.equals(entry.getKey())) {
				if (pass) {
					courseDao.save(entry.getValue());
					CoursePO coursePO = entry.getValue();
					messageDao.save(new MessagePO(null, coursePO.getTeacherId(), LocalDateTime.now(),
							"创建课程审核通过", "您创建的课程《" + coursePO.getName() + "》已通过管理员审核，您现在可以发布该课程了。", true));
				} else {
					CoursePO coursePO = entry.getValue();
					messageDao.save(new MessagePO(null, coursePO.getTeacherId(), LocalDateTime.now(),
							"创建课程审核未通过", "您创建的课程《" + coursePO.getName() + "》未能通过管理员审核，请联系管理员了解详情。", true));
				}
				entries.remove();
				System.out.println("完成开课在线审核：" + courseId);
				return Result.SUCCESS;
			}
		}
		return Result.NOT_EXIST;
	}

	@Override
	public Long uploadCourseware(Long courseId, String name) {
		return coursewareDao.save(new CoursewarePO(courseId, name)).getId();
	}

	@Override
	public String getFilePathByCoursewareId(Long coursewareId) {
		CoursewarePO coursewarePO = coursewareDao.findOne(coursewareId);
		return "/courseware/" + coursewarePO.getCourseId() + "/" + coursewarePO.getName();
	}

	@Override
	public List<TeacherCourseVO> getCourseToReviewByTeacherId(Long teacherId) {
		List<TeacherCourseVO> ret = new ArrayList<>();
		this.coursesToReviewed.forEach((id, coursePO) -> {
			if (coursePO.getTeacherId().equals(teacherId)) {
				ret.add(new TeacherCourseVO(id, coursePO.getName(), coursePO.getGrade(), false,
						new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
			}
		});
		return ret;
	}

	@Override
	public List<NewCourseVO> getAllCoursesToReview() {
		List<NewCourseVO> ret = new ArrayList<>();
		this.coursesToReviewed.forEach((id, course) -> {
			ret.add(new NewCourseVO(id, course.getGrade(), course.getName(), teacherDao.findOne(course.getTeacherId()).getName()));
		});
		return ret;
	}

	@Override
	public PostVO addPost(Long userId, Long courseId, String title, String text) {
		PostPO postPO = new PostPO(userId, courseId, title, text, LocalDateTime.now());
		postDao.save(postPO);
		if (postPO.getId() == null) {
			return null;
		}
		return new PostVO(postPO.getId(), userDao.findOne(userId).getName(), title, text, postPO.getTime());
	}

	@Override
	public ReplyVO addReply(Long userId, Long postId, String text) {
		ReplyPO replyPO = new ReplyPO(userId, postId, text, LocalDateTime.now());
		replyDao.save(replyPO);
		if (replyPO.getId() == null) {
			return null;
		}
		return new ReplyVO(replyPO.getId(), userDao.findOne(replyPO.getUserId()).getName(), replyPO.getText(), replyPO.getTime());
	}
}
