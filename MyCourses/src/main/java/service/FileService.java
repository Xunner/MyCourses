package service;

import vo.HomeworkScore;

import java.util.List;

/**
 * 与文件的上传、下载有关的服务接口
 * <br>
 * created on 2019/03/31
 *
 * @author 巽
 **/
public interface FileService {
	String getFilePathBySubmissionId(Long submissionId);

	String getFilePathByCoursewareId(Long coursewareId);

	String getFilePathByHomeworkId(Long homeworkId);

	Long uploadCourseware(Long courseId, String name);

	Long uploadSubmission(Long studentId, Long homeworkId, String name);
}
