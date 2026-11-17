package com.subject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.subject.mapper.PrizeMapper;
import com.subject.domain.entity.Prize;
import com.subject.service.PrizeService;
import org.springframework.stereotype.Service;

@Service
public class PrizeServiceImpl extends ServiceImpl<PrizeMapper, Prize> implements PrizeService {

}
