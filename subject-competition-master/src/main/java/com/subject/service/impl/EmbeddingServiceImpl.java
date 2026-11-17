package com.subject.service.impl;

import com.subject.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

/**
 * 向量嵌入服务实现 - 使用OpenAI API获取文本向量
 */
@Service
public class EmbeddingServiceImpl implements EmbeddingService {

  @Value("${openai.api.key:sk-default}")
  private String apiKey;

  private static final String EMBEDDING_URL = "https://api.openai.com/v1/embeddings";
  private static final String MODEL = "text-embedding-3-small";
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public float[] getEmbedding(String text) throws Exception {
    try {
      // 调用OpenAI API
      Map<String, Object> request = new HashMap<>();
      request.put("model", MODEL);
      request.put("input", text);
      request.put("encoding_format", "float");

      URL url = new URL(EMBEDDING_URL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", "Bearer " + apiKey);
      conn.setDoOutput(true);

      // 发送请求
      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      // 解析响应
      if (conn.getResponseCode() != 200) {
        return getDefaultEmbedding(text);
      }

      Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
      String response = scanner.hasNext() ? scanner.next() : "";

      // 从响应中提取向量
      Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> data = (List<Map<String, Object>>) responseMap.get("data");

      if (data != null && !data.isEmpty()) {
        @SuppressWarnings("unchecked")
        List<Float> embedding = (List<Float>) data.get(0).get("embedding");
        float[] result = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
          result[i] = embedding.get(i);
        }
        return result;
      }

      return getDefaultEmbedding(text);
    } catch (Exception e) {
      // 异常时返回默认向量
      return getDefaultEmbedding(text);
    }
  }

  /**
   * 获取默认向量 (当API调用失败时使用)
   * 基于文本哈希生成伪向量
   */
  private float[] getDefaultEmbedding(String text) {
    float[] embedding = new float[1536];
    int hashCode = text.hashCode();
    java.util.Random random = new java.util.Random(hashCode);

    for (int i = 0; i < embedding.length; i++) {
      embedding[i] = random.nextFloat() * 2 - 1; // -1 到 1 之间
    }

    // 归一化
    float norm = 0;
    for (float v : embedding) {
      norm += v * v;
    }
    norm = (float) Math.sqrt(norm);

    for (int i = 0; i < embedding.length; i++) {
      embedding[i] /= norm;
    }

    return embedding;
  }

  @Override
  public float calculateSimilarity(float[] vector1, float[] vector2) {
    if (vector1.length != vector2.length) {
      return 0.0f;
    }

    float dotProduct = 0.0f;
    float norm1 = 0.0f;
    float norm2 = 0.0f;

    for (int i = 0; i < vector1.length; i++) {
      dotProduct += vector1[i] * vector2[i];
      norm1 += vector1[i] * vector1[i];
      norm2 += vector2[i] * vector2[i];
    }

    if (norm1 == 0 || norm2 == 0) {
      return 0.0f;
    }

    return dotProduct / ((float) Math.sqrt(norm1) * (float) Math.sqrt(norm2));
  }
}
