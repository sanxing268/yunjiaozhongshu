package com.subject.service;

/**
 * 向量服务接口 - 用于获取文本的向量表示
 */
public interface EmbeddingService {

  /**
   * 获取文本的向量表示
   * 
   * @param text 输入文本
   * @return 向量数组 (1536维)
   */
  float[] getEmbedding(String text) throws Exception;

  /**
   * 计算两个向量的相似度
   * 
   * @param vector1 向量1
   * @param vector2 向量2
   * @return 相似度 (0-1)
   */
  float calculateSimilarity(float[] vector1, float[] vector2);
}
