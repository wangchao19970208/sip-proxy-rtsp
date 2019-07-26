package com.shenzhen.teamway.mapper;

import com.shenzhen.teamway.model.Camera;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CameraMapper {
    int insert(Camera record);

    List<Camera> selectAll();

    Camera findCameraByCode(@Param("code") String code);
}