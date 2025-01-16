package com.rgzn.ttd.web;
import com.rgzn.ttd.core.Result;
import com.rgzn.ttd.core.ResultGenerator;
import com.rgzn.ttd.model.ColumnMete;
import com.rgzn.ttd.service.ColumnMeteService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by lgy on 2024/11/04.
*/
@RestController
@RequestMapping("/column/mete")
public class ColumnMeteController {
    @Resource
    private ColumnMeteService columnMeteService;

    @PostMapping("/add")
    public Result add(ColumnMete columnMete) {
        columnMeteService.save(columnMete);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        columnMeteService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(ColumnMete columnMete) {
        columnMeteService.update(columnMete);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        ColumnMete columnMete = columnMeteService.findById(id);
        return ResultGenerator.genSuccessResult(columnMete);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<ColumnMete> list = columnMeteService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
