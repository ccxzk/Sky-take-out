package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Resource
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @Transactional
    @PostMapping
    @ApiOperation("新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);

        if (dishService.getByName(dishDTO.getName()) == StatusConstant.ISEXISTED){
            return Result.error("菜品已存在");
        }else {
            dishService.addDish(dishDTO);
        }

        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> dishPage(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}", dishPageQueryDTO);

        return Result.success(dishService.pageQuery(dishPageQueryDTO));
    }

    /**
     * 菜品删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品删除：{}", ids);
        dishService.delete(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品(前端回显)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 菜品修改
     * @param dishDTO
     * @return
     */
    @Transactional
    @PutMapping
    @ApiOperation("菜品修改")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("菜品修改：{}", dishDTO);

        dishService.update(dishDTO);
        return Result.success();
    }










}
