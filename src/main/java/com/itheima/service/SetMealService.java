package com.itheima.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.SetMeal;

import java.util.List;

public interface SetMealService extends IService<SetMeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> id);
}


