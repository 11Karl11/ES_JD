package com.karl.esjd.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author karl xie
 * Created on 2021-01-09 14:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    private String title;

    private String price;

    private String img;
}