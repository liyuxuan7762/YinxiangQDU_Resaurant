package com.qdu.reggie.dto;

import com.qdu.reggie.entity.Dish;
import com.qdu.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;

    private Long saleNum;
}
