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
	public List<TreeNode> classesTaken;
	public List<TreeNode> classesQuit;
	public List<TreeNode> scores;
	public List<MessageVO> messages;

	public MyInformationVO(Result result, List<Pair> userInfo, List<TreeNode> classesTaken, List<TreeNode> classesQuit, List<TreeNode> scores, List<MessageVO> messages) {
		this.result = result;
		this.userInfo = userInfo;
		this.classesTaken = classesTaken;
		this.classesQuit = classesQuit;
		this.scores = scores;
		this.messages = messages;
	}
}
