package com.goodstudy.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/24 14:46
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
 @Override
 public CheckCodeClient create(Throwable throwable) {
  return new CheckCodeClient() {

   @Override
   public Boolean verify(String key, String code) {
    log.debug("调用验证码服务熔断异常:{}", throwable.getMessage());
    return null;
   }
  };
 }
}
