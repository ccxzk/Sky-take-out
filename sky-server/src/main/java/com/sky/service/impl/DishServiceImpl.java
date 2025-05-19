package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private SetMealMapper setMealMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    public void addDish(DishDTO dishDTO) {
        // 添加菜品
        Dish dish = Dish.builder()
                .status(StatusConstant.ENABLE) // 默认开启
                .build();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.addDish(dish);

        //  添加菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()) {
            for (DishFlavor dishFlavor : flavors) {
                dishFlavor.setDishId(dish.getId());
            }
        }
        dishFlavorMapper.addBatchDishFlavor(flavors);
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        //封装结果集(菜品)
        long total = page.getTotal();
        List<DishVO> records = page.getResult();


        return new PageResult(total, records);
    }

    /**
     * 根据菜品名称查询菜品
     * @param name
     * @return
     */
    @Override
    public int getByName(String name) {
        return dishMapper.getByName(name);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        //根据id获取菜品数据
        Dish dish = dishMapper.getById(id);

        //获取菜品对应口味
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        //封装结果集
        DishVO dishVO = new DishVO();
        dishVO.setFlavors(flavors);
        BeanUtils.copyProperties(dish,dishVO);

        return dishVO;
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    public void delete(List<Long> ids) {
        // 判断当前菜品是否能够被删除 - 是否存在在售菜品
        // 判断当前菜品是否能够被删除 - 是否包含在套餐
        for (Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus().equals(StatusConstant.ENABLE)){
                throw new RuntimeException(dish.getName() + MessageConstant.DISH_ON_SALE);
            } else if (setMealMapper.getBySetMealId(id) == StatusConstant.ISEXISTED) {
                throw new RuntimeException(dish.getName() + MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }

        // 删除菜品对应的口味
        dishFlavorMapper.deleteBatchByDishId(ids);

        // 删除菜品数据
        dishMapper.deleteBatch(ids);

    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        //拷贝属性
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //先删除原本的口味数据，再重新进行插入
        List<DishFlavor> flavors = dishDTO.getFlavors();
        dishFlavorMapper.deleteByDishId(dishDTO.getId()); //删除原本口味
        if(flavors != null && !flavors.isEmpty()){
            dishFlavorMapper.addBatchDishFlavor(flavors); //插入新口味
        }

        //更新菜品数据
        dishMapper.update(dish);
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.update(dish);
    }

    /**
     * 获取菜品列表
     * @param categoryId
     * @param name
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId, String name) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .name(name)
                .build();
        return dishMapper.list(dish);
    }


    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}


