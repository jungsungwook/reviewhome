package com.memeki.reviewhome.postAddress.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.memeki.reviewhome.postAddress.entity.PostAddress;

public interface PostAddressRepository extends JpaRepository<PostAddress, Integer> {
    PostAddress findPostAddressById(int id);

    PostAddress findPostAddressBySigunguCdAndBjdongCdAndBunAndJi(
            String sigunguCd,
            String bjdongCd,
            String bun,
            String ji);
}
