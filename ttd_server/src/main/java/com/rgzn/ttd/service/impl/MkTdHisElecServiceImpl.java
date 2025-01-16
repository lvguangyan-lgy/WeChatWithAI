package com.rgzn.ttd.service.impl;

import com.rgzn.ttd.dao.MkTdHisElecMapper;
import com.rgzn.ttd.model.MkTdHisElec;
import com.rgzn.ttd.service.MkTdHisElecService;
import com.rgzn.ttd.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by lgy on 2024/11/01.
 */
@Service
@Transactional
public class MkTdHisElecServiceImpl extends AbstractService<MkTdHisElec> implements MkTdHisElecService {
    @Resource
    private MkTdHisElecMapper mkTdHisElecMapper;

}
