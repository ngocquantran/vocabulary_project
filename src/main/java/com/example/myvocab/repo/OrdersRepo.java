package com.example.myvocab.repo;

import com.example.myvocab.dto.OrdersInfo;
import com.example.myvocab.model.Orders;
import com.example.myvocab.model.enummodel.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {


    Optional<OrdersInfo> findOrderInfoById(Long aLong);

    List<OrdersInfo> findByUser_Id(String userId);

    <T> List<T> findByUser_IdAndStatus(String id, OrderStatus status, Class<T> type);

    Page<OrdersInfo> findByStatusOrderByOrderDateDesc(OrderStatus status, Pageable pageable);

    @Query("select o from Orders o left join o.user left join o.aPackage where o.status = ?1 and concat(o.user.fullName,' ',o.user.email,' ',o.aPackage.title) like %?2% group by o order by o.orderDate desc ")
    Page<OrdersInfo> findByStatusAndKeyWord(OrderStatus status,String keyword, Pageable pageable);





}