package vo;

import enums.PublishMethod;
import lombok.Data;

import java.util.List;

/**
 * 成绩发布VO
 * <br>
 * created on 2019/04/04
 *
 * @author 巽
 **/
@Data
public class ClassScorePublishVO {
	public Long classId;
	public PublishMethod publishMethod;
	public List<ClassScore> scores;

	public ClassScorePublishVO() {
	}

	public ClassScorePublishVO(Long classId, PublishMethod publishMethod, List<ClassScore> scores) {
		this.classId = classId;
		this.publishMethod = publishMethod;
		this.scores = scores;
	}
}
