package org.fage.mysearchengine.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午2:09 2018/5/21
 * @description
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String hex; //tags
    private String label;   //title
    private String link;    //链接
}
