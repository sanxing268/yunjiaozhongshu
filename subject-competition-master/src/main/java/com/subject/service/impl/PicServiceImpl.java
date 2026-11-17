package com.subject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.mapper.PicMapper;
import com.subject.domain.entity.Pic;
import com.subject.service.PicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PicServiceImpl extends ServiceImpl<PicMapper, Pic> implements PicService {

    @Autowired
    private PicMapper picMapper;
    @Override
    public boolean addPic(Pic pic) {
        if (pic == null || pic.getUrl() == null || pic.getTitle() == null) {
            return false; // 参数校验，避免空数据插入
        }
        int rows = picMapper.insertPic(pic);
        return rows > 0; //新增成功
    }

    @Override
    public boolean updatePic(Pic pic) {
        if (pic == null || pic.getId() == null) {
            return false; // 必须传入id才能修改
        }
        int rows = picMapper.updatePicById(pic);
        return rows > 0;
    }

    @Override
    public boolean removePic(Integer id) {
        if (id == null || id <= 0) {
            return false; // 无效id直接返回失败
        }
        int rows = picMapper.deletePicById(id);
        return rows > 0;
    }
}

