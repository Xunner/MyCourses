package vo;

import java.util.List;
import java.util.Map;

/**
 * 发送消息VO
 * <br>
 * created on 2019/03/28
 *
 * @author 巽
 **/
public class NewMessagesVO {
	public Long senderId;
	public String title;
	public String message;
	public List<Long> classes;
	public List<Map<String, String>> users;
}
