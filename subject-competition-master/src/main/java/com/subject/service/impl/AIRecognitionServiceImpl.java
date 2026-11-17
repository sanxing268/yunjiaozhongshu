package com.subject.service.impl;

import com.subject.mapper.UserMapper;
import com.subject.service.AIRecognitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI图像识别服务实现
 */
@Service
public class AIRecognitionServiceImpl implements AIRecognitionService {

  private static final Logger log = LoggerFactory.getLogger(AIRecognitionServiceImpl.class);

  @Autowired
  private UserMapper userMapper;

  // 阿里云千问API相关配置（从配置文件注入）
  @org.springframework.beans.factory.annotation.Value("${ai.dashscope.api-key:}")
  private String qwenApiKey;
  @org.springframework.beans.factory.annotation.Value("${ai.dashscope.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
  private String qwenBaseUrl;
  @org.springframework.beans.factory.annotation.Value("${ai.dashscope.model:qwen-plus-latest}")
  private String textModel;

  @Override
  public List<Map<String, String>> recognizeTeacherInfo(byte[] imageData) {
    List<Map<String, String>> results = new ArrayList<>();

    try {
      // 调用千问视觉识别API识别图片中的文字
      String recognizedText = recognizeImageText(imageData);
      log.info("识别的文字内容：{}", recognizedText);

      // 解析识别的文字提取教师信息
      List<Map<String, String>> teacherInfoList = parseTeacherInfo(recognizedText);

      // 一次性获取最大的用户名，然后为所有教师生成用户名（避免重复）
      long nextUsername = getMaxTeacherUsernameAndIncrement(teacherInfoList.size());

      // 为每条教师信息生成账号
      for (Map<String, String> teacherInfo : teacherInfoList) {
        if (!teacherInfo.containsKey("username") || teacherInfo.get("username").isEmpty()) {
          teacherInfo.put("username", String.valueOf(nextUsername++));
        }
        // 设置默认密码
        teacherInfo.put("password", "123456");
        // 设置状态为启用
        teacherInfo.put("enabled", "1");
        results.add(teacherInfo);
      }

    } catch (Exception e) {
      log.error("图像识别异常：", e);
    }

    return results;
  }

  /**
   * 调用千问API识别图片中的文字
   */
  private String recognizeImageText(byte[] imageData) throws Exception {
    if (qwenApiKey == null || qwenApiKey.isEmpty()) {
      throw new RuntimeException("AI API Key未配置，请在application.yaml中配置ai.dashscope.api-key或设置环境变量DASHSCOPE_API_KEY");
    }
    // 1. 图片转base64
    String base64Img = java.util.Base64.getEncoder().encodeToString(imageData);
    // 2. 构造请求体 - 使用qwen3-vl-flash进行图像识别
    String visionModel = "qwen3-vl-flash"; // 使用闪电版本的视觉模型
    String question = "请仔细识别图片中的所有人员信息。\n" +
        "对于每一个人，请分别列出：\n" +
        "姓名：[具体的人名]\n" +
        "手机号：[11位数字]\n" +
        "邮箱：[邮箱地址]\n" +
        "\n" +
        "如果有多个人，请用空行分隔不同的人。\n" +
        "重要：每个人的信息必须单独成行，不要把多个人的信息混在一起。";

    // 使用Map和ObjectMapper构造JSON，避免手动拼接导致的格式错误
    java.util.Map<String, Object> imageUrlMap = new java.util.HashMap<>();
    imageUrlMap.put("type", "image_url");
    java.util.Map<String, String> imageContentMap = new java.util.HashMap<>();
    imageContentMap.put("url", "data:image/jpeg;base64," + base64Img);
    imageUrlMap.put("image_url", imageContentMap);

    java.util.Map<String, Object> textContentMap = new java.util.HashMap<>();
    textContentMap.put("type", "text");
    textContentMap.put("text", question);

    java.util.Map<String, Object> messageMap = new java.util.HashMap<>();
    messageMap.put("role", "user");
    messageMap.put("content", java.util.Arrays.asList(imageUrlMap, textContentMap));

    java.util.Map<String, Object> requestMap = new java.util.HashMap<>();
    requestMap.put("model", visionModel);
    requestMap.put("messages", java.util.Collections.singletonList(messageMap));

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    String json = mapper.writeValueAsString(requestMap);

    okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
    okhttp3.Request request = new okhttp3.Request.Builder()
        .url(qwenBaseUrl + "/chat/completions")
        .addHeader("Authorization", "Bearer " + qwenApiKey)
        .addHeader("Content-Type", "application/json")
        .post(okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json")))
        .build();
    try (okhttp3.Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new RuntimeException(
            "千问视觉API调用失败: " + response.code() + " - " + (response.body() != null ? response.body().string() : "无响应体"));
      }
      String resp = response.body() != null ? response.body().string() : "";
      // 解析返回内容
      com.fasterxml.jackson.databind.JsonNode root = mapper
          .readTree(resp);
      com.fasterxml.jackson.databind.JsonNode choices = root.get("choices");
      if (choices != null && choices.isArray() && choices.size() > 0) {
        com.fasterxml.jackson.databind.JsonNode message = choices.get(0).get("message");
        if (message != null && message.has("content")) {
          String extractedText = message.get("content").asText();
          // 打印视觉模型的原始识别结果
          log.info("========== 视觉模型识别结果（原始） ==========");
          log.info("\n{}", extractedText);
          log.info("========== 视觉识别结束 ==========");
          // 用文本模型处理和整理识别的内容
          return processRecognizedText(extractedText);
        }
      }
      return "";
    }
  }

  /**
   * 用文本模型处理识别的文字，整理为结构化数据
   */
  private String processRecognizedText(String recognizedText) throws Exception {
    // 使用qwen-plus-latest文本模型处理识别的文字
    String promptText = "请整理以下识别到的教师信息，提取出清晰的姓名、手机号和邮箱。\n\n" +
        "输出要求：\n" +
        "1. 每一个人的信息单独为一段\n" +
        "2. 每段中用以下格式：\n" +
        "   姓名：xxx\n" +
        "   手机号：xxx\n" +
        "   邮箱：xxx\n" +
        "3. 不同的人之间用空行分隔\n" +
        "4. 如果某字段缺失，则该字段整行留空\n\n" +
        "识别的内容:\n" +
        recognizedText;

    // 使用Map构造JSON，避免手动拼接导致的格式错误
    java.util.Map<String, Object> messageMap = new java.util.HashMap<>();
    messageMap.put("role", "user");
    messageMap.put("content", promptText);

    java.util.Map<String, Object> requestMap = new java.util.HashMap<>();
    requestMap.put("model", textModel);
    requestMap.put("messages", java.util.Collections.singletonList(messageMap));

    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    String json = mapper.writeValueAsString(requestMap);

    okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
    okhttp3.Request request = new okhttp3.Request.Builder()
        .url(qwenBaseUrl + "/chat/completions")
        .addHeader("Authorization", "Bearer " + qwenApiKey)
        .addHeader("Content-Type", "application/json")
        .post(okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json")))
        .build();
    try (okhttp3.Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new RuntimeException(
            "千问文本API调用失败: " + response.code() + " - " + (response.body() != null ? response.body().string() : "无响应体"));
      }
      String resp = response.body() != null ? response.body().string() : "";
      // 解析返回内容
      com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(resp);
      com.fasterxml.jackson.databind.JsonNode choices = root.get("choices");
      if (choices != null && choices.isArray() && choices.size() > 0) {
        com.fasterxml.jackson.databind.JsonNode message = choices.get(0).get("message");
        if (message != null && message.has("content")) {
          String result = message.get("content").asText();
          // 打印文本模型处理后的结果
          log.info("========== 文本模型处理结果 ==========");
          log.info("\n{}", result);
          log.info("========== 文本处理结束 ==========");
          return result;
        }
      }
      return recognizedText;
    }
  }

  /**
   * 解析识别的文字，提取教师信息
   */
  private List<Map<String, String>> parseTeacherInfo(String text) {
    List<Map<String, String>> results = new ArrayList<>();

    log.info("========== 开始解析提取的信息 ==========");
    log.info("\n{}", text);
    log.info("========== 解析开始 ==========");

    // 方案1：先按空行分割（最优先）
    String[] paragraphs = text.split("\\n\\s*\\n");
    log.info("方案1-按空行分割: 获得 {} 段", paragraphs.length);

    // 如果只分出一段，尝试其他分割方式
    if (paragraphs.length == 1) {
      // 方案2：按单数字序号分割（如 1. 张三 或 (1) 李四）
      String[] alt2 = text.split("(?=\\n?[\\(\\(]?\\d+[\\)\\)\\.]\\s)");
      if (alt2.length > 1) {
        paragraphs = alt2;
        log.info("方案2-按数字序号分割: 获得 {} 段", paragraphs.length);
      } else {
        // 方案3：按"姓名："关键词分割
        String[] alt3 = text.split("(?=姓名[：:]|[\\n]姓名[：:])");
        if (alt3.length > 1) {
          paragraphs = alt3;
          log.info("方案3-按姓名关键词分割: 获得 {} 段", paragraphs.length);
        } else {
          // 方案4：按换行分割，每两行作为一个人的信息
          String[] lines = text.split("\\n");
          List<String> groupedParagraphs = new ArrayList<>();
          StringBuilder group = new StringBuilder();
          for (String line : lines) {
            if (line.trim().isEmpty()) {
              if (group.length() > 0) {
                groupedParagraphs.add(group.toString());
                group = new StringBuilder();
              }
            } else {
              if (group.length() > 0) {
                group.append("\n");
              }
              group.append(line);
            }
          }
          if (group.length() > 0) {
            groupedParagraphs.add(group.toString());
          }
          if (groupedParagraphs.size() > 1) {
            paragraphs = groupedParagraphs.toArray(new String[0]);
            log.info("方案4-按换行+空行分割: 获得 {} 段", paragraphs.length);
          } else {
            log.info("所有分割方案都失败，只能作为1段处理");
          }
        }
      }
    }

    for (String paragraph : paragraphs) {
      if (paragraph.trim().isEmpty())
        continue;

      Map<String, String> teacherInfo = new HashMap<>();

      // 提取姓名
      String name = extractField(paragraph, "姓名", "name");
      if (!name.isEmpty()) {
        teacherInfo.put("name", name);
        log.info("提取到姓名: {}", name);
      }

      // 提取手机号
      String phone = extractPhoneNumber(paragraph);
      if (!phone.isEmpty()) {
        teacherInfo.put("phone", phone);
        log.info("提取到手机号: {}", phone);
      }

      // 提取邮箱
      String email = extractEmail(paragraph);
      if (!email.isEmpty()) {
        teacherInfo.put("email", email);
        log.info("提取到邮箱: {}", email);
      }

      // 如果至少有一个字段有内容，则添加到结果
      if (!teacherInfo.isEmpty()) {
        results.add(teacherInfo);
        log.info("已添加一条教师记录");
      }
    }

    log.info("========== 共提取 {} 条教师信息 ==========", results.size());
    return results;
  }

  /**
   * 提取指定字段的值 - 改进版本
   * 防止多个字段在同一行时被误识别
   */
  private String extractField(String text, String... fieldNames) {
    for (String fieldName : fieldNames) {
      // 使用更严格的正则：在遇到下一个字段标签时停止
      // 支持：姓名：xxx 手机号：yyy 的格式，能正确分离
      Pattern pattern = Pattern.compile(
          fieldName + "[：:]\\s*" + // 字段名和冒号
              "([^：:【】()\n\r]*)" + // 字段值（不包含冒号和换行）
              "(?=\\s*(?:姓名|手机号|邮箱|手机|电话|邮件|Email|name|phone|email)[：:]|[\\n\\r]|$)", // 停止条件
          Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(text);
      if (matcher.find()) {
        String value = matcher.group(1).trim();
        // 特殊处理：提取前移除任何跟在后面的其他字段标签
        value = value.replaceAll("(\\s+(姓名|手机号|邮箱|手机|电话|邮件|Email|name|phone|email)[：:].*)$", "").trim();

        // 进一步清理不需要的字符（保留中英文、数字、邮箱符号）
        value = value.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9@\\.\\-\\+_]", "").trim();

        // 对姓名字段特殊处理：限制长度为2-20个字符（通常人名不会太长）
        if (fieldName.equals("姓名")) {
          // 移除任何数字（防止混入电话号码的开头）
          value = value.replaceAll("[0-9]", "").trim();
          // 限制长度
          if (value.length() > 20) {
            value = value.substring(0, 20);
          }
        }

        if (!value.isEmpty()) {
          return value;
        }
      }
    }
    return "";
  }

  /**
   * 提取手机号（11位数字） - 改进版本
   * 只从"手机号:"或"电话:"之后提取
   */
  private String extractPhoneNumber(String text) {
    // 先尝试从 "手机号：xxx" 的格式中提取
    Pattern fieldPattern = Pattern.compile(
        "(?:手机号|电话|手机|phone)[：:]\\s*(1[3-9]\\d{9})",
        Pattern.CASE_INSENSITIVE);
    Matcher fieldMatcher = fieldPattern.matcher(text);
    if (fieldMatcher.find()) {
      return fieldMatcher.group(1);
    }

    // 备选方案：直接在整个文本中查找电话号（但要确保没有被其他标签混淆）
    Pattern pattern = Pattern.compile("1[3-9]\\d{9}");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      String phone = matcher.group();
      // 确保这个电话号不在其他上下文中
      return phone;
    }
    return "";
  }

  /**
   * 提取邮箱地址 - 改进版本
   * 只从"邮箱:"或"Email:"之后提取
   */
  private String extractEmail(String text) {
    // 先尝试从 "邮箱：xxx" 的格式中提取
    Pattern fieldPattern = Pattern.compile(
        "(?:邮箱|邮件|Email|email)[：:]\\s*([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
        Pattern.CASE_INSENSITIVE);
    Matcher fieldMatcher = fieldPattern.matcher(text);
    if (fieldMatcher.find()) {
      return fieldMatcher.group(1);
    }

    // 备选方案：直接查找邮箱地址
    Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.group();
    }
    return "";
  }

  @Override
  public String generateNextTeacherUsername() {
    try {
      // 查询最大的username序号
      List<String> usernames = userMapper.selectTeacherUsernames();

      long maxNum = 1000000;
      if (usernames != null && !usernames.isEmpty()) {
        for (String username : usernames) {
          try {
            long num = Long.parseLong(username);
            if (num > maxNum) {
              maxNum = num;
            }
          } catch (NumberFormatException e) {
            // 忽略非数字账号
          }
        }
      }

      return String.valueOf(maxNum + 1);
    } catch (Exception e) {
      log.error("生成教师账号异常：", e);
      return "1000001";
    }
  }

  /**
   * 获取最大的用户名序号，然后返回下一个可用的起始编号（用于批量生成）
   * 这样可以确保批量添加时不会出现用户名重复
   */
  private long getMaxTeacherUsernameAndIncrement(int count) {
    try {
      // 查询最大的username序号
      List<String> usernames = userMapper.selectTeacherUsernames();

      long maxNum = 1000000;
      if (usernames != null && !usernames.isEmpty()) {
        for (String username : usernames) {
          try {
            long num = Long.parseLong(username);
            if (num > maxNum) {
              maxNum = num;
            }
          } catch (NumberFormatException e) {
            // 忽略非数字账号
          }
        }
      }

      log.info("当前最大用户名: {}, 本次需要生成 {} 个用户名，起始编号: {}", maxNum, count, maxNum + 1);
      return maxNum + 1;
    } catch (Exception e) {
      log.error("获取最大用户名异常：", e);
      return 1000001L;
    }
  }
}
