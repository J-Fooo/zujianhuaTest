package com.dd.order;

import com.ddd.annotation_api.jrouter_api.ParameterGet;

public class Order_MainActivity$$Parameter implements ParameterGet {
  @Override
  public void getParameter(Object targetParameter) {
    Order_MainActivity t = (Order_MainActivity) targetParameter;
    t.name = t.getIntent().getStringExtra("name");
  }
}
