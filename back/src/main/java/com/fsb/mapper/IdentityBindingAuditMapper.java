package com.fsb.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.fsb.pojo.entity.IdentityBindingAudit;

import java.util.List;
import java.util.Map;

@Mapper
public interface IdentityBindingAuditMapper {
    @Insert("insert into identity_binding_audit "
            + "(operator_name, action, customer_id, platform, external_user_id_hash, reason, outcome, created_at) "
            + "values (#{operatorName}, #{action}, #{customerId}, #{platform}, #{externalUserIdHash}, #{reason}, #{outcome}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(IdentityBindingAudit audit);

    @Update("update identity_binding_audit set outcome = #{outcome} where id = #{id}")
    void updateOutcome(@Param("id") Long id, @Param("outcome") String outcome);

    @Select("<script>select id, operator_name as operatorName, action, customer_id as customerId, "
            + "platform, concat(left(external_user_id_hash, 8), '…') as externalUserIdHash, reason, outcome, created_at as createdAt "
            + "from identity_binding_audit <where>"
            + "<if test='customerId != null'>customer_id = #{customerId}</if>"
            + "</where> order by id desc limit 200</script>")
    List<Map<String, Object>> list(@Param("customerId") Long customerId);
}
