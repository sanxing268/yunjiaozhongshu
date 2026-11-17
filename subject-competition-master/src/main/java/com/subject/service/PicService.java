package com.subject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.subject.domain.entity.Pic;
import org.apache.ibatis.annotations.Param;

public interface PicService extends IService<Pic> {
    // 新增：轮播图
    boolean addPic(Pic pic);

    // 修改：轮播图
    boolean updatePic(Pic pic);

    // 删除：轮播图（根据 id）
    boolean removePic(Integer id);
}

