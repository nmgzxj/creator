package cn.jbolt.common.model;

import cn.jbolt.common.model.base.BaseJboltFile;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class JboltFile extends BaseJboltFile<JboltFile> {
	public static final JboltFile dao = new JboltFile().dao();
	public static final int FILE_TYPE_IMAGE=1;//图片
	public static final int FILE_TYPE_VEDIO=2;//视频
	public static final int FILE_TYPE_AUDIO=3;//音频
	public static final int FILE_TYPE_ATTACHMENT=4;//附件
}
