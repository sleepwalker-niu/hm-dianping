package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryTypeList() {
        //1.查询redis中是否有数据
        String shop_list = stringRedisTemplate.opsForValue().get("SHOP_LIST");

        //2.如果有，直接返回
        if (StrUtil.isNotBlank(shop_list)) {
            List<ShopType> shopTypeList = JSONUtil.toList(shop_list, ShopType.class);
            return Result.ok(shopTypeList);
        }
        //3.如果没有，从数据库中查找
        List<ShopType> shopTypeList = query().orderByAsc("id").list();
        //4.如果数据库中不存在，返回不存在
        if (shopTypeList.size()==0) {
            return Result.fail("数据不存在");
        }
        //5.如果存在，将数据存入redis中
        stringRedisTemplate.opsForValue().set("SHOP_LIST",JSONUtil.toJsonStr(shopTypeList));
        //6.返回数据
        return Result.ok(shopTypeList);
    }
}
