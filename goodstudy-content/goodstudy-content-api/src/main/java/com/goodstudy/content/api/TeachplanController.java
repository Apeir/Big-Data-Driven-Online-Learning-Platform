package com.goodstudy.content.api;

import com.goodstudy.content.model.dto.BindTeachplanMediaDto;
import com.goodstudy.content.model.dto.SaveTeachplanDto;
import com.goodstudy.content.model.dto.TeachplanDto;
import com.goodstudy.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程计划管理相关的接口
 * @date 2023/2/14 11:25
 */
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

   @ApiOperation("查询课程计划树形结构")
   //查询课程计划  GET /teachplan/22/tree-nodes
   @GetMapping("/teachplan/{courseId}/tree-nodes")
 public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
       List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

       return teachplanTree;
   }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

}
