package com.frost.web;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.frost.utils.BqbUtil;
import com.frost.utils.DishesUtil;
import com.frost.utils.FileUtil;
import com.plexpt.chatgpt.ChatGPT;

import com.frost.entity.Result;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.util.ChatContextHolder;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Created by ${Frost-YAN} on 2018/12/18
 */
@RestController
@RequestMapping("/chatGPT")
public class HttpController {

    private static String deafualt = "你是一个非常强大、全面的人工智能助手，可以准确地回答我的问题。";
    @Autowired
    private ChatGPT chatGPT;
    @Autowired
    private OkHttpClient client;
    @Value("${file.path}")
    private String baseFilePath;
    @Value("${dashscope.qwen.apiKey}")
    private String qwenApiKey;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Result test(@RequestParam String quest){
        System.out.println("GET请求GPT,问题:"+ quest);
        try {
            Message gptMessage = Message.of(quest);
            List<Message> gptMessages = ChatContextHolder.get("1");
            if (gptMessages.size() == 0){
                Message ofSystem = Message.ofSystem(deafualt);
                Message system = ofSystem;
                gptMessages.add(system);
            }else{
                //更新上下文记录，只取第一个和最后1/10条
                ChatContextHolder.remove("1");
                ChatContextHolder.add("1",gptMessages.get(0));
                ChatContextHolder.add("1",gptMessages.get(gptMessages.size()-2));
                ChatContextHolder.add("1",gptMessages.get(gptMessages.size()-1));
            }
            //保存问题到历史记录
            ChatContextHolder.add("1",gptMessage);

            ChatCompletion chatCompletion = ChatCompletion.builder()
                    .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                    .messages(ChatContextHolder.get("1"))
                    .maxTokens(3000)
                    .temperature(0.9)
                    .build();
            ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
            Message resMessage = response.getChoices().get(0).getMessage();
            //保存回答到历史记录
            ChatContextHolder.add("1",resMessage);
            String content = resMessage.getContent();
            System.out.println("返回:"+content);
            return Result.success(content);
        }catch (Exception e){
            return Result.success("chatGPT访问异常，请联系管理员！");
        }
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Result gpt(@RequestBody com.frost.entity.Message message){
        System.out.println("POST请求GPT,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());
        try {
            Message gptMessage = Message.of(message.getContext());
            List<Message> gptMessages = ChatContextHolder.get(message.getSessionId());
            if (gptMessages.size() == 0){
                Message ofSystem = Message.ofSystem(deafualt);
                Message system = ofSystem;
                gptMessages.add(system);
            }else if (gptMessages.size() > 5){
                //更新记录，只取第一个和最后2条
                ChatContextHolder.remove(message.getSessionId());
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(0));
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(gptMessages.size()-4));
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(gptMessages.size()-3));
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(gptMessages.size()-2));
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(gptMessages.size()-1));
            }

            //保存问题到历史记录
            ChatContextHolder.add(message.getSessionId(),gptMessage);

            ChatCompletion chatCompletion = ChatCompletion.builder()
                    .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                    .messages(ChatContextHolder.get(message.getSessionId()))
                    .maxTokens(3000)
                    .temperature(0.9)
                    .build();
            ChatCompletionResponse response = chatGPT.chatCompletion(chatCompletion);
            Message resMessage = response.getChoices().get(0).getMessage();
            //保存回答到历史记录
            ChatContextHolder.add(message.getSessionId(),resMessage);
            String content = resMessage.getContent();
            System.out.println("返回:"+content);
            return Result.success(content);
        }catch (Exception e){
            return Result.success("chatGPT访问异常，请联系管理员！");
        }
    }

    @RequestMapping(method = RequestMethod.POST,value = "qwen")
    @ResponseBody
    public Result qwen(@RequestBody com.frost.entity.Message message){
        System.out.println("POST请求千问通义,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());
        try {
            Message gptMessage = Message.of(message.getContext());
            List<Message> gptMessages = ChatContextHolder.get(message.getSessionId());
            if (gptMessages.size() == 0){
                Message ofSystem = Message.ofSystem(deafualt);
                Message system = ofSystem;
                gptMessages.add(system);
            }else if (gptMessages.size() > 5){
                //更新记录
                ChatContextHolder.remove(message.getSessionId());
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(0));
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(gptMessages.size()-4));
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(gptMessages.size()-3));
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(gptMessages.size()-2));
                ChatContextHolder.add(message.getSessionId(),gptMessages.get(gptMessages.size()-1));
            }

            //保存问题到历史记录
            ChatContextHolder.add(message.getSessionId(),gptMessage);


            Generation gen = new Generation();
            Constants.apiKey=qwenApiKey;//这里填写自己申请的APIKEY
            //多轮对话内容可存入数据库，加载时循环放入放入MessageManager 对象实现对话内容加载
            List<Message> gptMessageList = ChatContextHolder.get(message.getSessionId());
            MessageManager msgManager = new MessageManager(gptMessageList.size());
            for (Message msg : gptMessageList) {
                msgManager.add(com.alibaba.dashscope.common.Message.builder().role(msg.getRole()).content(msg.getContent()).build());
            }
            QwenParam params = QwenParam.builder().model("qwen-max")//此处可根据自己需要更换AI模型
                    .messages(msgManager.get())
                    .seed(1234)
                    .topP(0.8)
                    .resultFormat("message")
                    .enableSearch(false)
                    .maxTokens(2048)
                    .temperature((float)1.0)
                    .repetitionPenalty((float)1.0)
                    .build();

            GenerationResult result = gen.call(params);
            com.alibaba.dashscope.common.Message resultMessage = result.getOutput().getChoices().get(0).getMessage();
            String content = resultMessage.getContent();


            //保存回答到历史记录
            ChatContextHolder.add(message.getSessionId(),Message.ofAssistant(content));
            System.out.println("返回:"+content);
            return Result.success(content);
        }catch (Exception e){
            return Result.success("千问通义访问异常，请联系管理员！");
        }
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/cat")
    @ResponseBody
    public Result picCat(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("图片请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());

        //构建客户端
        String url = "https://cataas.com/cat";
        if (message.getContext().contains("gif")){
            url = url + "/gif";
        }
        //构建参数
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //调用
        Call call = client.newCall(request);
        Response response = call.execute();
        InputStream inputStream = response.body().byteStream();
        String filePath = baseFilePath+System.currentTimeMillis()+".jpg";
        if (message.getContext().contains("gif")){
            filePath = baseFilePath+System.currentTimeMillis()+".gif";
        }

        FileUtil.conver(inputStream,filePath);

        //压缩gif至少于1MB
        if (filePath.endsWith(".gif")){
            filePath = FileUtil.compressGif(filePath);
        }
        System.out.println("返回:"+filePath);
        return Result.success(filePath);
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/dog")
    @ResponseBody
    public Result picDog(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("图片请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());

        //构建客户端
        String url = "https://shibe.online/api/shibes?count=1";
        //构建参数
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //调用
        Call call = client.newCall(request);
        Response response = call.execute();
        String StringTemp = response.body().string();
        JSONArray jsonArray = JSONObject.parseArray(StringTemp);
        String dogPicUrl = jsonArray.get(0).toString();

        Request request2 = new Request.Builder()
                .url(dogPicUrl)
                .get()
                .build();
        Call call2 = client.newCall(request2);
        Response response2 = call2.execute();
        InputStream inputStream = response2.body().byteStream();

        String filePath = baseFilePath+System.currentTimeMillis()+".jpg";

        FileUtil.conver(inputStream,filePath);

        //压缩gif至少于1MB
        if (filePath.endsWith(".gif")){
            filePath = FileUtil.compressGif(filePath);
        }

        System.out.println("返回:"+filePath);
        return Result.success(filePath);
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/duck")
    @ResponseBody
    public Result picDuck(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("图片请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());

        //构建客户端
        String url = "https://random-d.uk/api/v2/randomimg";
        //构建参数
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //调用
        Call call = client.newCall(request);
        Response response = call.execute();
        InputStream inputStream = response.body().byteStream();

        String filePath = baseFilePath+System.currentTimeMillis()+".jpg";

        FileUtil.conver(inputStream,filePath);

        //压缩gif至少于1MB
        if (filePath.endsWith(".gif")){
            filePath = FileUtil.compressGif(filePath);
        }
        System.out.println("返回:"+filePath);
        return Result.success(filePath);
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/waifu")
    @ResponseBody
    public Result picWaifu(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("图片请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());

        //构建客户端
        String url = "https://api.waifu.im/";
        String context = message.getContext().replace(" ","")
                .replace("#waifu","");
        if (!StringUtils.hasLength(context)){
            url = url + "search?included_tags=waifu";
        }else if (context.equals("tags")){
            url = url +context;
        }else if (context.contains("gif")){
            url = url + "search?gif=true&included_tags=" + context.replace("gif","");
        }else {
            url = url + "search?included_tags=" + context;
        }
        //构建参数
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //调用
        Call call = client.newCall(request);
        Response response = call.execute();
        String StringTemp = response.body().string();
        if (context.equals("tags")){
            System.out.println("返回:"+StringTemp);
            return Result.success(StringTemp);
        }else {
            JSONObject jsonObj = JSONObject.parseObject(StringTemp);

            JSONArray images = jsonObj.getJSONArray("images");
            JSONObject imageJsonObj = images.getJSONObject(0);
            String extension = imageJsonObj.getString("extension");
            if (".png".equals(extension)){
                extension = ".jpg";
            }
            String waifuUrl = imageJsonObj.getString("url");


            Request request2 = new Request.Builder()
                    .url(waifuUrl)
                    .get()
                    .build();
            Call call2 = client.newCall(request2);
            Response response2 = call2.execute();
            InputStream inputStream = response2.body().byteStream();

            String filePath = baseFilePath+System.currentTimeMillis()+extension;

            FileUtil.conver(inputStream,filePath);

            //压缩gif至少于1MB
            if (filePath.endsWith(".gif")){
                filePath = FileUtil.compressGif(filePath);
            }

            System.out.println("返回:"+filePath);
            return Result.success(filePath);
        }
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/waifu2")
    @ResponseBody
    public Result picWaifu2(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("图片请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());

        //构建客户端
        String url = "https://api.waifu.pics/";
        String context = message.getContext().replace(" ","")
                .replace("#2waifu","");
        if (!StringUtils.hasLength(context)){
            url = url + "SFW/waifu";
        }else if (context.equals("tags")){
            JSONArray jsonArray = new JSONArray();
            jsonArray.add("SFW/waifu");
            jsonArray.add("SFW/neko");
            jsonArray.add("SFW/shinobu");
            jsonArray.add("SFW/megumin");
            jsonArray.add("SFW/bully");
            jsonArray.add("SFW/cuddle");
            jsonArray.add("SFW/cry");
            jsonArray.add("SFW/hug");
            jsonArray.add("SFW/awoo");
            jsonArray.add("SFW/kiss");
            jsonArray.add("SFW/lick");
            jsonArray.add("SFW/pat");
            jsonArray.add("SFW/smug");
            jsonArray.add("SFW/bonk");
            jsonArray.add("SFW/yeet");
            jsonArray.add("SFW/blush");
            jsonArray.add("SFW/smile");
            jsonArray.add("SFW/wave");
            jsonArray.add("SFW/highfive");
            jsonArray.add("SFW/handhold");
            jsonArray.add("SFW/nom");
            jsonArray.add("SFW/bite");
            jsonArray.add("SFW/glomp");
            jsonArray.add("SFW/slap");
            jsonArray.add("SFW/kill");
            jsonArray.add("SFW/kick");
            jsonArray.add("SFW/happy");
            jsonArray.add("SFW/wink");
            jsonArray.add("SFW/poke");
            jsonArray.add("SFW/dance");
            jsonArray.add("SFW/cringe");

            jsonArray.add("NSFW/waifu");
            jsonArray.add("NSFW/neko");
            jsonArray.add("NSFW/trap");
            jsonArray.add("NSFW/blowjob");

            String StringTemp = jsonArray.toJSONString();
            System.out.println("返回:"+StringTemp);
            return Result.success(StringTemp);
        }else {
            url = url + context;
        }
        //构建参数
        Request request = new Request.Builder()
                .url(url.toLowerCase())
                .get()
                .build();
        //调用
        Call call = client.newCall(request);
        Response response = call.execute();
        String StringTemp = response.body().string();
        JSONObject imageJsonObj = JSONObject.parseObject(StringTemp);
        String waifuUrl = imageJsonObj.getString("url");


        Request request2 = new Request.Builder()
                .url(waifuUrl)
                .get()
                .build();
        Call call2 = client.newCall(request2);
        Response response2 = call2.execute();
        InputStream inputStream = response2.body().byteStream();

        String filePath = baseFilePath+System.currentTimeMillis()+".jpg";

        FileUtil.conver(inputStream,filePath);

        //压缩gif至少于1MB
        if (filePath.endsWith(".gif")){
            filePath = FileUtil.compressGif(filePath);
        }

        System.out.println("返回:"+filePath);
        return Result.success(filePath);
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/anosu")
    @ResponseBody
    public Result picAnosu(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("图片请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());

        //构建客户端
        String url = "https://image.anosu.top/pixiv/json";
        String context = message.getContext();
        if (context.contains("r18")){
            url = url + "?r18=1";
        }
        //构建参数
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //调用
        Call call = client.newCall(request);
        Response response = call.execute();
        String StringTemp = response.body().string();
        JSONArray jsonArray = JSONObject.parseArray(StringTemp);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String imageUrl = jsonObject.getString("url");
        //获取文件后缀名
        String suffix = imageUrl.substring(imageUrl.lastIndexOf("."));

        if (".png".equals(suffix)){
            suffix = ".jpg";
        }

        Request request2 = new Request.Builder()
                .url(imageUrl)
                .get()
                .build();
        Call call2 = client.newCall(request2);
        Response response2 = call2.execute();
        InputStream inputStream = response2.body().byteStream();

        String filePath = baseFilePath+System.currentTimeMillis()+suffix;

        FileUtil.conver(inputStream,filePath);

        //压缩gif至少于1MB
        if (filePath.endsWith(".gif")){
            filePath = FileUtil.compressGif(filePath);
        }

        System.out.println("返回:"+filePath);
        return Result.success(filePath);
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/xyz")
    @ResponseBody
    public Result picXyz(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("图片请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());

        //构建客户端
        String url = "https://3650000.xyz/api/?type=json&mode=1,2,3,5,7,8";

        //构建参数
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //调用
        Call call = client.newCall(request);
        Response response = call.execute();
        String StringTemp = response.body().string();
        JSONObject jsonObject = JSONObject.parseObject(StringTemp);
        String imageUrl = jsonObject.getString("url");

        Request request2 = new Request.Builder()
                .url(imageUrl)
                .get()
                .build();
        Call call2 = client.newCall(request2);
        Response response2 = call2.execute();
        InputStream inputStream = response2.body().byteStream();

        String filePath = baseFilePath+System.currentTimeMillis()+".jpg";

        FileUtil.conver(inputStream,filePath);

        System.out.println("返回:"+filePath);
        return Result.success(filePath);
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/bqb")
    @ResponseBody
    public Result picBqb(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("图片请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());
        String imageUrl = "";
        //处理参数
        String context = message.getContext().replace(" ","")
                .replace("#表情包","");
        if (StringUtils.hasLength(context)){
            imageUrl = BqbUtil.getRandomBqbByKeyWork(context);
        }else {
            imageUrl = BqbUtil.getRandomBqb();
        }

        if (!StringUtils.hasLength(imageUrl)){
            return Result.success("找不到您想要的表情捏，可以尝试更换或减少关键词哟");
        }

        //构建参数
        Request request = new Request.Builder()
                .url(imageUrl)
                .get()
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        InputStream inputStream = response.body().byteStream();

        //获取文件后缀名
        String suffix = imageUrl.substring(imageUrl.lastIndexOf("."));
        if (".png".equals(suffix)){
            suffix = ".jpg";
        }

        String filePath = baseFilePath+System.currentTimeMillis()+suffix;

        FileUtil.conver(inputStream,filePath);

        //压缩gif至少于1MB
        if (filePath.endsWith(".gif")){
            filePath = FileUtil.compressGif(filePath);
        }

        System.out.println("返回:"+filePath);
        return Result.success(filePath);
    }

    @RequestMapping(method = RequestMethod.POST,value = "dishes")
    @ResponseBody
    public Result dishes(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("菜肴请求,sessionId:"+message.getSessionId()+",问题:"+ message.getContext());
        String result = "";
        //处理参数
        String context = message.getContext().replace(" ","")
                .replace("#菜肴","");
        if (context.contains("tags")){
            context = context.replace("tags","");
            result = DishesUtil.getDishesName(context);
        }else {
            result = DishesUtil.getDishesByName(context);
        }

        return Result.success(result);
    }

    @RequestMapping(method = RequestMethod.POST,value = "pic/tags")
    @ResponseBody
    public Result picTags(@RequestBody com.frost.entity.Message message) throws IOException {
        System.out.println("获取话题关键词");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("#随机猫猫");
        jsonArray.add("#随机猫猫gif");
        jsonArray.add("#随机狗狗");
        jsonArray.add("#随机鸭鸭");
        jsonArray.add("#随机猫猫");
        jsonArray.add("#waifu");
        jsonArray.add("#waifutags");
        jsonArray.add("#2waifu");
        jsonArray.add("#2waifutags");
        jsonArray.add("#anosu");
        //jsonArray.add("#anosur18");
        jsonArray.add("#xyz");
        jsonArray.add("#表情包");
        jsonArray.add("#菜肴tags");
        jsonArray.add("#菜肴");
        String context = jsonArray.toJSONString();
        return Result.success(context);
    }

        public static void main(String[] args) {
        String s = "@喜羊羊?? 2@22?2";
        String s1 = "#waifu";
        String s2 = " https://i.pixiv.re/img-original/img/2021/09/28/11/04/22/93074640_p0.jpg";
        System.out.println(s2.substring(s2.lastIndexOf(".")));
    }
}
