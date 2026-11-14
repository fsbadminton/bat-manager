package com.fsb.Mapper;

import com.fsb.pojo.entity.AiConversation;
import com.fsb.pojo.entity.AiDrawingRecord;
import com.fsb.pojo.entity.AiMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AiConversationMapper {
    @Select("select id, conversation_id as conversationId, user_id as userId, scene, status, "
            + "last_message_at as lastMessageAt, last_channel as lastChannel, created_at as createdAt, "
            + "updated_at as updatedAt, version from ai_conversation where user_id=#{userId} and scene=#{scene} limit 1")
    AiConversation findByUserAndScene(@Param("userId") Long userId, @Param("scene") String scene);

    @Select("select id, conversation_id as conversationId, user_id as userId, scene, status, "
            + "last_message_at as lastMessageAt, last_channel as lastChannel, created_at as createdAt, "
            + "updated_at as updatedAt, version from ai_conversation where conversation_id=#{conversationId} limit 1")
    AiConversation findByConversationId(@Param("conversationId") String conversationId);

    @Insert("insert into ai_conversation (conversation_id,user_id,scene,status,created_at,updated_at,version) "
            + "values (#{conversationId},#{userId},#{scene},'open',now(),now(),0) "
            + "on duplicate key update id=last_insert_id(id)")
    int insertConversation(AiConversation conversation);

    @Update("update ai_conversation set version=version+1, last_message_at=now(), last_channel=#{channel}, updated_at=now() "
            + "where id=#{id}")
    int advanceConversation(@Param("id") Long id, @Param("channel") String channel);

    @Select("select version from ai_conversation where id=#{id} for update")
    Long currentVersion(@Param("id") Long id);

    @Select("select id, conversation_id as conversationId, user_id as userId, channel, external_chat_id as externalChatId, "
            + "external_message_id as externalMessageId, client_message_id as clientMessageId, direction, content_type as contentType, "
            + "content, status, sequence_no as sequenceNo, model_name as modelName, tools_used as toolsUsed, error_code as errorCode, "
            + "created_at as createdAt, completed_at as completedAt from ai_message where conversation_id=#{conversationId} "
            + "order by sequence_no asc, id asc limit #{limit}")
    List<AiMessage> listMessages(@Param("conversationId") Long conversationId, @Param("limit") int limit);

    @Select("select id, conversation_id as conversationId, user_id as userId, channel, external_chat_id as externalChatId, "
            + "external_message_id as externalMessageId, client_message_id as clientMessageId, direction, content_type as contentType, "
            + "content, status, sequence_no as sequenceNo, model_name as modelName, tools_used as toolsUsed, error_code as errorCode, "
            + "created_at as createdAt, completed_at as completedAt from ai_message where id=#{id} limit 1")
    AiMessage findMessage(@Param("id") Long id);

    @Select("select id, conversation_id as conversationId, user_id as userId, channel, external_chat_id as externalChatId, "
            + "external_message_id as externalMessageId, client_message_id as clientMessageId, direction, content_type as contentType, "
            + "content, status, sequence_no as sequenceNo, model_name as modelName, tools_used as toolsUsed, error_code as errorCode, "
            + "created_at as createdAt, completed_at as completedAt from ai_message where conversation_id=#{conversationId} "
            + "and client_message_id=#{clientMessageId} limit 1")
    AiMessage findMessageByClientId(@Param("conversationId") Long conversationId,
                                    @Param("clientMessageId") String clientMessageId);

    @Insert("insert into ai_message (conversation_id,user_id,channel,external_chat_id,external_message_id,client_message_id,"
            + "direction,content_type,content,status,sequence_no,model_name,tools_used,created_at,completed_at) "
            + "values (#{conversationId},#{userId},#{channel},#{externalChatId},#{externalMessageId},#{clientMessageId},"
            + "#{direction},#{contentType},#{content},#{status},#{sequenceNo},#{modelName},#{toolsUsed},now(),"
            + "case when #{status}='completed' then now() else null end) "
            + "on duplicate key update id=last_insert_id(id)")
    int insertMessage(AiMessage message);

    @Update("update ai_message set content=#{content}, status='completed', model_name=#{modelName}, tools_used=#{toolsUsed}, "
            + "completed_at=now() where id=#{id} and user_id=#{userId}")
    int completeMessage(@Param("id") Long id, @Param("userId") Long userId,
                        @Param("content") String content, @Param("modelName") String modelName,
                        @Param("toolsUsed") String toolsUsed);

    @Update("update ai_message set status='failed', error_code=#{errorCode}, completed_at=now() "
            + "where id=#{id} and user_id=#{userId}")
    int failMessage(@Param("id") Long id, @Param("userId") Long userId, @Param("errorCode") String errorCode);

    @Select("select id, drawing_id as drawingId, user_id as userId, conversation_id as conversationId, message_id as messageId, "
            + "prompt, model_name as modelName, provider, object_key as objectKey, image_url as imageUrl, status, width, height, "
            + "error_code as errorCode, created_at as createdAt, completed_at as completedAt from ai_drawing_record "
            + "where user_id=#{userId} order by id desc limit #{limit}")
    List<AiDrawingRecord> listDrawings(@Param("userId") Long userId, @Param("limit") int limit);
}
