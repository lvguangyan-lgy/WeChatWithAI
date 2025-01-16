package org.zhong.chatgpt.wechat.bot.msgprocess;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.zhong.chatgpt.wechat.bot.builder.OpenAiServiceBuilder;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;
import org.zhong.chatgpt.wechat.bot.model.FifoLinkedList;
import org.zhong.chatgpt.wechat.bot.model.WehchatMsgQueue;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import cn.zhouyafeng.itchat4j.beans.BaseMsg;

/**
 * 使用ChatGPT接口进行回复
 * @author zhong
 *
 */
public class ChatGPTReplyProcessor implements MsgProcessor{

	private static OkHttpClient client = OpenAiServiceBuilder.okHttpClient(BotConfig.getAppKey(), Duration.ofSeconds(300));
	private static OpenAiService service = OpenAiServiceBuilder.build(BotConfig.getAppKey(), Duration.ofSeconds(300));
	/**
	 * 无竞争
	 */
	private static Map<String, FifoLinkedList<ChatMessage>> mgsMap = new TreeMap<>();

	private static ChatMessage systemMessage;
	
	private static String deafualt = "你是一个非常强大、全面的人工智能助手，可以准确地回答我的问题。";
	
	public ChatGPTReplyProcessor() {
		systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), deafualt);
	}
		
	public ChatGPTReplyProcessor(String sysPrompt) {
		if(sysPrompt != null) {
			systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), sysPrompt);
		}else {
			systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), deafualt);
		}
	}
	
	@Override
	public void process(BotMsg botMsg) {
		
		BaseMsg baseMsg = botMsg.getBaseMsg();
		String userName = botMsg.getUserName();
        final FifoLinkedList<ChatMessage> messages = mgsMap.getOrDefault(userName, new FifoLinkedList<ChatMessage>(30));
        messages.add(systemMessage);
        
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), baseMsg.getContent());
        messages.add(userMessage);
        
		try {
	        /*ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
	                .builder()
	                .model("gpt-3.5-turbo")
	                .messages(messages)
	                .n(1)
	                .maxTokens(2000)
	                .logitBias(new HashMap<>())
	                .build();

	        List<ChatCompletionChoice> choices = service.createChatCompletion(chatCompletionRequest).getChoices();
	        String text = choices.get(0).getMessage().getContent();
	        */

			//默认调用gpt
			String url = BotConfig.getProxyService();
			if (baseMsg.getContent().contains("#随机猫猫")){
				url = url +"/pic/cat";
			}else if (baseMsg.getContent().contains("#随机狗狗")){
				url = url +"/pic/dog";
			}else if (baseMsg.getContent().contains("#随机鸭鸭")){
				url = url +"/pic/duck";
			}else if (baseMsg.getContent().contains("#waifu")){
				url = url +"/pic/waifu";
			}else if (baseMsg.getContent().contains("#2waifu")){
				url = url +"/pic/waifu2";
			}else if (baseMsg.getContent().contains("#anosu")){
				url = url +"/pic/anosu";
			}else if (baseMsg.getContent().contains("#xyz")){
				url = url +"/pic/xyz";
			}else if (baseMsg.getContent().contains("#表情包")){
				url = url +"/pic/bqb";
			}else if (baseMsg.getContent().contains("#picTags")){
				url = url +"/pic/tags";
			}else if (baseMsg.getContent().contains("#菜肴")){
				url = url +"/dishes";
			}

			MediaType MEDIA_TYPE_MARKDOWN = MediaType.get("application/json");
			String content = baseMsg.getContent();
			if (StringUtils.isNotEmpty(content)){
				content = content.replace(BotConfig.getAtBotName(),"");
			}
			JSONObject jsonParams = new JSONObject();
			jsonParams.put("context", content);
			if (StringUtils.isEmpty(baseMsg.getGroupName())){
				jsonParams.put("sessionId",baseMsg.getFromUserName());
			}else {
				jsonParams.put("sessionId",baseMsg.getGroupName()+baseMsg.getGroupUserName());
			}

			RequestBody requestBody = RequestBody.create(jsonParams.toJSONString(),MEDIA_TYPE_MARKDOWN);
			Request request = new Request.Builder()
					.url(url)
					.post(requestBody)
					.build();

			Call call = client.newCall(request);
			// 同步调用
			Response response = call.execute();
			String StringTemp = response.body().string();
			JSONObject parse = (JSONObject) JSONObject.parse(StringTemp);
			String text = parse.getString("msg");


	        ChatMessage assistantMessage = new ChatMessage(ChatMessageRole.ASSISTANT.value(), text);
	        messages.add(assistantMessage);
	        mgsMap.put(userName, messages);
	        
			botMsg.setReplyMsg(text);
			WehchatMsgQueue.pushSendMsg(botMsg);
		}catch (Exception e) {
			e.printStackTrace();
			
			botMsg.setRetries(botMsg.getRetries() + 1);
			if(botMsg.getRetries() < 1) {
				WehchatMsgQueue.pushReplyMsg(botMsg);
			}else {
				String recontent = baseMsg.getContent();
				if(recontent.length() > 20) {
					recontent = recontent.substring(0, 17) + "...\n";
				}
				botMsg.setReplyMsg(recontent+ "该提问已失效，请重新提问");
				WehchatMsgQueue.pushSendMsg(botMsg);
			}
			
		}
		
	}

}
