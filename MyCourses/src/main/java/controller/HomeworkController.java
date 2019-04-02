package controller;

import enums.PublishMethod;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.ClassService;
import vo.HomeworkScore;
import vo.HomeworkVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业功能控制器
 * <br>
 * created on 2019/03/29
 *
 * @author 巽
 **/
@RestController
public class HomeworkController {
	private final ClassService classService;

	@Autowired
	public HomeworkController(ClassService classService) {
		this.classService = classService;
	}

	@RequestMapping(value = "/publishHomework", method = RequestMethod.POST)
	public Map<String, Object> publishHomework(@RequestBody HomeworkVO homeworkVO) {
		System.out.println("publishHomework: " + homeworkVO.toString());
		Map<String, Object> ret = new HashMap<>();
		HomeworkVO returnVO = classService.publishHomework(homeworkVO);
		if (returnVO.id == null) {
			ret.put("result", Result.FAILED);
		} else {
			ret.put("result", Result.SUCCESS);
			ret.put("homework", returnVO);
		}
		return ret;
	}

	@RequestMapping(value = "/getHomeworkScores", method = RequestMethod.GET)
	public Map<String, Object> getHomeworkScores(@RequestParam(value = "homeworkId") Long homeworkId) {
		System.out.println("getHomeworkScores: " + homeworkId);
		Map<String, Object> ret = new HashMap<>();
		List<HomeworkScore> homeworkScores = classService.getHomeworkScores(homeworkId);
		if (homeworkScores == null) {
			ret.put("result", Result.FAILED);
		} else {
			ret.put("result", Result.SUCCESS);
			ret.put("homeworkScores", homeworkScores);
		}
		return ret;
	}

	@RequestMapping(value = "/updateHwScores", method = RequestMethod.POST)
	public Result updateHwScores(@RequestParam(value = "homeworkId") Long homeworkId,
	                             @RequestParam(value = "publishMethod") String publishMethod,
	                             @RequestParam(value = "scores") List<HomeworkScore> scores) {
		System.out.println("updateHwScores: " + scores.toString());
		return classService.updateHomeworkScores(homeworkId, PublishMethod.valueOf(publishMethod), scores);
	}
}
