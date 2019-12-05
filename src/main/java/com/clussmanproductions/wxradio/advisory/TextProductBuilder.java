package com.clussmanproductions.wxradio.advisory;

import com.clussmanproductions.wxradio.util.Region;

public class TextProductBuilder {
	private String productType;
	private int number;
	private String office;
	private Region region;
	private String precautionaryPreparednessActions;
	
	public TextProductBuilder setProductType(String productType)
	{
		this.productType = productType;
		return this;
	}
	
	public TextProductBuilder setNumber(int number)
	{
		this.number = number;
		return this;
	}
}
