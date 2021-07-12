package com.hebau.controller;


import com.hebau.bean.Department;
import com.hebau.bean.Msg;
import com.hebau.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


    @RequestMapping("/depts")
    @ResponseBody
    public Msg getDepts(){

        List<Department> list=departmentService.getDepts();

        return Msg.success().add("depts",list);
    }


}
