package com.subject.service;

import java.util.List;
import java.util.Map;

/**
 * AI图像识别服务接口
 */
public interface AIRecognitionService {
  /**
   * 识别图像中的文字，提取教师信息
   * 
   * @param imageData 图片数据
   * @return 识别结果列表
   */
  List<Map<String, String>> recognizeTeacherInfo(byte[] imageData);

  /**
   * 生成下一个教师账号
   * 
   * @return 下一个账号
   */
  String generateNextTeacherUsername();
}
