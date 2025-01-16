package org.zhong.chatgpt.wechat.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zhong.chatgpt.wechat.bot.model.Bot;

@SpringBootApplication
public class ChatgptWechatBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatgptWechatBotApplication.class, args);
		Bot.buildChatGPTWechatBot().start();
	}

}
