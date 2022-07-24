package com.hzzt.common.entity.resp;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author: Allen
 * @date: 2022/7/24
 * @description:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentServerResp implements Serializable {
    private String country;
    private String iconUrl;
    private String serUrl;
    private String key;
    private int weight;
}
