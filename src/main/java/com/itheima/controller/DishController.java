package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//菜品与菜品口味都放在这一个controller中
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @RequestMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page(page, pageSize);
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        lqw.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, lqw);
        //由于页面上还需要菜品分类名，而Dish中没有，所以我们需要用DishDto的dishDtoPage
        //然后将 Page<Dish> pageInfo的信息拷贝到 Page<DishDto> dishDtoPage中
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        //item名字随便我们任意取的，item就是表示集合中的每一条数据
        //map是对每一个元素特定的操作
        List<DishDto> dishDtos=new ArrayList<>();
        for (Dish item:records
             ) {
            Long categoryId = item.getCategoryId();
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Category category = categoryService.getById(categoryId);
            if(category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
                dishDtos.add(dishDto);
            }
        }
        dishDtoPage.setRecords(dishDtos);
        return R.success(dishDtoPage);
//        List<DishDto> collect = records.stream()
//                .map(item -> {
////                    得到每一个元素的id值
//                    Long categoryId = item.getCategoryId();
//                    DishDto dishDto = new DishDto();
//                    BeanUtils.copyProperties(item,dishDto);
////                    查分类表得到分类的名称
//                    Category category = categoryService.getById(categoryId);
//                    String categoryName = category.getName();
//                    dishDto.setCategoryName(categoryName);
//                    return dishDto;
//                }).collect(Collectors.toList());
//        //将所有的东西又变成了一个集合的形式
//        dishDtoPage.setRecords(collect);
//        return R.success(dishDtoPage);//页面就是需要这个里面的信息，所以我们返回的就是这个
        //而我们需要的这个值就是通过对象拷贝过来的，并在其中增加菜品名称。
    }
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
         dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }
    @GetMapping("/list")
    public  R<List<Dish>> list(Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId)
                .eq(Dish::getStatus,1)
                .orderByAsc(Dish::getSort);
        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
    }
}
