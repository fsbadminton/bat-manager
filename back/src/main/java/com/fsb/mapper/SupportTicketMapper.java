package com.fsb.Mapper;

import com.fsb.pojo.entity.SupportTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SupportTicketMapper {
    @Insert("insert into support_ticket (ticket_no, user_id, title, description, priority, status, idempotency_key, created_at, updated_at) "
            + "values (#{ticketNo}, #{userId}, #{title}, #{description}, #{priority}, 'open', #{idempotencyKey}, now(), now()) "
            + "on duplicate key update id=last_insert_id(id)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SupportTicket ticket);

    @Select("select id, ticket_no as ticketNo, user_id as userId, title, description, priority, status, idempotency_key as idempotencyKey, admin_reply as adminReply, created_at as createdAt, updated_at as updatedAt "
            + "from support_ticket where user_id = #{userId} and idempotency_key = #{idempotencyKey} limit 1")
    SupportTicket findByIdempotencyKey(@Param("userId") Long userId,
                                       @Param("idempotencyKey") String idempotencyKey);

    @Select("select id, ticket_no as ticketNo, user_id as userId, title, description, priority, status, idempotency_key as idempotencyKey, admin_reply as adminReply, created_at as createdAt, updated_at as updatedAt "
            + "from support_ticket where user_id = #{userId} and ticket_no = #{ticketNo} limit 1")
    SupportTicket findByTicketNo(@Param("userId") Long userId, @Param("ticketNo") String ticketNo);

    @Select("select id, ticket_no as ticketNo, user_id as userId, title, description, priority, status, idempotency_key as idempotencyKey, admin_reply as adminReply, created_at as createdAt, updated_at as updatedAt "
            + "from support_ticket where user_id = #{userId} order by id desc limit #{limit}")
    List<SupportTicket> listByUser(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("select id, ticket_no as ticketNo, user_id as userId, title, description, priority, status, idempotency_key as idempotencyKey, admin_note as adminNote, admin_reply as adminReply, handled_by as handledBy, created_at as createdAt, updated_at as updatedAt "
            + "from support_ticket where ticket_no = #{ticketNo} limit 1")
    SupportTicket findAnyByTicketNo(String ticketNo);

    @Select("select id, ticket_no as ticketNo, user_id as userId, title, description, priority, status, idempotency_key as idempotencyKey, admin_note as adminNote, admin_reply as adminReply, handled_by as handledBy, created_at as createdAt, updated_at as updatedAt "
            + "from support_ticket order by id desc limit #{limit}")
    List<SupportTicket> listAll(int limit);

    @org.apache.ibatis.annotations.Update("update support_ticket set status=#{status}, admin_note=#{adminNote}, admin_reply=#{adminReply}, handled_by=#{handledBy}, updated_at=now() where ticket_no=#{ticketNo}")
    int updateStatus(@Param("ticketNo") String ticketNo, @Param("status") String status,
                     @Param("adminNote") String adminNote, @Param("adminReply") String adminReply,
                     @Param("handledBy") String handledBy);
}
