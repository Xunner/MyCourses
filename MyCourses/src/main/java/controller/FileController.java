package controller;

import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.FileService;
import service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件上传下载控制器
 * <br>
 * created on 2019/03/28
 *
 * @author 巽
 **/
@RestController
public class FileController {
	private final FileService fileService;
	private final UserService userService;

	@Autowired
	public FileController(FileService fileService, UserService userService) {
		this.fileService = fileService;
		this.userService = userService;
	}

	/**
	 * 课件上传
	 */
	@RequestMapping(value = "/uploadCourseware", method = RequestMethod.POST)
	public Map<String, Object> uploadCourseware(@RequestParam("coursewares") MultipartFile courseware,
	                                            Long courseId, HttpServletRequest request) {
		System.out.println("uploadCourseware: " + courseId);
		// 获取文件在服务器的储存位置
		String fileName = courseware.getOriginalFilename();
		boolean uploaded = this.uploadFile(courseware, request, "/courseware/" + courseId, fileName);
		Map<String, Object> ret = new HashMap<>();
		if (uploaded) {
			ret.put("result", Result.SUCCESS);
			ret.put("courseId", courseId);
			Long coursewareId = fileService.uploadCourseware(courseId, fileName);
			ret.put("coursewareId", coursewareId);
			ret.put("coursewareName", fileName);
		} else {
			ret.put("result", Result.FAILED);
		}
		return ret;
	}

	/**
	 * 作业提交
	 */
	@RequestMapping(value = "/submitHomework", method = RequestMethod.POST)
	public Map<String, Object> submitHomework(@RequestParam("file") MultipartFile submission, Long homeworkId,
	                                            Long studentId, HttpServletRequest request) {
		System.out.println("submitHomework: " + studentId + ", " + homeworkId);
		// 修改文件名
		String type = submission.getOriginalFilename();
		int idx = type.lastIndexOf('.');
		type = (idx == -1 ? "" : type.substring(idx));
		String fileName = userService.getEmailPrefixById(studentId) + type;
		// 获取文件在服务器的储存位置
		boolean uploaded = this.uploadFile(submission, request, "/homework/" + homeworkId, fileName);
		Map<String, Object> ret = new HashMap<>();
		if (uploaded) {
			Long submissionId = fileService.uploadSubmission(studentId, homeworkId, fileName);
			ret.put("result", Result.SUCCESS);
			ret.put("homeworkId", homeworkId);
			ret.put("submissionId", submissionId);
		} else {
			ret.put("result", Result.FAILED);
		}
		return ret;
	}

	private boolean uploadFile(MultipartFile file, HttpServletRequest request, String relativePath, String fileName) {
		// 获取文件在服务器的储存位置
		String path = request.getSession().getServletContext().getRealPath(relativePath);
		File filePath = new File(path);
		System.out.println("文件的保存路径：" + path);
		if (!filePath.exists() && !filePath.isDirectory()) {
			System.out.println("目录不存在，创建目录:" + filePath);
			filePath.mkdir();
		}

		File targetFile = new File(path, fileName);
		// 将文件保存到服务器指定位置
		try {
			file.transferTo(targetFile);
			return true;
		} catch (IOException e) {
			System.out.println("上传失败：" + targetFile.getAbsolutePath());
			e.printStackTrace();
			return false;
		}
	}

	@RequestMapping(value = "/downloadCourseware", method = RequestMethod.GET)
	public void downloadCourseware(@RequestParam("coursewareId")Long coursewareId, HttpServletRequest request,
	                               HttpServletResponse response) throws IOException {
		// 获取文件的相对位置
		String filePath = fileService.getFilePathByCoursewareId(coursewareId);
		this.downloadFile(request, response, filePath);
	}

	@RequestMapping(value = "/downloadSubmission", method = RequestMethod.GET)
	public void downloadSubmission(@RequestParam("submissionId")Long submissionId, HttpServletRequest request,
	                               HttpServletResponse response) throws IOException {
		// 获取文件的相对位置
		String filePath = fileService.getFilePathBySubmissionId(submissionId);
		this.downloadFile(request, response, filePath);
	}

	@RequestMapping(value = "/downloadSubmissions", method = RequestMethod.GET)
	public void downloadSubmissions(@RequestParam("homeworkId")Long homeworkId, HttpServletRequest request,
	                                HttpServletResponse response) throws IOException {
		// 获取文件的相对路径
		String filePath = fileService.getFilePathByHomeworkId(homeworkId);
		// 获取文件在服务器的储存路径
		String path = request.getSession().getServletContext().getRealPath(filePath);
		// 需要压缩的作业所在文件夹
		File file = new File(path.substring(0, path.lastIndexOf('\\')));
		// 压缩文件名
		String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

		System.out.println("downloadSubmissions: " + path);
		System.out.println("\t" + file);
		System.out.println("\t" + fileName);

		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(path));
		for (File f : Objects.requireNonNull(file.listFiles())) {
			if (f.getName().equals(fileName)) continue; // 跳过压缩文件本身
			InputStream input = new FileInputStream(f);
			zipOut.putNextEntry(new ZipEntry(f.getName()));
			int temp;
			while((temp = input.read()) != -1){
				zipOut.write(temp);
			}
			input.close();
		}
		zipOut.close();

		this.downloadFile(request, response, filePath);
		File zip = new File(path);
		zip.delete();
	}

	private void downloadFile(HttpServletRequest request, HttpServletResponse response, String filePath) throws IOException {
		// 获取文件在服务器的储存位置
		String path = request.getSession().getServletContext().getRealPath(filePath);
		// 获取文件名
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		File file = new File(path);
		System.out.println("download: " + path);
		// 设置以流的形式下载文件，这样可以实现任意格式的文件下载
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", " attachment;filename=" + fileName);
		response.setContentLength((int) file.length());

		FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
	}
}
