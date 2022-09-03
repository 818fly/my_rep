package com.itheima.dto;

import com.itheima.entity.SetMeal;

import com.itheima.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends SetMeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
