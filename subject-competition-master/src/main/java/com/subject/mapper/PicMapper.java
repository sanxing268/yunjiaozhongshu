package com.subject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.subject.domain.entity.Pic;
import org.apache.ibatis.annotations.Param;

public interface PicMapper extends BaseMapper<Pic> {
    // 新增：轮播图
    int insertPic(Pic pic);

    // 修改：轮播图（根据 id 更新）
    int updatePicById(Pic pic);

    // 删除：轮播图（根据 id 删除）
    int deletePicById(@Param("id") Integer id);

}

