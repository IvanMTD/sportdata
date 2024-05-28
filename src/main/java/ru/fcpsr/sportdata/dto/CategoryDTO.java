package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.enums.Category;

@Data
@NoArgsConstructor
public class CategoryDTO {
    private Category category;
    private String title;

    public CategoryDTO(Category category){
        if(category != null) {
            this.category = category;
            this.title = category.getTitle();
        }else{
            this.category = Category.NO;
            this.title = this.category.getTitle();
        }
    }
}
