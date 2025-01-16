package com.rgzn.ttd.web;
import com.rgzn.ttd.core.Result;
import com.rgzn.ttd.core.ResultGenerator;
import com.rgzn.ttd.model.MkTdHisElec;
import com.rgzn.ttd.service.MkTdHisElecService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by lgy on 2024/11/01.
*/
@RestController
@RequestMapping("/mk/td/his/elec")
public class MkTdHisElecController {
    @Resource
    private MkTdHisElecService mkTdHisElecService;

    @PostMapping("/add")
    public Result add(MkTdHisElec mkTdHisElec) {
        mkTdHisElecService.save(mkTdHisElec);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        mkTdHisElecService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(MkTdHisElec mkTdHisElec) {
        mkTdHisElecService.update(mkTdHisElec);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        MkTdHisElec mkTdHisElec = mkTdHisElecService.findById(id);
        return ResultGenerator.genSuccessResult(mkTdHisElec);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<MkTdHisElec> list = mkTdHisElecService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @GetMapping("/getList")
    public Result getlist() {
        PageHelper.startPage(0, 10);
        List<MkTdHisElec> list = mkTdHisElecService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
