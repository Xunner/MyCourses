package service;

import enums.Result;
import vo.NewCourseVO;
import vo.TeacherCourseVO;

import java.util.List;

/**
 * 课程服务接口
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
public interface CourseService {
	Result createCourse(Long teacherId, String name, Integer grade);

	Result reviewCourse(Long courseId, boolean pass);

	/**
	 * TODO
	 */
	Result uploadCourseware();

	List<TeacherCourseVO> getCourseToReviewByTeacherId(Long teacherId);

	List<NewCourseVO> getAllCoursesToReview();
}
