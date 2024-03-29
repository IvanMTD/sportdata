package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.Category;

@Data
@NoArgsConstructor
public class CategoryDTO {
    private Category category;
    private String title;

    public CategoryDTO(Category category){
        this.category = category;
        this.title = category.getTitle();
    }
}
