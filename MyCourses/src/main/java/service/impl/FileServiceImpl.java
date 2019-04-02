package service.impl;

import dao.CoursewareDao;
import dao.HomeworkDao;
import dao.StudentDao;
import dao.SubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import po.CoursewarePO;
import po.HomeworkPO;
import po.SubmissionPO;
import service.FileService;

import java.time.LocalDateTime;

/**
 * 文件服务实现类
 * <br>
 * created on 2019/03/31
 *
 * @author 巽
 **/
@Service
public class FileServiceImpl implements FileService {
	private final CoursewareDao coursewareDao;
	private final HomeworkDao homeworkDao;
	private final SubmissionDao submissionDao;
	private final StudentDao studentDao;

	@Autowired
	public FileServiceImpl(CoursewareDao coursewareDao, HomeworkDao homeworkDao, SubmissionDao submissionDao, StudentDao studentDao) {
		this.coursewareDao = coursewareDao;
		this.homeworkDao = homeworkDao;
		this.submissionDao = submissionDao;
		this.studentDao = studentDao;
	}

	@Override
	public String getFilePathBySubmissionId(Long submissionId) {
		SubmissionPO submissionPO = submissionDao.findOne(submissionId);
		return "/homework/" + submissionPO.getHomeworkId() + "/" + submissionPO.getName();
	}

	@Override
	public Long uploadCourseware(Long courseId, String name) {
		return coursewareDao.save(new CoursewarePO(courseId, name)).getId();
	}

	@Override
	public Long uploadSubmission(Long studentId, Long homeworkId, String name) {
		HomeworkPO homeworkPO = homeworkDao.findOne(homeworkId);
		homeworkPO.getStudentScores().put(studentDao.findOne(studentId), 0d);
		homeworkDao.save(homeworkPO);
		return submissionDao.save(new SubmissionPO(homeworkId, studentId, name, LocalDateTime.now())).getId();
	}

	@Override
	public String getFilePathByCoursewareId(Long coursewareId) {
		CoursewarePO coursewarePO = coursewareDao.findOne(coursewareId);
		return "/courseware/" + coursewarePO.getCourseId() + "/" + coursewarePO.getName();
	}

	@Override
	public String getFilePathByHomeworkId(Long homeworkId) {
//		return "/homework/" + homeworkId + "/" + homeworkDao.findOne(homeworkId).getName() + ".zip";    // 无法解决中文乱码问题，改用homeworkId作为文件名
		return "/homework/" + homeworkId + "/" + homeworkId + ".zip";
	}
}
