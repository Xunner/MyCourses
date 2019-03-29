package service;

import enums.Result;
import po.CoursewarePO;
import vo.NewCourseVO;
import vo.PostVO;
import vo.ReplyVO;
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

	Long uploadCourseware(Long courseId, String name);

	String getFilePathByCoursewareId(Long coursewareId);

	List<TeacherCourseVO> getCourseToReviewByTeacherId(Long teacherId);

	List<NewCourseVO> getAllCoursesToReview();

	PostVO addPost(Long userId, Long courseId, String title, String text);

	ReplyVO addReply(Long userId, Long postId, String text);
}
