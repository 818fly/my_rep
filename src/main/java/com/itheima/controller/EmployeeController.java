package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Employee;
import com.itheima.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
//    /employee和/login是根据前端的请求路径来写的
//    请求的参数中带有username和password，这个名字要与实体类的属性一样，否则是无法封装成功的
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//        将页面提交的密码加密
//        实体类存放是页面的数据
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        根据页面提交的用户名查询数据库，得到查询数据库的内容
        LambdaQueryWrapper<Employee> lqw = new  LambdaQueryWrapper<Employee>();
//        Employee::getUsername其实就是表的username属性值
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);
        if(emp==null){
            return R.error("登录失败");
        }
        if(!emp.getPassword().equals(password)){
             return R.error("登录失败");
        }
//        查看员工状态是否可用
        if(emp.getStatus()==0){
            return R.error("账号被禁用");
        }
//        登录成功，将用户的id放到session中
//        session将数据保存在服务端的手段
//        将数据放到session域中
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
}
/*
* 退出*/
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
//        退出将存储的用户id清除掉
        request.getSession().removeAttribute("employee");
            return R.success("退出成功");

    }
//    新增
    @PostMapping
    public R<String> add(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
//        新增页面没有密码，需要给用户使用默认的密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
       // employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
      //  employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
        //employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.save(employee);
        return R.success("新增成功");

    }
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info(page+"  "+pageSize+" "+name);
        //mp提供的Page构造器
        Page pageInfo =new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> lqw=new LambdaQueryWrapper<>();
        //如果name不为空进行模糊查询
        lqw.like(StringUtils.isNotEmpty(name),Employee::getUsername,name);
        lqw.orderByDesc(Employee::getUpdateTime);
        //分页查询
        employeeService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        //启用与禁用，其中status与id前端请求中会带过来，放在了实体中了
        log.info(employee.toString());
       // Long empId = (Long)request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("更新成功");


    }
@GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
    Employee employee = employeeService.getById(id);
    return R.success(employee);
    }
}
