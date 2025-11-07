package co.jp.library.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import co.jp.library.entity.*;

@Mapper
public interface BookCategoryMapper {
	
	List<Category> getAllCategories();

    Category getCategoryById(Long categoryId);

	void addCategory(Category category);

}
