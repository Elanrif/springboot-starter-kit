package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {

    List<Address> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}