package com.hebau.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hebau.bean.Employee;
import com.hebau.bean.Msg;
import com.hebau.service.EmployeeService;


import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//处理员工CRUD请求

@Controller
public class EmployeeController {


    //@Autowired报错换@Resource
    @Resource
    EmployeeService employeeService;




    //删除员工 单个批量二合一
    //批量删除 1-2-3
    //单个删除 1
    @ResponseBody
    @RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
    public Msg deleteEmp(@PathVariable("ids")String ids){
        //批量删除
        if(ids.contains("-")){
            List<Integer> del_ids = new ArrayList<>();
            String[] str_ids = ids.split("-");
            //组装id的集合
            for (String string : str_ids) {
                del_ids.add(Integer.parseInt(string));
            }
            employeeService.deleteBatch(del_ids);
        }else{
            Integer id = Integer.parseInt(ids);
            employeeService.deleteEmp(id);
        }
        return Msg.success();
    }





    //根据id保存员工
    //如果直接发生ajax=PUT方式的请求，封装的数据全是null
    //问题是：请求体中有数据，但是Employee对象封装不上
    //原因：tomcat问题

    @ResponseBody
    @RequestMapping(value = "/emp/{empId}",method=RequestMethod.PUT)
    public Msg saveEmp(Employee employee){

        System.out.println("将要更新的员工数据："+ employee);
        employeeService.upDateEmp(employee);
        return Msg.success();
    }







    //根据id查找员工
    @RequestMapping(value="/emp/{id}",method=RequestMethod.GET)
    @ResponseBody
    public Msg getEmp(@PathVariable("id")Integer id){

        Employee employee = employeeService.getEmp(id);
        return Msg.success().add("emp", employee);
    }







    @ResponseBody
    @RequestMapping("/checkuser")
    public Msg checkuser(@RequestParam("empName")String empName){
        //先判断用户名是否是合法的表达式;
        String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
        if(!empName.matches(regx)){
            return Msg.fail().add("va_msg", "用户名必须是6-16位数字和字母的组合或者2-5位中文");
        }

        //数据库用户名重复校验
        boolean b = employeeService.checkUser(empName);
        if(b){
            return Msg.success();
        }else{
            return Msg.fail().add("va_msg", "用户名不可用");
        }
    }




    /*
    * 1.支持JSR303校验
    * 2.导入Hibernate-Validaor
    *
    * */



    @RequestMapping(value="/emp",method=RequestMethod.POST)
    @ResponseBody
    public Msg saveEmp(@Valid Employee employee,BindingResult result){
        if(result.hasErrors()){
            //校验失败，应该返回失败，在模态框中显示校验失败的错误信息
            Map<String, Object> map = new HashMap<>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError fieldError : errors) {
                System.out.println("错误的字段名："+fieldError.getField());
                System.out.println("错误信息："+fieldError.getDefaultMessage());
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return Msg.fail().add("errorFields", map);
        }else{
            employeeService.SaveEmp(employee);
            return Msg.success();
        }

    }



    @RequestMapping("/emps")
    @ResponseBody
    public Msg getEmpWithJson(@RequestParam(value = "pn",defaultValue = "1")Integer pn){
        //引入PageHelper分页插件
        //在查询之前只需调用,传入页面，以及每页大小
        PageHelper.startPage(pn,5);
        //startPage后面紧跟的查询就是分页查询
        List<Employee> emps=employeeService.getAll();
        //使用PageInfo包装查询后的结果，只需要将pageInfo交给页面就行，封装了详细的包括有我们查询出来的数据,传入连续显示的页数
        PageInfo page=new PageInfo(emps,5);

        return  Msg.success().add("pageInfo",page);
    }


    //查询员工数据（分页查询）
    //@RequestMapping("/emps")
    public String getEmps(@RequestParam(value = "pn",defaultValue = "1")Integer pn, Model model) {
        //引入PageHelper分页插件
        //在查询之前只需调用,传入页面，以及每页大小
        PageHelper.startPage(pn,5);
        //startPage后面紧跟的查询就是分页查询
        List<Employee> emps=employeeService.getAll();
        //使用PageInfo包装查询后的结果，只需要将pageInfo交给页面就行，封装了详细的包括有我们查询出来的数据,传入连续显示的页数
        PageInfo page=new PageInfo(emps,5);

        model.addAttribute("pageInfo",page);

        return "list";
    }


}
