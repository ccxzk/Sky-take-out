package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    // 微信服务接口地址
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    
    @Resource
    private WeChatProperties weChatProperties;

    @Resource
    UserMapper userMapper;;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //获取openId
        String openid = getOpenid(userLoginDTO);

        //检验openId是否有效
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //检验用户是否已注册
        User user = userMapper.getByOpenid(openid);

        //若未注册则创建新用户
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.register(user);
        }

        //返回用户信息
        return user;
    }

    /**
     * 获取openId
     * @param userLoginDTO
     * @return
     */
    @Override
    public String getOpenid(UserLoginDTO userLoginDTO) {
        //封装请求参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",weChatProperties.getAppid());
        paramMap.put("secret",weChatProperties.getSecret());
        paramMap.put("js_code",userLoginDTO.getCode());
        paramMap.put("grant_type","authorization_code");

        //调用微信接口获取openId
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, paramMap);
        JSONObject jsonObject = JSONObject.parseObject(json);

        return jsonObject.getString("openid");
    }


}
