package controller;

import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.ClassService;
import service.CourseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传下载控制器
 * <br>
 * created on 2019/03/28
 *
 * @author 巽
 **/
@RestController
public class FileController {
	private final CourseService courseService;
	private final ClassService classService;

	@Autowired
	public FileController(CourseService courseService, ClassService classService) {
		this.courseService = courseService;
		this.classService = classService;
	}

	/**
	 * 课件上传
	 */
	@RequestMapping(value = "/uploadCourseware", method = RequestMethod.POST)
	public Map<String, Object> uploadCourseware(@RequestParam("coursewares") MultipartFile coursewares,
	                                            Long courseId, HttpServletRequest request) {
		System.out.println("uploadCourseware: " + courseId);

		// 获取文件在服务器的储存位置
		String path = request.getSession().getServletContext().getRealPath("/courseware/" + courseId);
		File filePath = new File(path);
		System.out.println("文件的保存路径：" + path);
		if (!filePath.exists() && !filePath.isDirectory()) {
			System.out.println("目录不存在，创建目录:" + filePath);
			filePath.mkdir();
		}

		Map<String, Object> ret = new HashMap<>();
		// 获取原始文件名称(包含格式)
		String originalFileName = coursewares.getOriginalFilename();
		System.out.println("原始文件名称：" + originalFileName);

		File targetFile = new File(path, originalFileName);
		// 将文件保存到服务器指定位置
		try {
			coursewares.transferTo(targetFile);
			ret.put("result", Result.SUCCESS);
			ret.put("courseId", courseId);
			Long coursewareId = courseService.uploadCourseware(courseId, originalFileName);
			ret.put("coursewareId", coursewareId);
			ret.put("coursewareName", originalFileName);
		} catch (IOException e) {
			ret.put("result", Result.FAILED);
			System.out.println("上传失败");
			e.printStackTrace();
		}
		return ret;
	}

	@RequestMapping(value = "/downloadCourseware", method = RequestMethod.GET)
	public void downloadCourseware(@RequestParam("coursewareId")Long coursewareId, HttpServletRequest request,
	                               HttpServletResponse response) throws IOException {
		// 获取文件在服务器的储存位置
		String filePath = courseService.getFilePathByCoursewareId(coursewareId);
		String path = request.getSession().getServletContext().getRealPath(filePath);
		File file = new File(path);

		// 获取文件名
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		System.out.println("downloadCourseware: " + path);

		// 设置以流的形式下载文件，这样可以实现任意格式的文件下载
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", " attachment;filename=" + fileName);
		response.setContentLength((int) file.length());

		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[128];
			int count;
			while ((count = fis.read(buffer)) > 0) {
				response.getOutputStream().write(buffer, 0, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}

	}

	// TODO /downloadSubmission
}
