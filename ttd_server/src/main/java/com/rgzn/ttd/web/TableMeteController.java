package com.rgzn.ttd.web;
import com.rgzn.ttd.core.Result;
import com.rgzn.ttd.core.ResultGenerator;
import com.rgzn.ttd.model.TableMete;
import com.rgzn.ttd.service.TableMeteService;
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
@RequestMapping("/table/mete")
public class TableMeteController {
    @Resource
    private TableMeteService tableMeteService;

    @PostMapping("/add")
    public Result add(TableMete tableMete) {
        tableMeteService.save(tableMete);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        tableMeteService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(TableMete tableMete) {
        tableMeteService.update(tableMete);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        TableMete tableMete = tableMeteService.findById(id);
        return ResultGenerator.genSuccessResult(tableMete);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<TableMete> list = tableMeteService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
