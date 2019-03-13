package enums;

/**
 * 开课状态
 * <br>
 * created on 2019/03/13
 *
 * @author 巽
 **/
public enum ClassState {
	NOT_STARTED("未开课"), STARTED("开课中"), CLOSED("已结课");

	ClassState(String value) {
		this.value = value;
	}

	String value;

	public String getValue() {
		return value;
	}
}
