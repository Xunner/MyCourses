package vo;

import enums.Result;

import java.util.List;

/**
 * getMyInformation方法 VO
 * <br>
 * created on 2019/03/09
 *
 * @author 巽
 **/
public class MyInformationVO {
	public Result result;
	public List<Pair> userInfo;
	public List<ClassStatisticVO> classesStatistic;
	public List<MessageVO> messages;

	public MyInformationVO(Result result, List<Pair> userInfo, List<ClassStatisticVO> classesStatistic, List<MessageVO> messages) {
		this.result = result;
		this.userInfo = userInfo;
		this.classesStatistic = classesStatistic;
		this.messages = messages;
	}
}
