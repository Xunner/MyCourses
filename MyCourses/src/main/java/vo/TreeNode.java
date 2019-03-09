package vo;

import java.util.List;

/**
 * 树节点
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class TreeNode {
	public String label;
	public List<TreeNode> children;

	public TreeNode(String label, List<TreeNode> children) {
		this.label = label;
		this.children = children;
	}
}
