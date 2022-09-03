package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.SetMeal;
import com.itheima.entity.SetmealDish;
import com.itheima.mapper.SetMealMapper;
import com.itheima.service.SetMealService;
import com.itheima.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.ref.PhantomReference;
import java.util.List;
@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //这样set_meal表中的数据就存进去了，但是setmeal_dish表还没有存进去
        this.save(setmealDto);
        //存setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish item:
             setmealDishes) {
            item.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    //删除与批量删除
    @Override
    public void removeWithDish(List<Long> id) {
        LambdaQueryWrapper<SetMeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetMeal::getId,id)
                .eq(SetMeal::getStatus,1);

        long count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖不能删除");
        }
        this.removeByIds(id);


        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,id);
//删除关系表中的数据----setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);


    }
}
