package cn.jbolt._admin.updatemgr;

import java.util.Date;

import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.DownloadLog;

/**
* @author 小木 qq:909854136
* @version 创建时间：2019年1月11日 下午3:15:08
* 类说明
*/
public class DownloadLogService extends BaseService<DownloadLog> {

	@Override
	protected DownloadLog dao() {
		return DownloadLog.dao;
	}
	
	public void addLog(String ip,int downloadType) {
		DownloadLog log=new DownloadLog();
		log.setIpaddress(ip);
		log.setDownloadTime(new Date());
		log.setDownloadType(downloadType);
		log.save();
	}
	
	public int getIpCount() {
		Integer count=Db.queryInt("SELECT COUNT(DISTINCT ipaddress) from "+table());
		return count==null?0:count.intValue();
	}
 

}
