package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.SetMeal;
import com.itheima.service.CategoryService;
import com.itheima.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
            setMealService.saveWithDish(setmealDto);
        return R.success("保存成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<SetMeal> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(SetMeal::getCreateTime);
        setMealService.page(pageInfo,queryWrapper);

        //在这个page中添加SetmealDto的page
        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPage);

        List<SetMeal> setMealList = pageInfo.getRecords();

        List<SetmealDto> setmealDtos = new ArrayList<>();
        for (SetMeal item:
             setMealList) {
            //将得到的记录拷贝到dto中
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            setmealDtos.add(setmealDto);
        }
        dtoPage.setRecords(setmealDtos);
        return R.success(dtoPage);
    }
    @DeleteMapping
    public R<String> deleteById( List<Long> id){
     setMealService.removeWithDish(id);
     return R.success("删除成功");
    }

}
