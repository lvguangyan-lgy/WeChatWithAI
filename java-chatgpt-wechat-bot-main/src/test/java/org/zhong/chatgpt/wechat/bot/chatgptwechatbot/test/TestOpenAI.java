/*
package org.zhong.chatgpt.wechat.bot.chatgptwechatbot.test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.zhong.chatgpt.wechat.bot.builder.OpenAiServiceBuilder;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;

import com.theokanning.openai.completion.CompletionRequest;

import cn.zhouyafeng.itchat4j.utils.MyHttpClient;

import com.theokanning.openai.OpenAiService;

public class TestOpenAI {
	
	private static CloseableHttpClient httpClient;

	private static MyHttpClient instance = null;

	private static CookieStore cookieStore;

	static {
		cookieStore = new BasicCookieStore();

		// 将CookieStore设置到httpClient中
		//httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		
	}

	public static String getCookie(String name) {
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) {
				return cookie.getValue();
			}
		}
		return null;

	}
	
	@Test
	public void test() {
		ChatMessage sysMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "AI助手");
		ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "测试问题");
		ArrayList<ChatMessage> messages = new ArrayList<>();
		messages.add(sysMessage);
		messages.add(userMessage);
		com.theokanning.openai.service.OpenAiService service = OpenAiServiceBuilder.build(BotConfig.getAppKey(), Duration.ofSeconds(300));
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
				.builder()
				.model("gpt-3.5-turbo")
				.messages(messages)
				.n(1)
				.maxTokens(2000)
				.logitBias(new HashMap<>())
				.build();

		List<ChatCompletionChoice> choices = service.createChatCompletion(chatCompletionRequest).getChoices();
		String text = choices.get(0).getMessage().getContent();

		System.out.print(text);
	}
}
*/
