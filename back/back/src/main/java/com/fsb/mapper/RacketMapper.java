package com.fsb.Mapper;

import com.fsb.entity.Racket;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RacketMapper {
    @Select("select * from racket where isDeleted=0")
    List<Racket> racketList();

    @Insert("insert into racket (racketid, brandid, model, type, material, price,isDeleted) VALUES (#{RacketID},#{BrandID},#{Model}, #{Type}, #{Material}, #{Price}, 0 )")
    int add(Racket racket);

    // 修改球拍信息
    @Update("update racket set racketid=#{RacketID}, brandid=#{BrandID}, model=#{Model}, type=#{Type}, " +
            "material=#{Material}, price=#{Price} where racketid=#{RacketID} and isDeleted=0")
    int update(Racket racket);

    //删除球拍信息(软删除)
    @Update("update racket set isDeleted=1 where RacketID=#{RacketID}")
    int delete(int id);

    //  查询球拍信息
    @Select("SELECT * FROM Racket WHERE RacketID=#{RacketID} and isDeleted=0")
    Racket findById(int id);

    //查询是否存在球拍id
    @Select("SELECT COUNT(*) FROM racket WHERE RacketID = #{RacketID} and isDeleted=0")
    int countRacketById(int racketID);
}
