package com.d5.john.car.custom.common;

import com.d5.john.car.custom.bean.User;

import java.util.Comparator;

public class PinyinComparator implements Comparator {

	@Override
	public int compare(Object arg0, Object arg1) {
		// 按照名字排序
		User user0 = (User) arg0;
		User user1 = (User) arg1;
		String catalog0 = "";
		String catalog1 = "";
		String catStr = "";

		if (user0 != null && user0.getUserName() != null
				&& user0.getUserName().length() > 1) {
			catStr = PingYinUtil.converterToFirstSpell(user0.getUserName());
			if("".equals(catStr)){
				catalog0 = "#";
			}else {
				catalog0 = catStr.substring(0, 1);
			}
		}

		if (user1 != null && user1.getUserName() != null
				&& user1.getUserName().length() > 1) {
			catStr = PingYinUtil.converterToFirstSpell(user1.getUserName());
			if("".equals(catStr)){
				catalog0 = "#";
			}else {
				catalog1 = catStr.substring(0, 1);
			}
		}
		int flag = catalog0.compareTo(catalog1);
		return flag;

	}

}
