package org.zhong.chatgpt.wechat.bot.msgprocess;

import org.zhong.chatgpt.wechat.bot.model.BotMsg;

import cn.zhouyafeng.itchat4j.api.MessageTools;

import java.io.File;

public class WechatSendProcessor implements MsgProcessor{

	@Override
	public void process(BotMsg botMsg) {
		//判断回复是不是图片
		if (isFilePath(botMsg.getReplyMsg())){
			String filePath = botMsg.getReplyMsg();
			if (filePath.contains("@")){
				if (filePath.contains("</span>")){
					filePath = filePath.replace(filePath.substring(filePath.indexOf("@"), filePath.indexOf("</span>")+8), "");
				}else {
					filePath = filePath.replace(filePath.substring(filePath.indexOf("@"), filePath.indexOf(" ")+1), "");
				}
			}
			//发送图片
			MessageTools.sendPicMsgByUserId(botMsg.getBaseMsg().getFromUserName(),filePath);


		}else {
			MessageTools.sendMsgById(botMsg.getReplyMsg(), botMsg.getBaseMsg().getFromUserName());
		}
	}

	private boolean isFilePath(String path){
		try {
			if (path.contains("@")){
				if (path.contains("</span>")){
					path = path.replace(path.substring(path.indexOf("@"), path.indexOf("</span>")+8), "");
				}else {
					path = path.replace(path.substring(path.indexOf("@"), path.indexOf(" ")+1), "");
				}
			}
			File file = new File(path);
			return file.exists();
		}catch (Exception e){
			System.out.println("文件路径判断异常,path="+path);
			return false;
		}
	}


}
