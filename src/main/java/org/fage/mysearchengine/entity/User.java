package org.fage.mysearchengine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Caizhf
 * @version 1.0
 * @date 下午3:36 2018/5/4
 * @description 用户实体
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false,unique = true)
    private String username;        //用户名
    private String nickname;        //昵称
    private String personDetail;    //个人粗贴标签
    private String personSign;      //个人细标签
    private Integer fans;           //粉丝数量
    private Integer focus;          //关注别人的数量
    private String myUrl;         //个人主页

    public User(Integer id){
        this.id = id;
    }
}
