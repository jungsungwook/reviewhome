package com.memeki.reviewhome.postAddress.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.postAddress.entity.PostAddressInfo;

public interface PostAddressInfoRepository extends JpaRepository<PostAddressInfo, String>{
    PostAddressInfo findPostAddressInfoByUuid(String uuid);
    PostAddressInfo findPostAddressInfoByDongNm(String dongNm);
    PostAddressInfo findPostAddressInfoByPostAddressId(int postAddressId);
    PostAddressInfo findPostAddressInfoByDongNmAndPostAddressId(String dongNm, int postAddressId);

    List<PostAddressInfo> findAllByPostAddressId(int postAddressId);
    List<PostAddressInfo> findAllByDongNm(String dongNm);
}
