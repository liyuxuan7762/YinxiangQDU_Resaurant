package com.qdu.reggie.dto;

import com.qdu.reggie.entity.Setmeal;
import com.qdu.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

    private Long saleNum;
}
