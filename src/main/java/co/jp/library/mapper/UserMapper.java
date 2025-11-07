package co.jp.library.mapper;

import org.apache.ibatis.annotations.Mapper;

import co.jp.library.entity.User;

@Mapper
public interface UserMapper {
	
	void insert(User user);

	User findByUsername(String username);

	

	
}
