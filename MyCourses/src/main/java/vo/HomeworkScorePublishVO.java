package vo;

import enums.PublishMethod;
import lombok.Data;

import java.util.List;

/**
 * 作业成绩发布VO
 * <br>
 * created on 2019/04/04
 *
 * @author 巽
 **/
@Data
public class HomeworkScorePublishVO {
	public Long homeworkId;
	public String publishMethod;
	public List<HomeworkScore> scores;

	public HomeworkScorePublishVO() {
	}

	public HomeworkScorePublishVO(Long homeworkId, String publishMethod, List<HomeworkScore> scores) {
		this.homeworkId = homeworkId;
		this.publishMethod = publishMethod;
		this.scores = scores;
	}
}
